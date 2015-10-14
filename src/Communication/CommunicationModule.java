package Communication;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Johan on 2015-10-14.
 */
public class CommunicationModule {

    private int counter;
    private String userName;
    private HashMap<String, ArrayList<String>> groupsInfo;

    public CommunicationModule(String userName, HashMap<String, ArrayList<String>> groupsInfo){
        counter = 0;
        this.userName = userName;
        this.groupsInfo = groupsInfo;
    }

    public void updateGroupsInfo(HashMap<String, ArrayList<String>> groupsInfo){
        this.groupsInfo = groupsInfo;
    }

    public void sendMessage(String message){
        counter ++;
        TextMessage textMessage = new TextMessage(counter, message, userName);


        
        // RMI SEND MESSAGE
    }
}
