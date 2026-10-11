package tcp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class MessageLogServer {
    private static final int DEFAULT_PORT = 5007;
    private static final Pattern CLIENT_ID = Pattern.compile("[A-Za-z0-9_-]+");
    private static final Path LOG_DIRECTORY = Path.of("data", "logs");
    private static final ConcurrentHashMap<String, Object> CLIENT_LOCKS = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port;
        try {
            port = args.length == 0 ? DEFAULT_PORT : Integer.parseInt(args[0]);
            Files.createDirectories(LOG_DIRECTORY);
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        } catch (IOException e) {
            System.err.println("Không tạo được thư mục log: " + e.getMessage());
            return;
        }

        ExecutorService clients = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Message log server listening on port " + port
                    + "; logs: " + LOG_DIRECTORY.toAbsolutePath());
            while (true) {
                Socket socket = server.accept();
                clients.execute(() -> serve(socket));
            }
        } catch (IOException e) {
            System.err.println("Lỗi log server: " + e.getMessage());
        } finally {
            clients.shutdownNow();
        }
    }

    private static void serve(Socket socket) {
        try (Socket client = socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     new BufferedInputStream(client.getInputStream()), StandardCharsets.UTF_8));
             var out = new java.io.PrintWriter(client.getOutputStream(), true, StandardCharsets.UTF_8)) {
            String hello = in.readLine();
            if (hello == null || !hello.startsWith("HELLO ")) {
                out.println("ERR EXPECTED_HELLO");
                return;
            }
            String clientId = hello.substring(6);
            if (!CLIENT_ID.matcher(clientId).matches()) {
                out.println("ERR INVALID_CLIENT_ID");
                return;
            }
            Path logPath = LOG_DIRECTORY.resolve(clientId + ".txt").normalize();
            if (!logPath.getParent().equals(LOG_DIRECTORY.normalize())) {
                out.println("ERR INVALID_CLIENT_ID");
                return;
            }

            Object lock = CLIENT_LOCKS.computeIfAbsent(clientId, ignored -> new Object());
            out.println("OK HELLO " + clientId);
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("QUIT")) {
                    out.println("OK BYE");
                    break;
                }
                String entry = Instant.now() + "\t" + client.getRemoteSocketAddress()
                        + "\t" + message.replace("\t", "\\t");
                synchronized (lock) {
                    try (BufferedWriter log = Files.newBufferedWriter(logPath, StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                        log.write(entry);
                        log.newLine();
                    }
                }
                out.println("OK STORED");
            }
        } catch (IOException e) {
            System.err.println("Lỗi phiên log client: " + e.getMessage());
        }
    }
}