package udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class DateTimeUdpClient {
    private static final int DEFAULT_PORT = 5003;
    private static final int TIMEOUT_MILLIS = 3000;
    private static final int MAX_PACKET_SIZE = 1024;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port;
        try {
            port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        try (DatagramSocket socket = new DatagramSocket();
             BufferedReader console = new BufferedReader(new InputStreamReader(
                     System.in, StandardCharsets.UTF_8))) {
            socket.setSoTimeout(TIMEOUT_MILLIS);
            InetAddress serverAddress = InetAddress.getByName(host);
            System.out.println("Nhập DATE, TIME, DATETIME hoặc QUIT:");
            String command;
            while ((command = console.readLine()) != null) {
                byte[] requestData = command.getBytes(StandardCharsets.UTF_8);
                DatagramPacket request = new DatagramPacket(requestData, requestData.length,
                        serverAddress, port);
                socket.send(request);

                byte[] responseData = new byte[MAX_PACKET_SIZE];
                DatagramPacket response = new DatagramPacket(responseData, responseData.length);
                try {
                    socket.receive(response);
                    System.out.println(new String(response.getData(), response.getOffset(),
                            response.getLength(), StandardCharsets.UTF_8));
                } catch (SocketTimeoutException e) {
                    System.err.println("Hết thời gian chờ phản hồi; UDP không bảo đảm gói tin được giao.");
                }

                if ("QUIT".equalsIgnoreCase(command.trim())) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi UDP client: " + e.getMessage());
        }
    }
}