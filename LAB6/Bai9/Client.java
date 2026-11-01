import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Client {
    public static final String HOST = "localhost";
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "AuctionService";

    // Giữ static reference để callback không bị JVM GC thu gom
    private static AuctionCallback callback;

    public static void main(String[] args) {
        try {
            // Đảm bảo RMI callback export dùng loopback localhost 127.0.0.1
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");

            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            AuctionService service = (AuctionService) registry.lookup(SERVICE_NAME);

            Scanner scanner = new Scanner(System.in);
            NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));

            System.out.println("==================================================");
            System.out.println("   HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN JAVA RMI (BÀI 9)   ");
            System.out.println("==================================================");
            System.out.println("Chọn vai trò truy cập:");
            System.out.println("1. Người bán (Seller)");
            System.out.println("2. Người đấu giá (Bidder)");
            System.out.println("0. Thoát");
            System.out.print("> Chọn (0-2): ");

            if (!scanner.hasNextLine()) return;
            String roleChoice = scanner.nextLine().trim();

            if (roleChoice.equals("1")) {
                // CHẾ ĐỘ NGƯỜI BÁN (SELLER)
                System.out.print("\nNhập tên Người bán (Seller Name): ");
                if (!scanner.hasNextLine()) return;
                String sellerName = scanner.nextLine().trim();
                if (sellerName.isEmpty()) sellerName = "Seller_Anonym";

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
            } else if (roleChoice.equals("2")) {
                // CHẾ ĐỘ NGƯỜI ĐẤU GIÁ (BIDDER)
                System.out.print("\nNhập tên Người đấu giá (Bidder Name): ");
                if (!scanner.hasNextLine()) return;
                String bidderName = scanner.nextLine().trim();
                if (bidderName.isEmpty()) bidderName = "Bidder_NoName";

                callback = new AuctionCallbackImpl(bidderName);
                service.registerCallback(callback);

                while (true) {
                    System.out.println("\n--- MENU NGƯỜI ĐẤU GIÁ [" + bidderName + "] ---");
                    System.out.println("1. Xem danh sách sản phẩm đang đấu giá");
                    System.out.println("2. Đặt giá thầu (Bid) cho một sản phẩm");
                    System.out.println("0. Thoát");
                    System.out.print("> Lựa chọn của bạn: ");

                    if (!scanner.hasNextLine()) break;
                    String ch = scanner.nextLine().trim();

                    if (ch.equals("1")) {
                        List<Product> prods = service.getProducts();
                        System.out.println("\n--- DANH SÁCH SẢN PHẨM ĐANG ĐẤU GIÁ (" + prods.size() + ") ---");
                        for (Product p : prods) System.out.println(p.getFormattedInfo());
                    } else if (ch.equals("2")) {
                        System.out.print("Nhập mã sản phẩm muốn đấu giá (VD: SP101): ");
                        if (!scanner.hasNextLine()) break;
                        String pId = scanner.nextLine().trim().toUpperCase();

                        Product target = service.getProduct(pId);
                        if (target == null) {
                            System.out.println(">>> Không tìm thấy mã sản phẩm: " + pId);
                            continue;
                        }

                        boolean isFirst = "(Chưa có)".equals(target.getHighestBidder());
                        String cond = isFirst ? ">=" : ">";

                        System.out.println("Sản phẩm: " + target.getName());
                        System.out.println("Giá hiện tại: " + currencyFmt.format(target.getCurrentHighestBid()) + " (Người trả cao nhất: " + target.getHighestBidder() + ")");
                        System.out.print("Nhập mức giá bạn muốn đặt (phải " + cond + " " + currencyFmt.format(target.getCurrentHighestBid()) + "): ");

                        if (!scanner.hasNextLine()) break;
                        try {
                            double bid = Double.parseDouble(scanner.nextLine().trim());
                            boolean ok = service.placeBid(pId, bid, bidderName);
                            if (ok) {
                                System.out.println(">>> ĐẶT GIÁ THẦU THÀNH CÔNG! Bạn hiện là người trả giá cao nhất.");
                            } else {
                                System.out.println(">>> ĐẶT GIÁ THẤT BẠI! Giá thầu không hợp lệ.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(">>> Số tiền không hợp lệ!");
                        }
                    } else if (ch.equals("0")) {
                        try { service.unregisterCallback(callback); } catch (Exception ignored) {}
                        System.out.println("Tạm biệt!");
                        System.exit(0);
                    }
                }
            } else {
                System.out.println("Thoát chương trình.");
            }
            if (callback != null) {
                try { service.unregisterCallback(callback); } catch (Exception ignored) {}
            }
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
