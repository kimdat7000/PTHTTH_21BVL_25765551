import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatServiceImpl extends UnicastRemoteObject implements ChatService {
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
