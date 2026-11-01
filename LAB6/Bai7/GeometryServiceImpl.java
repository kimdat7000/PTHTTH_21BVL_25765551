import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class GeometryServiceImpl extends UnicastRemoteObject implements GeometryService {

    public GeometryServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public double rectangleArea(double width, double height) throws RemoteException {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Chiều dài và chiều rộng không được âm!");
        }
        double area = width * height;
        System.out.printf("[Server] Tính diện tích hình chữ nhật (w=%.2f, h=%.2f) = %.4f\n", width, height, area);
        return area;
    }

    @Override
    public double circleArea(double radius) throws RemoteException {
        if (radius < 0) {
            throw new IllegalArgumentException("Bán kính hình tròn không được âm!");
        }
        double area = Math.PI * radius * radius;
        System.out.printf("[Server] Tính diện tích hình tròn (r=%.2f) = %.4f\n", radius, area);
        return area;
    }

    @Override
    public double triangleArea(double base, double height) throws RemoteException {
        if (base < 0 || height < 0) {
            throw new IllegalArgumentException("Đáy và chiều cao tam giác không được âm!");
        }
        double area = 0.5 * base * height;
        System.out.printf("[Server] Tính diện tích hình tam giác (base=%.2f, h=%.2f) = %.4f\n", base, height, area);
        return area;
    }
}
