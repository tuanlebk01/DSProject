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

public class NameServer extends RemoteServer implements NameServerInterface {

	private static final long serialVersionUID = 1L;
	private NameServerInterface nameServer;
	private static String Name = "NamingService";

	private HashMap<String, String> leaderInfo = new HashMap<String, String>();
	private HashMap<String, Integer> ClientInfo = new HashMap<String, Integer>();
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

		this.ClientInfo.put(userName, clientID);
		System.out.println("NS: Connected: " + ClientInfo.get(userName));
		return clientID;
	}

	public boolean createGroup(String groupName, String userName)
			throws RemoteException, ServerNotActiveException {

		ArrayList<String> tempList = new ArrayList<String>();

		if (!groupInfo.containsKey(groupName)) {
			tempList.add(userName);
			groupInfo.put(groupName, tempList);
			leaderInfo.put(groupName, userName);

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

	public void removeMember(String groupName, String userName)
			throws RemoteException {
		ArrayList<String> clientList = getMemberInGroup(groupName);
		clientList.remove(userName);
		this.groupInfo.put(groupName, clientList);
	}

	public HashMap<String, ArrayList<String>> getGroupsInfo() {

		return groupInfo;
	}

	public HashMap<String, String> getGroupLeaders() {

		return leaderInfo;
	}

	@Override
	public ArrayList<String> getMemberInGroup(String groupName)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteGroup(String groupName) throws RemoteException {

		// If group is empty, remove

	}

	@Override
	public void updateGroupLeaderInfo(String Groupname) throws RemoteException {

	}

	@Override
	public HashMap<String, Integer> getClientInfo() throws RemoteException {
		return ClientInfo;
	}
}
