package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import exceptions.WrongDataException;
import interfaces.observer.RemoteObserver;
import util.Report;
import util.User;

/**
 * The <code> SubUrbInterface </code>
 * provides the user with services to perform operations after access
 * to the application, like report and user account operations, if logged
 * @author Nicola Landro
 * @author Luca Landa
 */
public interface SubUrbInterface extends Remote{	
	
	/**
	 * Sends a new report to Watchneighbours server
	 * @param newReport the new report created
	 * @return success if the new report data is correct
	 * @throws RemoteException if a connection issue occurred
	 */
	public boolean sendReport(Report newReport) throws RemoteException;
	
	/**
	 * Takes charge of an existing report
	 * @param existingReport the report to take charge of
	 * @param user the logged user which is taking charge of the report
	 * @return success of the operation, returns false if a server synchronizing issue occurred
	 * @throws RemoteException if a connection issue occurred
	 */
	public boolean takeChargeOf(Report existingReport, User user) throws RemoteException;
	
	/**
	 * Notifies the server that the given observer is not listening for updates anymore
	 * @param observerID the observer's ID
	 * @param observer the observer listening for updates
	 * @throws RemoteException if a connection issue occurred
	 */
	public void exit(String observerID, RemoteObserver observer) throws RemoteException;
	
	/**
	 * Updates a user account with new data
	 * @param oldUser the old user account to update
	 * @param newUser the new user account updated
	 * @return success of the operation
	 * @throws WrongDataException if the updated account contains wrong data
	 * @throws RemoteException if a connection issue occurred
	 */
	public boolean updateProfile(User oldUser, User newUser) throws WrongDataException, RemoteException;
	
	/**
	 * Closes a report actually in "in charge" state
	 * @param existingReport the report to close
	 * @param user the user which is closing the report
	 * @param result the report result
	 * @return success of the operation
	 * @throws RemoteException if a connection issue occurred
	 * @throws WrongDataException if the given result is not acceptable
	 */
	public boolean closeReport(Report existingReport, User user, String result) throws RemoteException, WrongDataException;
	
	/**
	 * Adds an observer for the <code> SubUrbInterface </code> instance,
	 * in order to receive suburb reports and report updates
	 * @param observerID the observed ID
	 * @param observer the remote observer
	 * @return ArrayList with actual reports not closed yet for the specific suburb
	 * @throws RemoteException if a connection issue occurred
	 */
	public ArrayList<Report> addObserver(String observerID, RemoteObserver observer) throws RemoteException;
	
	/**
	 * Deletes an user account from Watchneighbours server
	 * @param user the user account
	 * @return success of the operation
	 * @throws RemoteException if a connection issue occurred
	 */
	public boolean removeUser(User user) throws RemoteException;
	
}
