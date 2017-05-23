package server;

import interfaces.ConnectionBalancerInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import util.IPRetriever;

/**
 *The class <code>RMIServerMain</code>
 *contain the part of server that launch the RMI_Server_Balancer, and contains the main method. 
 *@author Nicola Landro
 *@version 1.0
 */
public class RMIServerMain {
	private static Registry registry;
	/**
	 * The main method make server ready.
	 * @param args some input, not required
	 */
	public static void main (String args[]) {
		try{
			//This istruction corrects a Bug in linux O.S.
			IPRetriever.linux_RMI_BugCorrection();
			RMI_Server_Balancer obj= new RMI_Server_Balancer( ) ;
			ConnectionBalancerInterface stub= (ConnectionBalancerInterface) UnicastRemoteObject.exportObject(obj,0) ;
			//registry= LocateRegistry.getRegistry( ) ;
			registry= LocateRegistry.createRegistry(1099);
			registry.rebind("Balancer",stub) ;
			System.out.println("Serverready") ;
			}catch( Exception e) {
				System.err.println( "Server Main exception:"+e.toString( ) ) ;
			}
	}

}
