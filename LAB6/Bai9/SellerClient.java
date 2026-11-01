import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class SellerClient {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "AuctionService";

    private static AuctionCallback callback;

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");

            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            AuctionService service = (AuctionService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            System.out.println("==================================================");
            System.out.println("   CHỦ SỞ HỮU / NGƯỜI BÁN (SELLER) - BÀI 9       ");
            System.out.println("==================================================");
            System.out.print("Nhập tên Người bán (Seller Name): ");
            if (!scanner.hasNextLine()) return;
            String sellerName = scanner.nextLine().trim();
            if (sellerName.isEmpty()) sellerName = "Seller_Nguyen";

            callback = new AuctionCallbackImpl(sellerName);
            service.registerCallback(callback);

            while (true) {
                System.out.println("\n--- MENU NGƯỜI BÁN [" + sellerName + "] ---");
                System.out.println("1. Thêm sản phẩm mới vào sàn đấu giá");
                System.out.println("2. Xem danh sách tất cả sản phẩm");
                System.out.println("3. Xem sản phẩm của chính tôi");
                System.out.println("0. Thoát");
                System.out.print("> Lựa chọn của bạn: ");

                if (!scanner.hasNextLine()) break;
                String ch = scanner.nextLine().trim();

                if (ch.equals("1")) {
                    System.out.print("Nhập tên sản phẩm: ");
                    if (!scanner.hasNextLine()) break;
                    String pName = scanner.nextLine().trim();

                    System.out.print("Nhập giá khởi điểm (VNĐ): ");
                    if (!scanner.hasNextLine()) break;
                    try {
                        double pPrice = Double.parseDouble(scanner.nextLine().trim());
                        if (pPrice <= 0) {
                            System.out.println("Giá khởi điểm phải lớn hơn 0!");
                            continue;
                        }
                        Product newProd = service.addProduct(pName, pPrice, sellerName);
                        System.out.println(">>> ĐĂNG BÁN THÀNH CÔNG: " + newProd.getName() + " (Mã: " + newProd.getId() + ")");
                    } catch (NumberFormatException e) {
                        System.out.println("Giá không hợp lệ!");
                    }
                } else if (ch.equals("2")) {
                    List<Product> list = service.getProducts();
                    System.out.println("\n--- TẤT CẢ SẢN PHẨM TRÊN SÀN (" + list.size() + ") ---");
                    for (Product p : list) System.out.println(p.getFormattedInfo());
                } else if (ch.equals("3")) {
                    List<Product> list = service.getProducts();
                    System.out.println("\n--- SẢN PHẨM CỦA BẠN [" + sellerName + "] ---");
                    int count = 0;
                    for (Product p : list) {
                        if (p.getSeller().equalsIgnoreCase(sellerName)) {
                            System.out.println(p.getFormattedInfo());
                            count++;
                        }
                    }
                    if (count == 0) System.out.println("(Bạn chưa đăng bán sản phẩm nào)");
                } else if (ch.equals("0")) {
                    try { service.unregisterCallback(callback); } catch (Exception ignored) {}
                    System.out.println("Tạm biệt!");
                    System.exit(0);
                }
            }
            if (callback != null) {
                try { service.unregisterCallback(callback); } catch (Exception ignored) {}
            }
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Lỗi SellerClient: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
