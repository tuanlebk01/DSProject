package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import Application.GUI;


public class Client extends UnicastRemoteObject implements ClientInterface {

	private static final long serialVersionUID = 1L;
	private Registry registry;
	private NameServerInterface ns;
	private ArrayList <String> groupList = new ArrayList<String>();
	private ArrayList <String> memberList = new ArrayList<String>();

	public Client() throws RemoteException {
		super();
	}

	public void retrieveMessage(String message) throws RemoteException {
		GUI.writeMsg(message);
	}

	public void connectToNameServer(String userName, int portNr) throws RemoteException, AlreadyBoundException, ServerNotActiveException, NotBoundException {

			this.registry = LocateRegistry.getRegistry("Harry.cs.umu.se", portNr);
			this.ns = (NameServerInterface) registry.lookup("NamingService");
			ns.registerChatClient1(userName);
			groupList = ns.getGroupList();

	}

	public void createGroup(String groupName, String userName) throws RemoteException, ServerNotActiveException {

		ns.createGroup(groupName, userName);

	}

	@Override
	public void connectToNameServer() throws RemoteException, AlreadyBoundException {

	}

	public ArrayList<String> getGroupList() {
		return groupList;
	}

	public ArrayList<String> getGroupMembers(String groupName) throws RemoteException {
		return memberList = ns.getMemberInGroup("Group 1");

	}
}
