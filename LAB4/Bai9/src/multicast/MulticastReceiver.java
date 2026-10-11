package multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

public class MulticastReceiver {
    private static final String GROUP = "239.255.0.1";
    private static final int DEFAULT_PORT = 5009;

    public static void main(String[] args) {
        boolean localhost = args.length > 0 && args[0].equalsIgnoreCase("--localhost");
        String interfaceName = !localhost && args.length > 0 ? args[0] : null;
        int portIndex = localhost ? 1 : 1;
        int port;
        int messageCount;
        try {
            port = args.length > portIndex ? Integer.parseInt(args[portIndex]) : DEFAULT_PORT;
            messageCount = args.length > portIndex + 1 ? Integer.parseInt(args[portIndex + 1]) : 0;
            if (messageCount < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.err.println("Port/count không hợp lệ");
            return;
        }

        MulticastSocket multicastSocket = null;
        DatagramSocket socket = null;
        NetworkInterface networkInterface = null;
        InetSocketAddress groupAddress = null;
        boolean joined = false;
        try {
            if (localhost) {
                socket = new DatagramSocket(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port));
                System.out.println("Listening on localhost:" + port);
            } else {
                networkInterface = MulticastNetwork.select(interfaceName);
                groupAddress = new InetSocketAddress(InetAddress.getByName(GROUP), port);
                multicastSocket = new MulticastSocket(null);
                multicastSocket.setReuseAddress(true);
                multicastSocket.bind(new InetSocketAddress(port));
                multicastSocket.joinGroup(groupAddress, networkInterface);
                joined = true;
                socket = multicastSocket;
                System.out.println("Joined " + GROUP + ":" + port + " via " + networkInterface.getName());
            }

            byte[] buffer = new byte[2048];
            int received = 0;
            while (messageCount == 0 || received < messageCount) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String message = new String(packet.getData(), packet.getOffset(), packet.getLength(),
                        StandardCharsets.UTF_8);
                System.out.println(packet.getAddress().getHostAddress() + ": " + message);
                received++;
            }
        } catch (IOException e) {
            System.err.println("Lỗi multicast receiver: " + e.getMessage());
        } finally {
            if (joined && multicastSocket != null) {
                try {
                    multicastSocket.leaveGroup(groupAddress, networkInterface);
                } catch (IOException e) {
                    System.err.println("Không rời được multicast group: " + e.getMessage());
                }
            }
            if (socket != null) {
                socket.close();
            }
        }
    }
}