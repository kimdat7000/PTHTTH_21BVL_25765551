import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

class HelloServiceImpl extends UnicastRemoteObject implements HelloService {
    public HelloServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String sayHello() throws RemoteException {
        System.out.println("[Server] Client vừa gọi phương thức sayHello()");
        return "Hello, World!";
    }
}

public class Server {
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "HelloService";

    public static void main(String[] args) {
        try {
            HelloService service = new HelloServiceImpl();

            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
                System.out.println("RMI Registry đã được tạo trên cổng " + PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry("localhost", PORT);
                System.out.println("Kết nối tới RMI Registry đã có trên cổng " + PORT);
            }

            registry.rebind(SERVICE_NAME, service);
            System.out.println("Bài 1 - Server đã sẵn sàng phục vụ (" + SERVICE_NAME + ")...");
        } catch (Exception e) {
            System.err.println("Lỗi Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
