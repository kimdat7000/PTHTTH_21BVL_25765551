import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

// Interface dịch vụ Đấu giá RMI chính
public interface AuctionService extends Remote {

    // 1. Model đối tượng sản phẩm đấu giá (public static trong interface để Dynamic Proxy của JDK 17+ truy cập an toàn)
    public static class Product implements Serializable {
        private static final long serialVersionUID = 1L;

        private String id;
        private String name;
        private double initialPrice;
        private double currentHighestBid;
        private String highestBidder;
        private String seller;

        public Product(String id, String name, double initialPrice, String seller) {
            this.id = id;
            this.name = name;
            this.initialPrice = initialPrice;
            this.currentHighestBid = initialPrice;
            this.highestBidder = "(Chưa có)";
            this.seller = seller;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getInitialPrice() {
            return initialPrice;
        }

        public synchronized double getCurrentHighestBid() {
            return currentHighestBid;
        }

        public synchronized void setCurrentHighestBid(double currentHighestBid) {
            this.currentHighestBid = currentHighestBid;
        }

        public synchronized String getHighestBidder() {
            return highestBidder;
        }

        public synchronized void setHighestBidder(String highestBidder) {
            this.highestBidder = highestBidder;
        }

        public String getSeller() {
            return seller;
        }

        public String getFormattedInfo() {
            NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
            return String.format("[%s] %-20s | Khởi điểm: %-15s | Giá cao nhất: %-15s | Người trả cao nhất: %-15s | Người bán: %s",
                    id, name, fmt.format(initialPrice), fmt.format(currentHighestBid), highestBidder, seller);
        }

        @Override
        public String toString() {
            return getFormattedInfo();
        }
    }

    // 2. Interface Callback nhận thông báo đẩy thời gian thực từ Server
    public static interface AuctionCallback extends Remote {
        void notifyNewBid(String productId, String productName, double newBid, String bidderName) throws RemoteException;
        void notifyNewProduct(Product product) throws RemoteException;
    }

    // 3. Dành cho Chủ sở hữu (Seller): Thêm sản phẩm vào hệ thống
    Product addProduct(String name, double startingPrice, String sellerName) throws RemoteException;

    // 4. Dành cho Bidder & Seller: Xem danh sách sản phẩm
    List<Product> getProducts() throws RemoteException;
    Product getProduct(String productId) throws RemoteException;

    // 5. Dành cho Người đấu giá (Bidder): Đưa ra giá thầu
    boolean placeBid(String productId, double bidAmount, String bidderName) throws RemoteException;

    // 6. Đăng ký & Hủy đăng ký nhận thông báo thời gian thực
    void registerCallback(AuctionCallback callback) throws RemoteException;
    void unregisterCallback(AuctionCallback callback) throws RemoteException;
}
