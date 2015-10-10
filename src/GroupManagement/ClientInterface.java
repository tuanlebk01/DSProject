package GroupManagement;

import java.rmi.RemoteException;

public interface ClientInterface {
	public void retrieveMessage(String message) throws RemoteException;
}
