package Communication;


/**
 * Created by Johan on 2015-10-14.
 */
public class TextMessage {

    private int seqNr;
    private String message;
    private String senderUserName;


    public  TextMessage(int seqNr, String message, String senderUserName){
        this.seqNr = seqNr;
        this.message = message;
        this.senderUserName = senderUserName;
    }
    public int getSeqNr(){
        return seqNr;
    }

    public String getMessage(){
        return message;
    }

    public String getSenderUserName() {
        return senderUserName;
    }
}
