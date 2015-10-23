package Communication;

import java.io.Serializable;


/**
 * Created by Johan on 2015-10-14.
 */
public class TextMessage implements Serializable {

    private int seqNr;
    private String message;
    private String senderUserName;
    private int clientID;


    public  TextMessage(int seqNr, String message, String senderUserName, int clientID){
        this.seqNr = seqNr;
        this.message = message;
        this.senderUserName = senderUserName;
        this.clientID = clientID;
    }
    public int getSeqNr(){
        return seqNr;
    }

    public int getClientID(){
        return clientID;
    }

    public void addStringToMessage(String additionalMessage){
        message = message + additionalMessage;
    }

    public String getMessage(){
        return message;
    }

    public String getSenderUserName() {
        return senderUserName;
    }
}
