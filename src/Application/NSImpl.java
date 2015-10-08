package Application;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.util.*;
import java.io.*;

public class NSImpl implements NameServer {

    private static final long serialVersionUID = 1L;
    private NameServer ns;
    private static String Name = "NamingService";

    // Map<String,Member> groupList;
    
    

    public NSImpl() throws RemoteException, AlreadyBoundException {
        // groupList=new HashMap<String,Member>();
        bind();
        System.out.println("Naming Service Started");
        // TODO Auto-generated constructor stub
    }

    private void bind() throws RemoteException, AlreadyBoundException {
        int port = 1111;
        /*
         * String portString = null;
         * 
         * try { Properties prop = new Properties();
         * 
         * InputStream propStream =
         * this.getClass().getResourceAsStream("/gcom.properties");
         * if(propStream != null) { prop.load(propStream); portString =
         * prop.getProperty("gcom.ns.port"); } } catch (IOException e) { //
         * logger.warn("gcom.properties not found"); }
         * 
         * if (portString == null) {// use the default port if it is not set
         * port = Registry.REGISTRY_PORT; } else { try { port =
         * Integer.parseInt(portString); } catch (NumberFormatException e)
         * {//use default port if it is not a number
         * 
         * port = Registry.REGISTRY_PORT; } }
         */

        this.ns = (NameServer) UnicastRemoteObject.exportObject(this, 0);
        Registry registry = LocateRegistry.createRegistry(port);
        registry.bind(NSImpl.Name, ns);
        System.out.println("Naming Service Running on port " + port);

    }

    public String connect(String member) {

        System.out.println("Member " + member + "connected");
        return "welcome" + member;
        // Member leader=groupList.get(member.getGroupName());

        // if(leader==null){
        // groupList.put(member.getGroupName(), member);
        // return member;
        // }
        // else
        // {
        // leader.addGroupLeader(member);//it should be in the if case
        // return leader;
        // }

    }

    public static void main(String args[]) {
        try {

            new NSImpl();

        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
