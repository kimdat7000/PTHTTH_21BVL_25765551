package tcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class SafeFileClient {
    private static final int DEFAULT_PORT = 5008;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: SafeFileClient <filePath> [remoteName] [host] [port]");
            return;
        }
        Path source = Path.of(args[0]);
        String remoteName = args.length > 1 ? args[1] : source.getFileName().toString();
        String host = args.length > 2 ? args[2] : "localhost";
        int port;
        try {
            port = args.length > 3 ? Integer.parseInt(args[3]) : DEFAULT_PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        try {
            sendFile(source, remoteName, host, port);
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Lỗi gửi file: " + e.getMessage());
        }
    }

    private static void sendFile(Path source, String remoteName, String host, int port)
            throws IOException, NoSuchAlgorithmException {
        if (!Files.isRegularFile(source)) {
            throw new IOException("Không tìm thấy file thường: " + source);
        }
        if (remoteName.isBlank() || remoteName.contains(" ") || remoteName.contains("\n")) {
            throw new IOException("Tên truyền phải không rỗng và không chứa khoảng trắng");
        }

        long size = Files.size(source);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream fileIn = Files.newInputStream(source)) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = fileIn.read(buffer)) != -1) {
                digest.update(buffer, 0, count);
            }
        }
        String hash = HexFormat.of().formatHex(digest.digest());

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(10_000);
            socket.getOutputStream().write(("FILE " + remoteName + " " + size + " " + hash + "\n")
                    .getBytes(StandardCharsets.UTF_8));
            socket.getOutputStream().flush();
            Files.copy(source, socket.getOutputStream());
            socket.getOutputStream().flush();
            System.out.println(readLine(socket.getInputStream()));
        }
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int value;
        while ((value = in.read()) != -1 && value != '\n') {
            if (value != '\r') {
                bytes.write(value);
            }
        }
        return value == -1 && bytes.size() == 0 ? null : bytes.toString(StandardCharsets.UTF_8);
    }
}