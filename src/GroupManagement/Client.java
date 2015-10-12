package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

import Application.GUI;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class Client extends UnicastRemoteObject implements ClientInterface {

	private static final long serialVersionUID = 1L;
	private Registry registry;
	private NameServerInterface ns;

	public Client() throws RemoteException {
		super();
	}

<<<<<<< HEAD
	public void retrieveMessage(String message) throws RemoteException {
		GUI.writeMsg(message);
	}

	public void connectToNameServer(int portNr) throws RemoteException, AlreadyBoundException {

=======
	private static final long serialVersionUID = 1L;
	private

	public  void main(String[] args) throws RemoteException, AlreadyBoundException {

>>>>>>> e611aa0e2ba78ca99018e38f65eae1f7daff23e3
		try {
			this.registry = LocateRegistry.getRegistry("localhost", portNr);
			this.ns = (NameServerInterface) registry
					.lookup("NamingService");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createGroup(String groupName, String userName) {

		ns.createGroup(groupName, userName);

	}

	@Override
	public void connectToNameServer() throws RemoteException, AlreadyBoundException {

	}
}
