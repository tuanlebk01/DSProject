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

public class Client extends Observable implements ClientInterface {

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

	public int connectToNameServer(String userName, String host, int portNr)
			throws RemoteException, AlreadyBoundException,
			ServerNotActiveException, NotBoundException, UnknownHostException {

		if(host.equals("localhost")) {
			host = InetAddress.getLocalHost().getHostName().concat("");
		}

		this.myUserName = userName;
		this.registry = LocateRegistry.getRegistry(host, portNr);

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
			try {
				registry.bind(userName, ci);
			} catch (Exception e) {
				registry.rebind(userName, ci);
			}

		} catch (Exception e) {
	         Registry registry = LocateRegistry.createRegistry(1234);
	         try {
					registry.bind(userName, ci);
				} catch (Exception e1) {
					registry.rebind(userName, ci);
				}
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
		myGroup = groupName;
		myLeader = leaderName;
		ClientInterface ciLeader;
		clientInfo.setGroup(groupName);
		connectToGroupLeader(myLeader);

		// Tuan updated here

		ArrayList<Triple> tempClients = new ArrayList<Triple>();
		tempClients = ns.getClientList();

		// To here

		try {
			ci = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry1 = LocateRegistry.getRegistry(1234);
			try {
				registry1.bind(myUserName,ci);
			} catch (Exception e) {
				registry1.rebind(myUserName,ci);
			}

		} catch (Exception e) {
	         Registry registry1 = LocateRegistry.createRegistry(1234);
	         try {
					registry1.bind(myUserName,ci);
				} catch (Exception e1) {
					registry1.rebind(myUserName,ci);
				}
		}

		leaderRegistry = LocateRegistry.getRegistry(IpOfLeader, 1234);
		ciLeader = (ClientInterface) leaderRegistry.lookup(myLeader);
		clients = ciLeader.getClientlist(groupName);

		int temp = clients.size();
		for (int i = 0; i < temp; i++){
			if (!clients.get(i).getUsername().equals(myUserName)){
				String ip = clients.get(i).getIp().toString().split("/")[1];
				Registry goRegistry = LocateRegistry.getRegistry(ip, 1234);
				try {
					ci = (ClientInterface) goRegistry.lookup(clients.get(i).getUsername());
					ci.setClientList(clients);
					ci.addClientInterface(clientInfo);
				} catch (Exception e) {
//					e.printStackTrace();
				}
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
		getGroupsInfo();
		return myLeader;
	}

    public void setNewLeader(String leader) throws RemoteException{
        myLeader = leader;
    }

	public String getMyLeader() {
		return myLeader;
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


	public boolean addMemberToGroup(String userName) throws RemoteException {
		groupJoined = ns.addMember(myGroup, userName);
		groupsInfo = ns.getGroupsInfo();
		listOfClientsInMyGroup = groupsInfo.get(myGroup);
		clients = ns.getGroupTriples(myGroup);
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
			//gui handle's error
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
				if(listOfClientsInMyGroup.size() == 1) {

					// Tuan added this

					ns.removeMemberFromGroup(myGroup, myUserName);

					//

					ns.removeGroup(groupName);


				} else {
					groupsInfo = ns.getGroupsInfo();
					listOfClientsInMyGroup = groupsInfo.get(myGroup);
					myOldLeader = myLeader;
					myLeader = startElection();
					ns.updateNewLeader(myGroup, myLeader);
					sharegroup(1); // update new leader for members
					sharegroup(2); // remove old leader from client list of members
				}

			} else {
				sharegroup(2); // update client list for members
			}
	}


	public void broadcastTestMessages(int nrOftestMSG) throws RemoteException, java.rmi.NotBoundException {
		cm.sendMessagesInRandomOrder(nrOftestMSG);
	}

	public void broadcastTestMessages2(int nrOftestMSG) throws RemoteException, java.rmi.NotBoundException {
		cm.sendMessageWithOneDrop(nrOftestMSG);
	}

    public ArrayList<TextMessage> getMessagesInQueue() {
        return cm.getWaitingMessageQueue();
    }

	public HashMap<Integer, ClientInterface> getInterfaceOfGroup() throws RemoteException {
		return clientInterfaces;
	}

	public void setClientInterfaces(HashMap<Integer, ClientInterface> clientInterfaces) throws RemoteException {
		this.clientInterfaces = clientInterfaces;
	}

	public void sharegroup(int option) throws RemoteException, NotBoundException {

		if (option == 1) {
			String IpLeader = null;

			for (int i = 0; i < clients.size(); i++){
				if (clients.get(i).getUsername().equals(myLeader)) {
					IpLeader = clients.get(i).getIp().toString().split("/")[1];
				}
			}
			for (int i = 0; i < clients.size(); i++){
				if (!clients.get(i).getUsername().equals(myOldLeader)){
					String ip = clients.get(i).getIp().toString().split("/")[1];
					Registry registry = LocateRegistry.getRegistry(ip, 1234);
					ci = (ClientInterface) registry.lookup(clients.get(i).getUsername());
					ci.setNewLeader(myLeader);
					ci.updateIpOfLeader(IpLeader);
				}
			}
			ns.updateNewLeader(myGroup, myLeader);
			IpOfLeader = IpLeader;
		}

		if (option == 2) {
			int k = 100000000; //any number here
			leaderRegistry = LocateRegistry.getRegistry(IpOfLeader, 1234);
			ci = (ClientInterface) leaderRegistry.lookup(myLeader);
			clients = ci.getClientlist(myGroup);

			for (int i = 0; i < clients.size(); i++) {
				if (clients.get(i).getUsername().equals(myUserName)) {
					k = i;
					clients.remove(k); // remove itself from the client list
				}
			}

			ns.removeMemberFromGroup(myGroup, myUserName); // remove itself from the NS
			for (int i = 0; i < clients.size(); i++){

				String ip = clients.get(i).getIp().toString().split("/")[1];
				Registry registry1 = LocateRegistry.getRegistry(ip, 1234);
				ci = (ClientInterface) registry1.lookup(clients.get(i).getUsername());
				ci.setClientList(clients);
				ci.removeClientInterface(clientInfo);
				ci.removeMemberFromListOfClientsInMyGroup(myUserName);
				ci.setNewLeader(myLeader);
				setValue(myUserName);
				ci.setValue(myUserName);
			}
		}
	}

	public void handleError(String crashedUserName) throws RemoteException, NotBoundException{
		if (crashedUserName.equals(myLeader)) {
			myOldLeader = myLeader;
			myLeader = startElection();
			sharegroup(1); // update new leader for members
			shareGroupForCrashedInfo(crashedUserName); // remove old leader from client list of members
		} else {
			shareGroupForCrashedInfo(crashedUserName); // update client list for members
		}
	}

	public boolean isGroupJoined() {
		return groupJoined;
	}

	public HashMap<String, ArrayList<String>> getGroupsInfo() throws RemoteException, NotBoundException {

		try {
			if(!myUserName.equals(myLeader)){
			leaderRegistry = LocateRegistry.getRegistry(IpOfLeader, 1234);
			ci = (ClientInterface) leaderRegistry.lookup(myLeader);
			groupsInfo = ci.askNSforGroupsInfo();
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupsInfo;
	}

	public HashMap<String, ArrayList<String>> askNSforGroupsInfo() throws RemoteException {
		groupsInfo = ns.getGroupsInfo();
		return groupsInfo;
	}

	public void setClientList(ArrayList<Triple> clients ) throws RemoteException {
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

    public void addMemberToListOfClientsInMyGroup(String member) throws RemoteException {
    	if(!listOfClientsInMyGroup.contains(member)) {
    		listOfClientsInMyGroup.add(member);
    	}
	}

    public void removeMemberFromListOfClientsInMyGroup(String member) throws RemoteException {
    	if(listOfClientsInMyGroup.contains(member)) {
    		listOfClientsInMyGroup.remove(member);
    	}
	}

	public HashMap<Integer, Integer> getLastAcceptedSeqNr() throws RemoteException{
    	return cm.getLastAcceptedSeqNr();
    }
    public void updateIpOfLeader(String Ip) throws RemoteException{
    	IpOfLeader = Ip;
    }

    public void shareGroupForCrashedInfo(String crashedUserName) throws RemoteException, NotBoundException{
		int k = 100000000; //any number here


		Triple crashedUser;
		leaderRegistry = LocateRegistry.getRegistry(IpOfLeader, 1234);
		ci = (ClientInterface) leaderRegistry.lookup(myLeader);
		clients = ci.getClientlist(myGroup);

		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getUsername().equals(crashedUserName)) {
				k = i;
			}
		}
		crashedUser = clients.get(k); // get triple of crashed user

		clients.remove(k); // remove crashed client from the client list of sender
		ns.removeMemberFromGroup(myGroup, crashedUserName); // remove crashed client from the NS
		for (int i = 0; i < clients.size(); i++){

			if (!clients.get(i).getUsername().equals(crashedUserName)) {
				String ip = clients.get(i).getIp().toString().split("/")[1];
				Registry registry1 = LocateRegistry.getRegistry(ip, 1234);
				ci = (ClientInterface) registry1.lookup(clients.get(i).getUsername());
				ci.setClientList(clients);
				ci.removeClientInterface(crashedUser);
				ci.removeMemberFromListOfClientsInMyGroup(crashedUserName);
				setValue(clients.get(i).getUsername());
				if(clients.get(i).getUsername().equals(myUserName)) {
					ci.setValue("asd");
				} else {
					ci.setValue(myUserName);
				}
			}
		}
    }

	public void notifyOthers() throws RemoteException, NotBoundException {
		for (int i = 0; i < clients.size(); i++) {
			if(!clients.get(i).getUsername().equals(myUserName)) {
				String ip = clients.get(i).getIp().toString().split("/")[1];
				Registry registry1 = LocateRegistry.getRegistry(ip, 1234);
				ci = (ClientInterface) registry1.lookup(clients.get(i).getUsername());
				ci.addMemberToListOfClientsInMyGroup(myUserName);
				ci.setValue(myUserName);
			}
		}
	}

	private String watchedValue = "";

	@Override
	public void updateGroupList(String value) throws RemoteException {
		watchedValue = value;
	}

	public void setValue(String value) throws RemoteException {
		if(!watchedValue.equals(value)) {
			setChanged();
			notifyObservers();
			clearChanged();
			watchedValue = "asd";
		}
	}

	public void setOutgoingMsg(boolean b) {
		if(cm != null) {
			cm.setHoldOutgoingMessages(b);
		}
	}

	public void setIncommingMsg(boolean b) {
		if(cm != null) {
			cm.setHoldIncomingMessages(b);
		}
	}

	public ArrayList<TextMessage> getOutgoingMsgQueue() {
		if(cm != null) {
			return cm.getOutgoingMessageQueue();
		}
		return null;
	}

	public ArrayList<TextMessage> getIncommingMsgQueue() {
		if(cm != null) {
			return cm.getIncomingMessageQueue();
		}
		return null;
	}

	public ArrayList<TextMessage> getHoldbackQueue() {
		if(cm != null) {
			return cm.getWaitingMessageQueue();
		}
		return null;
	}

	public ArrayList<TextMessage> getMessages() {
		if(cm != null) {
			return cm.getAcceptedMessages();
		}
		return null;
	}

	public void setTimeout() {
		if(cm != null) {
			cm.setTimeOutTime(99999999);
		}
	}

	public void forwardMsg(TextMessage textMessage) {
		if(cm != null) {
			try {
				cm.releaseMsgFromOutgoingQuene(textMessage);
			} catch (RemoteException | NotBoundException e) {
				System.out.println("Some error forwarding message, client probably crashed");
				//e.printStackTrace();
			}
		}
	}

	public void releaseMsg(TextMessage textMessage) {
		if(cm != null) {
			cm.releaseMsgFromIncomingQuene(textMessage);
		}
	}

	public void acceptMsg(TextMessage textMessage) {
		if(cm != null) {
			cm.acceptMessage(textMessage, textMessage.getClientID());
		}
	}
}




