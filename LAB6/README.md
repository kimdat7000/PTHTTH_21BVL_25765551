# BÁO CÁO VÀ HƯỚNG DẪN THỰC THI LAB 6 - JAVA RMI
**Môn học:** Phát triển hệ thống tích hợp / phân tán (PTHTTH)  
**Công nghệ:** Java Remote Method Invocation (Java RMI)  

---

## 📁 Cấu trúc thư mục dự án (Tách riêng từng file theo chuẩn OOP & Java RMI)

Toàn bộ các Interface, Implementation (triển khai Remote), Model và các lớp Client/Server đều đã được tách biệt thành từng file `.java` độc lập:

```text
LAB6/
├── Bai1/   # Máy chủ RMI trả về chuỗi "Hello, World!"
│   ├── HelloService.java           (Remote Interface: sayHello())
│   ├── HelloServiceImpl.java       (Remote Object Implementation)
│   ├── Server.java                 (Khởi tạo RMI Registry & Rebind service)
│   └── Client.java                 (Client gọi hàm sayHello từ xa)
├── Bai2/   # Bài tập Tính Tổng Hai Số
│   ├── CalculatorService.java      (Remote Interface: int add(int a, int b))
│   ├── CalculatorServiceImpl.java  (Remote Object Implementation)
│   ├── Server.java                 (Server Main)
│   └── Client.java                 (Client nhập số và nhận kết quả)
├── Bai3/   # Bài tập Kiểm Tra Số Nguyên Tố
│   ├── PrimeService.java           (Remote Interface: boolean isPrime(int n))
│   ├── PrimeServiceImpl.java       (Remote Object Implementation)
│   ├── Server.java                 (Server Main)
│   └── Client.java                 (Client kiểm tra số nguyên tố)
├── Bai4/   # Bài tập Quản Lý Danh Bạ (HashMap<String, String>)
│   ├── ContactService.java         (Remote Interface: add, find, delete contact)
│   ├── ContactServiceImpl.java     (Triển khai HashMap lưu danh bạ)
│   ├── Server.java                 (Server Main)
│   └── Client.java                 (Client giao diện dòng lệnh quản lý danh bạ)
├── Bai5/   # Bài tập Chat Đơn Giản 1-1 với RMI
│   ├── ChatService.java            (Remote Interface: sendMessage, getResponse)
│   ├── ChatServiceImpl.java        (Server xử lý tin nhắn và phản hồi)
│   ├── Server.java                 (Server Main)
│   └── Client.java                 (Client chat tương tác 1-1)
├── Bai6/   # Bài tập Quản Lý Tài Khoản Ngân Hàng
│   ├── BankAccountService.java     (Remote Interface: getBalance, deposit, withdraw)
│   ├── BankAccountServiceImpl.java (Triển khai xử lý số dư tài khoản)
│   ├── Server.java                 (Server Main)
│   └── Client.java                 (Client thực hiện giao dịch nạp/rút tiền)
├── Bai7/   # Bài tập Tính Diện Tích Hình Học
│   ├── GeometryService.java        (Remote Interface: hình chữ nhật, hình tròn, tam giác)
│   ├── GeometryServiceImpl.java    (Triển khai các công thức hình học)
│   ├── Server.java                 (Server Main)
│   └── Client.java                 (Client nhập kích thước và nhận diện tích)
├── Bai8/   # Bài tập Hệ Thống Đặt Vé Máy Bay
│   ├── FlightBookingService.java   (Remote Interface: bookTicket, availableSeats, cancelBooking)
│   ├── FlightBookingServiceImpl.java (Quản lý các chuyến bay & số ghế)
│   ├── Server.java                 (Server Main)
│   └── Client.java                 (Client đặt vé, tra cứu, hủy vé)
└── Bai9/   # Hệ thống Đấu giá trực tuyến (Online Auction System)
    ├── Product.java                (Model đối tượng sản phẩm - Serializable)
    ├── AuctionCallback.java        (Callback Interface nhận thông báo từ xa)
    ├── AuctionCallbackImpl.java    (Triển khai Callback Client nhận thông báo đẩy)
    ├── AuctionService.java         (Remote Interface dịch vụ đấu giá chính)
    ├── AuctionServiceImpl.java     (Quản lý sản phẩm, phiên đấu giá & thông báo thời gian thực)
    ├── Server.java                 (Auction Server Main)
    ├── Client.java                 (Console Client đa năng: chọn Seller hoặc Bidder)
    ├── SellerClient.java           (Client chuyên dụng cho Chủ sở hữu / Người bán)
    └── BidderClient.java           (Client chuyên dụng cho Người đấu giá)
```

---

## 🚀 Hướng dẫn biên dịch và thực thi

Bạn có thể chạy theo một trong 2 cách sau:

### Cách 1: Chạy từ thư mục gốc `LAB6` (Khuyên dùng - Không đè file .class giữa các bài)
Biên dịch vào thư mục riêng `bin/BaiX` giúp thư mục mã nguồn luôn sạch và các bài không bị trùng lặp `Server.class` hay `Client.class`.

* **Lệnh biên dịch tất cả 9 bài một lần duy nhất (từ thư mục `LAB6`):**
  ```powershell
  javac -d bin/Bai1 Bai1/*.java; javac -d bin/Bai2 Bai2/*.java; javac -d bin/Bai3 Bai3/*.java; javac -d bin/Bai4 Bai4/*.java; javac -d bin/Bai5 Bai5/*.java; javac -d bin/Bai6 Bai6/*.java; javac -d bin/Bai7 Bai7/*.java; javac -d bin/Bai8 Bai8/*.java; javac -d bin/Bai9 Bai9/*.java
  ```

---

### Cách 2: Chạy trực tiếp từ bên trong từng thư mục bài (`cd BaiX`)
Mỗi bài có thể mở 2 Terminal và di chuyển vào thư mục đó:
* **Terminal 1 (Server):** `cd BaiX; javac *.java; java Server`
* **Terminal 2 (Client):** `cd BaiX; java Client`

---

## 📖 Chi tiết cách chạy từng bài (Thực hiện từ thư mục gốc `LAB6`)

### Bài 1. Máy chủ RMI trả về chuỗi "Hello, World!"
* **Mô tả:** Client gọi phương thức từ xa `sayHello()` của Server để nhận chuỗi `"Hello, World!"`.
* **Cách chạy:**
  * **Terminal 1 (Server):**
    ```powershell
    javac -d bin/Bai1 Bai1/*.java; java -cp bin/Bai1 Server
    ```
  * **Terminal 2 (Client):**
    ```powershell
    java -cp bin/Bai1 Client
    ```

---

### Bài 2. Bài tập Tính Tổng Hai Số
* **Mô tả:** Client nhập 2 số nguyên $a$ và $b$ từ bàn phím, gửi đến Server thông qua RMI `add(a, b)` để nhận tổng $a + b$.
* **Cách chạy:**
  * **Terminal 1 (Server):**
    ```powershell
    javac -d bin/Bai2 Bai2/*.java; java -cp bin/Bai2 Server
    ```
  * **Terminal 2 (Client):**
    ```powershell
    java -cp bin/Bai2 Client
    ```

---

### Bài 3. Bài tập Kiểm Tra Số Nguyên Tố
* **Mô tả:** Client nhập một số nguyên $n$, Server thực hiện kiểm tra `isPrime(n)` và trả kết quả về cho Client.
* **Cách chạy:**
  * **Terminal 1 (Server):**
    ```powershell
    javac -d bin/Bai3 Bai3/*.java; java -cp bin/Bai3 Server
    ```
  * **Terminal 2 (Client):**
    ```powershell
    java -cp bin/Bai3 Client
    ```

---

### Bài 4. Bài tập Quản Lý Danh Bạ
* **Mô tả:** Server lưu trữ danh bạ bằng `HashMap<String, String>`. Hỗ trợ các phương thức: `addContact(name, phone)`, `findContact(name)`, `deleteContact(name)`, cập nhật và xem toàn bộ danh bạ.
* **Cách chạy:**
  * **Terminal 1 (Server):**
    ```powershell
    javac -d bin/Bai4 Bai4/*.java; java -cp bin/Bai4 Server
    ```
  * **Terminal 2 (Client):**
    ```powershell
    java -cp bin/Bai4 Client
    ```

---

### Bài 5. Bài tập Chat Đơn Giản 1-1 với RMI
* **Mô tả:** Client gửi tin nhắn tới Server qua `sendMessage(String message)`. Server xử lý, ghi nhận và tự động phản hồi lại Client theo ngữ cảnh tin nhắn.
* **Cách chạy:**
  * **Terminal 1 (Server):**
    ```powershell
    javac -d bin/Bai5 Bai5/*.java; java -cp bin/Bai5 Server
    ```
  * **Terminal 2 (Client):**
    ```powershell
    java -cp bin/Bai5 Client
    ```

---

### Bài 6. Bài tập Quản Lý Tài Khoản Ngân Hàng
* **Mô tả:** Server quản lý tài khoản ngân hàng với số dư ban đầu. Client có thể:
  * Kiểm tra số dư: `getBalance()`
  * Gửi tiền: `deposit(amount)`
  * Rút tiền: `withdraw(amount)` (kiểm tra điều kiện số dư trước khi rút)
* **Cách chạy:**
  * **Terminal 1 (Server):**
    ```powershell
    javac -d bin/Bai6 Bai6/*.java; java -cp bin/Bai6 Server
    ```
  * **Terminal 2 (Client):**
    ```powershell
    java -cp bin/Bai6 Client
    ```

---

### Bài 7. Bài tập Tính Diện Tích Hình Học
* **Mô tả:** Client chọn loại hình và gửi các kích thước để Server tính diện tích:
  * Hình chữ nhật: `rectangleArea(width, height)`
  * Hình tròn: `circleArea(radius)`
  * Hình tam giác: `triangleArea(base, height)`
* **Cách chạy:**
  * **Terminal 1 (Server):**
    ```powershell
    javac -d bin/Bai7 Bai7/*.java; java -cp bin/Bai7 Server
    ```
  * **Terminal 2 (Client):**
    ```powershell
    java -cp bin/Bai7 Client
    ```

---

### Bài 8. Bài tập Hệ Thống Đặt Vé Máy Bay
* **Mô tả:** Server quản lý danh sách chuyến bay và kiểm soát số lượng ghế trống:
  * Đặt vé: `bookTicket(flightNumber, seats)`
  * Kiểm tra ghế trống: `availableSeats(flightNumber)`
  * Hủy vé: `cancelBooking(flightNumber, seats)`
* **Cách chạy:**
  * **Terminal 1 (Server):**
    ```powershell
    javac -d bin/Bai8 Bai8/*.java; java -cp bin/Bai8 Server
    ```
  * **Terminal 2 (Client):**
    ```powershell
    java -cp bin/Bai8 Client
    ```

---

### Bài 9. Hệ thống Đấu giá trực tuyến (Online Auction System)
* **Mô tả chức năng:**
  1. **Chủ sở hữu (Seller):** Thêm sản phẩm lên sàn đấu giá (tên sản phẩm, giá khởi điểm) và xem danh sách sản phẩm.
  2. **Người đấu giá (Bidder):** Xem danh sách sản phẩm đang đấu giá, đưa ra giá thầu (bid) cao hơn giá hiện tại.
  3. **Server (Auction Server):** Quản lý danh sách sản phẩm, cập nhật giá cao nhất và gửi **thông báo đẩy thời gian thực (Callback)** đến tất cả các bên khi có giá thầu mới hoặc sản phẩm mới.

* **Cách chạy (Hỗ trợ 2 lựa chọn):**

  #### Lựa chọn A: Sử dụng các Client chuyên dụng (Rất tiện lợi để mở 3 cửa sổ):
  * **Cửa sổ 1 - Server:**
    ```powershell
    javac -d bin/Bai9 Bai9/*.java; java -cp bin/Bai9 Server
    ```
  * **Cửa sổ 2 - Người bán (Seller):**
    ```powershell
    java -cp bin/Bai9 SellerClient
    ```
  * **Cửa sổ 3 - Người đấu giá (Bidder):**
    ```powershell
    java -cp bin/Bai9 BidderClient
    ```
    *(Khi Bidder đặt giá thầu mới ở Cửa sổ 3, Cửa sổ 2 của Seller và Server sẽ lập tức nhảy chuông thông báo theo thời gian thực!)*

  #### Lựa chọn B: Sử dụng Client đa năng (Chọn vai trò trong menu):
  * **Cửa sổ 1 - Server:**
    ```powershell
    javac -d bin/Bai9 Bai9/*.java; java -cp bin/Bai9 Server
    ```
  * **Cửa sổ 2 - Client (Nhập `1` để chọn vai trò Người bán Seller):**
    ```powershell
    java -cp bin/Bai9 Client
    ```
  * **Cửa sổ 3 - Client (Nhập `2` để chọn vai trò Người đấu giá Bidder):**
    ```powershell
    java -cp bin/Bai9 Client
    ```

---

## 🧹 Lệnh dọn dẹp thư mục build
Khi muốn xóa các file `.class` đã biên dịch:
* **PowerShell:** `Remove-Item -Recurse -Force bin`
* **CMD:** `rmdir /s /q bin`
