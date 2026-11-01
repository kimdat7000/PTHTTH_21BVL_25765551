import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatService extends Remote {
    // Phương thức theo yêu cầu đề bài
    void sendMessage(String message) throws RemoteException;

    // Phương thức giúp client nhận phản hồi từ server
    String getResponse() throws RemoteException;
}
