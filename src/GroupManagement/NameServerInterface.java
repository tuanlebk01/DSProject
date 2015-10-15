package GroupManagement;

import java.io.*;
import java.rmi.*;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.HashMap;

public interface NameServerInterface extends Remote, Serializable {

	public int registerChatClient(String userName) throws RemoteException,
			ServerNotActiveException;

	public boolean createGroup(String goupName, String client)
			throws RemoteException, ServerNotActiveException;

	public boolean addMember(String groupName, String userName)
			throws RemoteException;

	public void removeMember(String groupName, String userName)
			throws RemoteException;

	public void updateGroupLeaderInfo(String Groupname) throws RemoteException;

	public HashMap<String, ArrayList<String>> getGroupsInfo()
			throws RemoteException;

	public ArrayList<String> getMemberInGroup(String groupName)
			throws RemoteException;

	public HashMap<String, String> getGroupLeaders() throws RemoteException;

}
