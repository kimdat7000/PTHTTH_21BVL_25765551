import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.NumberFormat;
import java.util.Locale;

public class AuctionCallbackImpl extends UnicastRemoteObject implements AuctionCallback {
    private final String clientUsername;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));

    public AuctionCallbackImpl(String clientUsername) throws RemoteException {
        super();
        this.clientUsername = clientUsername;
    }

    @Override
    public void notifyNewBid(String productId, String productName, double newBid, String bidderName) throws RemoteException {
        System.out.println();
        System.out.println("=============================================================");
        System.out.println("🔔 [THÔNG BÁO THỜI GIAN THỰC - GIÁ THẦU MỚI]");
        System.out.printf("   Sản phẩm      : [%s] %s\n", productId, productName);
        System.out.printf("   Giá thầu mới  : %s\n", currencyFormatter.format(newBid));
        System.out.printf("   Người trả giá : %s %s\n", bidderName, bidderName.equals(clientUsername) ? "(Bạn)" : "");
        System.out.println("=============================================================");
        System.out.print("> Lựa chọn của bạn: ");
    }

    @Override
    public void notifyNewProduct(Product product) throws RemoteException {
        System.out.println();
        System.out.println("=============================================================");
        System.out.println("📢 [SẢN PHẨM MỚI LÊN SÀN ĐẤU GIÁ]");
        System.out.printf("   Sản phẩm      : [%s] %s\n", product.getId(), product.getName());
        System.out.printf("   Giá khởi điểm : %s\n", currencyFormatter.format(product.getInitialPrice()));
        System.out.printf("   Người bán     : %s\n", product.getSeller());
        System.out.println("=============================================================");
        System.out.print("> Lựa chọn của bạn: ");
    }
}
