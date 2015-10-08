import java.rmi.Naming;
 
public class ServerConnector{
    public static CommonInterface getRemoteObject() throws Exception{
        CommonInterface remoteObjectInstance = (CommonInterface) Naming.lookup("rmi://localhost:1312/ChatRoom"); //localhost can be replace with server machine IP
        return remoteObjectInstance;
    }
}