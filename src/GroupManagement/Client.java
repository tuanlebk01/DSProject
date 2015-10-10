package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class Client extends UnicastRemoteObject implements ClientInterface {

	protected Client() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;
	private 

	public  void main(String[] args) throws RemoteException, AlreadyBoundException {
		
		try {
			Registry registry = LocateRegistry.getRegistry("localhost", 1111);
			NameServerInterface ns = (NameServerInterface) registry
					.lookup("NamingService");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void retrieveMessage(String message) throws RemoteException {
		System.out.println(message);
	}

}
