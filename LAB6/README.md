# BÁO CÁO VÀ HƯỚNG DẪN THỰC THI LAB 6 - JAVA RMI
**Môn học:** Phát triển hệ thống tích hợp / phân tán (PTHTTH)  
**Công nghệ:** Java Remote Method Invocation (Java RMI)  

---

## Cấu trúc thư mục dự án (Mỗi bài đúng 3 file `.java`)

```text
LAB6/
├── Bai1/   # Máy chủ RMI trả về chuỗi "Hello, World!"
│   ├── HelloService.java       (Remote Interface)
│   ├── Server.java             (Implement + Server Main)
│   └── Client.java             (Client Main)
├── Bai2/   # Bài tập Tính Tổng Hai Số (a + b)
│   ├── CalculatorService.java  (Remote Interface)
│   ├── Server.java             (Implement + Server Main)
│   └── Client.java             (Client Main)
├── Bai3/   # Bài tập Kiểm Tra Số Nguyên Tố
│   ├── PrimeService.java       (Remote Interface)
│   ├── Server.java             (Implement + Server Main)
│   └── Client.java             (Client Main)
├── Bai4/   # Bài tập Quản Lý Danh Bạ (HashMap<String, String>)
│   ├── ContactService.java     (Remote Interface)
│   ├── Server.java             (Implement + Server Main)
│   └── Client.java             (Client Main)
├── Bai5/   # Bài tập Chat Đơn Giản 1-1 với RMI
│   ├── ChatService.java        (Remote Interface)
│   ├── Server.java             (Implement + Server Main)
│   └── Client.java             (Client Main)
├── Bai6/   # Bài tập Quản Lý Tài Khoản Ngân Hàng
│   ├── BankAccountService.java (Remote Interface)
│   ├── Server.java             (Implement + Server Main)
│   └── Client.java             (Client Main)
├── Bai7/   # Bài tập Tính Diện Tích Hình Học
│   ├── GeometryService.java    (Remote Interface)
│   ├── Server.java             (Implement + Server Main)
│   └── Client.java             (Client Main)
├── Bai8/   # Bài tập Hệ Thống Đặt Vé Máy Bay
│   ├── FlightBookingService.java (Remote Interface)
│   ├── Server.java             (Implement + Server Main)
│   └── Client.java             (Client Main)
└── Bai9/   # Hệ thống Đấu giá trực tuyến (Online Auction System)
    ├── AuctionService.java     (Remote Interface + Callback Interface + Model Product)
    ├── Server.java             (Implement + Server Main)
    └── Client.java             (Callback Implement + Client Main hỗ trợ Seller & Bidder)
```

---

## Hướng dẫn thực thi (Chạy thẳng từ thư mục gốc, không rác file .class)

> ### 💡 Điểm cải tiến và lưu ý quan trọng:
> 1. **Chạy thẳng từ thư mục gốc `LAB6`:** Không cần phải gõ lệnh `cd` vào từng bài tập.
> 2. **Không làm rác thư mục code:** Sử dụng tham số `-d bin` khi biên dịch. Toàn bộ file `.class` sẽ được đẩy vào thư mục riêng biệt `bin/`, **tuyệt đối không sinh bất kỳ file `.class` nào vào trong các thư mục `Bai1`, `Bai2`,...**, giữ cho thư mục mã nguồn luôn sạch 100%.
> 3. **Tự động RMI Registry:** Mỗi bài đều tự động tạo Registry trên cổng `1099` (hoặc tái sử dụng cổng nếu đang chạy), không cần chạy lệnh `rmiregistry` riêng.
> 4. **Mở 2 cửa sổ Terminal (PowerShell hoặc CMD)** tại thư mục gốc `LAB6`:
>    - **Terminal 1:** Biên dịch và khởi chạy `Server`.
>    - **Terminal 2:** Khởi chạy `Client`.
> 5. **Dọn dẹp nhanh sau khi test:** Khi muốn xóa sạch các file `.class` đã biên dịch, chỉ cần chạy:
>    - PowerShell: `Remove-Item -Recurse -Force bin`
>    - CMD: `rmdir /s /q bin`

---

### Bài 1. Máy chủ RMI trả về chuỗi "Hello, World!"
* **Mô tả:** Client gọi phương thức `sayHello()` từ xa trên Server để nhận về chuỗi `"Hello, World!"`.
* **Cách chạy từ thư mục gốc `LAB6`:**
  * **Cửa sổ 1 (Server):**
    ```powershell
    javac -d bin Bai1/*.java; java -cp bin Server
    ```
  * **Cửa sổ 2 (Client):**
    ```powershell
    java -cp bin Client
    ```

---

### Bài 2. Bài tập Tính Tổng Hai Số
* **Mô tả:** Client nhập 2 số nguyên $a$ và $b$ từ bàn phím, gửi qua RMI để Server tính tổng $a + b$ và trả kết quả.
* **Cách chạy từ thư mục gốc `LAB6`:**
  * **Cửa sổ 1 (Server):**
    ```powershell
    javac -d bin Bai2/*.java; java -cp bin Server
    ```
  * **Cửa sổ 2 (Client):**
    ```powershell
    java -cp bin Client
    ```

---

### Bài 3. Bài tập Kiểm Tra Số Nguyên Tố
* **Mô tả:** Client nhập số nguyên $n$, Server thực hiện thuật toán kiểm tra số nguyên tố và trả kết quả cho Client.
* **Cách chạy từ thư mục gốc `LAB6`:**
  * **Cửa sổ 1 (Server):**
    ```powershell
    javac -d bin Bai3/*.java; java -cp bin Server
    ```
  * **Cửa sổ 2 (Client):**
    ```powershell
    java -cp bin Client
    ```

---

### Bài 4. Bài tập Quản Lý Danh Bạ
* **Mô tả:** Server lưu trữ danh bạ bằng `HashMap<String, String>`. Client có menu tương tác thêm, tìm kiếm, sửa, xóa và hiển thị toàn bộ danh bạ.
* **Cách chạy từ thư mục gốc `LAB6`:**
  * **Cửa sổ 1 (Server):**
    ```powershell
    javac -d bin Bai4/*.java; java -cp bin Server
    ```
  * **Cửa sổ 2 (Client):**
    ```powershell
    java -cp bin Client
    ```

---

### Bài 5. Bài tập Chat Đơn Giản với RMI (1-1)
* **Mô tả:** Ứng dụng chat 1-1 giữa Client và Server qua RMI với phương thức `sendMessage(String message)` và hiển thị phản hồi tức thì.
* **Cách chạy từ thư mục gốc `LAB6`:**
  * **Cửa sổ 1 (Server):**
    ```powershell
    javac -d bin Bai5/*.java; java -cp bin Server
    ```
  * **Cửa sổ 2 (Client):**
    ```powershell
    java -cp bin Client
    ```

---

### Bài 6. Bài tập Quản Lý Tài Khoản Ngân Hàng
* **Mô tả:** Server quản lý tài khoản ngân hàng với số dư ban đầu. Client có thể kiểm tra số dư (`getBalance`), nạp tiền (`deposit`) và rút tiền (`withdraw`) an toàn với kiểm tra số dư.
* **Cách chạy từ thư mục gốc `LAB6`:**
  * **Cửa sổ 1 (Server):**
    ```powershell
    javac -d bin Bai6/*.java; java -cp bin Server
    ```
  * **Cửa sổ 2 (Client):**
    ```powershell
    java -cp bin Client
    ```

---

### Bài 7. Bài tập Tính Diện Tích Hình Học
* **Mô tả:** Client chọn loại hình học (chữ nhật, hình tròn, tam giác), nhập các thông số và gọi Server tính diện tích trả về.
* **Cách chạy từ thư mục gốc `LAB6`:**
  * **Cửa sổ 1 (Server):**
    ```powershell
    javac -d bin Bai7/*.java; java -cp bin Server
    ```
  * **Cửa sổ 2 (Client):**
    ```powershell
    java -cp bin Client
    ```

---

### Bài 8. Bài tập Hệ Thống Đặt Vé Máy Bay
* **Mô tả:** Server quản lý danh sách chuyến bay và số ghế trống. Client có thể tra cứu danh sách, kiểm tra ghế trống theo chuyến, đặt vé (`bookTicket`) và hủy vé (`cancelBooking`).
* **Cách chạy từ thư mục gốc `LAB6`:**
  * **Cửa sổ 1 (Server):**
    ```powershell
    javac -d bin Bai8/*.java; java -cp bin Server
    ```
  * **Cửa sổ 2 (Client):**
    ```powershell
    java -cp bin Client
    ```

---

### Bài 9. Hệ thống Đấu giá trực tuyến (Online Auction System)
* **Mô tả:**
  - **Người bán (Seller):** Đăng sản phẩm mới lên sàn với tên sản phẩm và giá khởi điểm.
  - **Người đấu giá (Bidder):** Xem danh sách sản phẩm, đặt giá thầu cao hơn giá hiện tại.
  - **Server & Callback:** Cập nhật giá cao nhất theo thời gian thực và đẩy thông báo tức thì (Real-time Push Notification) tới tất cả Seller & Bidder mỗi khi có người trả giá cao hơn hoặc có sản phẩm mới.
* **Cách chạy từ thư mục gốc `LAB6`:**
  * **Cửa sổ 1 (Server):**
    ```powershell
    javac -d bin Bai9/*.java; java -cp bin Server
    ```
  * **Cửa sổ 2 (Client - Chọn 1: Người bán Seller):**
    ```powershell
    java -cp bin Client
    ```
    *(Nhập phím `1` để vào vai trò Người bán: thêm sản phẩm, xem danh sách)*
  * **Cửa sổ 3 (Client - Chọn 2: Người đấu giá Bidder):**
    ```powershell
    java -cp bin Client
    ```
    *(Nhập phím `2` để vào vai trò Người đấu giá: xem sản phẩm, trả giá thầu. Khi trả giá thầu mới, Cửa sổ 2 và các cửa sổ khác sẽ lập tức nhận chuông thông báo giá mới thời gian thực!)*
