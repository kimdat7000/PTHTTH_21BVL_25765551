package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpCalculatorServer {
    private static final int DEFAULT_PORT = 5004;

    public static void main(String[] args) {
        int port;
        try {
            port = args.length == 0 ? DEFAULT_PORT : Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        ExecutorService clients = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Calculator server listening on port " + port);
            while (true) {
                Socket socket = server.accept();
                clients.execute(() -> serve(socket));
            }
        } catch (IOException e) {
            System.err.println("Lỗi calculator server: " + e.getMessage());
        } finally {
            clients.shutdown();
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
                if (request.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }
                out.println(CalculatorProtocol.process(request));
            }
        } catch (IOException e) {
            System.err.println("Lỗi phiên calculator: " + e.getMessage());
        }
    }
}