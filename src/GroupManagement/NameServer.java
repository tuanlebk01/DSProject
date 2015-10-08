package GroupManagement;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;

public class NameServer implements NameServerInterface {

	private static final long serialVersionUID = 1L;
	private NameServerInterface nameServer;
	private static String Name = "NamingService";
	private Hashtable groupLeaderList = new Hashtable();

	public NameServer() throws RemoteException, AlreadyBoundException {
		bind();
		System.out.println("Naming Service Started");
		// TODO Auto-generated constructor stub
	}

	private void bind() throws RemoteException, AlreadyBoundException {
		int port = 1111;
		this.nameServer = (NameServerInterface) UnicastRemoteObject
				.exportObject(this, 0);
		Registry registry = LocateRegistry.createRegistry(port);
		registry.bind(NameServer.Name, nameServer);
		System.out.println("Naming Service Running on port " + port);

	}

	public static void main(String args[]) {
		try {

			new NameServer();

		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String registerChatClient(String Client) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createGroup(String goupName) throws RemoteException {

	}

	@Override
	public void deleteGroup(String groupName) throws RemoteException {
		// TODO Auto-generated method stub
	}

	@Override
	public String getGroupLeaderInfo() throws RemoteException {
		return null;
		
	}

	@Override
	public void updateGroupLeaderInfo(String Groupname) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
