import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "PrimeService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            PrimeService service = (PrimeService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            System.out.println("=== CHƯƠNG TRÌNH KIỂM TRA SỐ NGUYÊN TỐ (RMI) ===");

            while (true) {
                try {
                    System.out.print("\nNhập một số nguyên n cần kiểm tra [hoặc gõ 'exit' để thoát]: ");
                    String input = scanner.next();
                    if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("q")) {
                        break;
                    }

                    int n = Integer.parseInt(input);
                    boolean isPrime = service.isPrime(n);

                    if (isPrime) {
                        System.out.println(">>> Kết quả từ Server: Số " + n + " LÀ số nguyên tố.");
                    } else {
                        System.out.println(">>> Kết quả từ Server: Số " + n + " KHÔNG PHẢI là số nguyên tố.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Giá trị không hợp lệ! Vui lòng nhập số nguyên.");
                }
            }

            System.out.println("Đã đóng kết nối client.");
            scanner.close();
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
