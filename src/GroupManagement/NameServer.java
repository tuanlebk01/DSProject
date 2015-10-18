package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NameServer extends RemoteServer implements NameServerInterface {

	private static final long serialVersionUID = 1L;
	private NameServerInterface nameServer;
	private static String Name = "NamingService";
	private HashMap<String, String> leaderInfo = new HashMap<String, String>();
	// the first column is ClientID, the second column is user name
	private HashMap<Integer, String> ClientInfo = new HashMap<Integer, String>();
	private HashMap<String, ArrayList<String>> groupInfo = new HashMap<String, ArrayList<String>>();
	private int clientID = 0;

	public static void main(String args[]) {
		try {

			new NameServer();

		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public NameServer() throws RemoteException, AlreadyBoundException {
		bind();
	}

	private void bind() throws RemoteException, AlreadyBoundException {

		int port = 1111;
		this.nameServer = (NameServerInterface) UnicastRemoteObject
				.exportObject(this, 0);
		Registry registry = LocateRegistry.createRegistry(port);
		registry.bind(NameServer.Name, nameServer);
		System.out.println("NS: Naming Service Running on port " + port);

		ArrayList<String> tempList = new ArrayList<String>();

		if (!(groupInfo.containsKey("Group 1"))) {

			tempList.add("User 1");
			tempList.add("User 2");
			tempList.add("User 3");
			leaderInfo.put("Group 1", "User 1");
			groupInfo.put("Group 1", tempList);
		}

		tempList = new ArrayList<String>();

		if (!(groupInfo.containsKey("Group 2"))) {
			tempList.add("User 4");
			tempList.add("User 5");
			tempList.add("User 6");
			leaderInfo.put("Group 2", "User 4");
			groupInfo.put("Group 2", tempList);
		}
	}

	public int registerChatClient(String userName) throws RemoteException,
			ServerNotActiveException {

		clientID++;
		ClientInfo.put(clientID, userName);

		this.ClientInfo.put(clientID, userName);
		System.out.println("NS: Connected: " + ClientInfo.get(clientID));
		return clientID;
	}

	public boolean createGroup(String groupName, String userName)
			throws RemoteException, ServerNotActiveException {

		ArrayList<String> tempList = new ArrayList<String>();

		if (!groupInfo.containsKey(groupName)) {
			tempList.add(userName);
			groupInfo.put(groupName, tempList);
			leaderInfo.put(groupName, userName);
			System.out.println("NS: Group created with name: " + groupName);
			System.out.println("Groups: " + groupInfo.keySet());
			return true;
		}
		return false;
	}

	public boolean addMember(String groupName, String userName)
			throws RemoteException {

		ArrayList<String> tempList = groupInfo.get(groupName);

		if (!(tempList.contains(userName))) {
			tempList.add(userName);
			groupInfo.put(groupName, tempList);
			return true;
		}
		return false;
	}

	public void removeMemberFromGroup(String groupName, String userName)
			throws RemoteException {

		ArrayList<String> clientList = groupInfo.get(groupName);

		clientList.remove(userName);
		groupInfo.put(groupName, clientList);

		System.out.println("NS: User: " + userName + " left group: " + groupName);
	}

	public HashMap<String, ArrayList<String>> getGroupsInfo() {

		return groupInfo;
	}

	public HashMap<String, String> getGroupLeaders() {

		return leaderInfo;
	}

	public void leaveServer(String groupName, int ID) throws RemoteException {


		if(groupName == null) {

			System.out.println("NS: The following user left the server: " + ClientInfo.get(ID));
			ClientInfo.remove(ID);

		} else {
			System.out.println("NS: Leader: " + ClientInfo.get(ID) + " left in group: " + groupName + ". Start election");
			ClientInfo.remove(ID);

		}
	}

	public void removeGroup(String groupName) throws RemoteException {

		groupInfo.remove(groupName);
		System.out.println("Group: " + groupName + " was removed");

	}

	public HashMap<Integer, String> getClientInfo() {
		return ClientInfo;
	}

	@Override
	public void updateGroupLeaderInfo(String Groupname) throws RemoteException {

	}

	@Override
	public ArrayList<String> getMemberInGroup(String groupName)
			throws RemoteException {
		return null;
	}

}
