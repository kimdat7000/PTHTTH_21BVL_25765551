import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "FlightBookingService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            FlightBookingService service = (FlightBookingService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n========== HỆ THỐNG ĐẶT VÉ MÁY BAY TỪ XA (RMI) ==========");
                System.out.println("1. Xem danh sách chuyến bay và số ghế trống");
                System.out.println("2. Kiểm tra số ghế trống của một chuyến bay");
                System.out.println("3. Đặt vé máy bay");
                System.out.println("4. Hủy vé máy bay đã đặt");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng (0-4): ");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        Map<String, Integer> flights = service.getAllFlights();
                        System.out.println("\n--- DANH SÁCH CHUYẾN BAY HIỆN CÓ ---");
                        if (flights.isEmpty()) {
                            System.out.println("(Không có chuyến bay nào)");
                        } else {
                            System.out.printf("%-15s | %-15s\n", "Mã Chuyến Bay", "Số Ghế Còn Trống");
                            System.out.println("---------------------------------");
                            for (Map.Entry<String, Integer> entry : flights.entrySet()) {
                                System.out.printf("%-15s | %-15d\n", entry.getKey(), entry.getValue());
                            }
                        }
                        break;

                    case "2":
                        System.out.print("Nhập mã chuyến bay cần tra cứu (VD: VN123): ");
                        String codeCheck = scanner.nextLine().trim().toUpperCase();
                        int available = service.availableSeats(codeCheck);
                        if (available == -1) {
                            System.out.println(">>> Không tìm thấy chuyến bay có mã: " + codeCheck);
                        } else {
                            System.out.println(">>> Chuyến bay " + codeCheck + " hiện còn: " + available + " ghế trống.");
                        }
                        break;

                    case "3":
                        System.out.print("Nhập mã chuyến bay muốn đặt (VD: VN123): ");
                        String codeBook = scanner.nextLine().trim().toUpperCase();
                        System.out.print("Nhập số lượng vé cần đặt: ");
                        try {
                            int seatsBook = Integer.parseInt(scanner.nextLine().trim());
                            if (seatsBook <= 0) {
                                System.out.println(">>> Số lượng ghế phải lớn hơn 0!");
                                break;
                            }
                            boolean success = service.bookTicket(codeBook, seatsBook);
                            if (success) {
                                System.out.println(">>> ĐẶT VÉ THÀNH CÔNG cho chuyến bay " + codeBook + " (" + seatsBook + " ghế)!");
                                System.out.println(">>> Số ghế còn lại: " + service.availableSeats(codeBook));
                            } else {
                                System.out.println(">>> ĐẶT VÉ THẤT BẠI! Mã chuyến bay không đúng hoặc không đủ số ghế trống yêu cầu.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(">>> Số lượng vé phải là một số nguyên hợp lệ!");
                        }
                        break;

                    case "4":
                        System.out.print("Nhập mã chuyến bay muốn hủy vé: ");
                        String codeCancel = scanner.nextLine().trim().toUpperCase();
                        System.out.print("Nhập số lượng vé muốn hủy: ");
                        try {
                            int seatsCancel = Integer.parseInt(scanner.nextLine().trim());
                            if (seatsCancel <= 0) {
                                System.out.println(">>> Số lượng ghế hủy phải lớn hơn 0!");
                                break;
                            }
                            boolean cancelSuccess = service.cancelBooking(codeCancel, seatsCancel);
                            if (cancelSuccess) {
                                System.out.println(">>> HỦY VÉ THÀNH CÔNG cho chuyến bay " + codeCancel + " (" + seatsCancel + " ghế)!");
                                System.out.println(">>> Số ghế còn lại hiện tại: " + service.availableSeats(codeCancel));
                            } else {
                                System.out.println(">>> HỦY VÉ THẤT BẠI! Vui lòng kiểm tra lại mã chuyến bay hoặc số lượng hủy.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(">>> Số lượng vé phải là một số nguyên hợp lệ!");
                        }
                        break;

                    case "0":
                        System.out.println("Cảm ơn đã sử dụng hệ thống đặt vé máy bay!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn từ 0 đến 4.");
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
