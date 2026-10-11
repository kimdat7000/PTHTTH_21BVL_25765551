package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DigitTcpServer {
    private static final int DEFAULT_PORT = 5001;
    private static final String[] DIGIT_WORDS = {
            "không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"
    };

    public static void main(String[] args) {
        int port;
        try {
            port = args.length == 0 ? DEFAULT_PORT : Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("TCP digit server listening on port " + port);
            while (true) {
                Socket socket = server.accept();
                Thread clientThread = new Thread(() -> serve(socket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi TCP server: " + e.getMessage());
        }
    }

    private static void serve(Socket socket) {
        try (Socket client = socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     client.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     client.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String request;
            while ((request = in.readLine()) != null) {
                if ("QUIT".equalsIgnoreCase(request)) {
                    out.println("OK BYE");
                    break;
                }
                out.println(toVietnamese(request));
            }
        } catch (IOException e) {
            System.err.println("Lỗi phiên client: " + e.getMessage());
        }
    }

    private static String toVietnamese(String input) {
        if (input.length() != 1 || input.charAt(0) < '0' || input.charAt(0) > '9') {
            return "ERR INVALID_DIGIT";
        }
        return DIGIT_WORDS[input.charAt(0) - '0'];
    }
}