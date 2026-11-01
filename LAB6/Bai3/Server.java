import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

class PrimeServiceImpl extends UnicastRemoteObject implements PrimeService {
    public PrimeServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean isPrime(int n) throws RemoteException {
        System.out.println("[Server] Đang kiểm tra số nguyên tố cho n = " + n);
        if (n <= 1) {
            return false;
        }
        if (n <= 3) {
            return true;
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        for (int i = 5; (long) i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}

public class Server {
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "PrimeService";

    public static void main(String[] args) {
        try {
            PrimeService service = new PrimeServiceImpl();

            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
                System.out.println("RMI Registry đã được tạo trên cổng " + PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry("localhost", PORT);
                System.out.println("Kết nối tới RMI Registry đã có trên cổng " + PORT);
            }

            registry.rebind(SERVICE_NAME, service);
            System.out.println("Bài 3 - Prime Server đã sẵn sàng phục vụ (" + SERVICE_NAME + ")...");
        } catch (Exception e) {
            System.err.println("Lỗi Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
