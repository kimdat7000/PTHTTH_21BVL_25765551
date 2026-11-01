import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class AuctionServiceImpl extends UnicastRemoteObject implements AuctionService {
    private final Map<String, Product> productMap = Collections.synchronizedMap(new LinkedHashMap<>());
    private final List<AuctionCallback> callbacks = new CopyOnWriteArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(100);
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));

    public AuctionServiceImpl() throws RemoteException {
        super();
        initDefaultProducts();
    }

    private void initDefaultProducts() {
        Product p1 = new Product("SP" + idCounter.incrementAndGet(), "Laptop Dell XPS 15", 15000000.0, "Nguyen Van An");
        Product p2 = new Product("SP" + idCounter.incrementAndGet(), "iPhone 15 Pro Max 256GB", 20000000.0, "Tran Van Binh");
        Product p3 = new Product("SP" + idCounter.incrementAndGet(), "Đồng hồ Rolex Submariner", 50000000.0, "Le Thi Cam");

        productMap.put(p1.getId(), p1);
        productMap.put(p2.getId(), p2);
        productMap.put(p3.getId(), p3);
    }

    @Override
    public synchronized Product addProduct(String name, double startingPrice, String sellerName) throws RemoteException {
        String newId = "SP" + idCounter.incrementAndGet();
        Product product = new Product(newId, name, startingPrice, sellerName);
        productMap.put(newId, product);

        System.out.printf("[Server] [Seller: %s] đã thêm sản phẩm mới: [%s] %s | Giá khởi điểm: %s\n",
                sellerName, newId, name, currencyFormatter.format(startingPrice));

        // Thông báo bất đồng bộ để tránh nghẽn RMI
        CompletableFuture.runAsync(() -> notifyAllNewProduct(product));
        return product;
    }

    @Override
    public synchronized List<Product> getProducts() throws RemoteException {
        return new ArrayList<>(productMap.values());
    }

    @Override
    public synchronized Product getProduct(String productId) throws RemoteException {
        return productMap.get(productId);
    }

    @Override
    public synchronized boolean placeBid(String productId, double bidAmount, String bidderName) throws RemoteException {
        Product product = productMap.get(productId);
        if (product == null) {
            System.out.println("[Server] Đặt giá thất bại: Không tìm thấy sản phẩm " + productId);
            return false;
        }

        boolean isFirstBid = "(Chưa có)".equals(product.getHighestBidder());
        if (isFirstBid) {
            if (bidAmount < product.getCurrentHighestBid()) {
                System.out.printf("[Server] Đặt giá thất bại cho [%s] bởi %s: Mức giá %s thấp hơn giá khởi điểm %s\n",
                        productId, bidderName, currencyFormatter.format(bidAmount), currencyFormatter.format(product.getCurrentHighestBid()));
                return false;
            }
        } else {
            if (bidAmount <= product.getCurrentHighestBid()) {
                System.out.printf("[Server] Đặt giá thất bại cho [%s] bởi %s: Mức giá %s không cao hơn giá hiện tại %s\n",
                        productId, bidderName, currencyFormatter.format(bidAmount), currencyFormatter.format(product.getCurrentHighestBid()));
                return false;
            }
        }

        product.setCurrentHighestBid(bidAmount);
        product.setHighestBidder(bidderName);

        System.out.printf("[Server] [CẬP NHẬT GIÁ MỚI] [%s] %s: %s trả giá mới %s!\n",
                productId, product.getName(), bidderName, currencyFormatter.format(bidAmount));

        // Thông báo bất đồng bộ tới tất cả client
        CompletableFuture.runAsync(() -> notifyAllNewBid(productId, product.getName(), bidAmount, bidderName));
        return true;
    }

    @Override
    public void registerCallback(AuctionCallback callback) throws RemoteException {
        callbacks.add(callback);
        System.out.println("[Server] Đã có client đăng ký nhận thông báo thời gian thực (Tổng clients: " + callbacks.size() + ")");
    }

    @Override
    public void unregisterCallback(AuctionCallback callback) throws RemoteException {
        callbacks.remove(callback);
        System.out.println("[Server] Client hủy đăng ký thông báo (Còn lại: " + callbacks.size() + ")");
    }

    private void notifyAllNewBid(String productId, String productName, double newBid, String bidderName) {
        List<AuctionCallback> toRemove = new ArrayList<>();
        for (AuctionCallback cb : callbacks) {
            try {
                cb.notifyNewBid(productId, productName, newBid, bidderName);
            } catch (Exception e) {
                toRemove.add(cb);
            }
        }
        callbacks.removeAll(toRemove);
    }

    private void notifyAllNewProduct(Product product) {
        List<AuctionCallback> toRemove = new ArrayList<>();
        for (AuctionCallback cb : callbacks) {
            try {
                cb.notifyNewProduct(product);
            } catch (Exception e) {
                toRemove.add(cb);
            }
        }
        callbacks.removeAll(toRemove);
    }
}
