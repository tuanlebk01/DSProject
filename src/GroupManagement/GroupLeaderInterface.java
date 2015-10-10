package GroupManagement;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GroupLeaderInterface extends Remote, Serializable {
	public void joinGroup(String userName, String groupName) throws RemoteException;
	public void leaveGroup(String userName, String groupName) throws RemoteException;
	public void broadcastMessage(String message) throws RemoteException;
}
