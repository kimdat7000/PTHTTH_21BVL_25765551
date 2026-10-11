package benchmark;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Locale;

public class TransportBenchmark {
    private static final int ROUNDS = 5;
    private static final int MESSAGES_PER_ROUND = 1000;
    private static final int MESSAGE_SIZE = 128;
    private static final int UDP_TIMEOUT_MILLIS = 1000;
    private static final byte[] MESSAGE = new byte[MESSAGE_SIZE];

    static {
        Arrays.fill(MESSAGE, (byte) 'X');
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("OS: " + System.getProperty("os.name") + " "
                + System.getProperty("os.version") + "; Java: " + System.getProperty("java.version"));
        System.out.println("localhost; " + MESSAGES_PER_ROUND + " messages/round; "
                + MESSAGE_SIZE + " bytes/message; UDP timeout: " + UDP_TIMEOUT_MILLIS + " ms");
        runTcpRounds();
        runUdpRounds();
        System.out.println("Timing includes sequential request/response round trips; compare all rounds, not one run.");
    }

    private static void runTcpRounds() throws InterruptedException {
        System.out.println("TCP round,elapsed_ms,responses");
        for (int round = 1; round <= ROUNDS; round++) {
            try (ServerSocket server = new ServerSocket(0)) {
                Thread echo = new Thread(() -> tcpEcho(server));
                echo.start();
                int responses = 0;
                long elapsed = 0;
                try (Socket socket = new Socket(InetAddress.getLoopbackAddress(), server.getLocalPort());
                     DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                     DataInputStream in = new DataInputStream(socket.getInputStream())) {
                    socket.setSoTimeout(UDP_TIMEOUT_MILLIS);
                    byte[] reply = new byte[MESSAGE_SIZE];
                    long started = System.nanoTime();
                    for (int index = 0; index < MESSAGES_PER_ROUND; index++) {
                        out.write(MESSAGE);
                        out.flush();
                        in.readFully(reply);
                        if (Arrays.equals(MESSAGE, reply)) {
                            responses++;
                        }
                    }
                    elapsed = System.nanoTime() - started;
                } catch (IOException e) {
                    System.err.println("TCP round " + round + ": " + e.getMessage());
                }
                server.close();
                echo.join();
                System.out.printf(Locale.ROOT, "%d,%.3f,%d%n", round, elapsed / 1_000_000.0, responses);
            } catch (IOException e) {
                System.err.println("TCP setup failed: " + e.getMessage());
            }
        }
    }

    private static void tcpEcho(ServerSocket server) {
        try (Socket client = server.accept()) {
            byte[] buffer = new byte[MESSAGE_SIZE];
            DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            for (int index = 0; index < MESSAGES_PER_ROUND; index++) {
                in.readFully(buffer);
                out.write(buffer);
                out.flush();
            }
        } catch (IOException e) {
            if (!server.isClosed()) {
                System.err.println("TCP echo server: " + e.getMessage());
            }
        }
    }

    private static void runUdpRounds() throws InterruptedException {
        System.out.println("UDP round,elapsed_ms,responses");
        for (int round = 1; round <= ROUNDS; round++) {
            try (DatagramSocket server = new DatagramSocket(0, InetAddress.getLoopbackAddress());
                 DatagramSocket client = new DatagramSocket()) {
                server.setSoTimeout(UDP_TIMEOUT_MILLIS);
                client.setSoTimeout(UDP_TIMEOUT_MILLIS);
                Thread echo = new Thread(() -> udpEcho(server));
                echo.start();
                long started = System.nanoTime();
                int responses = 0;
                byte[] replyBytes = new byte[MESSAGE_SIZE];
                for (int index = 0; index < MESSAGES_PER_ROUND; index++) {
                    DatagramPacket request = new DatagramPacket(MESSAGE, MESSAGE.length,
                            InetAddress.getLoopbackAddress(), server.getLocalPort());
                    client.send(request);
                    DatagramPacket reply = new DatagramPacket(replyBytes, replyBytes.length);
                    try {
                        client.receive(reply);
                        if (reply.getLength() == MESSAGE_SIZE && Arrays.equals(MESSAGE, reply.getData())) {
                            responses++;
                        }
                    } catch (SocketTimeoutException e) {
                        // Continue measuring and count only received replies.
                    }
                }
                long elapsed = System.nanoTime() - started;
                server.close();
                echo.join();
                System.out.printf(Locale.ROOT, "%d,%.3f,%d%n", round, elapsed / 1_000_000.0, responses);
            } catch (IOException e) {
                System.err.println("UDP round " + round + ": " + e.getMessage());
            }
        }
    }

    private static void udpEcho(DatagramSocket server) {
        byte[] buffer = new byte[MESSAGE_SIZE];
        for (int index = 0; index < MESSAGES_PER_ROUND; index++) {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            try {
                server.receive(request);
                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(),
                        request.getAddress(), request.getPort());
                server.send(reply);
            } catch (SocketTimeoutException e) {
                index--;
            } catch (IOException e) {
                if (!server.isClosed()) {
                    System.err.println("UDP echo server: " + e.getMessage());
                }
                return;
            }
        }
    }
}