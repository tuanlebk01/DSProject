package GroupManagement;

import Communication.TextMessage;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.HashMap;

public interface ClientInterface extends Remote, Serializable {

	public void addMessageToQueue (TextMessage message) throws RemoteException;

	public int connectToNameServer(String userName, String host, int portNr)
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

	public void disconnect(String groupName, String userName) throws RemoteException, java.rmi.NotBoundException;

	public HashMap<Integer, ClientInterface> getInterfaceOfGroup()
			throws RemoteException;
	public void setClientInterfaces(HashMap<Integer, ClientInterface> clientInterfaces) throws RemoteException;

	public void sharegroup(int option) throws RemoteException, NotBoundException;

	public HashMap<String, ArrayList<String>> getGroupsInfo() throws RemoteException, NotBoundException;

	public void setClientList(ArrayList<Triple> clients) throws RemoteException;

	public void addClientInterface(Triple triple) throws RemoteException;

	public void removeClientInterface(Triple triple) throws RemoteException;

    public void setNewLeader(String leader) throws RemoteException;

    public ArrayList<Triple> getClientlist(String groupName) throws RemoteException;

	public ArrayList<String> getListOfClientsInMyGroup() throws RemoteException;

	public HashMap<String, ArrayList<String>> askNSforGroupsInfo() throws RemoteException;

	public ArrayList<Triple> getClientListFromMember() throws RemoteException;

    public HashMap<Integer, Integer> getLastAcceptedSeqNr() throws RemoteException;

    public void updateIpOfLeader(String Ip) throws RemoteException;
    
    public void registryNewLeader() throws RemoteException;
    
    public void handleError(String userName) throws RemoteException, NotBoundException;
    
    public void shareGroupForCrashedInfo(String crashedUserName) throws RemoteException, NotBoundException;
    
    public void notifyOthers() throws RemoteException, NotBoundException;

	public void updateGroupList(String value) throws RemoteException;
	
	public void setValue(String value) throws RemoteException;
	
	public void addMemberToListOfClientsInMyGroup(String member) throws RemoteException;
	
	public void removeMemberFromListOfClientsInMyGroup(String member) throws RemoteException;
    
}
