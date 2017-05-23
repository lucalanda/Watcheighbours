package interfaces.observer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The <code>RemoteObserver</code>
 * provides Watchneighbours server with methods to send connected users,
 * implementing an Observer pattern
 * report updates 
 * @author Nicola Landro
 * @author Luca Landa
 */
public interface RemoteObserver extends Remote {
	
	void remoteUpdate(Object observable, Object updateMsg) throws RemoteException;
	
	default void isConnected() throws RemoteException {

	}
	
	
}
