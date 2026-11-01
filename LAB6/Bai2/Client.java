import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "CalculatorService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            CalculatorService service = (CalculatorService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            System.out.println("=== CHƯƠNG TRÌNH TÍNH TỔNG HAI SỐ (RMI) ===");

            while (true) {
                try {
                    System.out.print("\nNhập số nguyên thứ nhất (a) [hoặc gõ 'exit' để thoát]: ");
                    String inputA = scanner.next();
                    if (inputA.equalsIgnoreCase("exit") || inputA.equalsIgnoreCase("q")) {
                        break;
                    }
                    int a = Integer.parseInt(inputA);

                    System.out.print("Nhập số nguyên thứ hai (b): ");
                    int b = scanner.nextInt();

                    int result = service.add(a, b);
                    System.out.println(">>> Kết quả từ Server: " + a + " + " + b + " = " + result);
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
