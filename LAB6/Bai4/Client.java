import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "ContactService";

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            ContactService service = (ContactService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n========== QUẢN LÝ DANH BẠ TỪ XA (RMI) ==========");
                System.out.println("1. Thêm liên hệ mới");
                System.out.println("2. Tìm kiếm liên hệ theo tên");
                System.out.println("3. Cập nhật số điện thoại");
                System.out.println("4. Xóa liên hệ");
                System.out.println("5. Hiển thị toàn bộ danh bạ");
                System.out.println("0. Thoát chương trình");
                System.out.print("Chọn chức năng (0-5): ");

                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        System.out.print("Nhập họ tên: ");
                        String nameAdd = scanner.nextLine().trim();
                        System.out.print("Nhập số điện thoại: ");
                        String phoneAdd = scanner.nextLine().trim();
                        if (nameAdd.isEmpty() || phoneAdd.isEmpty()) {
                            System.out.println("Tên và số điện thoại không được để trống!");
                        } else {
                            service.addContact(nameAdd, phoneAdd);
                            System.out.println(">>> Đã thêm liên hệ thành công!");
                        }
                        break;

                    case "2":
                        System.out.print("Nhập tên cần tìm kiếm: ");
                        String nameFind = scanner.nextLine().trim();
                        String phoneFound = service.findContact(nameFind);
                        if (phoneFound != null) {
                            System.out.println(">>> Tìm thấy: " + nameFind + " - SĐT: " + phoneFound);
                        } else {
                            System.out.println(">>> Không tìm thấy liên hệ nào có tên: " + nameFind);
                        }
                        break;

                    case "3":
                        System.out.print("Nhập tên liên hệ cần sửa: ");
                        String nameUpdate = scanner.nextLine().trim();
                        System.out.print("Nhập số điện thoại mới: ");
                        String newPhone = scanner.nextLine().trim();
                        boolean updated = service.updateContact(nameUpdate, newPhone);
                        if (updated) {
                            System.out.println(">>> Cập nhật số điện thoại thành công!");
                        } else {
                            System.out.println(">>> Không tìm thấy tên '" + nameUpdate + "' để cập nhật!");
                        }
                        break;

                    case "4":
                        System.out.print("Nhập tên liên hệ cần xóa: ");
                        String nameDel = scanner.nextLine().trim();
                        boolean deleted = service.deleteContact(nameDel);
                        if (deleted) {
                            System.out.println(">>> Xóa liên hệ thành công!");
                        } else {
                            System.out.println(">>> Không tìm thấy liên hệ '" + nameDel + "' để xóa!");
                        }
                        break;

                    case "5":
                        Map<String, String> contacts = service.getAllContacts();
                        System.out.println("--- DANH SÁCH LIÊN HỆ HIỆN CÓ (" + contacts.size() + ") ---");
                        if (contacts.isEmpty()) {
                            System.out.println("(Danh bạ trống)");
                        } else {
                            int idx = 1;
                            for (Map.Entry<String, String> entry : contacts.entrySet()) {
                                System.out.printf("%2d. %-25s : %s\n", idx++, entry.getKey(), entry.getValue());
                            }
                        }
                        break;

                    case "0":
                        System.out.println("Cảm ơn đã sử dụng hệ thống!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Lựa chọn không hợp lệ. Vui lòng chọn từ 0 đến 5.");
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
