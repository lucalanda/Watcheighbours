package interfaces.observer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The <code> ObservableService </code> 
 * provides connected users with report updates,
 * implementing an Observer pattern
 * @author Nicola Landro
 * @author Luca Landa
 */
public interface ObservableService extends Remote {

	void addObserver(RemoteObserver o) throws RemoteException;
	void setObserversList(LinkedBlockingQueue<RemoteObserver> observersList);
	void notityAllObservers(Object updateMsg) throws RemoteException;
	void deleteObserver(RemoteObserver o) throws RemoteException;
}
