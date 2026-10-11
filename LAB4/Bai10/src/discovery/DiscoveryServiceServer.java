package discovery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscoveryServiceServer {
    private static final int DEFAULT_TCP_PORT = 5011;
    private static final int DEFAULT_UDP_PORT = 5010;
    private static final String DEFAULT_SERVICE_NAME = "EchoService";
    private static final String VERSION = "1";

    public static void main(String[] args) {
        String serviceName = args.length > 0 ? args[0] : DEFAULT_SERVICE_NAME;
        int tcpPort;
        int udpPort;
        try {
            tcpPort = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_TCP_PORT;
            udpPort = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_UDP_PORT;
            if (!serviceName.matches("[A-Za-z0-9_-]+") || tcpPort < 1 || tcpPort > 65535
                    || udpPort < 1 || udpPort > 65535) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Usage: DiscoveryServiceServer [serviceName] [tcpPort] [udpPort]");
            return;
        }

        ExecutorService tcpClients = Executors.newCachedThreadPool();
        try (ServerSocket tcpServer = new ServerSocket(tcpPort);
             DatagramSocket discoveryServer = new DatagramSocket(udpPort)) {
            Thread tcpThread = new Thread(() -> acceptTcp(tcpServer, tcpClients, serviceName));
            tcpThread.setDaemon(true);
            tcpThread.start();
            System.out.println("Service " + serviceName + " listening: TCP " + tcpPort
                    + ", UDP discovery " + udpPort);

            byte[] buffer = new byte[512];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                discoveryServer.receive(request);
                String message = new String(request.getData(), request.getOffset(),
                        request.getLength(), StandardCharsets.UTF_8).trim();
                if (!message.equals("DISCOVER_SERVICE")) {
                    continue;
                }
                byte[] response = ("SERVICE " + serviceName + " " + tcpPort + " " + VERSION)
                        .getBytes(StandardCharsets.UTF_8);
                DatagramPacket reply = new DatagramPacket(response, response.length,
                        request.getAddress(), request.getPort());
                discoveryServer.send(reply);
            }
        } catch (IOException e) {
            System.err.println("Lỗi discovery server: " + e.getMessage());
        } finally {
            tcpClients.shutdownNow();
        }
    }

    private static void acceptTcp(ServerSocket server, ExecutorService clients, String serviceName) {
        while (!server.isClosed()) {
            try {
                Socket client = server.accept();
                clients.execute(() -> serveTcp(client, serviceName));
            } catch (IOException e) {
                if (!server.isClosed()) {
                    System.err.println("Lỗi accept TCP: " + e.getMessage());
                }
                return;
            }
        }
    }

    private static void serveTcp(Socket socket, String serviceName) {
        try (Socket client = socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     client.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     client.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String request;
            while ((request = in.readLine()) != null) {
                switch (request) {
                    case "PING" -> out.println("PONG " + serviceName + " " + VERSION);
                    case "QUIT" -> {
                        out.println("OK BYE");
                        return;
                    }
                    default -> out.println("ERR UNKNOWN_COMMAND");
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi TCP service client: " + e.getMessage());
        }
    }
}