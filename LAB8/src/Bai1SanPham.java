import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Bai1SanPham {
    public static void main(String[] args) {
        // Đường dẫn CSDL: tự động nhận diện khi chạy từ LAB8 hoặc thư mục gốc
        String dbPath = new java.io.File("LAB8/CSDL").exists() ? "LAB8/CSDL/sanpham.db" : "CSDL/sanpham.db";
        String url = "jdbc:sqlite:" + dbPath;
        String username = "";
        String passwd = "";

        try {
            Connection conn = DriverManager.getConnection(url, username, passwd);
            if (conn != null) {
                System.out.println("Kết nối SQLite thành công!");
                Statement stmt = conn.createStatement();

                String sql_create = "CREATE TABLE IF NOT EXISTS SanPham ("
                        + "maSP INTEGER PRIMARY KEY, "
                        + "tenSP TEXT NOT NULL, "
                        + "gia REAL, "
                        + "soluong INTEGER)";
                stmt.executeUpdate(sql_create);

                String insert_1 = "INSERT OR IGNORE INTO SanPham (maSP, tenSP, gia, soluong) VALUES (1, 'Laptop Dell', 15000000, 10);";
                String insert_2 = "INSERT OR IGNORE INTO SanPham (maSP, tenSP, gia, soluong) VALUES (2, 'Chuột Logitech', 500000, 50);";
                String insert_3 = "INSERT OR IGNORE INTO SanPham (maSP, tenSP, gia, soluong) VALUES (3, 'Bàn phím cơ', 1200000, 20);";

                stmt.executeUpdate(insert_1);
                stmt.executeUpdate(insert_2);
                stmt.executeUpdate(insert_3);

                String update_sql = "UPDATE SanPham SET gia = 14500000, soluong = 8 WHERE maSP = 1;";
                stmt.executeUpdate(update_sql);

                String delete_sql = "DELETE FROM SanPham WHERE maSP = 3;";
                stmt.executeUpdate(delete_sql);

                String sql_select = "SELECT maSP, tenSP, gia, soluong FROM SanPham";
                ResultSet rs = stmt.executeQuery(sql_select);

                System.out.println("--- DANH SÁCH SẢN PHẨM ---");
                while (rs.next()) {
                    int maSP = rs.getInt("maSP");
                    String tenSP = rs.getString("tenSP");
                    double gia = rs.getDouble("gia");
                    String giaFormatted = String.format("%.0f", gia);
                    int soluong = rs.getInt("soluong");

                    System.out.println("Mã SP: " + maSP + " | Tên: " + tenSP + " | Giá: " + giaFormatted + " | SL: " + soluong);
                }

                rs.close();
                stmt.close();
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }
}