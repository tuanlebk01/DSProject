package Communication;


import GroupManagement.ClientInterface;
import GroupManagement.NameServerInterface;
import GroupManagement.Triple;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.*;

/** Communication module, connects from one to many clients with basic multicast.
 *  It invokes an object over the interface to directly add the messages to the other clients queues.
 *
 * Created by Johan Hagner - c11jhr
 */
public class CommunicationModule {

    private int counter=0;
    private String userName;
    private int clientID;
    private HashMap <Integer, ClientInterface> clientInterfaces;
    private HashMap <Integer, Integer> lastAcceptedSeqNr;
    private ArrayList <TextMessage> acceptedMessages = new ArrayList<>();
    private Registry registry;
	private ArrayList<Triple> clients;


    /** The communication module keep track of the sequence numbers received by any client.
     *
     * @param userName - userName of the client that created the communication module
     * @param clientID - clientId of the client that created the communication module
     * @param clientInterfaces - A hashmap of all clientIds and clientInterfaces to all the clients in the group
     * @param clients
     */
    public CommunicationModule(String userName, int clientID, ArrayList<Triple> clients, Registry registry){
        counter = 0;
        this.userName = userName;
        this.clientID = clientID;
        this.clients = clients;
        this.registry = registry;

        this.lastAcceptedSeqNr = new HashMap<>();

        for(int i = 0; i < clients.size(); i++ ) {
        	lastAcceptedSeqNr.put(i, 0);
        }
    }

    /** Sends the message to all clientInterfaces that the communication module have knowledge of.
     *
     * @param message - The string that the message contains
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void sendMessage(String message) throws RemoteException, NotBoundException{
        TextMessage textMessage = new TextMessage(counter, message, userName, clientID);
        ClientInterface ci;

//        Registry = registry;
        for(int i= 0; i<clients.size(); i++){
//        	System.out.println(key + " " + clientInterfaces.get(key));
//        	ClientInterface ac = (ClientInterface) registry.lookup("key");
//        	clientInterfaces.get(key).addMessageToQueue(textMessage);

            ci = (ClientInterface) registry.lookup("username");
            ci.addMessageToQueue(textMessage);

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
    public void sendMessagesInRandomOrder(int numberOfMessages) throws RemoteException{

        List<TextMessage> messages = new ArrayList<>();
        for (int i = 0; i < numberOfMessages; i++)
        {
            messages.add(new TextMessage(i, "Textmessage nr: " + i, userName, clientID));
        }
        Collections.shuffle(messages);

        for (int j=0; j<=messages.size();j++){
            for(int key: clientInterfaces.keySet()){
                messages.get(j).addStringToMessage(" -  Was sent as message nr: "+j);
                clientInterfaces.get(key).addMessageToQueue(messages.get(j));
            }
            counter++;
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

        System.out.println("ERROR: " + userName);

        int seqNr = lastAcceptedSeqNr.get(id);

        if (textMessage.getSeqNr() <= (seqNr+1) ){
            AcceptMessage(textMessage, id);
        } else {
            new InnerThread(id, textMessage);
        }
    }

    /** This is the thread that holds a message if it arrived in the wrong order until it can be accepted in right
     * order of time out.
     *
     */
    private class InnerThread extends Thread {
        private int timeOutTime = 1000; // Milliseconds
        private boolean timedOut = false;
        private int clientID;
        private TextMessage message;

        InnerThread(int clientID, TextMessage message) {
            this.clientID = clientID;
            this.message = message;
            start();
        }

        public void run() {
            long time1 = System.currentTimeMillis();
            long time2;
            while (!timedOut){

                time2 = System.currentTimeMillis();
                if(time2 >= time1 + timeOutTime){
                    timedOut = true;
                }
                if (message.getSeqNr() <= (lastAcceptedSeqNr.get(clientID))+1){
                    timedOut = true;
                }
            }
            AcceptMessage(message, clientID);
        }

        /** Accepts a message and adds it to the list of accepted messages. It also updates the lastAccepted message if
         * the sequence number is greater than the lastAccepted.
         *
         * @param message - Message to accept
         * @param clientID - clientID of the client that sent the message.
         */
        private void AcceptMessage(TextMessage message, int clientID){
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
    private void AcceptMessage(TextMessage message, int clientID){
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
    public ArrayList<TextMessage> getAcceptedMessages(){

        ArrayList<TextMessage> temp = acceptedMessages;

        acceptedMessages = new ArrayList<>();
        return  temp;
    }

    /** Add another client interface to the communication module. A counter for the sequence number from that client is
     * also added.
     *
     * @param clientID - clientId of the client to be added.
     * @param ci - clientInterface to of the client to be added.
     */
    public void addAnotherClientInterface(int clientID, ClientInterface ci){
        clientInterfaces.put(clientID, ci);
        lastAcceptedSeqNr.put(clientID, 0);
    }

    /** Removes a client from the communication module. The counter for the sequence number is removed. It is important
     * that this method is called whenever a client leaves a group so it the communication module does not try to send
     * messages to that client any longer.
     *
     * @param clientID - clientId of the client that left the group.
     */
    public void removeClientInterface (int clientID){
        clientInterfaces.remove(clientID);
        lastAcceptedSeqNr.remove(clientID);
    }
}
