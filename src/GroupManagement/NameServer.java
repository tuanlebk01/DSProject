package GroupManagement;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;

public class NameServer implements NameServerInterface {

	private static final long serialVersionUID = 1L;
	private NameServerInterface nameServer;
	private GroupLeaderInterface groupLeader;
	private static String Name = "NamingService";
	private HashMap<String, ArrayList<String>> groupList = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<String>> groupLeaderInfo = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<String>> clientInfo = new HashMap<String, ArrayList<String>>();

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
		Map<String, ArrayList<String>> myMap = new HashMap<String, ArrayList<String>>();
		ArrayList<String> list = new ArrayList<String>();
		list.add("client1");
		list.add("client2");
		myMap.put("group1", list); // stores list containing instances #1 and #2 under key "key1"
		ArrayList<String> list2 = new ArrayList<String>();
		list2.add("client3");
		myMap.put("group2", list2); // stores list2 containing instance #3 under key "key2"

		String obj1 = myMap.get("group1").get(1); // returns instance #1
		ArrayList<String> obj2 = myMap.get("group1");
		//myMap.remove("group2");
		ArrayList<String> key = myMap.get(1);
		ArrayList<String> obj3 = myMap.get("group2");

		System.out.println(obj1 + obj2 + obj3 + key);
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
	public void getGroupLeaderInfo() throws RemoteException {

	}

	@Override
	public void updateGroupLeaderInfo(String Groupname) throws RemoteException {
		// TODO Auto-generated method stub

	}

	public void registerChatClient(ClientInterface name) throws RemoteException, ServerNotActiveException  {
		System.out.println(RemoteServer.getClientHost());
		//this.get.add(name);
	}

	public void createGroup(String groupName, String name)
			throws RemoteException {

		int portNumber = 8080;
		ArrayList<String> clientList = new ArrayList<String>();
		ArrayList<String> info = new ArrayList<String>();
		for (String groupN : this.groupList.keySet()) {
			if (groupN == groupName) {
				System.out.println("The group existed");
				return;
			}
		}

		this.groupLeader = (GroupLeaderInterface) UnicastRemoteObject
				.exportObject(this, 0);
		portNumber++;
		// register a name service
		Registry registry = LocateRegistry.createRegistry(portNumber);
		try {
			registry.bind(groupName, groupLeader);
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
		// add this client to this group
		clientList.add(name); // add this client to the group
		// add port number and naming service to this group info
		info.add(groupName);
		String port = String.valueOf(portNumber);
		info.add(port);
		this.groupList.put(groupName, info);
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
		//ArrayList<ClientInterface> clientList = this.groupList.get(groupName);

	}
}
