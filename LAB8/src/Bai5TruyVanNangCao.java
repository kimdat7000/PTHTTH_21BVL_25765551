import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Bai5TruyVanNangCao {
    public static void main(String[] args) {
        // Đường dẫn CSDL: tự động nhận diện khi chạy từ LAB8 hoặc thư mục gốc
        String dbPath = new java.io.File("LAB8/CSDL").exists() ? "LAB8/CSDL/nhanvien.db" : "CSDL/nhanvien.db";
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

            String sql_alter = "ALTER TABLE NhanVien ADD COLUMN luong REAL;";
            try {
                stmt.executeUpdate(sql_alter);
            } catch (SQLException e) {
            }

            String update_1 = "UPDATE NhanVien SET luong = 15000000 WHERE id = 1;";
            String update_2 = "UPDATE NhanVien SET luong = 12000000 WHERE id = 2;";

            stmt.executeUpdate(update_1);
            stmt.executeUpdate(update_2);

            conn.commit();

            System.out.println("--- THỐNG KÊ LƯƠNG NHÂN VIÊN ---");
            
            String sql_agg = "SELECT SUM(luong) AS TongLuong, AVG(luong) AS LuongTB, MIN(luong) AS LuongMin, MAX(luong) AS LuongMax FROM NhanVien";
            rs = stmt.executeQuery(sql_agg);
            if (rs.next()) {
                System.out.println("Tổng quỹ lương: " + String.format("%.0f", rs.getDouble("TongLuong")));
                System.out.println("Mức lương trung bình: " + String.format("%.0f", rs.getDouble("LuongTB")));
                System.out.println("Mức lương thấp nhất: " + String.format("%.0f", rs.getDouble("LuongMin")));
                System.out.println("Mức lương cao nhất: " + String.format("%.0f", rs.getDouble("LuongMax")));
            }
            rs.close();

            System.out.println("\n--- SỐ LƯỢNG NHÂN VIÊN THEO CHỨC VỤ ---");
            String sql_group = "SELECT chuc_vu, COUNT(*) AS SoLuong FROM NhanVien GROUP BY chuc_vu";
            rs = stmt.executeQuery(sql_group);
            while (rs.next()) {
                String chucVu = rs.getString("chuc_vu");
                int soLuong = rs.getInt("SoLuong");
                System.out.println("Chức vụ: " + chucVu + " | Số lượng: " + soLuong);
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
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Lỗi đóng tài nguyên: " + e.getMessage());
            }
        }
    }
}