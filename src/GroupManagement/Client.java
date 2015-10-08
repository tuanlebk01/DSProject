package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class Client {

	private String username;
	private String groupName;

	public static void main(String[] args) throws RemoteException, AlreadyBoundException {
		// TODO Auto-generated method stub
		try {
			Registry registry = LocateRegistry.getRegistry("localhost", 1111);
			NameServerInterface ns = (NameServerInterface) registry
					.lookup("NamingService");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
