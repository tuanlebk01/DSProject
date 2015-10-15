package Communication;

import GroupManagement.ClientInterface;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Johan on 2015-10-14.
 */
public class CommunicationModule {

    private int counter;
    private String userName;
    private int clientID;
    private HashMap<String, ArrayList<String>> groupsInfo;
    private HashMap <Integer, ArrayList<TextMessage>> clientBuffers;
    private Map<Integer, ClientInterface> clientInterfaces;
    private HashMap <Integer, Integer> lastAcceptedSeqNr;


    public CommunicationModule(String userName, HashMap<String, ArrayList<String>> groupsInfo, int clientID){
        counter = 0;
        this.userName = userName;
        this.groupsInfo = groupsInfo;
        this.clientID = clientID;


    }

    public void updateGroupsInfo(HashMap<String, ArrayList<String>> groupsInfo){
        this.groupsInfo = groupsInfo;
    }

    public void sendMessage(String message){
        counter ++;
        TextMessage textMessage = new TextMessage(counter, message, userName, clientID);

        ClientInterface ci;
        for(int key: clientInterfaces.keySet()){
            ci = clientInterfaces.get(key);
            // ci.
        }
    }

    public void addMessageToQueue (TextMessage textMessage){
        int id = textMessage.getClientID();

        int seqNr = lastAcceptedSeqNr.get(id);

        if (textMessage.getSeqNr() <= (seqNr+1) ){
            // Static call to client
            int acceptedSeqNr = lastAcceptedSeqNr.get(id);
            acceptedSeqNr++;
//            lastAcceptedSeqNr.replace(id, acceptedSeqNr-1, acceptedSeqNr);

        } else {
            ArrayList<TextMessage> buffer = clientBuffers.get(id);
            buffer.add(textMessage);
            Collections.sort(buffer, new Comparator<TextMessage>() {
                @Override
                public int compare(TextMessage tm1, TextMessage tm2) {
                    return tm1.getSeqNr() - tm2.getSeqNr(); // Ascending
                }
            });
            //checkBuffer(buffer, id);
        }
    }

//    public void checkBuffer(ArrayList<TextMessage> buffer, int id){
//        int seqNr = lastAcceptedSeqNr.get(id);
//        for (int i=0; i<buffer.size(); i++){
//            if(buffer.get(i).getSeqNr() <= (seqNr+1)){
//                // Static call to client
//                int acceptedSeqNr = lastAcceptedSeqNr.get(buffer.get(i).getClientID());
//                acceptedSeqNr++;
//
//                lastAcceptedSeqNr.replace(buffer.get(i).getClientID(), acceptedSeqNr-1, acceptedSeqNr);
//                buffer.remove(i);
//            }else {
//
//            }
//        }
//    }



}
