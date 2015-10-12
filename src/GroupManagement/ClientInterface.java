package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
	public void retrieveMessage(String message) throws RemoteException;
	public void connectToNameServer() throws RemoteException, AlreadyBoundException;
	public void createGroup(String groupName, String userName) throws RemoteException;
}
