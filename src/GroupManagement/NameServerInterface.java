package GroupManagement;

import java.io.*;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.HashMap;

public interface NameServerInterface extends Remote, Serializable {

	public int registerChatClient(String userName) throws RemoteException,
			ServerNotActiveException, UnknownHostException;

	public boolean createGroup(String goupName, String client)
			throws RemoteException, ServerNotActiveException;

	public boolean addMember(String groupName, String userName)
			throws RemoteException;

	public void removeMemberFromGroup(String groupName, String userName)
			throws RemoteException;

	public void updateGroupLeaderInfo(String Groupname) throws RemoteException;

	public HashMap<String, ArrayList<String>> getGroupsInfo()
			throws RemoteException;

	public HashMap<String, String> getGroupLeaders() throws RemoteException;

	public void leaveServer(String groupName, int ID) throws RemoteException;

	public void removeGroup(String groupName)throws RemoteException;

	public ArrayList<Triple> getClientList() throws RemoteException;

	public Triple getClientInfo() throws RemoteException;

	public void setClientInfo(Triple clientInfo) throws RemoteException;

	public ArrayList<Triple> getGroupTriples(String groupName) throws RemoteException;

}
