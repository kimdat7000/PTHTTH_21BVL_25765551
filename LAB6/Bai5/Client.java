import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "ChatService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            ChatService service = (ChatService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            System.out.println("=== ỨNG DỤNG CHAT 1-1 CLIENT VỚI SERVER (RMI) ===");
            System.out.println("Nhập tin nhắn để gửi đến Server (Gõ 'exit' hoặc 'quit' để kết thúc):\n");

            while (true) {
                System.out.print("[Client nhập]: ");
                String message = scanner.nextLine().trim();

                if (message.equalsIgnoreCase("exit") || message.equalsIgnoreCase("quit")) {
                    service.sendMessage("Tạm biệt server!");
                    String reply = service.getResponse();
                    System.out.println(">>> " + reply);
                    break;
                }

                if (message.isEmpty()) {
                    continue;
                }

                // Gửi tin nhắn qua RMI
                service.sendMessage(message);

                // Nhận phản hồi từ Server
                String reply = service.getResponse();
                System.out.println(">>> " + reply);
                System.out.println();
            }

            System.out.println("Phiên chat đã kết thúc.");
            scanner.close();
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
