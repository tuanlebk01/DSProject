import java.rmi.Remote;
import java.rmi.RemoteException;
 
public interface CommonInterface extends java.rmi.Remote {
 
    public String method1(Object arg) throws RemoteException;
     
    public void method2(Object arg) throws RemoteException;
     
}