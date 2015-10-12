package GroupManagement;
import java.io.*;
import java.rmi.*;
import java.util.ArrayList;

public interface NameServerInterface extends Remote, Serializable {

    public void registerChatClient(String name) throws RemoteException;
    public void createGroup(String goupName, String client) throws RemoteException;
    public void deleteGroup(GroupLeaderInterface groupName) throws RemoteException;
    public void addMember(String groupName,String member) throws RemoteException;
    public void removeMember(String groupName, String member) throws RemoteException;
    public ArrayList<String> getMemberOfGroup(String groupName) throws RemoteException;
    public void updateGroupLeaderInfo(String Groupname) throws RemoteException;
    public void getGroupLeaderInfo() throws RemoteException;
}
