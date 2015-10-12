package GroupManagement;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;

public class NameServer extends RemoteServer implements NameServerInterface {

	private static final long serialVersionUID = 1L;
	private NameServerInterface nameServer;
	private GroupLeaderInterface groupLeader;
	private static String Name = "NamingService";
	private HashMap<String, String> LeaderInfo = new HashMap<String, String>();
	private HashMap<String, ArrayList<String>> MemberInGroup = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<String>> ClientInfo = new HashMap<String, ArrayList<String>>();

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
	public void getGroupLeaderInfo() throws RemoteException {

	}

	@Override
	public void updateGroupLeaderInfo(String Groupname) throws RemoteException {
		// TODO Auto-generated method stub

	}

	public void registerChatClient1(String name) throws RemoteException {
		this.clientList.add(name);
		try {
			System.out.println(getClientHost());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void createGroup(String groupName, String userName)
			throws RemoteException {

		ArrayList<String> clientList = new ArrayList<String>();
		String address = getClientHost();

		for (String groupN : this.LeaderInfo.keySet()) {
			if (groupN == groupName) {
				System.out.println("The group existed");
				return;
			}
		}

		try {
			String hostAddress = getClientHost();
		} catch (Exception e) {
			// TODO: handle exception
		}

		LeaderInfo.put(groupName, userName);
		clientList.add(userName);
		MemberInGroup.put(groupName,clientList);


		System.out.println("Naming Service Running on port " + portNumber);
		System.out.println("Group Leader " + groupName + " Started");
	}

	public void deleteGroup(String groupName)
			throws RemoteException {
		this.groupList.remove(groupName);
	}

	public void addMember(String groupName, String member)
			throws RemoteException {
		ArrayList<String> clientList = this.groupList.get(groupName);
		clientList.add(member);
		this.groupList.put(groupName, clientList);
	}

	public void removeMember(String groupName,
			String member) throws RemoteException {
		ArrayList<String> clientList = this.groupList.get(groupName);
		clientList.remove(member);
		this.groupList.put(groupName, clientList);

	}

	public ArrayList<String> getMemberOfGroup(String groupName)
			throws RemoteException {
		ArrayList<String> clientList = this.groupList.get(groupName);
		return (ArrayList<String>) clientList;


	}

	@Override
	public void registerChatClient(String name) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteGroup(GroupLeaderInterface groupName)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

}
