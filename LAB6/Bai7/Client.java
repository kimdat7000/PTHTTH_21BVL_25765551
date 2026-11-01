import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "GeometryService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            GeometryService service = (GeometryService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n========== TÍNH DIỆN TÍCH HÌNH HỌC (RMI) ==========");
                System.out.println("1. Tính diện tích hình chữ nhật");
                System.out.println("2. Tính diện tích hình tròn");
                System.out.println("3. Tính diện tích hình tam giác");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng (0-3): ");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        try {
                            System.out.print("Nhập chiều dài: ");
                            double width = Double.parseDouble(scanner.nextLine().trim());
                            System.out.print("Nhập chiều rộng: ");
                            double height = Double.parseDouble(scanner.nextLine().trim());
                            double rectArea = service.rectangleArea(width, height);
                            System.out.printf(">>> Diện tích hình chữ nhật (%.2f x %.2f) = %.4f\n", width, height, rectArea);
                        } catch (Exception e) {
                            System.out.println("Lỗi: " + e.getMessage());
                        }
                        break;

                    case "2":
                        try {
                            System.out.print("Nhập bán kính (r): ");
                            double radius = Double.parseDouble(scanner.nextLine().trim());
                            double circArea = service.circleArea(radius);
                            System.out.printf(">>> Diện tích hình tròn (bán kính %.2f) = %.4f\n", radius, circArea);
                        } catch (Exception e) {
                            System.out.println("Lỗi: " + e.getMessage());
                        }
                        break;

                    case "3":
                        try {
                            System.out.print("Nhập độ dài cạnh đáy (base): ");
                            double base = Double.parseDouble(scanner.nextLine().trim());
                            System.out.print("Nhập chiều cao tương ứng (height): ");
                            double triHeight = Double.parseDouble(scanner.nextLine().trim());
                            double triArea = service.triangleArea(base, triHeight);
                            System.out.printf(">>> Diện tích hình tam giác (đáy %.2f, cao %.2f) = %.4f\n", base, triHeight, triArea);
                        } catch (Exception e) {
                            System.out.println("Lỗi: " + e.getMessage());
                        }
                        break;

                    case "0":
                        System.out.println("Cảm ơn đã sử dụng chương trình!");
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
