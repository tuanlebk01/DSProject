import java.io.*;
import java.rmi.*;

public interface NS extends Remote, Serializable {

    public String connect(String member) throws RemoteException;

}
