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
import java.util.*;

import Communication.CommunicationModule;
import Communication.TextMessage;

public class Client implements ClientInterface {

	private static final long serialVersionUID = 1L;
	private Registry registry;
	private Registry leaderRegistry;
	private NameServerInterface ns;
	private ClientInterface ci;
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
	private String myOldLeader;
	private String IpOfLeader;
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
		this.registry = LocateRegistry.getRegistry("Severus.cs.umu.se",
				portNr);

		ns = (NameServerInterface) registry.lookup("NamingService");
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

		try {
			ci = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry(1234);
			registry.bind(userName, ci);
		} catch (Exception e) {
	         Registry registry = LocateRegistry.createRegistry(1234);
	         registry.bind(userName, ci);
		}

		groupCreated = ns.createGroup(groupName, userName);
		clients.add(clientInfo);
		groupsInfo = ns.getGroupsInfo();
		listOfClientsInMyGroup = groupsInfo.get(groupName);

		cm = new CommunicationModule(myUserName, clientID, clients);

		return groupCreated;

	}

	public String joinGroup(String groupName, String leaderName)
			throws RemoteException, ServerNotActiveException,
			AlreadyBoundException, NotBoundException {

		this.myGroup = groupName;
		this.myLeader = leaderName;
		ClientInterface ciLeader;

		clientInfo.setGroup(groupName);

		connectToGroupLeader(myLeader);



		try {
			ci = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry1 = LocateRegistry.getRegistry(1234);
			registry1.bind(myUserName,ci);
		} catch (Exception e) {
	         Registry registry1 = LocateRegistry.createRegistry(1234);
	         registry1.bind(myUserName,ci);
		}


		leaderRegistry = LocateRegistry.getRegistry(IpOfLeader, 1234);
		ciLeader = (ClientInterface) leaderRegistry.lookup(myLeader);
		clients = ciLeader.getClientlist(groupName);

		int temp = clients.size();
		for (int i = 0; i < temp; i++){
			if (!clients.get(i).getUsername().equals(myUserName)){
				String ip = clients.get(i).getIp().toString().split("/")[1];
				Registry goRegistry = LocateRegistry.getRegistry(ip, 1234);
				ci = (ClientInterface) goRegistry.lookup(clients.get(i).getUsername());
				System.out.println("Client size, join group: " + clients.size());
				ci.setClientList(clients);

				ci.addClientInterface(clientInfo);

			}
		}

		cm = new CommunicationModule(myUserName, clientID, clients);
		cm.setLastAcceptedSeqNr(ciLeader.getLastAcceptedSeqNr());

		setClientList(clients);



		int temp2 = clients.size();
		for (int i = 0; i < temp2; i++){
			if (!clients.get(i).getUsername().equals(myUserName)){
				addClientInterface(clientInfo);
			}
		}
		listOfClientsInMyGroup = ciLeader.getListOfClientsInMyGroup();
		System.out.println("this is a list of Clients in my group: " + listOfClientsInMyGroup);
		getGroupsInfo();
		return myLeader;

	}

    public void setNewLeader(String leader) throws RemoteException{
    	System.out.println("changed leader in: " + myUserName + " too " + leader);
        myLeader = leader;
    }

	public boolean connectToGroupLeader(String groupLeader) throws RemoteException, AlreadyBoundException, NotBoundException {
		Registry leaderRegistry;
		ArrayList<Triple> clientList = ns.getClientList();
		String ip = null;
		for (int i = 0; i < clientList.size(); i++){
			String tempClient = clientList.get(i).getUsername();
			if (tempClient.equals(groupLeader)) {
				ip = clientList.get(i).getIp().toString().split("/")[1];

			}
		}

		leaderRegistry = LocateRegistry.getRegistry(ip, 1234);
		ci = (ClientInterface) leaderRegistry.lookup(groupLeader);


		groupJoined = ci.addMemberToGroup(myUserName);

		this.IpOfLeader = ip;
		return groupJoined;

	}


//	THIS BELONGS TO THE LEADER
	public boolean addMemberToGroup(String userName) throws RemoteException {

		groupJoined = ns.addMember(myGroup, userName);
		groupsInfo = ns.getGroupsInfo();
		listOfClientsInMyGroup = groupsInfo.get(myGroup);
		clients = ns.getGroupTriples(myGroup);

		//sharegroup();
		return groupJoined;

	}

	public HashMap<String, String> getGroupLeaders() throws RemoteException {
		leaders = ns.getGroupLeaders();
		return leaders;
	}

	public void addMessageToQueue (TextMessage message)throws RemoteException{
		cm.addMessageToQueue(message);
	}

	public void broadcastMessage(String message) throws RemoteException, NotBoundException {
		try {
			cm.sendMessage(message);
		} catch (Exception e) {
		}
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

		return clients.get(0).getUsername();

	}

	public void disconnect(String groupName, String userName) throws RemoteException, java.rmi.NotBoundException {

			if(groupName == null) {

				ns.leaveServer(groupName, clientID);

			} else if(userName.equals(myLeader)) {

				System.out.println("IpOfLeader 1: " + IpOfLeader);

				if(listOfClientsInMyGroup.size() == 1) {

					System.out.println("Leader leaving its own group and it was empty");
					ns.removeGroup(groupName);

				} else {

					//ns.removeMemberFromGroup(groupName, userName);
					System.out.println("remove member");
					groupsInfo = ns.getGroupsInfo();
					listOfClientsInMyGroup = groupsInfo.get(myGroup);
					myOldLeader = myLeader;
					myLeader = startElection();
					sharegroup(1); // update new leader for members
					sharegroup(2); // remove old leader from client list of members
				}

			} else {
				System.out.println("else");
				ci.removeFromGroup(groupName, userName);
				sharegroup(2); // update client list for members
			}
	}


	public void removeFromGroup(String groupName, String userName) throws RemoteException {
		clients.remove(userName);
	}

	public void broadcastTestMessages(int nrOftestMSG) throws RemoteException, java.rmi.NotBoundException {

		cm.sendMessagesInRandomOrder(nrOftestMSG);

	}

	public void broadcastTestMessages2(int nrOftestMSG) throws RemoteException, java.rmi.NotBoundException {

		cm.sendMessageWithOneDrop(nrOftestMSG);

	}

	public ArrayList<TextMessage> getMessages() {
		return cm.getAcceptedMessages();
	}

    public ArrayList<TextMessage> getMessagesInQueue() {
        return cm.getQueue();
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

	public void sharegroup(int option) throws RemoteException, NotBoundException {

		if (option == 1) {
			System.out.println("Client size: " + clients.size());
			for (int i = 0; i < clients.size(); i++){
				System.out.println("Loop nr " + i);
				if (!clients.get(i).getUsername().equals(myOldLeader)){
					String ip = clients.get(i).getIp().toString().split("/")[1];
					Registry registry = LocateRegistry.getRegistry(ip, 1234);
					ci = (ClientInterface) registry.lookup(clients.get(i).getUsername());
					ci.setNewLeader(myLeader);
					System.out.println("new leader from option 1: " +myLeader);

				}
			}
			ns.updateNewLeader(this.myGroup, this.myUserName);
		}

		if (option == 2) {
			System.out.println("opt 2 running");
			int k = 100000000; //any number here
			leaderRegistry = LocateRegistry.getRegistry(IpOfLeader, 1234);
			ci = (ClientInterface) leaderRegistry.lookup(myLeader);
			clients = ci.getClientlist(myGroup);

			for (int i = 0; i < clients.size(); i++) {
				if (clients.get(i).getUsername().equals(myUserName)) {
					k = i;
				}
			}
			clients.remove(k); // remove itself from the client list

			ns.removeMemberFromGroup(myGroup, myUserName);
			System.out.println("client size: " +clients.size());
			for (int i = 0; i < clients.size(); i++){

				String ip = clients.get(i).getIp().toString().split("/")[1];
				Registry registry1 = LocateRegistry.getRegistry(ip, 1234);
				ci = (ClientInterface) registry1.lookup(clients.get(i).getUsername());
				ci.setClientList(clients);
				ci.removeClientInterface(clientInfo);
			}
			Registry registry = LocateRegistry.getRegistry("localhost", 1234);
			registry.unbind(myUserName);
		}
	}

	public boolean isGroupJoined() {
		return groupJoined;
	}

	public HashMap<String, ArrayList<String>> getGroupsInfo() throws RemoteException, NotBoundException {


		System.out.println("client size: " + clients.size());
		System.out.println("Username "+myUserName);
		System.out.println("Leader " + myLeader);
		try {
		if(!myUserName.equals(myLeader)){
			leaderRegistry = LocateRegistry.getRegistry(IpOfLeader, 1234);
			ci = (ClientInterface) leaderRegistry.lookup(myLeader);
		}

			groupsInfo = ci.askNSforGroupsInfo();
		} catch (Exception e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}
		return groupsInfo;
	}

	public HashMap<String, ArrayList<String>> askNSforGroupsInfo() throws RemoteException {
		groupsInfo = ns.getGroupsInfo();
		return groupsInfo;
	}

	public void setClientList(ArrayList<Triple> clients ) throws RemoteException {
		System.out.println("clients size: " + clients.size());
		this.clients = clients;
		cm.setClientList(clients);
	}

	public void addClientInterface(Triple triple) throws RemoteException {
		cm.addAnotherClientInterface(triple);
	}

    public void removeClientInterface(Triple triple) throws RemoteException{
        cm.removeClientInterface(triple);
    }

	public ArrayList<Triple> getClientlist(String groupName) throws RemoteException {
		clients = ns.getGroupTriples(groupName);
		return clients;
	}

	public ArrayList<Triple> getClientListFromMember() throws RemoteException {
		return clients;
	}

	public void setOrdered(boolean b) {
		cm.setOrdered(b);
	}

	public ArrayList<String> getListOfClientsInMyGroup() throws RemoteException {
		return listOfClientsInMyGroup;
	}

    public HashMap<Integer, Integer> getLastAcceptedSeqNr() throws RemoteException{
    	return cm.getLastAcceptedSeqNr();
    }
}




