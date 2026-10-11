package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MessageLogClient {
    private static final int DEFAULT_PORT = 5007;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: MessageLogClient <clientId> [host] [port]");
            return;
        }
        String clientId = args[0];
        String host = args.length > 1 ? args[1] : "localhost";
        int port;
        try {
            port = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(new InputStreamReader(
                     System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {
            out.println("HELLO " + clientId);
            String response = in.readLine();
            System.out.println(response);
            if (response == null || !response.startsWith("OK HELLO ")) {
                return;
            }
            String message;
            while ((message = console.readLine()) != null) {
                out.println(message);
                response = in.readLine();
                if (response == null) {
                    System.out.println("Server đã đóng kết nối");
                    break;
                }
                System.out.println(response);
                if (message.equals("QUIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
    }
}