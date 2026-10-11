package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpChatClient {
    private static final int DEFAULT_PORT = 5005;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port;
        try {
            port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        try (Socket socket = new Socket(host, port);
             BufferedReader console = new BufferedReader(new InputStreamReader(
                     System.in, StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String greeting = in.readLine();
            if (!"NICK".equals(greeting)) {
                System.err.println("Handshake nickname thất bại");
                return;
            }

            while (true) {
                System.out.print("Nickname: ");
                String nickname = console.readLine();
                if (nickname == null) {
                    return;
                }
                out.println(nickname);
                String response = in.readLine();
                if (response == null) {
                    return;
                }
                if (response.startsWith("ERR ")) {
                    System.out.println(response);
                    String nextPrompt = in.readLine();
                    if (!"NICK".equals(nextPrompt)) {
                        return;
                    }
                    continue;
                }
                System.out.println(response);
                break;
            }

            Thread receiver = new Thread(() -> receiveMessages(in));
            receiver.setDaemon(true);
            receiver.start();

            String command;
            while ((command = console.readLine()) != null) {
                out.println(command);
                if (command.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
    }

    private static void receiveMessages(BufferedReader in) {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                if (message.equals("OK BYE")) {
                    return;
                }
            }
        } catch (IOException e) {
            System.err.println("Kết nối chat đã đóng");
        }
    }
}