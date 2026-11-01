import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

class FlightBookingServiceImpl extends UnicastRemoteObject implements FlightBookingService {
    // Lưu số ghế trống hiện tại của từng chuyến bay
    private final Map<String, Integer> availableSeatsMap = new HashMap<>();
    // Lưu tổng số ghế ban đầu (sức chứa tối đa) của từng chuyến bay để không hủy vé vượt quá sức chứa
    private final Map<String, Integer> totalCapacityMap = new HashMap<>();

    public FlightBookingServiceImpl() throws RemoteException {
        super();
        initDefaultFlights();
    }

    private void initDefaultFlights() {
        addFlight("VN123", 50); // Hà Nội - TP.HCM
        addFlight("VJ456", 40); // TP.HCM - Đà Nẵng
        addFlight("QH789", 30); // Hà Nội - Phú Quốc
        addFlight("VN999", 25); // TP.HCM - Đà Lạt
    }

    private void addFlight(String flightNumber, int totalSeats) {
        availableSeatsMap.put(flightNumber.toUpperCase(), totalSeats);
        totalCapacityMap.put(flightNumber.toUpperCase(), totalSeats);
    }

    @Override
    public synchronized boolean bookTicket(String flightNumber, int seats) throws RemoteException {
        String key = flightNumber.toUpperCase();
        if (seats <= 0) {
            System.out.println("[Server] Đặt vé thất bại: Số ghế phải > 0");
            return false;
        }

        if (!availableSeatsMap.containsKey(key)) {
            System.out.println("[Server] Không tìm thấy chuyến bay: " + key);
            return false;
        }

        int currentAvailable = availableSeatsMap.get(key);
        if (currentAvailable >= seats) {
            int remaining = currentAvailable - seats;
            availableSeatsMap.put(key, remaining);
            System.out.printf("[Server] Đặt vé thành công chuyến %s: %d ghế. Còn lại: %d ghế.\n", key, seats, remaining);
            return true;
        } else {
            System.out.printf("[Server] Đặt vé thất bại chuyến %s: Yêu cầu %d ghế nhưng chỉ còn %d ghế.\n", key, seats, currentAvailable);
            return false;
        }
    }

    @Override
    public synchronized int availableSeats(String flightNumber) throws RemoteException {
        String key = flightNumber.toUpperCase();
        if (!availableSeatsMap.containsKey(key)) {
            return -1; // Không tìm thấy chuyến bay
        }
        int seats = availableSeatsMap.get(key);
        System.out.println("[Server] Kiểm tra ghế chuyến " + key + ": còn " + seats + " ghế.");
        return seats;
    }

    @Override
    public synchronized boolean cancelBooking(String flightNumber, int seats) throws RemoteException {
        String key = flightNumber.toUpperCase();
        if (seats <= 0) {
            System.out.println("[Server] Hủy vé thất bại: Số ghế phải > 0");
            return false;
        }

        if (!availableSeatsMap.containsKey(key)) {
            System.out.println("[Server] Không tìm thấy chuyến bay: " + key);
            return false;
        }

        int currentAvailable = availableSeatsMap.get(key);
        int maxCapacity = totalCapacityMap.get(key);

        if (currentAvailable + seats > maxCapacity) {
            System.out.printf("[Server] Hủy vé thất bại chuyến %s: Số ghế hủy vượt quá sức chứa ban đầu (%d ghế).\n", key, maxCapacity);
            return false;
        }

        int updatedSeats = currentAvailable + seats;
        availableSeatsMap.put(key, updatedSeats);
        System.out.printf("[Server] Hủy vé thành công chuyến %s: %d ghế. Còn lại: %d ghế.\n", key, seats, updatedSeats);
        return true;
    }

    @Override
    public synchronized Map<String, Integer> getAllFlights() throws RemoteException {
        return new HashMap<>(availableSeatsMap);
    }
}

public class Server {
    public static final int PORT = 1099;
    public static final String SERVICE_NAME = "FlightBookingService";

    public static void main(String[] args) {
        try {
            FlightBookingService service = new FlightBookingServiceImpl();

            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(PORT);
                System.out.println("RMI Registry đã được tạo trên cổng " + PORT);
            } catch (Exception e) {
                registry = LocateRegistry.getRegistry("localhost", PORT);
                System.out.println("Kết nối tới RMI Registry đã có trên cổng " + PORT);
            }

            registry.rebind(SERVICE_NAME, service);
            System.out.println("Bài 8 - Flight Booking Server đã sẵn sàng phục vụ (" + SERVICE_NAME + ")...");
        } catch (Exception e) {
            System.err.println("Lỗi Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
