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

		this.registry = LocateRegistry.getRegistry("localhost",
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

		ci = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
		registry = LocateRegistry.createRegistry(1234);
		registry.bind(userName, ci);

		System.out.println("CLIENT: Groupleader: " + userName + " running on port " + "1234");

		groupCreated = ns.createGroup(groupName, userName);
		clients = ns.getClientList();
		groupsInfo = ns.getGroupsInfo();
		listOfClientsInMyGroup = groupsInfo.get(groupName);
		System.out.println("asd"  + listOfClientsInMyGroup);
		cm = new CommunicationModule(myUserName, clientID, clients, registry);

		return groupCreated;

	}

	public String joinGroup(String groupName, String leaderName)
			throws RemoteException, ServerNotActiveException,
			AlreadyBoundException, NotBoundException {

		this.myGroup = groupName;
		this.myLeader = leaderName;

		clientInfo.setGroup(groupName);

		System.out.println("CLIENT: Group: " + myGroup + " : Leader: " + myLeader);

		connectToGroupLeader(myLeader);
		listOfClientsInMyGroup = groupsInfo.get(groupName);
		ci = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
		registry.bind(myUserName, ci);
		ci = (ClientInterface) registry.lookup(myLeader);
		clients = ci.getClientlist(groupName);
		cm = new CommunicationModule(myUserName, clientID, clients, registry);

		for (int i = 0; i < clients.size(); i++){
			if (!clients.get(i).getUsername().equals(myUserName)){
				ci = (ClientInterface) registry.lookup(clients.get(i).getUsername());
				ci.addClientInterface(clientInfo);
				ci.setClientList(clients);
			}
		}

		listOfClientsInMyGroup = ci.getListOfClientsInMyGroup();
		System.out.println("asd : " + listOfClientsInMyGroup);
		return myLeader;

	}

    public void handleDisconnect (Triple triple) throws java.rmi.RemoteException, java.rmi.NotBoundException{

        if(triple.equals(myLeader)) {
            if (listOfClientsInMyGroup.size() == 1) {
                System.out.println("Leader leaving its own group and it was empty");
                ns.removeGroup(myGroup);
            }
            myLeader = startElection();
        }

            Iterator<Triple> it = clients.iterator();
            while (it.hasNext()) {
                if (it.next().getUsername().equals(triple.getUsername())) {
                    it.remove();
                }
            }
            it = clients.iterator();
            while (it.hasNext()) {
                if (!it.next().getUsername().equals(myUserName)) {
                    ci = (ClientInterface) registry.lookup(it.next().getUsername());
                    ci.removeClientInterface(triple);
                    ci.setClientList(clients);
                    ci.setNewLeader(myLeader);
                }
            }
            ns.removeMemberFromGroup(myGroup, triple.getUsername());
            ns.updateNewLeader(myGroup, myLeader);


    }

    public void setNewLeader(String leader) throws RemoteException{
        myLeader = leader;
    }

	public boolean connectToGroupLeader(String groupLeader) throws RemoteException, AlreadyBoundException, NotBoundException {


		//String leaderName = ns.getGroupLeaders().get(groupLeader);
		ArrayList<Triple> clientList = ns.getClientList();
		String ip = null;
		for (int i = 0; i < clientList.size(); i++){
			System.out.println("name of clients " +clientList.get(i).getUsername());
			String tempClient = clientList.get(i).getUsername();
			if (tempClient.equals(groupLeader)) {
				ip = clientList.get(i).getIp().toString().split("/")[1];

			}
		}
		registry = LocateRegistry.getRegistry(ip, 1234);
		ci = (ClientInterface) registry.lookup(groupLeader);

		System.out.println("CLIENT: connected to groupleader: " + groupLeader + " : with username: " + myUserName);

		groupJoined = ci.addMemberToGroup(myUserName);


		return groupJoined;

	}


//	THIS BELONGS TO THE LEADER
	public boolean addMemberToGroup(String userName) throws RemoteException {

		groupJoined = ns.addMember(myGroup, userName);
		groupsInfo = ns.getGroupsInfo();
		listOfClientsInMyGroup = groupsInfo.get(myGroup);
		System.out.println("asd: " + listOfClientsInMyGroup);
		clients = ns.getGroupTriples(myGroup);

		sharegroup();
		return groupJoined;

	}

	public HashMap<String, String> getGroupLeaders() throws RemoteException {
		leaders = ns.getGroupLeaders();
		return leaders;
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

	public void disconnect(String groupName, String userName) throws RemoteException, java.rmi.NotBoundException {

			if(groupName == null) {

				ns.leaveServer(groupName, clientID);

			} else if(userName.equals(myLeader)) {

				if(listOfClientsInMyGroup.size() == 1) {

				System.out.println("Leader leaving its own group and it was empty");

				ns.removeGroup(groupName);

				} else {

					ns.removeMemberFromGroup(groupName, userName);
					groupsInfo = ns.getGroupsInfo();
					listOfClientsInMyGroup = groupsInfo.get(myGroup);
					myLeader = startElection();
				}

			} else {

				ci.removeFromGroup(groupName, userName);
				ci.sharegroup();
			}
	}


	public void removeFromGroup(String groupName, String userName) throws RemoteException {
		clients.remove(userName);
		ns.removeMemberFromGroup(groupName, userName);
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

	public void sharegroup() throws RemoteException {

//        for (int i = 0; i < clients.size(); i++) {
//
//        	String ip = clients.get(i).getIp().toString();
//
//        	registry = LocateRegistry.getRegistry("localhost", 1234);
//    		try {
//
//    			if(!clients.get(i).getUsername().equals(myUserName)) {
//    				System.out.println(myUserName + " : " + clients.get(i).getUsername());
//
//    				ci = (ClientInterface) registry.lookup(clients.get(i).getUsername());
//    			}
//
//				ci.setClientList(clients);
//
//    		} catch (NotBoundException e) {
//				e.printStackTrace();
//			}
//		}
	}

	public boolean isGroupJoined() {
		return groupJoined;
	}

	public HashMap<String, ArrayList<String>> getGroupsInfo() throws RemoteException {
		groupsInfo = ns.getGroupsInfo();
		return groupsInfo;
	}

	public void setClientList(ArrayList<Triple> clients) throws RemoteException {

		this.clients = clients;
	}

	public void addClientInterface(Triple triple) throws RemoteException {
		cm.addAnotherClientInterface(triple);
	}

    public void removeClientInterface(Triple triple) throws RemoteException{
        cm.removeClientInterface(triple);
    }

	@Override
	public ArrayList<Triple> getClientlist(String groupName) throws RemoteException {
		clients = ns.getGroupTriples(groupName);
		return clients;
	}

	public ArrayList<String> getListOfClientsInMyGroup() throws RemoteException {
		return listOfClientsInMyGroup;
	}
}




