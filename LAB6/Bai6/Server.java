import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "BankAccountService";
    public static final double INITIAL_BALANCE = 5000000.0; // 5.000.000 VNĐ

    public static void main(String[] args) {
        try {
            BankAccountService service = new BankAccountServiceImpl(INITIAL_BALANCE);

            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
                System.out.println("RMI Registry đã được tạo trên cổng " + PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry("localhost", PORT);
                System.out.println("Kết nối tới RMI Registry đã có trên cổng " + PORT);
            }

            registry.rebind(SERVICE_NAME, service);
            System.out.println("Bài 6 - Bank Account Server đã sẵn sàng phục vụ (" + SERVICE_NAME + ")...");
            System.out.printf("Tài khoản khởi tạo với số dư ban đầu: %,.0f VNĐ\n", INITIAL_BALANCE);
        } catch (Exception e) {
            System.err.println("Lỗi Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
