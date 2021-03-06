package GroupManagement;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class NameServer extends RemoteServer implements NameServerInterface {

	private static final long serialVersionUID = 1L;
	private NameServerInterface nameServer;
	private static String Name = "NamingService";
	private Triple clientInfo;
	private HashMap<String, String> leaderInfo = new HashMap<String, String>();
	private HashMap<String, ArrayList<String>> groupUserlistMap = new HashMap<String, ArrayList<String>>();
	private ArrayList<Triple> listOfClients = new ArrayList<Triple>();
	private int clientID = 0;

	public static void main(String args[]) {
		try {

			new NameServer();
			System.out.println("Nameserver running on port 1111");

		} catch (AlreadyBoundException e) {
			System.out.println("Nameserver already running on this port!");
//			e.printStackTrace();
		} catch (RemoteException e) {
			System.out.println("Something went wrong with the nameserver!");
//			e.printStackTrace();
		}
	}

	public NameServer() throws RemoteException, AlreadyBoundException {
		bind();
	}

	private void bind() throws RemoteException, AlreadyBoundException {

		int port = 1111;
		this.nameServer = (NameServerInterface) UnicastRemoteObject
				.exportObject(this, 0);
		Registry registry = LocateRegistry.createRegistry(port);
		registry.bind(NameServer.Name, nameServer);
		LocateRegistry.createRegistry(1234);

	}

	public int registerChatClient(String userName) throws RemoteException,
			ServerNotActiveException, UnknownHostException {

		clientID++;
		String str = getClientHost();
		clientInfo = new Triple(clientID, userName, InetAddress.getByName(str));
		listOfClients.add(clientInfo);
		return clientID;
	}

	public boolean createGroup(String groupName, String userName)
			throws RemoteException, ServerNotActiveException {

		ArrayList<String> tempList = new ArrayList<String>();

		if (!groupUserlistMap.containsKey(groupName)) {
			tempList.add(userName);
			groupUserlistMap.put(groupName, tempList);
			leaderInfo.put(groupName, userName);

			for(int i = 0; i < listOfClients.size(); i++) {
				if(userName.equals(listOfClients.get(i).getUsername())) {
					listOfClients.get(i).setGroup(groupName);
				}
			}
			return true;
		}
		return false;
	}

	public boolean addMember(String groupName, String userName)
			throws RemoteException {

		ArrayList<String> tempList = groupUserlistMap.get(groupName);

		if (!(tempList.contains(userName))) {
			tempList.add(userName);
			groupUserlistMap.put(groupName, tempList);

			for(int i = 0; i < listOfClients.size(); i++) {
				if(userName.equals(listOfClients.get(i).getUsername())) {
					listOfClients.get(i).setGroup(groupName);
				}
			}

			return true;
		}
		return false;
	}

	public void removeMemberFromGroup(String groupName, String userName)
			throws RemoteException {

		ArrayList<String> clientList = groupUserlistMap.get(groupName);

		clientList.remove(userName);
		groupUserlistMap.put(groupName, clientList);

		for (int i = 0; i< listOfClients.size(); i++){
			if(listOfClients.get(i).getUsername().equals(userName)){
				listOfClients.remove(i);
			}
		}
	}

	public void leaveServer(String groupName, int ID) throws RemoteException {

		Triple triple;
		for (int i = 0; i < listOfClients.size(); i++) {

			triple = listOfClients.get(i);

			if(ID == triple.getClientID()) {
				if(groupName == null) {
					listOfClients.remove(i);
				}
			}
		}
	}

	public void removeGroup(String groupName) throws RemoteException {
		groupUserlistMap.remove(groupName);
		leaderInfo.remove(groupName);
	}


	public HashMap<String, ArrayList<String>> getGroupsInfo() {

		return groupUserlistMap;
	}

	public HashMap<String, String> getGroupLeaders() {
		return leaderInfo;
	}

	public ArrayList<Triple> getClientList() throws RemoteException {
		return listOfClients;
	}

	public Triple getClientInfo() throws RemoteException {
		return clientInfo;
	}

	public void setClientInfo(Triple clientInfo) throws RemoteException {
		this.clientInfo = clientInfo;
	}

	public ArrayList<Triple> getGroupTriples(String groupName) throws RemoteException {


		ArrayList<Triple> clients = new ArrayList<Triple>();

		Triple triple;
		for (int i = 0; i < listOfClients.size(); i++) {

			triple = listOfClients.get(i);

			if(groupName.equals(triple.getGroup())) {
				clients.add(triple);
			}
		}
		return clients;
	}

	public void updateNewLeader(String groupName, String newLeader) throws RemoteException {
		leaderInfo.remove(groupName);
		leaderInfo.put(groupName, newLeader);
	}
}
