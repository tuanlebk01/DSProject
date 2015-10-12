package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;

public interface ClientInterface extends Remote {
	public void retrieveMessage(String message) throws RemoteException;
	public void connectToNameServer(String userName, int portNr) throws RemoteException, AlreadyBoundException, ServerNotActiveException, NotBoundException;
	public void connectToNameServer() throws RemoteException, AlreadyBoundException;
	public void createGroup(String groupName, String userName) throws RemoteException;

}
