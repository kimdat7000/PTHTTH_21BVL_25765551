package discovery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceDiscoveryClient {
    private static final int DEFAULT_UDP_PORT = 5010;
    private static final int DEFAULT_TIMEOUT_MILLIS = 1500;

    public static void main(String[] args) {
        String target = args.length > 0 ? args[0] : "255.255.255.255";
        int udpPort;
        int timeoutMillis;
        try {
            udpPort = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_UDP_PORT;
            timeoutMillis = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_TIMEOUT_MILLIS;
            if (udpPort < 1 || udpPort > 65535 || timeoutMillis < 1) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Usage: ServiceDiscoveryClient [broadcastAddress] [udpPort] [timeoutMs]");
            return;
        }

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] request = "DISCOVER_SERVICE".getBytes(StandardCharsets.UTF_8);
            socket.send(new DatagramPacket(request, request.length,
                    InetAddress.getByName(target), udpPort));

            long deadline = System.nanoTime() + timeoutMillis * 1_000_000L;
            Map<String, Service> services = new LinkedHashMap<>();
            byte[] buffer = new byte[512];
            while (true) {
                long remainingNanos = deadline - System.nanoTime();
                if (remainingNanos <= 0) {
                    break;
                }
                socket.setSoTimeout((int) Math.max(1, remainingNanos / 1_000_000L));
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(response);
                } catch (java.net.SocketTimeoutException e) {
                    break;
                }
                parseService(response, services);
            }

            if (services.isEmpty()) {
                System.out.println("Không tìm thấy service trong thời gian chờ " + timeoutMillis + " ms");
                return;
            }
            services.values().forEach(ServiceDiscoveryClient::connectTcp);
        } catch (IOException e) {
            System.err.println("Lỗi discovery client: " + e.getMessage());
        }
    }

    private static void parseService(DatagramPacket packet, Map<String, Service> services) {
        String response = new String(packet.getData(), packet.getOffset(), packet.getLength(),
                StandardCharsets.UTF_8).trim();
        String[] parts = response.split("\\s+");
        if (parts.length != 4 || !parts[0].equals("SERVICE")
                || !parts[1].matches("[A-Za-z0-9_-]+")) {
            return;
        }
        try {
            int tcpPort = Integer.parseInt(parts[2]);
            if (tcpPort < 1 || tcpPort > 65535) {
                return;
            }
            Service service = new Service(packet.getAddress(), parts[1], tcpPort, parts[3]);
            String key = service.address.getHostAddress() + ":" + tcpPort + "/" + parts[1] + "/" + parts[3];
            services.putIfAbsent(key, service);
        } catch (NumberFormatException ignored) {
            // Ignore malformed service advertisements.
        }
    }

    private static void connectTcp(Service service) {
        System.out.println("Found SERVICE " + service.name + " " + service.tcpPort + " "
                + service.version + " at " + service.address.getHostAddress());
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(service.address, service.tcpPort), 2000);
            socket.setSoTimeout(2000);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(
                         socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
                out.println("PING");
                System.out.println("TCP: " + in.readLine());
            }
        } catch (IOException e) {
            System.err.println("Không kết nối được " + service.name + ": " + e.getMessage());
        }
    }

    private record Service(InetAddress address, String name, int tcpPort, String version) {
    }
}