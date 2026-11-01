import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "HelloService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            HelloService service = (HelloService) registry.lookup(SERVICE_NAME);

            System.out.println("Đang gọi phương thức từ xa sayHello()...");
            String message = service.sayHello();
            System.out.println("Kết quả nhận từ server: " + message);
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
