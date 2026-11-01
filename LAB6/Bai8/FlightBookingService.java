import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface FlightBookingService extends Remote {
    boolean bookTicket(String flightNumber, int seats) throws RemoteException;
    int availableSeats(String flightNumber) throws RemoteException;
    boolean cancelBooking(String flightNumber, int seats) throws RemoteException;
    Map<String, Integer> getAllFlights() throws RemoteException;
}
