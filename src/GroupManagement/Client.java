package GroupManagement;

import java.net.UnknownHostException;
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
import java.util.Map.Entry;

import Application.GUI;
import Communication.CommunicationModule;
import Communication.TextMessage;
import GroupManagement.NameServer.Triple;

public class Client implements ClientInterface {

	private static final long serialVersionUID = 1L;
	private Registry registry;
	private NameServerInterface ns;
	private ClientInterface ci;
	private ClientInterface leaderci;
	public CommunicationModule cm;
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
	private Triple clientInfo3;

	public Client() throws RemoteException {
		super();
	}

	public int connectToNameServer(String userName, int portNr)
			throws RemoteException, AlreadyBoundException,
			ServerNotActiveException, NotBoundException, UnknownHostException {

		this.myUserName = userName;

		this.registry = LocateRegistry.getRegistry("Weasley.cs.umu.se",
				portNr);
//		this.registry = LocateRegistry.getRegistry("localhost",
//				portNr);
		this.ns = (NameServerInterface) registry.lookup("NamingService");
		clientID = ns.registerChatClient(userName);

		return clientID;

	}

	public boolean createGroup(String groupName, String userName)
			throws RemoteException, ServerNotActiveException,
			NotBoundException, AlreadyBoundException {

		this.myGroup = groupName;
		this.myLeader = userName;

		registry = LocateRegistry.getRegistry("localhost", 1234);
		registry = LocateRegistry.createRegistry(1234);
		registry.bind(userName, leaderci);

		leaderci = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
		System.out.println("CLIENT: Groupleader: " + userName + " running on port " + "1234");




		groupCreated = ns.createGroup(groupName, userName);
		clientInfo = ns.getClientInfo();
		groupsInfo = ns.getGroupsInfo();


		clientInterfaces.put(clientID, ci);

		return groupCreated;

	}

	public boolean connectToGroupLeader(String groupLeader) throws RemoteException, AlreadyBoundException, NotBoundException {

		registry = LocateRegistry.getRegistry("Sirius.cs.umu.se", 1234);
//		registry = LocateRegistry.getRegistry("localhost", 1234);
		ci = (ClientInterface) registry.lookup(groupLeader);
		System.out.println("CLIENT: connected to groupleader: " + groupLeader + " : with username: " + myUserName);
		groupJoined = ci.addMemberToGroup(myUserName);
		//ci = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
		//clientInterfaces.put(clientID, ci);
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
		ArrayList<String> temp = new ArrayList<String>();
		ArrayList<Integer> tempIDs = new ArrayList<Integer>();
		ArrayList<Integer> IDs = new ArrayList<Integer>();
		ArrayList<String> tempClients = new ArrayList<String>();
		HashMap<Integer, String> clientInfo = ns.getClientInfo();
		HashMap<String, Integer> tempClientInfo = new HashMap<String, Integer>();
		groupsInfo = ns.getGroupsInfo();
		temp = groupsInfo.get(groupName);
		temp.remove(leaderName);

		Iterator it = clientInfo.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry pair = (Map.Entry) it.next();
			// temp.add(Integer.parseInt(pair.getKey().toString()));
//			System.out.println("key: " + pair.getKey() + " User: "
//					+ pair.getValue());
			tempIDs.add(Integer.parseInt(pair.getKey().toString()));
			tempClients.add((String) pair.getValue());
		}

		for (int i = 0; i < tempIDs.size(); i++) {
			int l = tempIDs.get(i);
			String str = tempClients.get(i);
			tempClientInfo.put(str, l);
		}

		for (String i : temp) {
			IDs.add(tempClientInfo.get(i));
		}


		System.out.println("sadsadadsa");
		registry = LocateRegistry.getRegistry("localhost", 1234);
		ci = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
//		registry = LocateRegistry.createRegistry(1234);
		System.out.println(myUserName);
		registry.bind(myUserName, ci);
		ClientInterface ciLeader = (ClientInterface) registry.lookup(myLeader);

		HashMap<Integer, ClientInterface> clientInterface = ciLeader.getInterfaceOfGroup();
		clientInterface.put(clientID, ci);
		ciLeader.sharegroup();


		cm = new CommunicationModule(myUserName, clientID, clientInterface, clients, registry);

		return myLeader;

	}

	public void addMessageToQueue (TextMessage message){
		cm.addMessageToQueue(message);
	}

	public void broadcastMessage(String message) throws RemoteException, NotBoundException {

		cm.sendMessage(message);

	}

	public String startElection(ArrayList<String> clients) throws RemoteException {

		for (int i = 0; i < clients.size(); i++) {
			System.out.println(clients.get(i));
		}


		ArrayList<Integer> ID = new ArrayList<Integer>();
		ArrayList<String> tempClients = new ArrayList<String>();
		ArrayList<Integer> tempID = new ArrayList<Integer>();
		HashMap<Integer, String> clientInfo = ns.getClientInfo();
		HashMap<String, Integer> tempClientInfo = new HashMap<String, Integer>();
		String newLeaderName = null;

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

			System.out.println(tempClientInfo.keySet());
		for (int i = 0; i < clients.size(); i++) {
			String client = clients.get(i);
			tempID.add(tempClientInfo.get(client));
		}
		System.out.println(tempID);
		Collections.sort(tempID);
		int leaderID = tempID.get(0);
		System.out.println(leaderID);
		for (int i = 0; i < clients.size(); i++) {
			int tempLeaderID = tempClientInfo.get(clients.get(i));
			if (leaderID == tempLeaderID) {
				newLeaderName = clients.get(i);
			}

		}
		System.out.println(newLeaderName);
		return newLeaderName;

	}

	public boolean isGroupJoined() {
		return groupJoined;
	}

	public void disconnect(String groupName, String userName) throws RemoteException {

			if(groupName == null) {

				ns.leaveServer(groupName, clientID);
				clientInfo = ns.getClientInfo();

			} else if(userName.equals(myLeader)) {

				if(clientInfo.size() == 1) {

				System.out.println("Leader leaving its own group and it was empty");

				ns.removeGroup(groupName);

				} else {

					ns.removeMemberFromGroup(groupName, userName);
					groupsInfo = ci.getGroups();
					clientInfo = ns.getClientInfo();
					ci.startElection(groupsInfo.get(groupName));

				}

			} else {

				ci.removeFromGroup(groupName, userName);
				groupsInfo = ci.getGroups();
			}
	}

	public void removeFromGroup(String groupName, String userName) throws RemoteException {

		ns.removeMemberFromGroup(groupName, userName);
	}

	public void broadcastTestMessages(int nrOftestMSG) throws RemoteException {

		cm.sendMessagesInRandomOrder(nrOftestMSG);

	}

	public ArrayList<TextMessage> getMessages() {
		return cm.getAcceptedMessages();

	}

	public HashMap<Integer, ClientInterface> getInterfaceOfGroup()
			throws RemoteException {
		return clientInterfaces;
	}

	public void setClientInterfaces(
			HashMap<Integer, ClientInterface> clientInterfaces)
			throws RemoteException {
		this.clientInterfaces = clientInterfaces;

	}

	public void sharegroup() throws RemoteException {

        for(int key: clientInterfaces.keySet()){
        	clientInterfaces.get(key).setClientInterfaces(clientInterfaces);
        }
	}
}




