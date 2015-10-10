package GroupManagement;
import java.io.*;
import java.rmi.*;

public interface NameServerInterface extends Remote, Serializable {

    public void registerChatClient(ClientInterface name) throws RemoteException;
    public void createGroup(GroupLeaderInterface goupName, ClientInterface client) throws RemoteException;
    public void deleteGroup(GroupLeaderInterface groupName) throws RemoteException;
    public void addMember(GroupLeaderInterface groupName,ClientInterface member) throws RemoteException;
    public void removeMember(GroupLeaderInterface groupName, ClientInterface member) throws RemoteException;
    public ClientInterface getMemberOfGroup(GroupLeaderInterface groupName) throws RemoteException;
    public void updateGroupLeaderInfo(String Groupname) throws RemoteException;
    public void getGroupLeaderInfo() throws RemoteException;
}
