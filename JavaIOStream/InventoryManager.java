
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    private static final Path CSV_PATH = Path.of("data", "inventory.csv");
    private static final Path REPORT_PATH = Path.of("data", "inventory-report.txt");

    public static void main(String[] args) {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        List<Product> inputList = new ArrayList<>();

        System.out.println("=== NHẬP DANH SÁCH SẢN PHẨM TỪ BÀN PHÍM ===");
        while (true) {
            try {
                System.out.print("Nhập mã SP (hoặc 'q' để kết thúc): ");
                String code = console.readLine();
                if (code == null || code.trim().equalsIgnoreCase("q")) {
                    break;
                }

                System.out.print("Nhập tên SP: ");
                String name = console.readLine();

                System.out.print("Nhập đơn giá: ");
                double price = Double.parseDouble(console.readLine().trim());

                System.out.print("Nhập số lượng: ");
                int quantity = Integer.parseInt(console.readLine().trim());

                Product p = new Product(code.trim(), name != null ? name.trim() : "", price, quantity);
                inputList.add(p);
                System.out.println("=> Thêm thành công sản phẩm: " + p.getName());
            } catch (NumberFormatException e) {
                System.err.println("Lỗi: Giá và số lượng phải là số!");
            } catch (IllegalArgumentException e) {
                System.err.println("Lỗi dữ liệu: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Lỗi nhập dữ liệu: " + e.getMessage());
            }
        }

        // 1. Lưu danh sách vào CSV
        saveToCsv(inputList, CSV_PATH);

        // 2. Đọc lại danh sách từ CSV
        List<Product> loadedProducts = loadFromCsv(CSV_PATH);

        // 3. Hiển thị & tìm sản phẩm có giá trị tồn kho cao nhất
        System.out.println("\n=== DANH SÁCH SẢN PHẨM ĐÃ LƯU ===");
        double totalValue = 0;
        Product maxValProduct = null;

        for (Product p : loadedProducts) {
            System.out.println(p);
            double val = p.inventoryValue();
            totalValue += val;
            if (maxValProduct == null || val > maxValProduct.inventoryValue()) {
                maxValProduct = val > 0 ? p : maxValProduct;
            }
        }
        System.out.printf("Tổng giá trị tồn kho: %,.0f VND%n", totalValue);

        // 4. Ghi báo cáo tổng hợp
        writeReport(loadedProducts, totalValue, maxValProduct, REPORT_PATH);
    }

    private static void saveToCsv(List<Product> products, Path path) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.write("ma,ten,donGia,soLuong");
                writer.newLine();
                for (Product p : products) {
                    writer.write("%s,%s,%.0f,%d".formatted(
                            p.getCode(), p.getName(), p.getUnitPrice(), p.getQuantity()));
                    writer.newLine();
                }
                System.out.println("Đã ghi tệp CSV thành công: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi tệp " + path + ": " + e.getMessage());
        }
    }

    private static List<Product> loadFromCsv(Path path) {
        List<Product> list = new ArrayList<>();
        if (!Files.exists(path)) {
            System.err.println("Tệp không tồn tại: " + path.toAbsolutePath());
            return list;
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            reader.readLine(); // Bỏ qua tiêu đề
            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    System.err.println("Bỏ qua dòng " + lineNum + " (tệp " + path.getFileName() + "): thiếu hoặc thừa cột.");
                    continue;
                }
                try {
                    list.add(new Product(
                            parts[0].trim(),
                            parts[1].trim(),
                            Double.parseDouble(parts[2].trim()),
                            Integer.parseInt(parts[3].trim())));
                } catch (Exception e) {
                    System.err.println("Dòng " + lineNum + " (tệp " + path.getFileName() + ") lỗi dữ liệu: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc tệp " + path + ": " + e.getMessage());
        }
        return list;
    }

    private static void writeReport(List<Product> list, double total, Product maxProduct, Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("=== BÁO CÁO TỒN KHO ===");
            writer.newLine();
            writer.write("Tổng số sản phẩm: " + list.size());
            writer.newLine();
            writer.write("Tổng giá trị tồn kho: %,.0f VND".formatted(total));
            writer.newLine();
            if (maxProduct != null) {
                writer.write("Sản phẩm giá trị tồn kho cao nhất: %s (%s) - %,.0f VND".formatted(
                        maxProduct.getName(), maxProduct.getCode(), maxProduct.inventoryValue()));
            } else {
                writer.write("Không có sản phẩm nào hợp lệ.");
            }
            writer.newLine();
            System.out.println("Đã ghi báo cáo: " + path.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Không thể ghi báo cáo vào tệp " + path + ": " + e.getMessage());
        }
    }
}
