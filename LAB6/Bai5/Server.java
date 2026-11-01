import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

class ChatServiceImpl extends UnicastRemoteObject implements ChatService {
    private String latestResponse = "";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ChatServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public synchronized void sendMessage(String message) throws RemoteException {
        String timestamp = LocalTime.now().format(TIME_FORMATTER);
        System.out.println("\n[" + timestamp + "] [Client gửi]: " + message);

        // Server tự động phân tích và tạo phản hồi tương tác
        String replyContent;
        String lower = message.trim().toLowerCase();
        if (lower.contains("xin chào") || lower.contains("chào") || lower.contains("hello") || lower.contains("hi")) {
            replyContent = "Chào bạn! Tôi là RMI Chat Server. Tôi có thể giúp gì cho bạn?";
        } else if (lower.contains("mấy giờ") || lower.contains("thời gian") || lower.contains("time")) {
            replyContent = "Bây giờ là: " + timestamp;
        } else if (lower.contains("bạn khỏe không") || lower.contains("how are you")) {
            replyContent = "Tôi là tiến trình RMI Server, đang hoạt động rất tốt! Cảm ơn bạn.";
        } else if (lower.contains("tạm biệt") || lower.contains("bye") || lower.contains("exit")) {
            replyContent = "Tạm biệt bạn! Hẹn gặp lại lần sau.";
        } else {
            replyContent = "Server đã nhận được thông điệp: \"" + message + "\"";
        }

        latestResponse = "[Server phản hồi lúc " + timestamp + "]: " + replyContent;
        System.out.println("-> Đã phản hồi cho Client: " + latestResponse);
    }

    @Override
    public synchronized String getResponse() throws RemoteException {
        return latestResponse;
    }
}

public class Server {
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "ChatService";

    public static void main(String[] args) {
        try {
            ChatService service = new ChatServiceImpl();

            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
                System.out.println("RMI Registry đã được tạo trên cổng " + PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry("localhost", PORT);
                System.out.println("Kết nối tới RMI Registry đã có trên cổng " + PORT);
            }

            registry.rebind(SERVICE_NAME, service);
            System.out.println("Bài 5 - Chat Server (1-1) đã sẵn sàng phục vụ (" + SERVICE_NAME + ")...");
            System.out.println("Đang lắng nghe tin nhắn từ Client...");
        } catch (Exception e) {
            System.err.println("Lỗi Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
