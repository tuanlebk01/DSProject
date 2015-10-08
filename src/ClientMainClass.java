 
public class ClientMainClass{
    public static void main(String[] args){
        try{
            CommonInterface remoteObjectInstance = ServerConnector.getRemoteObject();
            remoteObjectInstance.method1(remoteObjectInstance);
            //prints "Successfully Invoked Method1" on Client console
             
            remoteObjectInstance.method2(remoteObjectInstance);
            //prints "Successfully Invoked Method3" on Server console
            //prints "Successfully Invoked Method2" on Server console
             
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}