import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Bai4PreparedStatement {
    public static void main(String[] args) {
        // Đường dẫn CSDL: tự động nhận diện khi chạy từ LAB8 hoặc thư mục gốc
        String dbPath = new java.io.File("LAB8/CSDL").exists() ? "LAB8/CSDL/sanpham.db" : "CSDL/sanpham.db";
        String url = "jdbc:sqlite:" + dbPath;
        String username = "";
        String passwd = "";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, username, passwd);
            conn.setAutoCommit(false);

            Statement stmt = conn.createStatement();
            String sql_create = "CREATE TABLE IF NOT EXISTS SanPham ("
                    + "maSP INTEGER PRIMARY KEY, "
                    + "tenSP TEXT NOT NULL, "
                    + "gia REAL, "
                    + "soluong INTEGER)";
            stmt.executeUpdate(sql_create);
            stmt.close();

            String sql_insert = "INSERT OR IGNORE INTO SanPham (maSP, tenSP, gia, soluong) VALUES (?, ?, ?, ?);";
            pstmt = conn.prepareStatement(sql_insert);

            pstmt.setInt(1, 4);
            pstmt.setString(2, "Tai nghe Bluetooth");
            pstmt.setDouble(3, 850000);
            pstmt.setInt(4, 25);
            pstmt.executeUpdate();

            pstmt.setInt(1, 5);
            pstmt.setString(2, "Bàn phím cơ không dây");
            pstmt.setDouble(3, 1600000);
            pstmt.setInt(4, 15);
            pstmt.executeUpdate();

            conn.commit();

            String sql_select = "SELECT maSP, tenSP, gia, soluong FROM SanPham WHERE gia > ?";
            pstmt = conn.prepareStatement(sql_select);
            pstmt.setDouble(1, 1000000);

            rs = pstmt.executeQuery();

            System.out.println("--- DANH SÁCH SẢN PHẨM CÓ GIÁ TRÊN 1 TRIỆU ---");
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
                } catch (SQLException ex) {
                    System.out.println("Lỗi rollback: " + ex.getMessage());
                }
            }
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
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