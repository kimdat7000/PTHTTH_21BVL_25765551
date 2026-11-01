import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface AuctionService extends Remote {

    // 1. Dành cho Chủ sở hữu (Seller): Thêm sản phẩm vào hệ thống đấu giá
    Product addProduct(String name, double startingPrice, String sellerName) throws RemoteException;

    // 2. Dành cho Bidder & Seller: Xem danh sách sản phẩm
    List<Product> getProducts() throws RemoteException;
    Product getProduct(String productId) throws RemoteException;

    // 3. Dành cho Người đấu giá (Bidder): Đưa ra giá thầu cao hơn giá hiện tại
    boolean placeBid(String productId, double bidAmount, String bidderName) throws RemoteException;

    // 4. Đăng ký & Hủy đăng ký nhận thông báo thời gian thực từ Server
    void registerCallback(AuctionCallback callback) throws RemoteException;
    void unregisterCallback(AuctionCallback callback) throws RemoteException;
}
