import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.NumberFormat;
import java.util.Locale;

class BankAccountServiceImpl extends UnicastRemoteObject implements BankAccountService {
    // Số dư ban đầu của tài khoản
    private double balance;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));

    public BankAccountServiceImpl(double initialBalance) throws RemoteException {
        super();
        this.balance = initialBalance;
    }

    @Override
    public synchronized double getBalance() throws RemoteException {
        System.out.println("[Server] Client kiểm tra số dư: " + currencyFormatter.format(balance));
        return balance;
    }

    @Override
    public synchronized void deposit(double amount) throws RemoteException {
        if (amount <= 0) {
            System.out.println("[Server] Gửi tiền không hợp lệ: số tiền phải > 0 (amount = " + amount + ")");
            return;
        }
        balance += amount;
        System.out.println("[Server] Đã nạp +" + currencyFormatter.format(amount) + " | Số dư mới: " + currencyFormatter.format(balance));
    }

    @Override
    public synchronized boolean withdraw(double amount) throws RemoteException {
        if (amount <= 0) {
            System.out.println("[Server] Rút tiền không hợp lệ: số tiền phải > 0");
            return false;
        }
        if (amount > balance) {
            System.out.println("[Server] Rút tiền thất bại! Số tiền yêu cầu: " + currencyFormatter.format(amount) + " vượt quá số dư hiện tại: " + currencyFormatter.format(balance));
            return false;
        }
        balance -= amount;
        System.out.println("[Server] Rút tiền thành công: -" + currencyFormatter.format(amount) + " | Số dư mới: " + currencyFormatter.format(balance));
        return true;
    }
}

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
