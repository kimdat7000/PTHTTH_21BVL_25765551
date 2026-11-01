import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface ContactService extends Remote {
    void addContact(String name, String phone) throws RemoteException;
    String findContact(String name) throws RemoteException;
    boolean deleteContact(String name) throws RemoteException;
    boolean updateContact(String name, String newPhone) throws RemoteException;
    Map<String, String> getAllContacts() throws RemoteException;
}
