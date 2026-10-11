package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import tcp.DateTimeCommands;

public class DateTimeUdpServer {
    private static final int DEFAULT_PORT = 5003;
    private static final int MAX_PACKET_SIZE = 1024;

    public static void main(String[] args) {
        int port;
        try {
            port = args.length == 0 ? DEFAULT_PORT : Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        try (DatagramSocket server = new DatagramSocket(port)) {
            System.out.println("UDP date/time server listening on port " + port);
            byte[] buffer = new byte[MAX_PACKET_SIZE];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                server.receive(request);
                String command = new String(request.getData(), request.getOffset(),
                        request.getLength(), StandardCharsets.UTF_8);
                byte[] response = DateTimeCommands.process(command).getBytes(StandardCharsets.UTF_8);
                InetAddress clientAddress = request.getAddress();
                DatagramPacket reply = new DatagramPacket(response, response.length,
                        clientAddress, request.getPort());
                server.send(reply);
            }
        } catch (SocketException e) {
            System.err.println("Lỗi UDP server: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Lỗi xử lý UDP: " + e.getMessage());
        }
    }
}