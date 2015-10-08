package GroupManagement;
import java.io.*;
import java.rmi.*;

public interface NameServerInterface extends Remote, Serializable {

    public String registerChatClient(String Client) throws RemoteException;
    public void createGroup(String goupName) throws RemoteException;
    public void deleteGroup(String groupName) throws RemoteException;
    public void updateGroupLeaderInfo(String Groupname) throws RemoteException;
    public String getGroupLeaderInfo() throws RemoteException;
}
