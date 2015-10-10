package GroupManagement;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;

public class NameServer implements NameServerInterface {

	private static final long serialVersionUID = 1L;
	private NameServerInterface nameServer;
	private static String Name = "NamingService";
	private HashMap<GroupLeaderInterface, ArrayList<ClientInterface>> groupList = new HashMap<GroupLeaderInterface, ArrayList<ClientInterface>>();
	private ArrayList<ClientInterface> clientList;

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

	public void registerChatClient(ClientInterface name) throws RemoteException {
		this.clientList.add(name);
	}

	public void createGroup(GroupLeaderInterface groupName, ClientInterface name)
			throws RemoteException {
		for (GroupLeaderInterface groupN : this.groupList.keySet()) {
			if (groupN == groupName) {
				System.out.println("The group existed");
				return;
			}
		}
		ArrayList<ClientInterface> clientList = new ArrayList<ClientInterface>();
		clientList.add(name);
		this.groupList.put(groupName, clientList);
	}

	public void deleteGroup(GroupLeaderInterface groupName)
			throws RemoteException {
		this.groupList.remove(groupName);
	}

	public void addMember(GroupLeaderInterface groupName, ClientInterface member)
			throws RemoteException {
		ArrayList<ClientInterface> clientList = this.groupList.get(groupName);
		clientList.add(member);
		this.groupList.put(groupName, clientList);
	}

	public void removeMember(GroupLeaderInterface groupName,
			ClientInterface member) throws RemoteException {
		ArrayList<ClientInterface> clientList = this.groupList.get(groupName);
		clientList.remove(member);
		this.groupList.put(groupName, clientList);

	}

	public ClientInterface getMemberOfGroup(GroupLeaderInterface groupName)
			throws RemoteException {
		ArrayList<ClientInterface> clientList = this.groupList.get(groupName);
		return (ClientInterface) clientList;
		
		
	}
}
