package GroupManagement;
import java.io.*;
import java.rmi.*;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;

public interface NameServerInterface extends Remote, Serializable {

    public void registerChatClient(String userName) throws RemoteException, ServerNotActiveException;
    public void createGroup(String goupName, String client) throws RemoteException;
    public void deleteGroup(GroupLeaderInterface groupName) throws RemoteException;
    public void addMember(String groupName,String member) throws RemoteException;
    public void removeMember(String groupName, String userName) throws RemoteException;
    public ArrayList<String> getMemberOfGroup(String groupName);
    public void updateGroupLeaderInfo(String Groupname) throws RemoteException;
    public void getLeaderInfo() throws RemoteException;
    public ArrayList<String> getGroupList();
}
