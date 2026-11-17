import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Bai2TransactionSanPham {
    public static void main(String[] args) {
        // Đường dẫn CSDL: tự động nhận diện khi chạy từ LAB8 hoặc thư mục gốc
        String dbPath = new java.io.File("LAB8/CSDL").exists() ? "LAB8/CSDL/sanpham.db" : "CSDL/sanpham.db";
        String url = "jdbc:sqlite:" + dbPath;
        String username = "";
        String passwd = "";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, username, passwd);
            
            conn.setAutoCommit(false);

            stmt = conn.createStatement();

            String sql_create = "CREATE TABLE IF NOT EXISTS SanPham ("
                    + "maSP INTEGER PRIMARY KEY, "
                    + "tenSP TEXT NOT NULL, "
                    + "gia REAL, "
                    + "soluong INTEGER)";
            stmt.executeUpdate(sql_create);

            String insert_1 = "INSERT OR IGNORE INTO SanPham (maSP, tenSP, gia, soluong) VALUES (1, 'Laptop Dell', 15000000, 10);";
            String insert_2 = "INSERT OR IGNORE INTO SanPham (maSP, tenSP, gia, soluong) VALUES (2, 'Chuột Logitech', 500000, 50);";
            stmt.executeUpdate(insert_1);
            stmt.executeUpdate(insert_2);

            String update_sql = "UPDATE SanPham SET gia = 14500000, soluong = 8 WHERE maSP = 1;";
            stmt.executeUpdate(update_sql);

            conn.commit();
            System.out.println("Transaction thành công, đã lưu thay đổi vào cơ sở dữ liệu!");

            String sql_select = "SELECT maSP, tenSP, gia, soluong FROM SanPham";
            rs = stmt.executeQuery(sql_select);

            System.out.println("--- DANH SÁCH SẢN PHẨM ---");
            while (rs.next()) {
                int maSP = rs.getInt("maSP");
                String tenSP = rs.getString("tenSP");
                double gia = rs.getDouble("gia");
                String giaFormatted = String.format("%.0f", gia);
                int soluong = rs.getInt("soluong");

                System.out.println("Mã SP: " + maSP + " | Tên: " + tenSP + " | Giá: " + giaFormatted + " | SL: " + soluong);
            }

        } catch (SQLException e) {
            System.out.println("Lỗi thực thi: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Đã thực hiện Rollback hủy bỏ các thay đổi!");
                } catch (SQLException ex) {
                    System.out.println("Lỗi rollback: " + ex.getMessage());
                }
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Lỗi đóng tài nguyên: " + e.getMessage());
            }
        }
    }
}