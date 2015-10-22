package GroupManagement;

import Communication.TextMessage;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.HashMap;

public interface ClientInterface extends Remote, Serializable {

	public void addMessageToQueue (TextMessage message) throws RemoteException;

	public int connectToNameServer(String userName, int portNr)
			throws RemoteException, AlreadyBoundException,
			ServerNotActiveException, NotBoundException, UnknownHostException;

	public boolean createGroup(String groupName, String userName)
			throws RemoteException, ServerNotActiveException,
			NotBoundException, AlreadyBoundException;

	public String joinGroup(String groupName, String leaderName)
			throws RemoteException, ServerNotActiveException, AlreadyBoundException, NotBoundException;

	public boolean connectToGroupLeader(String groupLeader) throws RemoteException, AlreadyBoundException, NotBoundException;

	public String startElection() throws RemoteException;

	public boolean addMemberToGroup(String userName) throws RemoteException;

	public void disconnect(String groupName, String userName) throws RemoteException;

	public void removeFromGroup(String groupName, String userName) throws RemoteException;

	public HashMap<Integer, ClientInterface> getInterfaceOfGroup()
			throws RemoteException;
	public void setClientInterfaces(HashMap<Integer, ClientInterface> clientInterfaces) throws RemoteException;

	public void sharegroup() throws RemoteException;

	public HashMap<String, ArrayList<String>> getGroupsInfo() throws RemoteException;

	public void setClientList(ArrayList<Triple> clients) throws RemoteException;

	public void addClientInterface(Triple triple) throws RemoteException;

	public void removeClientInterface(Triple triple) throws RemoteException;

    public void setNewLeader(String leader);

	public ArrayList<Triple> getClientlist(String groupName)throws RemoteException;


}
