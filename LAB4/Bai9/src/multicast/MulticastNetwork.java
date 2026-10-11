package multicast;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public final class MulticastNetwork {
    private MulticastNetwork() {
    }

    public static void main(String[] args) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp() || !networkInterface.supportsMulticast()) {
                    continue;
                }
                System.out.printf("%s | %s | up=%s | multicast=%s%n",
                        networkInterface.getName(), networkInterface.getDisplayName(),
                        networkInterface.isUp(), networkInterface.supportsMulticast());
            }
        } catch (SocketException e) {
            System.err.println("Không liệt kê được NetworkInterface: " + e.getMessage());
        }
    }

    static NetworkInterface select(String name) throws IOException {
        if (name != null && !name.isBlank()) {
            NetworkInterface networkInterface = NetworkInterface.getByName(name);
            if (networkInterface == null || !networkInterface.isUp()
                    || !networkInterface.supportsMulticast()) {
                throw new IOException("NetworkInterface không dùng được cho multicast: " + name);
            }
            return networkInterface;
        }

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface candidate = interfaces.nextElement();
            try {
                if (candidate.isUp() && candidate.supportsMulticast()) {
                    return candidate;
                }
            } catch (SocketException ignored) {
                // Try the next interface if this one disappears during enumeration.
            }
        }
        throw new IOException("Không tìm thấy NetworkInterface hỗ trợ multicast");
    }
}