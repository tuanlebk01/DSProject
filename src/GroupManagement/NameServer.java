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
	private GroupLeaderInterface groupLeader;
	private static String Name = "NamingService";

	private HashMap<String, String> LeaderInfo = new HashMap<String, String>();
	private HashMap<String, ArrayList<String>> MemberInGroup = new HashMap<String, ArrayList<String>>();
	private HashMap<String, String> ClientInfo = new HashMap<String, String>();
	private ArrayList<String> groupList = new ArrayList<String>();
	private ArrayList<String> clientList = new ArrayList<String>();
	
	public NameServer() throws RemoteException, AlreadyBoundException {
		bind();
		System.out.println("Naming Service Started");
	}

	private void bind() throws RemoteException, AlreadyBoundException {
		int port = 1111;
		this.nameServer = (NameServerInterface) UnicastRemoteObject
				.exportObject(this, 0);
		Registry registry = LocateRegistry.createRegistry(port);
		registry.bind(NameServer.Name, nameServer);
		System.out.println("Naming Service Running on port " + port);

	}

	public static void main(String args[]) {
		try {

			new NameServer();

		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateGroupLeaderInfo(String Groupname) throws RemoteException {

	}

	public void registerChatClient1(String userName) throws RemoteException,
			ServerNotActiveException {

		System.out.println("Connected: " + getClientHost());

		String hostAddress = getClientHost();
		this.ClientInfo.put(userName, hostAddress);
	}

	public void createGroup(String groupName, String userName)
			throws RemoteException, ServerNotActiveException {

		ArrayList<String> clientList = new ArrayList<String>();

		for (String groupN : this.LeaderInfo.keySet()) {
			if (groupN == groupName) {
				System.out.println("The group existed");
				return;
			}
		}

		this.LeaderInfo.put(groupName, userName);
		clientList.add(userName);
		this.MemberInGroup.put(groupName, clientList);

		System.out.println("Group Leader of " + groupName + " is created");
	}

	public void deleteGroup(String groupName) throws RemoteException {
		this.LeaderInfo.remove(groupName);
		this.MemberInGroup.remove(groupName);
	}

	public void addMember(String groupName, String userName)
			throws RemoteException {
		ArrayList<String> clientList = getMemberInGroup(groupName);
		for (String groupN : clientList) {
			if (groupN == userName) {
				System.out.println("The user name existed");
				return;
			}
		clientList.add(userName);
		}
	}

	public void removeMember(String groupName, String userName)
			throws RemoteException {
		ArrayList<String> clientList = getMemberInGroup(groupName);
		clientList.remove(userName);
		this.MemberInGroup.put(groupName,clientList);
	}

	public ArrayList<String> getMemberInGroup(String groupName) {
		System.out.println(this.MemberInGroup.get(groupName));
//		ArrayList<String> clientList = this.MemberInGroup.get(groupName);
		clientList.add("User 2");
		clientList.add("User 3");
		clientList.add("User 4");

		return clientList;

	}

	@Override
	public void deleteGroup(GroupLeaderInterface groupName)
			throws RemoteException {

	}


	@Override
	public void getLeaderInfo() throws RemoteException {
		// TODO Auto-generated method stub

	}

	public ArrayList<String> getGroupList() {

		for (String groupN : this.MemberInGroup.keySet()) {
			groupList.add(groupN);
		}

		groupList.add("Group 1");
		groupList.add("Group 2");
		groupList.add("Group 3");

		return groupList;
	}

	public ArrayList<String> getGroupLists() {

		return groupList;
	}
}
