package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import util.User;
import exceptions.NotExistingUserIDException;
import exceptions.UserNotConfirmedException;
import exceptions.WrongConfirmationCodeException;
import exceptions.WrongDataException;
import exceptions.WrongPasswordException;

/**
 * The <code> ConnectionBalancerInterface </code> 
 * provides the user with services to perform initial operations to connect
 * the client to Watchneighbours' server for logIn, subscription, access and subscription utils operations
 * @author Nicola Landro
 * @author Luca Landa
 */
public interface ConnectionBalancerInterface extends Remote {
	
	/**
	 * Executes a logIn operation
	 * @param userID the user account ID
	 * @param password the user account password
	 * @return User the instance of connected user account
	 * @throws NotExistingUserIDException if the given ID has not been registered yet
	 * @throws WrongPasswordException if the given password is not correct
	 * @throws UserNotConfirmedException if the user has not been confirmed yet
	 * @throws RemoteException if cannot connect to Watchneighbours' sever
	 */
	public User logIn(String userID, String password) throws NotExistingUserIDException, WrongPasswordException, UserNotConfirmedException,RemoteException;
	
	/**
	 * Confirms a user account registration
	 * @param userID the user account ID
	 * @param password the user account password
	 * @param registrationCode the user confirmation code received as email
	 * @return the instance of connected user account
	 * @throws RemoteException if cannot connect to Watchneighbours' server
	 * @throws WrongConfirmationCodeException if the given confirmation code is wrong
	 * @throws NotExistingUserIDException if the given ID has not been registered yet
	 * @throws WrongPasswordException if the given password is not correct
	 * @throws UserNotConfirmedException in case of errors with the user confirmation operation
	 */
	public User confirmRegistration(String userID, String password, String registrationCode) throws RemoteException, WrongConfirmationCodeException, NotExistingUserIDException, WrongPasswordException, UserNotConfirmedException;
	
	/**
	 * Submits a new user account to Watchneighbours' server
	 * @param newUser the user account submitted
	 * @return success of the operation
	 * @throws WrongDataException if the account submitted contains wrong data
	 * @throws RemoteException if cannot connect to Watchneighbours' server
	 */
	public boolean register(User newUser) throws WrongDataException, RemoteException;
	
	/**
	 * Checks for an user ID availability
	 * @param userID the user id to verify
	 * @return availability of the ID
	 * @throws RemoteException if cannot connect to Watchneighbours' server
	 */
	public boolean userIDAvailable(String userID) throws RemoteException;
	
	/**
	 * Checks for an email address availability
	 * @param email the email to verify
	 * @return availability of the email address
	 * @throws RemoteException if cannot connect to Watchneighbours' server
	 */
	public boolean emailAvailable(String email) throws RemoteException;
	
	/**
	 * Access the application without an user account, in order to obtain reports
	 * and report updates of the specified suburb from server
	 * @param suburb the suburb of interest
	 * @param city the city of interest
	 * @return name of the SubUrbInterface instance to look for on server's rmi registry
	 * @throws RemoteException if cannot connect to Watchneighbours' server
	 */
	public String accessWithoutAccount(String suburb, String city) throws RemoteException;
}
