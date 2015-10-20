package Communication;

import GroupManagement.Client;
import GroupManagement.ClientInterface;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Johan on 2015-10-14.
 */
public class CommunicationModule {

    private static int timeOutTime = 1000; // Milliseconds
    private int counter;
    private String userName;
    private int clientID;
    private HashMap <Integer, ClientInterface> clientInterfaces;
    private HashMap <Integer, Integer> lastAcceptedSeqNr;
    private HashMap <Integer, Integer> lastSentSeqNr;
    private ArrayList <TextMessage> acceptedMessages = new ArrayList<TextMessage>();


    public CommunicationModule(String userName, int clientID, HashMap <Integer, ClientInterface> clientInterfaces){
        counter = 0;
        this.userName = userName;
        this.clientInterfaces = clientInterfaces;
        this.clientID = clientID;

        this.lastAcceptedSeqNr = new HashMap<Integer, Integer>();
        this.lastSentSeqNr = new HashMap<Integer, Integer>();

        for(int key: clientInterfaces.keySet()){
            lastSentSeqNr.put(key, 0);
            lastAcceptedSeqNr.put(key,0);
        }
    }

    public void sendMessage(String message){
        TextMessage textMessage = new TextMessage(counter, message, userName, clientID);

        for(int key: clientInterfaces.keySet()){
            clientInterfaces.get(key).addMessageToQueue(textMessage);
        }
    }

    public void addMessageToQueue (TextMessage textMessage){
        int id = textMessage.getClientID();

        int seqNr = lastAcceptedSeqNr.get(id);

        if (textMessage.getSeqNr() <= (seqNr+1) ){
            AcceptMessage(textMessage, id);
        } else {
            new InnerThread(id, textMessage);
        }
    }
    private class InnerThread extends Thread {
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

        private void AcceptMessage(TextMessage message, int clientID){
            acceptedMessages.add(message);
            lastAcceptedSeqNr.remove(clientID);
            lastAcceptedSeqNr.put(clientID, message.getSeqNr());

        }
    }
    private void AcceptMessage(TextMessage message, int clientID){
        acceptedMessages.add(message);
        lastAcceptedSeqNr.remove(clientID);
        lastAcceptedSeqNr.put(clientID, message.getSeqNr());

    }

    public void setClientInterface(HashMap<Integer, ClientInterface> clientInterfaces){
        this.clientInterfaces = clientInterfaces;
    }

    public ArrayList<TextMessage> getAcceptedMessages(){

        ArrayList<TextMessage> temp = acceptedMessages;

        // Sort temp
        Collections.sort(temp, new Comparator<TextMessage>() {
                    @Override
                    public int compare(TextMessage t1, TextMessage t2) {
                        return t1.getSeqNr() - t2.getSeqNr(); // Ascending
                    }
                }
        );
        acceptedMessages = new ArrayList<TextMessage>();
        return  temp;
    }

    //public void updateClientInterfaces(HashMap <Integer, ClientInterface> clientInterfaces){
    //    this.clientInterfaces = clientInterfaces;
    //}

    public void addAnotherClientInterface(int clientID, ClientInterface ci){
        clientInterfaces.put(clientID, ci);
        lastAcceptedSeqNr.put(clientID, 0);
        lastSentSeqNr.put(clientID, 0);
    }

    public void removeClientInterface (int clientID){
        clientInterfaces.remove(clientID);
        lastAcceptedSeqNr.remove(clientID);
        lastSentSeqNr.remove(clientID);
    }
}
