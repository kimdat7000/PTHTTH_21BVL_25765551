package tcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SafeFileServer {
    private static final int DEFAULT_PORT = 5008;
    private static final int BUFFER_SIZE = 8192;
    private static final long MAX_FILE_SIZE = 1_073_741_824L;
    private static final Path UPLOAD_DIRECTORY = Path.of("upload");

    public static void main(String[] args) {
        int port;
        try {
            port = args.length == 0 ? DEFAULT_PORT : Integer.parseInt(args[0]);
            Files.createDirectories(UPLOAD_DIRECTORY);
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        } catch (IOException e) {
            System.err.println("Không tạo được thư mục upload: " + e.getMessage());
            return;
        }

        ExecutorService clients = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Safe file server listening on port " + port
                    + "; uploads: " + UPLOAD_DIRECTORY.toAbsolutePath());
            while (true) {
                Socket socket = server.accept();
                clients.execute(() -> receiveFile(socket));
            }
        } catch (IOException e) {
            System.err.println("Lỗi file server: " + e.getMessage());
        } finally {
            clients.shutdownNow();
        }
    }

    private static void receiveFile(Socket socket) {
        Path temporaryFile = null;
        try (Socket client = socket) {
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            String header = readLine(in);
            String[] fields = header == null ? new String[0] : header.split(" ", -1);
            if (fields.length != 4 || !fields[0].equals("FILE")) {
                writeLine(out, "ERR INVALID_HEADER");
                return;
            }

            String fileName = fields[1];
            long size;
            try {
                size = Long.parseLong(fields[2]);
            } catch (NumberFormatException e) {
                writeLine(out, "ERR INVALID_SIZE");
                return;
            }
            if (!isSafeFileName(fileName)) {
                writeLine(out, "ERR INVALID_FILENAME");
                return;
            }
            if (size < 0 || size > MAX_FILE_SIZE) {
                writeLine(out, "ERR INVALID_SIZE");
                return;
            }
            if (!fields[3].matches("[0-9a-fA-F]{64}")) {
                writeLine(out, "ERR INVALID_HASH");
                return;
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            temporaryFile = Files.createTempFile(UPLOAD_DIRECTORY, "incoming-", ".part");
            long remaining = size;
            try (OutputStream fileOut = Files.newOutputStream(temporaryFile)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (remaining > 0) {
                    int count = in.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                    if (count < 0) {
                        throw new IOException("Client disconnected before sending declared file size");
                    }
                    fileOut.write(buffer, 0, count);
                    digest.update(buffer, 0, count);
                    remaining -= count;
                }
            }

            String actualHash = HexFormat.of().formatHex(digest.digest());
            if (!MessageDigest.isEqual(actualHash.getBytes(StandardCharsets.US_ASCII),
                    fields[3].toLowerCase().getBytes(StandardCharsets.US_ASCII))) {
                Files.deleteIfExists(temporaryFile);
                temporaryFile = null;
                writeLine(out, "ERR HASH_MISMATCH");
                return;
            }

            Path uploadRoot = UPLOAD_DIRECTORY.toAbsolutePath().normalize();
            Path destination = uploadRoot.resolve(UUID.randomUUID() + "-" + fileName).normalize();
            if (!destination.getParent().equals(uploadRoot)) {
                writeLine(out, "ERR INVALID_FILENAME");
                return;
            }
            try {
                Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, destination);
            }
            temporaryFile = null;
            writeLine(out, "OK " + destination.getFileName());
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Lỗi nhận file: " + e.getMessage());
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException e) {
                    System.err.println("Không xóa được file tạm: " + e.getMessage());
                }
            }
        }
    }

    private static boolean isSafeFileName(String fileName) {
        return !fileName.isBlank() && fileName.length() <= 255
                && !fileName.equals(".") && !fileName.equals("..")
                && !fileName.contains("..") && !fileName.contains("/")
                && !fileName.contains("\\") && !fileName.contains(":");
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int value;
        while ((value = in.read()) != -1 && value != '\n') {
            if (bytes.size() >= 8192) {
                throw new IOException("Header too long");
            }
            if (value != '\r') {
                bytes.write(value);
            }
        }
        if (value == -1 && bytes.size() == 0) {
            return null;
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    private static void writeLine(OutputStream out, String message) throws IOException {
        out.write((message + "\n").getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}