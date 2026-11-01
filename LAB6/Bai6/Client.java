import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "BankAccountService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            BankAccountService service = (BankAccountService) registry.lookup(SERVICE_NAME);

            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n========== HỆ THỐNG NGÂN HÀNG TỪ XA (RMI) ==========");
                System.out.println("1. Xem số dư tài khoản");
                System.out.println("2. Nạp tiền vào tài khoản (Deposit)");
                System.out.println("3. Rút tiền từ tài khoản (Withdraw)");
                System.out.println("0. Đăng xuất / Thoát");
                System.out.print("Chọn thao tác (0-3): ");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        double balance = service.getBalance();
                        System.out.println(">>> Số dư hiện tại của bạn: " + formatter.format(balance));
                        break;

                    case "2":
                        System.out.print("Nhập số tiền muốn gửi (VNĐ): ");
                        try {
                            double depositAmount = Double.parseDouble(scanner.nextLine().trim());
                            if (depositAmount <= 0) {
                                System.out.println(">>> Số tiền nạp phải lớn hơn 0!");
                            } else {
                                service.deposit(depositAmount);
                                System.out.println(">>> Nạp tiền thành công: +" + formatter.format(depositAmount));
                                System.out.println(">>> Số dư mới: " + formatter.format(service.getBalance()));
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(">>> Vui lòng nhập số tiền hợp lệ!");
                        }
                        break;

                    case "3":
                        System.out.print("Nhập số tiền muốn rút (VNĐ): ");
                        try {
                            double withdrawAmount = Double.parseDouble(scanner.nextLine().trim());
                            if (withdrawAmount <= 0) {
                                System.out.println(">>> Số tiền rút phải lớn hơn 0!");
                            } else {
                                boolean success = service.withdraw(withdrawAmount);
                                if (success) {
                                    System.out.println(">>> Rút tiền thành công: -" + formatter.format(withdrawAmount));
                                    System.out.println(">>> Số dư còn lại: " + formatter.format(service.getBalance()));
                                } else {
                                    System.out.println(">>> Giao dịch thất bại! Số dư không đủ hoặc số tiền không hợp lệ.");
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(">>> Vui lòng nhập số tiền hợp lệ!");
                        }
                        break;

                    case "0":
                        System.out.println("Cảm ơn quý khách đã sử dụng dịch vụ ngân hàng!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn 0, 1, 2 hoặc 3.");
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
