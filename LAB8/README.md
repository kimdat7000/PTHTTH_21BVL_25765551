# HƯỚNG DẪN THỰC HÀNH LAB 8: JDBC VỚI SQLITE (JAVA DATABASE CONNECTIVITY)

## 1. Cấu trúc thư mục dự án
Dự án được tổ chức gọn gàng theo mô hình chuẩn:
```text
LAB8/
├── CSDL/                                # Thư mục chứa các tệp cơ sở dữ liệu SQLite
│   ├── nhanvien.db                      # CSDL cho bài Nhân viên (App, Bai5)
│   └── sanpham.db                       # CSDL cho bài Sản phẩm (Bai1, Bai2, Bai3, Bai4)
├── lib/                                 # Thư viện ngoài
│   └── sqlite-jdbc-3.53.4.0.jar         # Driver JDBC kết nối SQLite
├── src/                                 # Thư mục mã nguồn Java
│   ├── App.java                         # B1 -> B5: Kết nối, tạo bảng NhanVien, Transaction & SELECT
│   ├── Bai1SanPham.java                 # Bài 1: Quản lý danh mục SanPham (Tạo bảng, thêm & hiển thị)
│   ├── Bai2TransactionSanPham.java      # Bài 2: Giao dịch (Transaction) khi thêm/cập nhật sản phẩm
│   ├── Bai3ExceptionHandling.java       # Bài 3: Xử lý ngoại lệ (Exception Handling) triệt để & Rollback
│   ├── Bai4PreparedStatement.java      # Bài 4: Sử dụng PreparedStatement bảo mật và tối ưu hiệu năng
│   └── Bai5TruyVanNangCao.java          # Bài 5: Truy vấn nâng cao (SUM, AVG, MIN, MAX, GROUP BY)
└── README.md                            # Hướng dẫn cách biên dịch và chạy chương trình
```

---

## 2. Hướng dẫn cách biên dịch và chạy chương trình

### Yêu cầu trước khi chạy:
- Máy đã cài đặt **Java JDK (bản 17 hoặc 21 trở lên)**.
- Mở **Terminal / PowerShell** hoặc **Command Prompt (CMD)** tại thư mục gốc của bài thực hành (`LAB8`).

> [!TIP]
> **Đường dẫn CSDL trong mã nguồn:**
> Chuỗi kết nối CSDL trong mã nguồn đã được chuẩn hóa theo đúng tên thư mục `LAB8` (`LAB8/CSDL/nhanvien.db` và `LAB8/CSDL/sanpham.db`).
> Mã nguồn được thiết kế tự động thích ứng thông minh:
> - Khi chạy từ thư mục gốc của repository (`PTHTTH_21BVL_25765551`): Chương trình tự động nhận diện `LAB8/CSDL/...`.
> - Khi chạy trực tiếp trong thư mục `LAB8`: Chương trình tự động nhận diện `CSDL/...`.
> Bạn có thể biên dịch và chạy trực tiếp mà không cần cấu hình thêm bất kỳ liên kết phụ nào.

---

### Bước 1: Biên dịch toàn bộ mã nguồn
Chạy lệnh sau để biên dịch toàn bộ các file trong thư mục `src/` và lưu file thực thi `.class` vào thư mục `bin/`:

```powershell
javac -cp ".;lib/sqlite-jdbc-3.53.4.0.jar" -d bin src/*.java
```

---

### Bước 2: Lệnh chạy từng bài tập cụ thể

#### 1. Chạy bài thực hành cơ bản Nhân viên (B1 - B5):
Thực hiện kết nối, tạo bảng `NhanVien`, thêm 3 nhân viên trong 1 transaction và truy vấn hiển thị:
```powershell
java -cp "bin;lib/sqlite-jdbc-3.53.4.0.jar" App
```

#### 2. Chạy Bài tập 1: Quản lý danh mục Sản phẩm (CRUD cơ bản)
Tạo bảng `SanPham (maSP, tenSP, gia, soluong)`, thêm các sản phẩm mẫu và truy vấn danh sách:
```powershell
java -cp "bin;lib/sqlite-jdbc-3.53.4.0.jar" Bai1SanPham
```

#### 3. Chạy Bài tập 2: Quản lý Giao dịch (Transaction) trong cập nhật dữ liệu
Thực hiện thêm và cập nhật giá/số lượng sản phẩm trong một giao dịch duy nhất có `commit` và `rollback`:
```powershell
java -cp "bin;lib/sqlite-jdbc-3.53.4.0.jar" Bai2TransactionSanPham
```

#### 4. Chạy Bài tập 3: Xử lý ngoại lệ (Exception Handling) triệt để
Bọc mã nguồn trong khối `try-catch-finally`, mô phỏng phát hiện lỗi SQL tiềm ẩn và gọi `conn.rollback()` an toàn, giải phóng tài nguyên trong `finally`:
```powershell
java -cp "bin;lib/sqlite-jdbc-3.53.4.0.jar" Bai3ExceptionHandling
```

#### 5. Chạy Bài tập 4: Sử dụng `PreparedStatement`
Sử dụng truy vấn tham số hóa (`?`) để tăng hiệu năng thực thi và phòng chống triệt để lỗ hổng SQL Injection:
```powershell
java -cp "bin;lib/sqlite-jdbc-3.53.4.0.jar" Bai4PreparedStatement
```

#### 6. Chạy Bài tập 5: Truy vấn dữ liệu nâng cao (Aggregation Functions)
Thêm cột `luong` vào bảng `NhanVien`, thực hiện các phép tính tổng hợp `SUM`, `AVG`, `MIN`, `MAX`, `COUNT` và `GROUP BY` theo chức vụ:
```powershell
java -cp "bin;lib/sqlite-jdbc-3.53.4.0.jar" Bai5TruyVanNangCao
```

---

## 3. Hướng dẫn chạy trên VS Code / Eclipse / IntelliJ IDEA

- **VS Code**:
  1. Cài Extension **Extension Pack for Java**.
  2. Mở thư mục bài tập.
  3. Tại mục **JAVA PROJECTS** ở thanh bên trái, cuộn xuống phần **Referenced Libraries** ➔ Nhấn dấu `+` ➔ Chọn file jar `lib/sqlite-jdbc-3.53.4.0.jar`.
  4. Mở file `.java` bất kỳ trong thư mục `src/` và nhấn nút **Run** (hoặc `F5`).

- **IntelliJ IDEA / Eclipse**:
  1. Chuột phải vào file `lib/sqlite-jdbc-3.53.4.0.jar` ➔ Chọn **Add as Library...** (hoặc **Build Path ➔ Add to Build Path**).
  2. Chuột phải vào file cần chạy trong `src/` ➔ Chọn **Run '...main()'**.

---

## 4. Tóm tắt nội dung kiến thức JDBC áp dụng trong bài

1. **Chu trình JDBC chuẩn**:
   - `DriverManager.getConnection(url, username, password)`: Thiết lập kết nối.
   - `Connection.createStatement()` / `Connection.prepareStatement(sql)`: Tạo phát biểu SQL.
   - `Statement.executeUpdate(sql)`: Thực thi lệnh DDL / DML (`CREATE`, `INSERT`, `UPDATE`, `DELETE`).
   - `Statement.executeQuery(sql)`: Lấy dữ liệu với `ResultSet`.
   - `ResultSet.next()` và các hàm `getInt()`, `getString()`, `getDouble()`: Đọc từng bản ghi.
   - Đóng tài nguyên theo thứ tự ngược: `ResultSet ➔ Statement ➔ Connection` trong khối `finally`.

2. **Quản lý Giao dịch (Transaction Management)**:
   - `conn.setAutoCommit(false)`: Tắt tự động lưu để mở Transaction.
   - `conn.commit()`: Lưu vĩnh viễn các thao tác khi tất cả đều thành công.
   - `conn.rollback()`: Hoàn tác toàn bộ thay đổi khi có ngoại lệ xảy ra trong khối `catch`.

3. **Bảo mật và hiệu năng với `PreparedStatement`**:
   - Biên dịch trước câu lệnh SQL trên database engine.
   - Truyền tham số an toàn qua các hàm `setInt()`, `setString()`, `setDouble()`, ngăn chặn hoàn toàn tấn công SQL Injection.

4. **Hàm tổng hợp (SQL Aggregation)**:
   - `SUM(luong)`: Tổng quỹ lương.
   - `AVG(luong)`: Mức lương bình quân.
   - `MAX(luong)` / `MIN(luong)`: Mức lương cao nhất / thấp nhất.
   - `COUNT(*)` kết hợp `GROUP BY chuc_vu`: Thống kê nhân sự theo từng chức danh.
