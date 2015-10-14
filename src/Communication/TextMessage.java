package Communication;


/**
 * Created by Johan on 2015-10-14.
 */
public class TextMessage {

    private int seqNr;
    private String textMessage;
    private String senderUserName;


    public  TextMessage(int seqNr, String textMessage, String senderUserName){
        this.seqNr = seqNr;
        this.textMessage = textMessage;
        this.senderUserName = senderUserName;
    }
    public int getSeqNr(){
        return seqNr;
    }

    public String getTextMessage(){
        return textMessage;
    }

    public String getSenderUserName() {
        return senderUserName;
    }
}
