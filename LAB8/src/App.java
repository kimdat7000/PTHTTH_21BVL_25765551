import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
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

            String sql_create_table = "CREATE TABLE IF NOT EXISTS NhanVien ("
                    + "id INTEGER PRIMARY KEY, "
                    + "ten TEXT NOT NULL, "
                    + "chuc_vu TEXT)";
            stmt.executeUpdate(sql_create_table);

            String sql_insert_1 = "INSERT OR IGNORE INTO NhanVien (id, ten, chuc_vu) VALUES (1, 'Nguyễn Văn A', 'Lập trình viên');";
            String sql_insert_2 = "INSERT OR IGNORE INTO NhanVien (id, ten, chuc_vu) VALUES (2, 'Trần Thị B', 'Kiểm thử phần mềm');";
            String sql_insert_3 = "INSERT OR IGNORE INTO NhanVien (id, ten, chuc_vu) VALUES (3, 'Lê Văn C', 'Quản lý dự án');";

            stmt.executeUpdate(sql_insert_1);
            stmt.executeUpdate(sql_insert_2);
            stmt.executeUpdate(sql_insert_3);

            conn.commit();

            String sql_select = "SELECT id, ten, chuc_vu FROM NhanVien";
            rs = stmt.executeQuery(sql_select);

            System.out.println("--- DANH SÁCH NHÂN VIÊN ---");
            while (rs.next()) {
                int id = rs.getInt("id");
                String ten = rs.getString("ten");
                String chucVu = rs.getString("chuc_vu");

                System.out.println("ID: " + id + " | Tên: " + ten + " | Chức vụ: " + chucVu);
            }

        } catch (SQLException e) {
            System.out.println("Lỗi kết nối hoặc thực thi: " + e.getMessage());
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