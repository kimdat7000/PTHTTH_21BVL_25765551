import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ChatClient - Ứng dụng Client chat nhóm qua TCP
 * - Nhập Tên và kết nối tới server
 * - Chọn phòng trong danh sách do server cung cấp hoặc tự đề xuất tên phòng
 * - Nhận tin nhắn từ các client khác trong cùng phòng
 * - Lệnh:
 *   /quit: rời phòng và quay lại danh sách các phòng
 *   /exit: ngắt kết nối tới server và thoát ứng dụng
 */
public class ChatClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        // Thiết lập UTF-8 cho console
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("==================================================");
        System.out.println("            CHƯƠNG TRÌNH TCP CHAT NHÓM            ");
        System.out.println("==================================================");

        // 1. Nhập cấu hình kết nối tới server
        System.out.print("Nhập địa chỉ Server (nhấn Enter để dùng '" + DEFAULT_HOST + "'): ");
        String hostInput = scanner.nextLine().trim();
        String host = hostInput.isEmpty() ? DEFAULT_HOST : hostInput;

        int port = DEFAULT_PORT;
        System.out.print("Nhập cổng Server (nhấn Enter để dùng " + DEFAULT_PORT + "): ");
        String portInput = scanner.nextLine().trim();
        if (!portInput.isEmpty()) {
            try {
                port = Integer.parseInt(portInput);
            } catch (NumberFormatException e) {
                System.out.println("Cổng không hợp lệ, chuyển về cổng mặc định " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        // 2. Nhập Tên của Client (theo yêu cầu đề bài: Mỗi client nhập Tên và kết nối tới server)
        System.out.print("Nhập Tên của bạn: ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty() || name.startsWith("/")) {
            if (name.startsWith("/")) {
                System.out.print("Tên không được bắt đầu bằng ký tự '/'. Vui lòng nhập lại: ");
            } else {
                System.out.print("Tên không được để trống. Vui lòng nhập lại Tên: ");
            }
            name = scanner.nextLine().trim();
        }

        // 3. Kết nối tới TCP Server
        System.out.println("\nĐang kết nối tới server " + host + ":" + port + "...");
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("-> Kết nối tới server thành công!");

            // Gửi tên client lên server
            out.println(name);

            AtomicBoolean running = new AtomicBoolean(true);

            // Luồng phụ: Lắng nghe tin nhắn từ Server và in ra màn hình
            Thread receiverThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while (running.get() && (serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    if (running.get()) {
                        System.out.println("\n[!] Đã mất kết nối tới server.");
                    }
                } finally {
                    running.set(false);
                }
            });
            receiverThread.setDaemon(true);
            receiverThread.start();

            // Luồng chính: Đọc dữ liệu từ bàn phím người dùng và gửi tới server
            while (running.get()) {
                if (!scanner.hasNextLine()) {
                    break;
                }
                String userInput = scanner.nextLine();
                if (!running.get()) {
                    break;
                }

                out.println(userInput);

                // Nếu người dùng nhập /exit: Dừng chương trình
                if (userInput.trim().equalsIgnoreCase("/exit")) {
                    running.set(false);
                    try {
                        // Nghỉ một chút để luồng nhận kịp in tin nhắn chào tạm biệt từ server
                        Thread.sleep(300);
                    } catch (InterruptedException ignored) {}
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Không thể kết nối tới Server: " + e.getMessage());
        }

        System.out.println("\nChương trình Chat đã kết thúc. Tạm biệt!");
    }
}
