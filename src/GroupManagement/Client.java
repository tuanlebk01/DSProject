package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import Application.GUI;


public class Client extends UnicastRemoteObject implements ClientInterface {

	private static final long serialVersionUID = 1L;
	private Registry registry;
	private NameServerInterface ns;

	public Client() throws RemoteException {
		super();
	}

	public void retrieveMessage(String message) throws RemoteException {
		GUI.writeMsg(message);
	}

	public void connectToNameServer(int portNr) throws RemoteException, AlreadyBoundException {


		try {

			this.registry = LocateRegistry.getRegistry("localhost", portNr);
			System.out.println("reg: " + registry);
			this.ns = (NameServerInterface) registry
					.lookup("NamingService");

			ns.registerChatClient("client1");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createGroup(String groupName, String userName) throws RemoteException {

		ns.createGroup(groupName, userName);

	}

	@Override
	public void connectToNameServer() throws RemoteException, AlreadyBoundException {

	}
}
