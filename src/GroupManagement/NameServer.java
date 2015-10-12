package GroupManagement;

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;

import sun.security.krb5.internal.HostAddress;

public class NameServer extends RemoteServer implements NameServerInterface {

	private static final long serialVersionUID = 1L;
	private NameServerInterface nameServer;
	private GroupLeaderInterface groupLeader;
	private static String Name = "NamingService";

	private HashMap<String, String> LeaderInfo = new HashMap<String, String>();
	private HashMap<String, ArrayList<String>> MemberInGroup = new HashMap<String, ArrayList<String>>();
	private HashMap<String, String> ClientInfo = new HashMap<String, String>();

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

		/*
		 * Map<String, ArrayList<String>> myMap = new HashMap<String,
		 * ArrayList<String>>(); ArrayList<String> list = new
		 * ArrayList<String>(); list.add("client1"); list.add("client2");
		 * myMap.put("group1", list); // stores list containing instances #1 and
		 * #2 under key "key1" ArrayList<String> list2 = new
		 * ArrayList<String>(); list2.add("client3"); myMap.put("group2",
		 * list2); // stores list2 containing instance #3 under key "key2"
		 *
		 * String obj1 = myMap.get("group1").get(1); // returns instance #1
		 * ArrayList<String> obj2 = myMap.get("group1");
		 * //myMap.remove("group2"); ArrayList<String> key = myMap.get(1);
		 * ArrayList<String> obj3 = myMap.get("group2");
		 *
		 * System.out.println(obj1 + obj2 + obj3 + key);
		 */

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
		// TODO Auto-generated method stub

	}

	public ArrayList<String> registerChatClient1(String userName) throws RemoteException,
			ServerNotActiveException {

		String hostAddress = getClientHost();
		this.ClientInfo.put(userName, hostAddress);
		return getGroupList();
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
		ArrayList<String> clientList = this.MemberInGroup.get(groupName);
		return (ArrayList<String>) clientList;

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
		ArrayList<String> groupList = new ArrayList<String>();
		for (String groupN : this.MemberInGroup.keySet()) {
			groupList.add(groupN);
		}
		return groupList;
	}
}
