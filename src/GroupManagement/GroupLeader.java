package GroupManagement;

import java.rmi.RemoteException;
import java.util.ArrayList;


public class GroupLeader implements GroupLeaderInterface {

	private static final long serialVersionUID = 1L;
	private NameServerInterface nameServer;
	private ArrayList<ClientInterface> clientList;
	

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

	@Override
	public void getClientList() throws RemoteException {
		// TODO Auto-generated method stub

	}

}
