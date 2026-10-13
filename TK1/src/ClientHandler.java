import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * ClientHandler - Xử lý phiên làm việc của từng client trên một luồng riêng
 * Quản lý trạng thái:
 * 1. Chờ chọn phòng (Lobby / Menu)
 * 2. Đang trò chuyện trong phòng (Chat Room)
 * 3. Xử lý các lệnh: /quit, /exit, /who
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;
    private String currentRoom;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public String getClientName() {
        return clientName;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Gửi tin nhắn đồng bộ tới client này
     */
    public synchronized void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            // 1. Nhận Tên client do client gửi lên ngay sau khi kết nối
            clientName = in.readLine();
            if (clientName == null || clientName.trim().isEmpty()) {
                clientName = "User_" + socket.getPort();
            } else {
                clientName = clientName.trim();
            }

            System.out.println("[KẾT NỐI] Client kết nối: " + clientName + " (" + socket.getRemoteSocketAddress() + ")");
            sendMessage("Chào mừng [" + clientName + "] đã kết nối thành công tới TCP Chat Server!");

            // 2. Vòng lặp chính: Luân chuyển giữa Chọn phòng và Trò chuyện trong phòng
            boolean sessionActive = true;
            while (sessionActive) {
                // Hiển thị danh sách phòng và chờ client chọn hoặc đề xuất phòng
                String selectedRoom = handleRoomSelection();
                if (selectedRoom == null) {
                    // Client đã chọn /exit hoặc đóng kết nối
                    sessionActive = false;
                    break;
                }

                // Client vào phòng trò chuyện
                boolean shouldExit = handleChatting(selectedRoom);
                if (shouldExit) {
                    // Client gõ /exit khi đang trong phòng
                    sessionActive = false;
                    break;
                }
                // Nếu client gõ /quit, vòng lặp tiếp tục và quay lại bước handleRoomSelection()
            }

        } catch (IOException e) {
            System.out.println("[THÔNG BÁO] Client " + (clientName != null ? clientName : "") + " ngắt kết nối: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Gửi danh sách phòng và tiếp nhận lựa chọn phòng từ client
     * Trả về tên phòng đã chọn, hoặc null nếu client chọn /exit
     */
    private String handleRoomSelection() throws IOException {
        while (true) {
            sendRoomListMenu();

            String input = in.readLine();
            if (input == null) {
                return null; // Mất kết nối
            }
            String trimmed = input.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            // Xử lý lệnh /exit tại màn hình chọn phòng
            if (trimmed.equalsIgnoreCase("/exit")) {
                sendMessage("*** Bạn đã ngắt kết nối tới server. Tạm biệt " + clientName + "! ***");
                return null;
            }

            // Xử lý lệnh /quit khi chưa vào phòng
            if (trimmed.equalsIgnoreCase("/quit")) {
                sendMessage("(!) Bạn hiện chưa ở trong phòng nào. Vui lòng chọn một phòng từ danh sách bên dưới.");
                continue;
            }

            List<String> rooms = server.getRoomList();

            // 1. Kiểm tra nếu nhập theo số thứ tự (1, 2, 3...)
            try {
                int roomIdx = Integer.parseInt(trimmed);
                if (roomIdx >= 1 && roomIdx <= rooms.size()) {
                    return rooms.get(roomIdx - 1);
                } else {
                    sendMessage("(!) Số phòng không hợp lệ. Vui lòng nhập số từ 1 đến " + rooms.size() + " hoặc nhập tên phòng.");
                    continue;
                }
            } catch (NumberFormatException ignored) {
                // Không phải số, tiếp tục kiểm tra theo tên
            }

            // 2. Kiểm tra nếu nhập tên phòng có sẵn
            String existingRoom = server.findRoomName(trimmed);
            if (existingRoom != null) {
                return existingRoom;
            }

            // 3. Sinh viên tự đề xuất tên phòng mới
            if (trimmed.startsWith("/")) {
                sendMessage("(!) Tên phòng không được bắt đầu bằng ký tự '/'. Vui lòng nhập lại.");
                continue;
            }

            boolean created = server.addRoom(trimmed);
            if (created) {
                sendMessage("*** Bạn đã đề xuất và tạo thành công phòng mới: [" + trimmed + "] ***");
                System.out.println("[TẠO PHÒNG MỚI] " + clientName + " đã tạo phòng: [" + trimmed + "]");
            }
            return trimmed;
        }
    }

    /**
     * Gửi menu danh sách các phòng
     */
    private void sendRoomListMenu() {
        List<String> rooms = server.getRoomList();
        sendMessage("\n============================================================");
        sendMessage("                   DANH SÁCH PHÒNG CHAT                    ");
        sendMessage("============================================================");
        for (int i = 0; i < rooms.size(); i++) {
            String rName = rooms.get(i);
            int count = server.getRoomMemberCount(rName);
            sendMessage(String.format(" %2d. %-22s (%d thành viên)", (i + 1), rName, count));
        }
        sendMessage("------------------------------------------------------------");
        sendMessage(" * Nhập số thứ tự (1, 2, 3...) hoặc tên phòng để tham gia.");
        sendMessage(" * Nhập tên phòng mới để tự đề xuất và tạo phòng.");
        sendMessage(" * Nhập /exit để ngắt kết nối tới server.");
        sendMessage("============================================================");
        sendMessage(">> Chọn phòng: ");
    }

    /**
     * Xử lý khi client đang ở trong phòng trò chuyện
     * Trả về true nếu client muốn thoát hoàn toàn (/exit), false nếu client muốn rời phòng về menu (/quit)
     */
    private boolean handleChatting(String roomName) throws IOException {
        this.currentRoom = roomName;
        server.joinRoom(roomName, this);

        System.out.println("[VÀO PHÒNG] " + clientName + " đã tham gia phòng [" + roomName + "]");

        sendMessage("\n============================================================");
        sendMessage("*** Bạn đã tham gia phòng [" + roomName + "] thành công! ***");
        List<String> members = server.getRoomMemberNames(roomName);
        sendMessage("*** Thành viên hiện tại trong phòng (" + members.size() + "): " + String.join(", ", members) + " ***");
        sendMessage("*** Gõ tin nhắn và nhấn Enter để trò chuyện với các thành viên. ***");
        sendMessage("*** Lệnh: /quit (rời phòng quay lại danh sách) | /exit (thoát kết nối) ***");
        sendMessage("============================================================\n");

        // Thông báo cho các client khác trong phòng
        server.broadcastToRoom(roomName, "*** [" + clientName + "] đã tham gia phòng! ***", this);

        // Vòng lặp nhận và xử lý tin nhắn trong phòng
        while (true) {
            String line = in.readLine();
            if (line == null) {
                // Client mất kết nối đột ngột
                leaveCurrentRoom("*** [" + clientName + "] đã rời phòng (mất kết nối). ***");
                return true;
            }

            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            // Lệnh /exit: Ngắt kết nối tới server
            if (trimmed.equalsIgnoreCase("/exit")) {
                leaveCurrentRoom("*** [" + clientName + "] đã rời phòng. ***");
                sendMessage("*** Bạn đã ngắt kết nối tới server. Tạm biệt! ***");
                return true;
            }

            // Lệnh /quit: Rời phòng và quay lại danh sách các phòng
            if (trimmed.equalsIgnoreCase("/quit")) {
                leaveCurrentRoom("*** [" + clientName + "] đã rời phòng. ***");
                sendMessage("\n*** Bạn đã rời phòng [" + roomName + "]. Đang quay lại danh sách phòng... ***");
                return false; // Thoát vòng lặp chat để quay lại màn hình chọn phòng
            }

            // Lệnh bổ trợ /who hoặc /members: Xem ai đang trong phòng
            if (trimmed.equalsIgnoreCase("/who") || trimmed.equalsIgnoreCase("/members")) {
                List<String> currentMembers = server.getRoomMemberNames(roomName);
                sendMessage("*** Thành viên trong phòng [" + roomName + "]: " + String.join(", ", currentMembers) + " ***");
                continue;
            }

            // Tin nhắn thông thường: Chỉ phát tới các client KHÁC trong cùng phòng
            server.broadcastToRoom(roomName, "[" + clientName + "]: " + line, this);
            System.out.println("[" + roomName + "] " + clientName + ": " + line);
        }
    }

    /**
     * Rời khỏi phòng hiện tại và thông báo cho thành viên trong phòng
     */
    private void leaveCurrentRoom(String broadcastNotice) {
        if (currentRoom != null) {
            if (broadcastNotice != null) {
                server.broadcastToRoom(currentRoom, broadcastNotice, this);
            }
            server.leaveRoom(currentRoom, this);
            System.out.println("[RỜI PHÒNG] " + clientName + " đã rời phòng [" + currentRoom + "]");
            currentRoom = null;
        }
    }

    /**
     * Dọn dẹp tài nguyên khi client ngắt kết nối
     */
    private void cleanup() {
        leaveCurrentRoom("*** [" + clientName + "] đã rời phòng. ***");
        server.unregisterClient(this);
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {}
        System.out.println("[NGẮT KẾT NỐI] Client " + (clientName != null ? clientName : "") + " đã ngắt kết nối hoàn toàn.");
    }
}
