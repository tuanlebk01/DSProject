package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import Application.GUI;

public class Client implements ClientInterface {

	private static final long serialVersionUID = 1L;
	private Registry registry;
	private NameServerInterface ns;
	private ClientInterface ci;
	private HashMap<String, ArrayList<String>> groupsInfo = new HashMap<String, ArrayList<String>>();
	private HashMap<String, String> leaders = new HashMap<String, String>();
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

		this.registry = LocateRegistry.getRegistry("Bellatrix.cs.umu.se", portNr);
		this.ns = (NameServerInterface) registry.lookup("NamingService");
		clientID = ns.registerChatClient(userName);
		return clientID;

	}

	public boolean createGroup(String groupName, String userName)
			throws RemoteException, ServerNotActiveException,
			NotBoundException, AlreadyBoundException {

		this.myGroup = groupName;
		this.myLeader = userName;

		this.ci = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
		Registry registry2 = LocateRegistry.createRegistry(1234);
		registry2.bind(userName, ci);
		System.out.println("CLIENT: Groupleader running on port " + "1234");

		this.registry = LocateRegistry.getRegistry("localhost", 1234);
		this.ci = (ClientInterface) registry.lookup(userName);

		groupCreated = ns.createGroup(groupName, userName);

		return groupCreated;

	}

	public boolean connectToGroupLeader(String groupLeader) throws RemoteException, AlreadyBoundException, NotBoundException {

		this.registry = LocateRegistry.getRegistry("Bellatrix.cs.umu.se", 1234);
		this.ci = (ClientInterface) registry.lookup(groupLeader);
		System.out.println("CLIENT: connected to groupleader: " + groupLeader);
		System.out.println("client: my userName: " + myUserName);
		return groupJoined = ci.addMemberToGroup(myUserName);
	}

	public boolean addMemberToGroup(String userName) throws RemoteException {

		groupJoined = ns.addMember(myGroup, userName);
		getGroupList(myGroup);
		return groupJoined;

	}

	public ArrayList<String> getGroupList(String myGroup) throws RemoteException {
		groupsInfo = getGroups();
		clients = groupsInfo.get(myGroup);
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
			throws RemoteException, ServerNotActiveException, AlreadyBoundException, NotBoundException {

		this.myGroup = groupName;
		this.myLeader = leaderName;
		System.out.println("CLIENT: Group: " + myGroup + " : Leader: " + myLeader);
		connectToGroupLeader(myLeader);
		return myLeader;

	}

	public void retrieveMessage(String message) throws RemoteException {
		GUI.writeMsg(message);
	}

	public void broadcastMessage() {

	}

	public void getUsersInGroup(String group) throws RemoteException {

	}

	public void getUsersIPs(String group) throws RemoteException {
		// TODO Auto-generated method stub

	}

	public boolean isGroupJoined() {
		return groupJoined;
	}
}
