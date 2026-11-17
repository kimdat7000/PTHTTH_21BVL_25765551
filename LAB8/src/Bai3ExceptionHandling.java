import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Bai3ExceptionHandling {
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

            String sql_invalid = "INSERT INTO SanPham_KhongTonTai (maSP) VALUES (1);";
            stmt.executeUpdate(sql_invalid);

            conn.commit();

        } catch (SQLException e) {
            System.out.println("Đã bắt được ngoại lệ SQLException: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("Đã rollback thành công do xảy ra lỗi!");
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
                System.out.println("Đã đóng tất cả tài nguyên JDBC an toàn trong khối finally.");
            } catch (SQLException e) {
                System.out.println("Lỗi đóng tài nguyên: " + e.getMessage());
            }
        }
    }
}