import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
 
public class RemoteObject extends UnicastRemoteObject implements CommonInterface {
 
    public RemoteObject() throws RemoteException {
        super(); //UnicastRemoteObject throws java.rmi.RemoteException
    }
     
    @Override
    public String method1(Object arg) throws RemoteException{
        //Do Something
        return "Successfully Invoked Method1";
    }
     
    @Override
    public void method2(Object arg) throws RemoteException{
        String response = method3(arg);
        System.out.println(response);
        System.out.println("Successfully Invoked Method2");
    }
     
    private String method3(Object arg) throws RemoteException{
        //Do Something
        return "Successfully Invoked Method3";
    }
}