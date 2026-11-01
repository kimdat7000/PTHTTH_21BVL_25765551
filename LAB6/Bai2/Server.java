import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

class CalculatorServiceImpl extends UnicastRemoteObject implements CalculatorService {
    public CalculatorServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public int add(int a, int b) throws RemoteException {
        int sum = a + b;
        System.out.println("[Server] Nhận yêu cầu tính tổng: " + a + " + " + b + " = " + sum);
        return sum;
    }
}

public class Server {
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "CalculatorService";

    public static void main(String[] args) {
        try {
            CalculatorService service = new CalculatorServiceImpl();

            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
                System.out.println("RMI Registry đã được tạo trên cổng " + PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry("localhost", PORT);
                System.out.println("Kết nối tới RMI Registry đã có trên cổng " + PORT);
            }

            registry.rebind(SERVICE_NAME, service);
            System.out.println("Bài 2 - Calculator Server đã sẵn sàng phục vụ (" + SERVICE_NAME + ")...");
        } catch (Exception e) {
            System.err.println("Lỗi Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
