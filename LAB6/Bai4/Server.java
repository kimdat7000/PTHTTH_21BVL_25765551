import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

class ContactServiceImpl extends UnicastRemoteObject implements ContactService {
    // Lưu trữ danh bạ bằng HashMap theo yêu cầu đề bài
    private final Map<String, String> phoneBook = new HashMap<>();

    public ContactServiceImpl() throws RemoteException {
        super();
        // Dữ liệu mẫu ban đầu
        phoneBook.put("Nguyen Van A", "0901234567");
        phoneBook.put("Tran Thi B", "0912345678");
        phoneBook.put("Le Van C", "0987654321");
    }

    @Override
    public synchronized void addContact(String name, String phone) throws RemoteException {
        phoneBook.put(name, phone);
        System.out.println("[Server] Đã thêm/cập nhật liên hệ: " + name + " -> " + phone);
    }

    @Override
    public synchronized String findContact(String name) throws RemoteException {
        System.out.println("[Server] Tìm kiếm liên hệ: " + name);
        return phoneBook.get(name);
    }

    @Override
    public synchronized boolean deleteContact(String name) throws RemoteException {
        if (phoneBook.containsKey(name)) {
            phoneBook.remove(name);
            System.out.println("[Server] Đã xóa liên hệ: " + name);
            return true;
        }
        System.out.println("[Server] Xóa thất bại, không tìm thấy: " + name);
        return false;
    }

    @Override
    public synchronized boolean updateContact(String name, String newPhone) throws RemoteException {
        if (phoneBook.containsKey(name)) {
            phoneBook.put(name, newPhone);
            System.out.println("[Server] Đã cập nhật số điện thoại cho " + name + " thành: " + newPhone);
            return true;
        }
        return false;
    }

    @Override
    public synchronized Map<String, String> getAllContacts() throws RemoteException {
        return new HashMap<>(phoneBook);
    }
}

public class Server {
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "ContactService";

    public static void main(String[] args) {
        try {
            ContactService service = new ContactServiceImpl();

            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
                System.out.println("RMI Registry đã được tạo trên cổng " + PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry("localhost", PORT);
                System.out.println("Kết nối tới RMI Registry đã có trên cổng " + PORT);
            }

            registry.rebind(SERVICE_NAME, service);
            System.out.println("Bài 4 - PhoneBook Server đã sẵn sàng phục vụ (" + SERVICE_NAME + ")...");
        } catch (Exception e) {
            System.err.println("Lỗi Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
