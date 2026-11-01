import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.NumberFormat;
import java.util.Locale;

public class BankAccountServiceImpl extends UnicastRemoteObject implements BankAccountService {
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
