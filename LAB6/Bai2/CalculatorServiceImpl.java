import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CalculatorServiceImpl extends UnicastRemoteObject implements CalculatorService {
    public CalculatorServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public int add(int a, int b) throws RemoteException {
        int sum = a + b;
        System.out.println("[Server] Nhận yêu cầu tính tổng: " + a + " + " + b + " = " + sum);
        return sum;
    }
}
