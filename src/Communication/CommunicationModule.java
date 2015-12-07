package Communication;


import GroupManagement.ClientInterface;
import GroupManagement.Triple;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/** Communication module, connects from one to many clients with basic multicast.
 *  It invokes an object over the interface to directly add the messages to the other clients queues.
 *
 * Created by Johan Hagner - c11jhr
 */
public class CommunicationModule {
	private int timeOutTime = 5000; // Milliseconds
	private int counter=0;

	// NEW --
	private ArrayList<TextMessage> outgoingMessageQueue;
	private ArrayList<TextMessage> incomingMessageQueue;
	private ArrayList<TextMessage> waitingQueue;
	private boolean holdOutgoingMessages = false; // should start false
	private boolean holdIncomingMessages = false; // should start false

	private String userName;
	private int clientID;
	private HashMap <Integer, Integer> lastAcceptedSeqNr;
	private ArrayList <TextMessage> acceptedMessages = new ArrayList<>();
	private ArrayList<Triple> clients;
	private boolean ordered=true;




	/** The communication module keep track of the sequence numbers received by any client.
	 *
	 * @param userName - userName of the client that created the communication module
	 * @param clientID - clientId of the client that created the communication module
	 * @param clients
	 */
	public CommunicationModule(String userName, int clientID, ArrayList<Triple> clients){
		counter = 1;
		this.userName = userName;
		this.clientID = clientID;
		this.clients = clients;

		// NEW --
		outgoingMessageQueue = new ArrayList<>();
		incomingMessageQueue = new ArrayList<>();
		waitingQueue = new ArrayList<>();

		this.lastAcceptedSeqNr = new HashMap<>();


		for(int i = 0; i < clients.size(); i++ ) {
			lastAcceptedSeqNr.put(clients.get(i).getClientID(), 0);
		}

	}

	/** Sends the message to all clientInterfaces that the communication module have knowledge of.
	 *
	 * @param message - The string that the message contains
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public void sendMessage(String message) throws RemoteException, NotBoundException{
		TextMessage textMessage = null;
		ClientInterface ci;
		textMessage = new TextMessage(counter, message, userName, clientID);
		Registry registry;
		boolean  meetCondition= false;
		int l = 0;
		ArrayList<String> crashedUser = new ArrayList<>();

		// NEW (if and else statement)
		if(holdOutgoingMessages){
			outgoingMessageQueue.add(textMessage);
		} else {
			for(int i= 0; i < clients.size(); i++){
				if(clients.get(i).getClientID() == clientID){
					addMessageToQueue(textMessage);
				} else {
					registry = LocateRegistry.getRegistry(clients.get(i).getIp().toString().split("/")[1], 1234);
					try {
						ci = (ClientInterface) registry.lookup(clients.get(i).getUsername());
						if (ci != null) {
							ci.addMessageToQueue(textMessage);
						}
					} catch (Exception e) {
						crashedUser.add(clients.get(i).getUsername());
						System.out.println("crashed clients: " + clients.get(i).getUsername());
					}
				}
			}

			if (crashedUser.size() > 0) {
				
				for (int j = 0; j < clients.size(); j++) {
					if (clients.get(j).getUsername().equals(userName)) {
						l = j;
					}
				}
				for (int i = 0; i < crashedUser.size(); i++) {
					try {
						Registry registryOfSender;
						System.out.println("XXX: "  + clients.get(l).getUsername());
						System.out.println("ip: " + clients.get(l).getIp().toString().split("/")[1]);
						registryOfSender = LocateRegistry.getRegistry(clients.get(l).getIp().toString().split("/")[1], 1234);
						ci = (ClientInterface) registryOfSender.lookup(clients.get(l).getUsername()); // use interface of sender
						System.out.println("crashed user in try catch: "+crashedUser.get(i));
						ci.handleError(crashedUser.get(i));
						// latest version
					} catch (Exception e) {
						System.out.println("handle error....");
						e.printStackTrace();
					}
				}
			}
		}
		counter++;
	}

	/** This method sends a number of testMessages in a random order to all the clientInterfaces. The number of messages
	 *  is specified by the parameter. These messages are considered to be legit messages and are handled the same way
	 *  as any other message. The actual string is predetermined and follows the following format:
	 *      - "Textmessage nr: x" where x is the number of the message.
	 *
	 * @param numberOfMessages - number of messages to send
	 * @throws RemoteException
	 */
	public void sendMessagesInRandomOrder(int numberOfMessages) throws RemoteException, java.rmi.NotBoundException{

		List<TextMessage> messages = new ArrayList<>();
		ClientInterface ci;
		int tempCounter = 0;
		for (int i = 0; i < numberOfMessages; i++)
		{
			messages.add(new TextMessage(i+counter, "Seq nr: " + (i+counter), userName, clientID));
			tempCounter++;
		}
		counter = counter + tempCounter;

		Collections.shuffle(messages);

		for (int l=0; l<messages.size();l++){
			messages.get(l).addStringToMessage(" -  Was sent as message nr: "+l);
		}

		for (int j=0; j<messages.size();j++){
			for(int k= 0; k < clients.size(); k++){
				Registry registry = LocateRegistry.getRegistry(clients.get(k).getIp().toString().split("/")[1], 1234);
				ci = (ClientInterface) registry.lookup(clients.get(k).getUsername());
				if (messages.get(j)!= null){
					ci.addMessageToQueue(messages.get(j));
				}
			}
		}
	}
	public void sendMessageWithOneDrop(int numberOfMessages) throws RemoteException, java.rmi.NotBoundException{

		List<TextMessage> messages = new ArrayList<>();
		ClientInterface ci;
		int tempCounter = 0;
		for (int i = 0; i < numberOfMessages; i++)
		{
			messages.add(new TextMessage(i+counter, "Seq nr: " + (i+counter), userName, clientID));
			tempCounter++;
		}

		counter = counter + tempCounter;

		messages.remove(messages.size()/2);

		for (int j=0; j<messages.size();j++){
			for(int k= 0; k < clients.size(); k++){
				Registry registry = LocateRegistry.getRegistry(clients.get(k).getIp().toString().split("/")[1], 1234);
				ci = (ClientInterface) registry.lookup(clients.get(k).getUsername());
				ci.addMessageToQueue(messages.get(j));
			}
		}
	}


	/** This method receives a message and determines if the message should be accepted in the right order or if it
	 * arrived in the wrong order. In that case the message is held of until the right message arrives or the first
	 * message times out.
	 *
	 * WARNING - this method should only be invoked from another client's communication module
	 *
	 * @param textMessage - The text message to arrive
	 */
	public void addMessageToQueue (TextMessage textMessage){
		int id = textMessage.getClientID();
		if(holdIncomingMessages){
			incomingMessageQueue.add(textMessage);
		} else {
			if(ordered){

				int seqNr = lastAcceptedSeqNr.get(id);

				if (textMessage.getSeqNr() <= (seqNr+1) ){
					acceptMessage(textMessage, id);
				} else {
					new InnerThread(id, textMessage);
				}
			} else {
				acceptMessage(textMessage, textMessage.getClientID());
			}
		}
	}

	/** This is the thread that holds a message if it arrived in the wrong order until it can be accepted in right
	 * order of time out.
	 *
	 */
	private class InnerThread extends Thread {

		private boolean timedOut = false;
		private boolean accepted = false;
		private int clientID;
		long time1;
		private TextMessage message;

		InnerThread(int clientID, TextMessage message) {
			this.clientID = clientID;
			this.message = message;
			waitingQueue.add(message);
			start();
		}

		public void run() {

			time1 = System.currentTimeMillis();
			long time2;
			while (!timedOut){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				time2 = System.currentTimeMillis();
				if(time2 >= time1 + timeOutTime){
					timedOut = true;
				}
				checkIfAcceptMessage(message);
			}
			if(!accepted){
				AcceptMessage(message, clientID);
			}
		}



		private synchronized void checkIfAcceptMessage(TextMessage message){

			if (message.getSeqNr() == (lastAcceptedSeqNr.get(clientID))+1){
				AcceptMessage(message, clientID);
				accepted = true;
			}
		}

		/** Accepts a message and adds it to the list of accepted messages. It also updates the lastAccepted message if
		 * the sequence number is greater than the lastAccepted.
		 *
		 * @param message - Message to accept
		 * @param clientID - clientID of the client that sent the message.
		 */
		private synchronized void AcceptMessage(TextMessage message, int clientID){
			waitingQueue.remove(message);
			acceptedMessages.add(message);
			if(message.getSeqNr()>lastAcceptedSeqNr.get(clientID)){
				lastAcceptedSeqNr.remove(clientID);
				lastAcceptedSeqNr.put(clientID, message.getSeqNr());
			}
		}
	}

	/** Duplicate of the AcceptMessage because of scope for runnable InnerThread.
	 *
	 */
	public synchronized void acceptMessage(TextMessage message, int clientID){
		waitingQueue.remove(message);
		acceptedMessages.add(message);
		if(message.getSeqNr()>lastAcceptedSeqNr.get(clientID)){
			lastAcceptedSeqNr.remove(clientID);
			lastAcceptedSeqNr.put(clientID, message.getSeqNr());
		}
	}

	/** This method returns the list of messages that have been accepted and in the right order. The list of accepted
	 * messages is then cleared, ready to filled again.
	 *
	 * @return - Array list of messages
	 */
	public synchronized ArrayList<TextMessage> getAcceptedMessages(){

		ArrayList<TextMessage> temp = acceptedMessages;

		acceptedMessages = new ArrayList<>();
		return  temp;
	}

	/** Add another client interface to the communication module. A counter for the sequence number from that client is
	 * also added.
	 */
	public void addAnotherClientInterface(Triple triple){
		if(!triple.getUsername().equals(userName)){
			//clients.add(triple);
			lastAcceptedSeqNr.put(triple.getClientID(), 0);
		}
	}

	/** Removes a client from the communication module. The counter for the sequence number is removed. It is important
	 * that this method is called whenever a client leaves a group so it the communication module does not try to send
	 * messages to that client any longer.
	 *
	 */
	public void removeClientInterface (Triple triple){
		clients.remove(triple);
		lastAcceptedSeqNr.remove(triple.getClientID());
	}


	public void setOrdered(boolean ordered){
		this.ordered = ordered;
	}

	public void setClientList(ArrayList<Triple> clients){
		this.clients = clients;
	}

	public HashMap<Integer, Integer> getLastAcceptedSeqNr(){
		return lastAcceptedSeqNr;
	}

	public void  setLastAcceptedSeqNr(HashMap<Integer, Integer> lastAcceptedSeqNr){
		this.lastAcceptedSeqNr = lastAcceptedSeqNr;
	}


	// NEW --
	public void releaseMsgFromIncomingQuene(TextMessage textMessage){
		int id = textMessage.getClientID();
		if(ordered){

			int seqNr = lastAcceptedSeqNr.get(id);

			if (textMessage.getSeqNr() <= (seqNr+1) ){
				acceptMessage(textMessage, id);
			} else {
				new InnerThread(id, textMessage);
			}
		} else {
			acceptMessage(textMessage, textMessage.getClientID());
		}
		incomingMessageQueue.remove(textMessage);
	}

	// NEW --
	public void releaseMsgFromOutgoingQuene(TextMessage textMessage) throws RemoteException, NotBoundException{

		ClientInterface ci;
		Registry registry;
		boolean timeOut = false;
		int l = 0;
		for(int i= 0; i < clients.size(); i++){
			if(clients.get(i).getClientID() == clientID){
				addMessageToQueue(textMessage);
			} else {
				registry = LocateRegistry.getRegistry(clients.get(i).getIp().toString().split("/")[1], 1234);
				try {
					ci = (ClientInterface) registry.lookup(clients.get(i).getUsername());
					if (ci != null) {
						ci.addMessageToQueue(textMessage);
					}
				} catch (Exception e) {
					timeOut = true;
				}
				if (timeOut) {
					for (int j = 0; j < clients.size(); j++) {
						if (clients.get(j).getUsername().equals(userName)) {
							l = j;
						}
					}
					ci = (ClientInterface) registry.lookup(clients.get(l).getUsername()); // use interface of sender
					ci.handleError(clients.get(i).getUsername());
				}
			}
		}
		outgoingMessageQueue.remove(textMessage);
	}

	// NEW --
	public ArrayList<TextMessage> getOutgoingMessageQueue(){
		return outgoingMessageQueue;
	}

	// NEW --
	public ArrayList<TextMessage> getIncomingMessageQueue(){
		return incomingMessageQueue;
	}

	// NEW --
	public ArrayList<TextMessage> getWaitingMessageQueue(){
		return waitingQueue;
	}

	// NEW --
	public void setTimeOutTime(int timeOutTime){
		this.timeOutTime = timeOutTime;
	}

	// NEW --
	public void setHoldOutgoingMessages(boolean b){
		holdOutgoingMessages = b;
	}

	// NEW --
	public void setHoldIncomingMessages(boolean b){
		holdIncomingMessages = b;
	}
}
