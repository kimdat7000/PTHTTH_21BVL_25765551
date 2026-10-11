# Bài tập Socket Java

Mỗi bài được tách riêng trong `Bai1` đến `Bai10`; mã mẫu cũ trong `src` được giữ nguyên. Tài liệu yêu cầu và lệnh chạy của cả mười bài được gộp tại đây. Các lệnh dưới đây chạy tại thư mục `LAB4` trong PowerShell.

## Biên dịch

```powershell
foreach ($assignment in 'Bai1', 'Bai2', 'Bai3', 'Bai4', 'Bai5', 'Bai6', 'Bai7', 'Bai8', 'Bai9', 'Bai10') {
	New-Item -ItemType Directory -Force "$assignment\out" | Out-Null
	Push-Location $assignment
	$files = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { Resolve-Path -Relative $_.FullName }
	javac -encoding UTF-8 -d out $files
	$compileResult = $LASTEXITCODE
	Pop-Location
	if ($compileResult -ne 0) { exit $compileResult }
}
```

## Bài 1: Host và URI Inspector

Hostname và URI là hai đối số độc lập:

```powershell
java -cp Bai1\out network.HostUriInspector localhost "https://localhost:8443/a/b?q=java#part"
```

Ba ca hợp lệ:

```powershell
java -cp Bai1\out network.HostUriInspector localhost "https://localhost:8443/a?q=1#top"
java -cp Bai1\out network.HostUriInspector 127.0.0.1 "http://127.0.0.1:8080/status"
java -cp Bai1\out network.HostUriInspector ::1 "http://[::1]:8080/status"
```

Ba ca lỗi:

```powershell
java -cp Bai1\out network.HostUriInspector
java -cp Bai1\out network.HostUriInspector localhost "http://bad host/path"
java -cp Bai1\out network.HostUriInspector host-khong-ton-tai.invalid "https://example.com/"
```

Ca thiếu đối số báo cách dùng; URI sai báo lỗi cú pháp; hostname không phân giải được báo lỗi riêng. Không tải nội dung trang web.

## Bài 2: TCP đổi chữ số thành chữ

Mở hai terminal, chạy server rồi client:

```powershell
java -cp Bai2\out tcp.DigitTcpServer
java -cp Bai2\out tcp.DigitTcpClient localhost
```

Client gửi mỗi dòng nguyên trạng, dùng UTF-8; gửi `QUIT` để kết thúc phiên. Thử lần lượt `0`, `9`, dòng rỗng, `10`, `a`, `1`: kết quả tương ứng là `không`, `chín`, rồi bốn lần `ERR INVALID_DIGIT`. Máy chủ không trim dữ liệu trước khi kiểm tra, nên khoảng trắng làm đầu vào không hợp lệ.

## Bài 3: Dịch vụ ngày giờ TCP và UDP

TCP:

```powershell
java -cp Bai3\out tcp.DateTimeTcpServer
java -cp Bai3\out tcp.DateTimeTcpClient localhost
```

UDP, chạy server và client trong hai terminal khác:

```powershell
java -cp Bai3\out udp.DateTimeUdpServer
java -cp Bai3\out udp.DateTimeUdpClient localhost
```

Hai giao thức nhận `DATE`, `TIME`, `DATETIME`; kết quả lần lượt theo định dạng `dd MM yyyy`, `HH mm ss`, và kết hợp cả hai. TCP giữ kết nối để nhận nhiều dòng, trả `OK BYE` rồi đóng kết nối khi nhận `QUIT`. UDP xử lý từng datagram độc lập; `QUIT` chỉ trả `OK BYE`, không dừng server hay đóng một phiên trên server.

### Khi server dừng trong lúc client đang hoạt động

Với TCP, kết nối có trạng thái nên client thường nhận EOF khi server đóng socket có trật tự, hoặc nhận IOException nếu kết nối bị ngắt bất thường; lần gửi tiếp theo không thể tiếp tục phiên cũ. Với UDP, không có kết nối để đóng và server không thể báo trực tiếp rằng nó đã dừng. Client chỉ biết không nhận được phản hồi sau thời gian chờ (ở đây là 3 giây); datagram có thể bị mất vì nhiều nguyên nhân, nên timeout không chứng minh chắc chắn server đã dừng. Client UDP có thể gửi yêu cầu mới khi server hoạt động lại.

## Bài 4: Máy tính từ xa

Giao thức mỗi dòng có dạng `CALC toán_tử số_1 số_2`. Hỗ trợ `+`, `-`, `*`, `/`; server trả `OK kết_quả`, `ERR DIVIDE_BY_ZERO`, `ERR UNSUPPORTED_OPERATOR`, `ERR INVALID_NUMBER` hoặc `ERR INVALID_FORMAT`. Mỗi client có thể gửi nhiều phép tính rồi `QUIT`.

Mở hai terminal:

```powershell
java -cp Bai4\out tcp.TcpCalculatorServer
java -cp Bai4\out tcp.TcpCalculatorClient localhost
```

Thử các dòng sau trong client:

```text
CALC + 100 200
CALC / 10 0
CALC % 10 3
CALC + a 2
CALC + 2
QUIT
```

Phản hồi tương ứng: `OK 300`, `ERR DIVIDE_BY_ZERO`, `ERR UNSUPPORTED_OPERATOR`, `ERR INVALID_NUMBER`, `ERR INVALID_FORMAT`, `OK BYE`.

## Bài 5: Chat TCP nhiều client

Server dùng thread pool và `ConcurrentHashMap`; nickname duy nhất, dài 1–20 ký tự ASCII chữ/số/gạch dưới/gạch ngang. Một client nhận `NICK` sau kết nối, rồi gửi nickname. Lệnh `USERS` liệt kê người đang kết nối, `MSG nội_dung` broadcast tới các client khác, `QUIT` rời chat. Mất kết nối đột ngột cũng xóa nickname.

Chạy server và mở ít nhất hai client trong các terminal riêng:

```powershell
java -cp Bai5\out tcp.TcpChatServer
java -cp Bai5\out tcp.TcpChatClient localhost
```

Sau khi chọn nickname, thử `USERS`, `MSG xin chào` và `QUIT`. Mở thêm client với nickname đang được sử dụng để kiểm tra phản hồi `ERR NICKNAME_TAKEN`.

## Bài 6: Thực nghiệm so sánh TCP và UDP

Chương trình tự tạo echo server trên localhost, gửi 1000 thông điệp 128 byte theo từng lượt hỏi/đáp, lặp 5 lần cho mỗi giao thức. Thời gian đo chỉ bao phủ vòng lặp gửi/nhận, không tính thời gian tạo socket; timeout nhận là 1000 ms. Mỗi dòng kết quả cho biết lượt, thời gian ms và số phản hồi hợp lệ. Chương trình in hệ điều hành và phiên bản Java; không kết luận giao thức nào luôn nhanh hơn từ một lượt.

```powershell
java -cp Bai6\out benchmark.TransportBenchmark
```

Môi trường đã kiểm thử: Windows 11, Java 21.0.12, localhost. Kết quả trên máy khác có thể khác; nên so sánh đủ năm lượt và số phản hồi nhận được.

## Bài 7: Server lưu nhật ký tin nhắn

Client gửi `HELLO clientId` trước nội dung. ID chỉ chứa chữ ASCII, số, `_`, `-`; log append vào `data/logs/clientId.txt` bên trong thư mục chạy server. Mỗi dòng có timestamp ISO-8601 UTC, địa chỉ remote và nội dung UTF-8. Nội dung chỉ kết thúc bằng dòng lệnh `QUIT`; chuỗi `HET` trong tin nhắn không kết thúc phiên.

Chạy hai terminal tại gốc `LAB4`:

```powershell
Push-Location Bai7
java -cp out tcp.MessageLogServer
```

```powershell
Push-Location Bai7
java -cp out tcp.MessageLogClient test_client-1 localhost
```

Trong client, gửi nội dung tùy ý rồi `QUIT`. Log xuất hiện tại `Bai7\data\logs\test_client-1.txt`. ID chứa ký tự không hợp lệ bị từ chối trước khi tạo file.

## Bài 8: Truyền file TCP an toàn

Header dạng `FILE tên_không_khoảng_trắng kích_thước_long sha256`, theo sau là đúng số byte đã khai báo. Server giới hạn file ở 1 GiB, chỉ lưu trong thư mục `upload` bên trong thư mục chạy server, dùng file tạm và chỉ công bố file sau khi SHA-256 khớp. Tên rỗng, tên có `..`, `/`, `\`, `:` hoặc đường dẫn sẽ bị từ chối; không nhận đường dẫn lưu từ client.

Chạy server:

```powershell
Push-Location Bai8
java -cp out tcp.SafeFileServer
```

Gửi một file:

```powershell
java -cp Bai8\out tcp.SafeFileClient "C:\duong-dan\file.txt"
```

Kiểm thử file rỗng, văn bản tiếng Việt, PNG/ảnh và tên traversal:

```powershell
java -cp Bai8\out tcp.SafeFileClient "C:\duong-dan\file-rong.txt"
java -cp Bai8\out tcp.SafeFileClient "C:\duong-dan\van-ban-tieng-viet.txt"
java -cp Bai8\out tcp.SafeFileClient "C:\duong-dan\hinh.png"
java -cp Bai8\out tcp.SafeFileClient "C:\duong-dan\file.txt" "../escape.txt"
```

Ba file hợp lệ nhận `OK tên-lưu`; tên traversal nhận `ERR INVALID_FILENAME`. Upload nằm trong `Bai8\upload`.

## Bài 9: Multicast thông báo

Mặc định sender phát tới `239.255.0.1:5009`, TTL 1. Receiver chọn rõ `NetworkInterface`, join group và leave group trước khi đóng socket. Liệt kê tên interface bằng:

```powershell
java -cp Bai9\out multicast.MulticastNetwork
```

Mở hai receiver (mỗi terminal một receiver), thay `<interface>` bằng tên interface hỗ trợ multicast:

```powershell
java -cp Bai9\out multicast.MulticastReceiver <interface> 5009 1
```

Gửi thông báo:

```powershell
java -cp Bai9\out multicast.MulticastSender "Thông báo thử" <interface> 5009
```

Wi-Fi, VPN hoặc firewall có thể chặn multicast. Fallback localhost không cần multicast hay interface; mở một terminal receiver và một terminal sender:

```powershell
java -cp Bai9\out multicast.MulticastReceiver --localhost 5009 1
java -cp Bai9\out multicast.MulticastSender --localhost "Thông báo localhost"
```

## Bài 10: UDP discovery kết hợp TCP service

Server lắng nghe `DISCOVER_SERVICE` trên UDP 5010 và quảng bá `SERVICE tên_dịch_vụ tcp_port phiên_bản`; TCP service nhận `PING`. Client chờ tối đa 1500 ms, loại phản hồi trùng theo IP/port/tên/phiên bản, hiển thị từng server và kết nối TCP theo địa chỉ nguồn của datagram.

Chạy server:

```powershell
Push-Location Bai10
java -cp out discovery.DiscoveryServiceServer
```

Mặc định client gửi broadcast; dùng đối số `127.0.0.1` để kiểm thử chắc chắn trên máy local:

```powershell
java -cp Bai10\out discovery.ServiceDiscoveryClient
java -cp Bai10\out discovery.ServiceDiscoveryClient 127.0.0.1 5010 1500
```

Khi tìm thấy service, client kết nối TCP và in phản hồi `PONG`. Có thể chạy nhiều server trên các máy khác nhau cùng mạng; broadcast phụ thuộc cấu hình mạng và firewall.
