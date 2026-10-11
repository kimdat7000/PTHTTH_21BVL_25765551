package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TcpChatServer {
    private static final int DEFAULT_PORT = 5005;
    private static final Pattern VALID_NICKNAME = Pattern.compile("[A-Za-z0-9_-]{1,20}");
    private static final ConcurrentHashMap<String, ClientSession> CLIENTS = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port;
        try {
            port = args.length == 0 ? DEFAULT_PORT : Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        ExecutorService pool = Executors.newFixedThreadPool(
                Math.max(4, Runtime.getRuntime().availableProcessors()));
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Chat server listening on port " + port);
            while (true) {
                Socket socket = server.accept();
                pool.execute(() -> handle(socket));
            }
        } catch (IOException e) {
            System.err.println("Lỗi chat server: " + e.getMessage());
        } finally {
            CLIENTS.values().forEach(ClientSession::close);
            pool.shutdownNow();
            try {
                pool.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void handle(Socket socket) {
        ClientSession session = null;
        try {
            session = new ClientSession(socket);
            String nickname = registerNickname(session);
            if (nickname == null) {
                return;
            }

            session.nickname = nickname;
            session.send("OK WELCOME " + nickname);
            broadcast("INFO " + nickname + " joined", session);

            String request;
            while ((request = session.in.readLine()) != null) {
                if (request.equalsIgnoreCase("USERS")) {
                    String users = CLIENTS.keySet().stream().sorted().collect(Collectors.joining(","));
                    session.send("USERS " + users);
                } else if (request.regionMatches(true, 0, "MSG ", 0, 4)) {
                    String message = request.substring(4);
                    if (message.isBlank()) {
                        session.send("ERR EMPTY_MESSAGE");
                    } else {
                        broadcast("MSG " + nickname + ": " + message, session);
                        session.send("OK SENT");
                    }
                } else if (request.equalsIgnoreCase("QUIT")) {
                    session.send("OK BYE");
                    break;
                } else {
                    session.send("ERR UNKNOWN_COMMAND");
                }
            }
        } catch (IOException e) {
            if (session != null && session.nickname != null) {
                System.err.println("Client disconnected: " + session.nickname);
            }
        } finally {
            if (session != null) {
                removeSession(session);
            } else {
                try {
                    socket.close();
                } catch (IOException ignored) {
                    // Socket is already unusable.
                }
            }
        }
    }

    private static String registerNickname(ClientSession session) throws IOException {
        session.send("NICK");
        while (true) {
            String nickname = session.in.readLine();
            if (nickname == null) {
                return null;
            }
            if (!VALID_NICKNAME.matcher(nickname).matches()) {
                session.send("ERR INVALID_NICKNAME");
                session.send("NICK");
                continue;
            }
            if (CLIENTS.putIfAbsent(nickname, session) != null) {
                session.send("ERR NICKNAME_TAKEN");
                session.send("NICK");
                continue;
            }
            return nickname;
        }
    }

    private static void broadcast(String message, ClientSession excluded) {
        CLIENTS.values().forEach(client -> {
            if (client != excluded) {
                client.send(message);
            }
        });
    }

    private static void removeSession(ClientSession session) {
        if (session.nickname != null && CLIENTS.remove(session.nickname, session)) {
            broadcast("INFO " + session.nickname + " left", session);
        }
        session.close();
    }

    private static final class ClientSession {
        private final Socket socket;
        private final BufferedReader in;
        private final PrintWriter out;
        private String nickname;

        private ClientSession(Socket socket) throws IOException {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), StandardCharsets.UTF_8));
            this.out = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.UTF_8), true);
        }

        private synchronized void send(String message) {
            out.println(message);
        }

        private void close() {
            try {
                socket.close();
            } catch (IOException ignored) {
                // Socket is already closed.
            }
        }
    }
}