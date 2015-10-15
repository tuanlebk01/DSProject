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

	public int connectToNameServer(String userName, int portNr)
			throws RemoteException, AlreadyBoundException,
			ServerNotActiveException, NotBoundException;

	public boolean createGroup(String groupName, String userName)
			throws RemoteException, ServerNotActiveException,
			NotBoundException, AlreadyBoundException;

	public HashMap<String, ArrayList<String>> getGroups()
			throws RemoteException;

	public String joinGroup(String groupName, String leaderName)
			throws RemoteException, ServerNotActiveException, AlreadyBoundException, NotBoundException;

	public void getUsersInGroup(String group) throws RemoteException;

	public void getUsersIPs(String group) throws RemoteException;

	public boolean connectToGroupLeader(String groupLeader) throws RemoteException, AlreadyBoundException, NotBoundException;

<<<<<<< HEAD
	public ArrayList<String> addMemberToGroup(String user)
			throws RemoteException;

	public void startElection() throws RemoteException;
=======
	public boolean addMemberToGroup(String user) throws RemoteException;

	public ArrayList<String> getGroupList(String myGroup) throws RemoteException;
>>>>>>> d7f29d7de72560490f2d3da304e0bf5d26074360

}
