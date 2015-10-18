package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Application.GUI;
import Communication.CommunicationModule;

public class Client implements ClientInterface {

	private static final long serialVersionUID = 1L;
	private Registry registry;
	private NameServerInterface ns;
	private ClientInterface ci;
	private CommunicationModule cm;
	private HashMap<String, ArrayList<String>> groupsInfo = new HashMap<String, ArrayList<String>>();
	private HashMap<String, String> leaders = new HashMap<String, String>();
	private HashMap<Integer, ClientInterface> clientInterfaces = new HashMap<Integer, ClientInterface>();
	private HashMap<Integer, String> clientInfo = new HashMap<Integer, String>();
	private ArrayList<String> clients = new ArrayList<String>();
	private int clientID;
	private String myUserName;
	private String myGroup;
	private String myLeader;
	private boolean groupCreated;
	private boolean groupJoined;

	public Client() throws RemoteException {
		super();
	}

	public int connectToNameServer(String userName, int portNr)
			throws RemoteException, AlreadyBoundException,
			ServerNotActiveException, NotBoundException {

		this.myUserName = userName;

		this.registry = LocateRegistry.getRegistry("Bellatrix.cs.umu.se",
				portNr);
		this.ns = (NameServerInterface) registry.lookup("NamingService");
		clientID = ns.registerChatClient(userName);

		return clientID;

	}

	public boolean createGroup(String groupName, String userName)
			throws RemoteException, ServerNotActiveException,
			NotBoundException, AlreadyBoundException {

		this.myGroup = groupName;
		this.myLeader = userName;

		ci = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
		registry = LocateRegistry.createRegistry(1234);
		registry.bind(userName, ci);
		System.out.println("CLIENT: Groupleader: " + userName + " running on port " + "1234");

		registry = LocateRegistry.getRegistry("localhost", 1234);
		ci = (ClientInterface) registry.lookup(userName);

		groupCreated = ns.createGroup(groupName, userName);

		clientInfo = ns.getClientInfo();
		groupsInfo = ns.getGroupsInfo();

		return groupCreated;

	}

	public boolean connectToGroupLeader(String groupLeader) throws RemoteException, AlreadyBoundException, NotBoundException {

		registry = LocateRegistry.getRegistry("Bellatrix.cs.umu.se", 1234);
		ci = (ClientInterface) registry.lookup(groupLeader);
		System.out.println("CLIENT: connected to groupleader: " + groupLeader + " : with username: " + myUserName);
		groupJoined = ci.addMemberToGroup(myUserName);
		return groupJoined;

	}

	public boolean addMemberToGroup(String userName) throws RemoteException {
		groupJoined = ns.addMember(myGroup, userName);
		getGroupList(myGroup);
		return groupJoined;

	}

	public ArrayList<String> getGroupList(String myGroup) throws RemoteException {
		groupsInfo = getGroups();
		clients = groupsInfo.get(myGroup);
		clientInfo = ns.getClientInfo();
		return clients;
	}

	public HashMap<String, ArrayList<String>> getGroups()
			throws RemoteException {

		return groupsInfo = ns.getGroupsInfo();

	}

	public HashMap<String, String> getGroupLeaders() throws RemoteException {

		return leaders = ns.getGroupLeaders();

	}

	public String joinGroup(String groupName, String leaderName)
			throws RemoteException, ServerNotActiveException,
			AlreadyBoundException, NotBoundException {

		this.myGroup = groupName;
		this.myLeader = leaderName;
		System.out.println("CLIENT: Group: " + myGroup + " : Leader: "
				+ myLeader);
		connectToGroupLeader(myLeader);

		System.out.println("CLIENT: nr of clients in list: " + clients.size());


		clientInfo = ns.getClientInfo();
		ArrayList<Integer> temp = new ArrayList<Integer>();

		Iterator it = clientInfo.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			temp.add(Integer.parseInt(pair.getKey().toString()));
			it.remove();
		}



		for (int i = 0; i < temp.size(); i++) {
			System.out.println("ASDSA: " + temp.get(i));
			//LOOK AT THIS AND FIX IT SOMEHOW
//			ci = (ClientInterface) UnicastRemoteObject.exportObject(this);
//			clientInterfaces.put(temp.get(i), ci);
		}

		return myLeader;

	}

	public void retrieveMessage(String message) throws RemoteException {
		GUI.writeMsg(message);
	}

	public void broadcastMessage(String message) {


	}

<<<<<<< HEAD
	public void getUsersInGroup(String group) throws RemoteException {

	}

	public void getUsersIPs(String group) throws RemoteException {
		// TODO Auto-generated method stub

	}

	public String startElection(ArrayList<String> clients) throws RemoteException {
=======
	public void startElection() throws RemoteException {
>>>>>>> ac4c7cc6c50c85a98074d1956bae6e3bf7dc977c
		ArrayList<Integer> ID = new ArrayList<Integer>();
		ArrayList<String> tempClients = new ArrayList<String>();
		ArrayList<Integer> tempID = new ArrayList<Integer>();
		HashMap<Integer, String> clientInfo = ns.getClientInfo();
		HashMap<String, Integer> tempClientInfo = new HashMap<String, Integer>();
		String newLeaderName = null;

		clients.remove(myLeader);


		Iterator it = clientInfo.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry pair = (Map.Entry) it.next();
			// temp.add(Integer.parseInt(pair.getKey().toString()));
			System.out.println("key: " + pair.getKey() + " User: "
					+ pair.getValue());
			ID.add(Integer.parseInt(pair.getKey().toString()));
			tempClients.add((String) pair.getValue());
			// tempClientInfo.put((String) pair.getKey(),
			// Integer.parseInt(pair.getKey().toString()));
		}

		for (int i = 0; i < ID.size(); i++) {
			int l = ID.get(i);
			String str = tempClients.get(i);
			tempClientInfo.put(str, l);
		}

<<<<<<< HEAD
		for (int i = 0; i < this.clients.size(); i++) {
			String client = this.clients.get(i);
			tempID.add(tempClientInfo.get(client));
		}

		Collections.sort(tempID);
		int leaderID = tempID.get(0);
		for (int i = 0; i < clients.size(); i++) {
			String tempClient = clients.get(i);
			int tempLeaderID = tempClientInfo.get(tempClient);
			if (leaderID == tempLeaderID) {
				newLeaderName = tempClient;
			}

		}
		return newLeaderName;
=======
//		for (int i = 0; i < this.clients.size(); i++) {
//			String client = this.clients.get(i);
//			ID.add(clientInfo.get(client));
//		}
//
//		Collections.sort(ID);
//		int leaderID = ID.get(0);
//		for (int i = 0; i < clients.size(); i++) {
//			String tempClient = clients.get(i);
//			int tempID = clientInfo.get(tempClient);
//			if (leaderID == tempID) {
//				this.myLeader = tempClient;
//			}
//
//		}
>>>>>>> ac4c7cc6c50c85a98074d1956bae6e3bf7dc977c
	}

	public boolean isGroupJoined() {
		return groupJoined;
	}

	public void disconnect(String groupName, String userName) throws RemoteException {

		System.out.println("asdsad: " + groupName + " : " + userName);

			if(groupName == null) {

				ns.leaveServer(groupName, clientID);
				clientInfo = ns.getClientInfo();

			} else if(userName.equals(myLeader)) {

				System.out.println("c size: " + clientInfo.size());

				if(clientInfo.size() == 1) {

				System.out.println("Leader leaving its own group and it was empty");

				ns.removeGroup(groupName);

				} else {

					removeFromGroup(groupName, userName);
					groupsInfo = ci.getGroups();

				}

			} else {

				ci.removeFromGroup(groupName, userName);
				groupsInfo = ci.getGroups();
			}
	}

	public void removeFromGroup(String groupName, String userName) throws RemoteException {

		ns.removeMemberFromGroup(groupName, userName);
	}
}




