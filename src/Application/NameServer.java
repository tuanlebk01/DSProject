package Application;
import java.io.*;
import java.rmi.*;

public interface NameServer extends Remote, Serializable {

    public String connect(String member) throws RemoteException;

}
