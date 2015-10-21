package GroupManagement;

import java.net.InetAddress;
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
import java.util.Comparator;
import java.util.HashMap;
import Communication.CommunicationModule;
import Communication.TextMessage;

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
	private ArrayList<Triple> clients = new ArrayList<Triple>();
	private ArrayList<String> listOfClientsInMyGroup = new ArrayList<String>();
	private int clientID;
	private String myUserName;
	private String myGroup;
	private String myLeader;
	private boolean groupCreated;
	private boolean groupJoined;
	private Triple clientInfo;

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

		clientInfo = new Triple(clientID, myUserName, InetAddress.getLocalHost());

		groupsInfo = ns.getGroupsInfo();

		return clientID;

	}

	public boolean createGroup(String groupName, String userName)
			throws RemoteException, ServerNotActiveException,
			NotBoundException, AlreadyBoundException {

		this.myGroup = groupName;
		this.myLeader = userName;

		leaderci = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
		registry = LocateRegistry.createRegistry(1234);
		registry.bind(userName, leaderci);

		System.out.println("CLIENT: Groupleader: " + userName + " running on port " + "1234");


		groupCreated = ns.createGroup(groupName, userName);
		clients = ns.getClientList();
		groupsInfo = ns.getGroupsInfo();
		listOfClientsInMyGroup = groupsInfo.get(groupName);

		return groupCreated;

	}

	public String joinGroup(String groupName, String leaderName)
			throws RemoteException, ServerNotActiveException,
			AlreadyBoundException, NotBoundException {

		this.myGroup = groupName;
		this.myLeader = leaderName;

		clientInfo.setGroup(groupName);

		System.out.println("CLIENT: Group: " + myGroup + " : Leader: " + myLeader);

		for(int i = 0; i < clients.size(); i++) {
			System.out.println(clients.get(i).getUsername()+ " : " + clients.get(i).getClientID() + " : " + clients.get(i).getGroup()+ " : " + clients.get(i).getIp());

		}

		connectToGroupLeader(myLeader);

		ci = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
//		registry = LocateRegistry.createRegistry(1234);
//		registry.bind(myUserName, ci);
		ci = (ClientInterface) registry.lookup(myLeader);

		ArrayList<String> temp = new ArrayList<String>();

		System.out.println("should not be null: " + groupsInfo.get(groupName));
		temp = groupsInfo.get(groupName);
		temp.remove(leaderName);



		HashMap<Integer, ClientInterface> clientInterface = leaderci.getInterfaceOfGroup();
		leaderci.sharegroup();


		cm = new CommunicationModule(myUserName, clientID, clients, registry);

		return myLeader;

	}

	public boolean connectToGroupLeader(String groupLeader) throws RemoteException, AlreadyBoundException, NotBoundException {

		//Should be leader ip
		registry = LocateRegistry.getRegistry("Weasley.cs.umu.se", 1234);
		leaderci = (ClientInterface) registry.lookup(groupLeader);

		System.out.println("CLIENT: connected to groupleader: " + groupLeader + " : with username: " + myUserName);

		groupJoined = leaderci.addMemberToGroup(myUserName);


		return groupJoined;

	}


//	THIS BELONGS TO THE LEADER
	public boolean addMemberToGroup(String userName) throws RemoteException {

		groupJoined = ns.addMember(myGroup, userName);
		groupsInfo = ns.getGroupsInfo();
		listOfClientsInMyGroup = groupsInfo.get(myGroup);

		System.out.println("leader getting info");
		System.out.println("groupjoined: " + groupJoined);
		System.out.println("groupsInfo: " + groupsInfo.get(myGroup));
		System.out.println("listOFclients: " + listOfClientsInMyGroup);
		sharegroup();
		return groupJoined;

	}

	public HashMap<String, String> getGroupLeaders() throws RemoteException {
		return leaders = ns.getGroupLeaders();
	}

	public void addMessageToQueue (TextMessage message){
		cm.addMessageToQueue(message);
	}

	public void broadcastMessage(String message) throws RemoteException, NotBoundException {
		cm.sendMessage(message);
	}

	public String startElection() throws RemoteException {

		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getUsername().equals(myLeader)) {
				clients.remove(i);
			}
		}

		Collections.sort(clients, new Comparator<Triple>() {
			@Override
			public int compare(Triple t1, Triple t2) {
				return t1.getClientID() - t2.getClientID();
			}

		});

		System.out.println(clients.get(0).getUsername());

		return clients.get(0).getUsername();

	}

	public void disconnect(String groupName, String userName) throws RemoteException {

			if(groupName == null) {

				ns.leaveServer(groupName, clientID);

			} else if(userName.equals(myLeader)) {

				System.out.println("size: " + listOfClientsInMyGroup.size());
				if(listOfClientsInMyGroup.size() == 1) {

				System.out.println("Leader leaving its own group and it was empty");

				ns.removeGroup(groupName);

				} else {

					ns.removeMemberFromGroup(groupName, userName);
					groupsInfo = ns.getGroupsInfo();
					listOfClientsInMyGroup = groupsInfo.get(myGroup);
					sharegroup();
					startElection();

				}

			} else {

				leaderci.removeFromGroup(groupName, userName);
				leaderci.sharegroup();
			}
	}


	public void removeFromGroup(String groupName, String userName) throws RemoteException {
		clients.remove(userName);
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

		System.out.println("DISTRIBUTING NEW LIST");
		for(int i = 0; i < clients.size(); i++) {
			System.out.println(clients.get(i).getUsername()+ " : " + clients.get(i).getClientID() + " : " + clients.get(i).getGroup()+ " : " + clients.get(i).getIp());

		}
		
        for(int key: clientInterfaces.keySet()){
        	clientInterfaces.get(key).setClientInterfaces(clientInterfaces);
        }
	}

	public boolean isGroupJoined() {
		return groupJoined;
	}

	public HashMap<String, ArrayList<String>> getGroupsInfo() throws RemoteException{
		return groupsInfo;
	}
}




