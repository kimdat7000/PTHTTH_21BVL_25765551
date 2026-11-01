import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuctionCallback extends Remote {
    void notifyNewBid(String productId, String productName, double newBid, String bidderName) throws RemoteException;
    void notifyNewProduct(Product product) throws RemoteException;
}
