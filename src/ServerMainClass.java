import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerMainClass{
    public static void main(String[] args){
        try{
            Registry registry = LocateRegistry.createRegistry(1312);
            registry.rebind("ChatRoom", new RemoteObject());
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}