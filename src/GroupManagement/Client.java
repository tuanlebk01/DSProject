package GroupManagement;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	public static void main(String[] args) throws RemoteException,

	AlreadyBoundException {
		// TODO Auto-generated method stub
		try {
			Registry registry = LocateRegistry.getRegistry("localhost", 1111);
			// NS nameService=new NSImpl();
			NS ns = (NS) registry.lookup("NamingService");
			String message = ns.connect("client1 connected");
			System.out.println(message);

			// System.out.print("client1 connected");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
