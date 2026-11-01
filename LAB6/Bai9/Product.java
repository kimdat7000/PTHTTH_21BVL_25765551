import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

public class Product implements Serializable {
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
        return String.format("[%s] %-25s | Khởi điểm: %-15s | Giá cao nhất: %-15s | Người trả cao nhất: %-15s | Người bán: %s",
                id, name, fmt.format(initialPrice), fmt.format(currentHighestBid), highestBidder, seller);
    }

    @Override
    public String toString() {
        return getFormattedInfo();
    }
}
