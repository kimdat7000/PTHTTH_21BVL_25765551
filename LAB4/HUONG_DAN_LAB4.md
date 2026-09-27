# Bài tập Socket Java

Các chương trình mới nằm trong `src/network`, `src/tcp` và `src/udp`; những lớp mẫu cũ không bị thay đổi. Lệnh dưới đây chạy tại thư mục `LAB4` trong PowerShell.

## Biên dịch

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$root = (Get-Location).Path
$files = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { Resolve-Path -Relative $_.FullName }
javac -encoding UTF-8 -d out $files
```

## Bài 1: Host và URI Inspector

Hostname và URI là hai đối số độc lập:

```powershell
java -cp out network.HostUriInspector localhost "https://localhost:8443/a/b?q=java#part"
```

Ba ca hợp lệ:

```powershell
java -cp out network.HostUriInspector localhost "https://localhost:8443/a?q=1#top"
java -cp out network.HostUriInspector 127.0.0.1 "http://127.0.0.1:8080/status"
java -cp out network.HostUriInspector ::1 "http://[::1]:8080/status"
```

Ba ca lỗi:

```powershell
java -cp out network.HostUriInspector
java -cp out network.HostUriInspector localhost "http://bad host/path"
java -cp out network.HostUriInspector host-khong-ton-tai.invalid "https://example.com/"
```

Ca thiếu đối số báo cách dùng; URI sai báo lỗi cú pháp; hostname không phân giải được báo lỗi riêng. Không tải nội dung trang web.

## Bài 2: TCP đổi chữ số thành chữ

Mở hai terminal, chạy server rồi client:

```powershell
java -cp out tcp.DigitTcpServer
java -cp out tcp.DigitTcpClient localhost
```

Client gửi mỗi dòng nguyên trạng, dùng UTF-8; gửi `QUIT` để kết thúc phiên. Thử lần lượt `0`, `9`, dòng rỗng, `10`, `a`, `1`: kết quả tương ứng là `không`, `chín`, rồi bốn lần `ERR INVALID_DIGIT`. Máy chủ không trim dữ liệu trước khi kiểm tra, nên khoảng trắng làm đầu vào không hợp lệ.

## Bài 3: Dịch vụ ngày giờ TCP và UDP

TCP:

```powershell
java -cp out tcp.DateTimeTcpServer
java -cp out tcp.DateTimeTcpClient localhost
```

UDP, chạy server và client trong hai terminal khác:

```powershell
java -cp out udp.DateTimeUdpServer
java -cp out udp.DateTimeUdpClient localhost
```

Hai giao thức nhận `DATE`, `TIME`, `DATETIME`; kết quả lần lượt theo định dạng `dd MM yyyy`, `HH mm ss`, và kết hợp cả hai. TCP giữ kết nối để nhận nhiều dòng, trả `OK BYE` rồi đóng kết nối khi nhận `QUIT`. UDP xử lý từng datagram độc lập; `QUIT` chỉ trả `OK BYE`, không dừng server hay đóng một phiên trên server.

### Khi server dừng trong lúc client đang hoạt động

Với TCP, kết nối có trạng thái nên client thường nhận EOF khi server đóng socket có trật tự, hoặc nhận IOException nếu kết nối bị ngắt bất thường; lần gửi tiếp theo không thể tiếp tục phiên cũ. Với UDP, không có kết nối để đóng và server không thể báo trực tiếp rằng nó đã dừng. Client chỉ biết không nhận được phản hồi sau thời gian chờ (ở đây là 3 giây); datagram có thể bị mất vì nhiều nguyên nhân, nên timeout không chứng minh chắc chắn server đã dừng. Client UDP có thể gửi yêu cầu mới khi server hoạt động lại.
