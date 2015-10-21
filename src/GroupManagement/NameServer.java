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
import java.util.Iterator;
import java.util.Map;

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

		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
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
		System.out.println("NS: Naming Service Running on port " + port);

	}

	public int registerChatClient(String userName) throws RemoteException,
			ServerNotActiveException, UnknownHostException {

		clientID++;
		String str = getClientHost();
		System.out.println(str);
		clientInfo = new Triple(clientID, userName, InetAddress.getByName(str));

		listOfClients.add(clientInfo);

		System.out.println("NS: Connected: " + clientInfo.getClientID() + " - " + clientInfo.getUsername() +" - " + clientInfo.getIp());
		return clientID;
	}

	private class Triple {

		private int clientID;
		private String username;
		private InetAddress ip;
		private String group;

		public Triple (int clientID,String username, InetAddress ip){
			this.clientID = clientID;
			this.username = username;
			this.ip = ip;
		}

		public int getClientID() {
			return clientID;
		}

		public void setClientID(int clientID) {
			this.clientID = clientID;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public InetAddress getIp() {
			return ip;
		}

		public void setIp(InetAddress ip) {
			this.ip = ip;
		}

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}
	}

	public boolean createGroup(String groupName, String userName)
			throws RemoteException, ServerNotActiveException {

		ArrayList<String> tempList = new ArrayList<String>();

		if (!groupUserlistMap.containsKey(groupName)) {
			tempList.add(userName);
			groupUserlistMap.put(groupName, tempList);
			leaderInfo.put(groupName, userName);
			System.out.println("NS: Group created with name: " + groupName);
			System.out.println("Groups: " + groupUserlistMap.keySet());
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
			return true;
		}
		return false;
	}

	public void removeMemberFromGroup(String groupName, String userName)
			throws RemoteException {

		ArrayList<String> clientList = groupUserlistMap.get(groupName);

		clientList.remove(userName);
		groupUserlistMap.put(groupName, clientList);

		System.out.println("NS: User: " + userName + " left group: " + groupName);
	}

	public void leaveServer(int ID) throws RemoteException {

//		if(groupName == null) {

//			System.out.println("NS: The following user left the server: " + ClientInfo.get(ID));
//			ClientInfo.remove(ID);

//		} else {
//			System.out.println("NS: Leader: " + ClientInfo.get(ID) + " left in group: " + groupName + ". Start election");
//			ClientInfo.remove(ID);

//		}
	}

	public void removeGroup(String groupName) throws RemoteException {

		groupUserlistMap.remove(groupName);
		System.out.println("Group: " + groupName + " was removed");

	}


	public HashMap<String, ArrayList<String>> getGroupsInfo() {

		return groupUserlistMap;
	}

	public HashMap<String, String> getGroupLeaders() {

		return leaderInfo;
	}

	@Override
	public void updateGroupLeaderInfo(String Groupname) throws RemoteException {

	}

}
