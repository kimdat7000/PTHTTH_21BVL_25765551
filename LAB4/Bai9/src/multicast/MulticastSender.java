package multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

public class MulticastSender {
    private static final String GROUP = "239.255.0.1";
    private static final int DEFAULT_PORT = 5009;

    public static void main(String[] args) {
        boolean localhost = args.length > 0 && args[0].equalsIgnoreCase("--localhost");
        String message = localhost
                ? (args.length > 1 ? args[1] : "LAB4 localhost test")
                : (args.length > 0 ? args[0] : "LAB4 multicast test");
        String interfaceName = !localhost && args.length > 1 ? args[1] : null;
        int port;
        try {
            int portIndex = localhost ? 2 : 2;
            port = args.length > portIndex ? Integer.parseInt(args[portIndex]) : DEFAULT_PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        try {
            InetAddress destination = InetAddress.getByName(localhost ? "127.0.0.1" : GROUP);
            NetworkInterface networkInterface = localhost ? null : MulticastNetwork.select(interfaceName);
            try (MulticastSocket socket = new MulticastSocket()) {
                if (networkInterface != null) {
                    socket.setNetworkInterface(networkInterface);
                    socket.setTimeToLive(1);
                }
                byte[] payload = message.getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(payload, payload.length, destination, port));
                System.out.println("Sent to " + destination.getHostAddress() + ":" + port
                        + (networkInterface == null ? "" : " via " + networkInterface.getName()));
            }
        } catch (IOException e) {
            System.err.println("Lỗi gửi multicast: " + e.getMessage());
        }
    }
}