package server.observable;

import interfaces.observer.ObservableService;
import interfaces.observer.RemoteObserver;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *The class <code>ConcreteObservableService</code> realize the ObservableService interfaces.
 *@author Nicola Landro
 *@version 1.0
 */
public abstract class ConcreteObservableService implements ObservableService, Serializable{

	private static final long serialVersionUID = 1L;
	transient protected BlockingQueue<RemoteObserver> observers;
	
	@Override
	public void addObserver(RemoteObserver o) throws RemoteException {
		observers.add(o);
	}

	@Override
	public void setObserversList(LinkedBlockingQueue<RemoteObserver> observersList) {
		this.observers = observersList;
	}

	@Override
	public synchronized void notityAllObservers(Object updateMsg) throws RemoteException {
		for(RemoteObserver r : observers){
			try {
				r.remoteUpdate(this, updateMsg);
			} catch (RemoteException e) {
				System.out.println("osservatore rimosso, RemoteException");
				e.printStackTrace();
				observers.remove(r);
			}
		}
	}

	@Override
	public void deleteObserver(RemoteObserver o) throws RemoteException {
		observers.remove(o);
	}
}
