package network;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HostUriInspector {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java network.HostUriInspector <hostname> <uri>");
            return;
        }

        URI uri;
        try {
            uri = new URI(args[1]);
        } catch (URISyntaxException e) {
            System.err.println("URI không hợp lệ: " + e.getMessage());
            return;
        }

        try {
            InetAddress[] addresses = InetAddress.getAllByName(args[0]);
            System.out.println("Hostname: " + args[0]);
            for (InetAddress address : addresses) {
                if (address == null) {
                    continue;
                }
                String type = address instanceof Inet4Address ? "IPv4"
                        : address instanceof Inet6Address ? "IPv6" : "Unknown";
                System.out.println("IP: " + address.getHostAddress());
                System.out.println("Type: " + type);
                System.out.println("Loopback: " + address.isLoopbackAddress());
                System.out.println("Site local: " + address.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            System.err.println("Không phân giải được hostname: " + args[0]);
            return;
        }

        System.out.println("URI scheme: " + uri.getScheme());
        System.out.println("URI host: " + uri.getHost());
        System.out.println("URI port: " + uri.getPort());
        System.out.println("URI path: " + uri.getPath());
        System.out.println("URI query: " + uri.getQuery());
        System.out.println("URI fragment: " + uri.getFragment());
    }
}