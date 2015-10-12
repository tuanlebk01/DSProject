package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class GroupLeader implements GroupLeaderInterface {

	private static final long serialVersionUID = 1L;
	private GroupLeaderInterface groupLeader;
	private String nameService;
	private int portNumber;
	private ArrayList<ClientInterface> clientList;

	public GroupLeader(String nameService, int portNumber) throws RemoteException, AlreadyBoundException {
		this.groupLeader = (GroupLeaderInterface) UnicastRemoteObject
				.exportObject(this, 0);
		Registry registry = LocateRegistry.createRegistry(portNumber);
		registry.bind(nameService, groupLeader);
		System.out.println("Naming Service Running on port " + portNumber);
		System.out.println("Group Leader " + nameService + " Started");
	}

	public static void main(String[] args) throws RemoteException, AlreadyBoundException{
		String servera = "serverA";
		new GroupLeader(servera,1234);
	}

	@Override
	public void joinGroup(String userName, String groupName)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void leaveGroup(String userName, String groupName)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void broadcastMessage(String message) throws RemoteException {
		// TODO Auto-generated method stub

	}


}
