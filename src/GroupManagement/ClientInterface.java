package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.HashMap;

public interface ClientInterface extends Remote {
	public void retrieveMessage(String message) throws RemoteException;
	public int connectToNameServer(String userName, int portNr) throws RemoteException, AlreadyBoundException, ServerNotActiveException, NotBoundException;
	public void connectToNameServer() throws RemoteException, AlreadyBoundException;
	public boolean createGroup(String groupName, String userName) throws RemoteException, ServerNotActiveException;
	public HashMap<String, ArrayList<String>> getGroups() throws RemoteException;
	public void joinGroup(String groupName) throws RemoteException, ServerNotActiveException;

}
