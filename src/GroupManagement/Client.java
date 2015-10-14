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

public class Client extends UnicastRemoteObject implements ClientInterface {

	private static final long serialVersionUID = 1L;
	private Registry registry;
	private NameServerInterface ns;
	private ClientInterface client;
	private HashMap<String, ArrayList<String>> groupMap = new HashMap<String, ArrayList<String>>();
	private int clientID;
	private boolean groupCreated;

	public Client() throws RemoteException {
		super();
	}

	public void retrieveMessage(String message) throws RemoteException {
		GUI.writeMsg(message);
	}

	public int connectToNameServer(String userName, int portNr)
			throws RemoteException, AlreadyBoundException,
			ServerNotActiveException, NotBoundException {

		this.registry = LocateRegistry.getRegistry("localhost", portNr);
		this.ns = (NameServerInterface) registry.lookup("NamingService");
		ns.registerChatClient(userName);
		return clientID;

	}

	public boolean createGroup(String groupName, String userName)
			throws RemoteException, ServerNotActiveException, NotBoundException {

		this.registry = LocateRegistry.getRegistry("localhost", 1112);
		System.out.println("1");
		this.client = (ClientInterface) registry.lookup("Clientlookup");
		System.out.println("2");

		groupCreated = ns.createGroup(groupName, userName);

		return groupCreated;

	}

	@Override
	public void connectToNameServer() throws RemoteException,
			AlreadyBoundException {

	}

	public HashMap<String, ArrayList<String>> getGroups()
			throws RemoteException {

		return groupMap = ns.getGroupsInfo();

	}

	public void joinGroup(String groupName) throws RemoteException,
			ServerNotActiveException {

	}

	public void broadcastMessage() {

	}

}
