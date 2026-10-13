import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ChatServer - Server quản lý phòng chat nhóm TCP
 * Đề tài: Chat nhóm qua TCP với các phòng (Study, Game, TrảiNghiệm,...)
 */
public class ChatServer {
    public static final int DEFAULT_PORT = 5000;
    private final int port;

    // Danh sách lưu thứ tự các phòng
    private final List<String> roomOrder = new CopyOnWriteArrayList<>();

    // Map quản lý phòng: Tên phòng -> Danh sách các ClientHandler đang ở trong phòng
    private final Map<String, Set<ClientHandler>> roomClients = new ConcurrentHashMap<>();

    // Tập hợp toàn bộ client đang kết nối với server
    private final Set<ClientHandler> allClients = ConcurrentHashMap.newKeySet();

    public ChatServer(int port) {
        this.port = port;
        initDefaultRooms();
    }

    /**
     * Khởi tạo danh sách các phòng mặc định do Server cung cấp theo đề bài
     */
    private void initDefaultRooms() {
        addRoom("Study");
        addRoom("Game");
        addRoom("TrảiNghiệm");
    }

    /**
     * Thêm một phòng mới vào hệ thống (nếu chưa tồn tại)
     */
    public synchronized boolean addRoom(String roomName) {
        if (roomName == null || roomName.trim().isEmpty()) {
            return false;
        }
        String name = roomName.trim();
        for (String existing : roomOrder) {
            if (existing.equalsIgnoreCase(name)) {
                return false;
            }
        }
        roomOrder.add(name);
        roomClients.put(name, ConcurrentHashMap.newKeySet());
        return true;
    }

    /**
     * Lấy danh sách tên tất cả các phòng hiện có
     */
    public synchronized List<String> getRoomList() {
        return new ArrayList<>(roomOrder);
    }

    /**
     * Tìm tên phòng chuẩn (theo chữ hoa/thường trong danh sách)
     */
    public synchronized String findRoomName(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        for (String r : roomOrder) {
            if (r.equalsIgnoreCase(trimmed)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Lấy số lượng thành viên hiện tại trong phòng
     */
    public synchronized int getRoomMemberCount(String roomName) {
        Set<ClientHandler> members = roomClients.get(roomName);
        return members != null ? members.size() : 0;
    }

    /**
     * Lấy danh sách tên các thành viên trong phòng
     */
    public synchronized List<String> getRoomMemberNames(String roomName) {
        Set<ClientHandler> members = roomClients.get(roomName);
        List<String> names = new ArrayList<>();
        if (members != null) {
            for (ClientHandler ch : members) {
                names.add(ch.getClientName());
            }
        }
        return names;
    }

    /**
     * Cho client tham gia phòng
     */
    public synchronized boolean joinRoom(String roomName, ClientHandler client) {
        if (!roomClients.containsKey(roomName)) {
            addRoom(roomName);
        }
        Set<ClientHandler> members = roomClients.get(roomName);
        return members != null && members.add(client);
    }

    /**
     * Rút client ra khỏi phòng
     */
    public synchronized void leaveRoom(String roomName, ClientHandler client) {
        if (roomName != null && roomClients.containsKey(roomName)) {
            roomClients.get(roomName).remove(client);
        }
    }

    /**
     * Phát tin nhắn tới các client KHÁC trong cùng phòng
     * Đảm bảo yêu cầu: "và chỉ nhận tin nhắn từ những client khác trong phòng đó"
     */
    public void broadcastToRoom(String roomName, String message, ClientHandler sender) {
        if (roomName == null) return;
        Set<ClientHandler> members = roomClients.get(roomName);
        if (members == null) return;

        for (ClientHandler client : members) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public void registerClient(ClientHandler client) {
        allClients.add(client);
    }

    public void unregisterClient(ClientHandler client) {
        allClients.remove(client);
    }

    /**
     * Khởi động Server
     */
    public void start() {
        System.out.println("==================================================");
        System.out.println("       TCP CHAT SERVER - QUẢN LÝ NHÓM CHAT        ");
        System.out.println("==================================================");
        System.out.println("Server đang lắng nghe trên cổng: " + port);
        System.out.println("Các phòng mặc định: Study, Game, TrảiNghiệm");
        System.out.println("Đang đợi các client kết nối...");
        System.out.println("==================================================");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                registerClient(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Thiết lập mã hóa UTF-8 cho console
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Cổng không hợp lệ, sử dụng cổng mặc định: " + DEFAULT_PORT);
            }
        }
        new ChatServer(port).start();
    }
}
