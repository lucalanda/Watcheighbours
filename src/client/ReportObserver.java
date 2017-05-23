package client;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

import GUI.controllers.MainWindowController;
import interfaces.observer.AddReportService;
import interfaces.observer.DeleteReportService;
import interfaces.observer.ObservableService;
import interfaces.observer.RemoteObserver;
import interfaces.observer.UpdateReportService;
import util.IPRetriever;
import util.Report;

/**
 * The <code> ReportObserver </code> instance receives and applies reports' updates from the Watchneighbours
 * server, with a ReportUpdater instance
 * @author Luca Landa
 */
public class ReportObserver extends UnicastRemoteObject implements RemoteObserver {

	private static final long serialVersionUID = 1L;
	private ReportUpdater reportUpdater;
	private String id;
	
	
	/**
	 * Instance of ReportObserver keeps MainView's reports list up to date, applying updates
	 * received from Watchneighbourss server. 
	 * This constructor automatically generates his own ID for the ReportObserver instance
	 * @param view the main view of client's application
	 * @throws RemoteException if a connection issue occurred
	 */
	public ReportObserver(MainWindowController view) throws RemoteException {
		super();
		this.reportUpdater = new ReportUpdater(view);
		reportUpdater.start();
		this.id = generateID();
	}
	
	/**
	 * Instance of ReportObserver keeps MainView's reports list up to date, applying updates
	 * received from Watchneighbourss server. 
	 * @param view the main view of client's application
	 * @param id the id to assign to the instance
	 * @throws RemoteException if a connection issue occurred
	 */
	public ReportObserver(MainWindowController view, String id) throws RemoteException {
		this(view);
		if(id == null)
			this.id = generateID();
		else
			this.id = id;
	}

	
	
	@Override
	/**
	 * Method called by Watchneighbours server to update the client about a new report 
	 * to add, or a new existing report's state
	 * @param observable the observable instance, identifying the operation to perform 
	 * @param updateMsg, the report subject of the operation to perform
	 */
	public void remoteUpdate(Object observable, Object updateMsg) throws RemoteException {
		if(!(updateMsg instanceof Report))
			throw new RuntimeException("The message is not a report");
		
		if(!(observable instanceof ObservableService))
			throw new RuntimeException("First parameter is not an instance of ObservableService");
		
		Report report = (Report) updateMsg;
		
		if(observable instanceof AddReportService)
			reportUpdater.putAddOperation(report);
		
		else if(observable instanceof UpdateReportService)
			reportUpdater.putUpdateOperation(report);
		
		else if(observable instanceof DeleteReportService)
			reportUpdater.putDeleteOperation(report);
		
		else
			throw new RuntimeException("Could't find the type of observer received");
		
	}

	private String generateID() {
		String result = "";
		try {
			String ip = IPRetriever.getMyIP();
			String localHostIP = IPRetriever.getLocalHostIP();
			result += ip;
			result += "_" + localHostIP + "_";
		} catch(ConnectException | UnknownHostException e) {
			//nothing to do, the id will be totally random generated
		}
		Random random = new Random();
		while(result.length() < 30) {
			boolean b = random.nextBoolean();
			if(b) 
				result += random.nextInt(10);
			else {
				int letter = random.nextInt(26);
				result += (char) (letter + 65);
			}
		}
		
		return result;
	}
	
	@Override
	/**
	 * Terminates ReportUpdater thread
	 */
	public void finalize() {
		reportUpdater.interrupt();
	}
	
	/**
	 * Gets instance's ID used to be identified by Watchneighbours server
	 * @return ID instance's id
	 */
	public String getID() {
		return id;
	}

}
