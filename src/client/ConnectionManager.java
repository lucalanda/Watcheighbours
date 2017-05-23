package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import exceptions.NotExistingUserIDException;
import exceptions.UserNotConfirmedException;
import exceptions.WrongConfirmationCodeException;
import exceptions.WrongDataException;
import exceptions.WrongPasswordException;
import interfaces.ConnectionBalancerInterface;
import interfaces.SubUrbInterface;
import client.ReportObserver;
import util.User;

/**
 * The <code> ConnectionManager </code> instance manages the connection to the Watchneighbours server,
 * connecting and communicating with ConnectionBalancer and SubUrbInterface instances
 * @author Luca Landa
 */
public class ConnectionManager {
	
	//Singleton pattern
	private static ConnectionManager manager;
	
	private String defaultHostAddress = "localhost";
	private int defaultHostPort = 1099; 

	private String hostAddress;
	private int hostPort;
	private final String serverBalancerName = "Balancer";
 
	private Registry registry;
	
	private ConnectionBalancerInterface stub = null;
	
	private ReportObserver observer;
	private SubUrbInterface mainSuburbHandler = null;
	private List<SubUrbInterface> otherSuburbHandlers = null;
	
	private User loggedUser;
	
	private String city;
	private String suburb;
	private String suburbHandlerName;

	
	
	
	private ConnectionManager() {
		hostAddress = defaultHostAddress;
		hostPort = defaultHostPort;
		
		otherSuburbHandlers = new ArrayList<SubUrbInterface>();
	}
	
	/**
	 * Class implemented with a Singleton pattern, this method is necessary to get
	 * the ConnectionManager instance
	 * @return instance of ConnectionManager
	 */
	public static ConnectionManager getConnectionManager() {
		if(manager == null)
			manager = new ConnectionManager();
		
		return manager;
	}
	
	
	/**
	 * Tests a custom connection to a Watchneighbours server
	 * @param host server's address
	 * @param port server's port
	 * @return success of the connection
	 */
	public boolean testCustomConnection(String host, int port) {
		String backupHost = hostAddress;
		int backupPort = hostPort;
		
		setConnectionData(host, port);
		boolean success = testConnection();
		setConnectionData(backupHost, backupPort);
		
		return success;
	}
	
	/**
	 * Tests a connection to the actual Watchneighbourss server
	 * @return success of the connection
	 */
	public boolean testConnection() {
		boolean success = true;
		
		try {
			Registry registry = LocateRegistry.getRegistry(hostAddress, hostPort);
			ConnectionBalancerInterface test = (ConnectionBalancerInterface) registry.lookup(serverBalancerName);
			success = (test != null);
		} catch (RemoteException | NotBoundException e) {
			success = false;
		} catch (ClassCastException e) {
			success = false;
			e.printStackTrace();
		}
		
		return success;
	}
	
	/**
	 * Connects the client to server's instance of ConnectionBalancer
	 * @return success of the operation
	 */
	public boolean connectToBalancer() {
		boolean connectionAvailable = testConnection();
		
		if(connectionAvailable) {
			try {
				registry = LocateRegistry.getRegistry(hostAddress, hostPort);
				stub = (ConnectionBalancerInterface) registry.lookup(serverBalancerName);
			} catch (RemoteException | NotBoundException e) {
				disconnect();
				ClientApplication.showConnectionAlert();
				return false;
			}
			System.out.println("ConnectionManager - connected to balancer");
		} 
				
		else 
			ClientApplication.showConnectionAlert();
		
		return connectionAvailable;
	}
	
	
	
	
	
	//SubUrbInterface interaction methods
	
	
	/**
	 * Tries to logIn into a Watchneighbour's account
	 * @param userID user's ID
	 * @param password user's account password
	 * @return success of the operation
	 * @throws UserNotConfirmedException if the specified user has not been confirmed yet
	 */
	public boolean tryLogin(String userID, String password) throws UserNotConfirmedException {
		if(!connectToBalancer())
			return false;
		
		boolean success = false;
		try {
			loggedUser = stub.logIn(userID, password);
			success = (loggedUser != null);
			if(success) {
				String suburb = loggedUser.getSuburb();
				String city = loggedUser.getCity();
				setSuburbAndCity(suburb, city);
				setSuburbHandlerName(suburb, city);
			}
			else
				disconnect();
		} catch (RemoteException e) {
			e.printStackTrace();
			ClientApplication.showConnectionAlert();
			return false;
		} catch (NotExistingUserIDException | WrongPasswordException  e) {
			String errorMessage = (e instanceof NotExistingUserIDException) ?
					"L'UserID inserito non esiste" : "La password inserita è errata";

			ClientApplication.showAccessErrorAlert(errorMessage);
		}
		
		return success;
	}
	
	/**
	 * Tries to confirm a registration, after a logIn operation failed for an account
	 * that had not been confirmed yet
	 * @param userID the user's account ID
	 * @param password the user's account password
	 * @param registrationCode the user's account registration code, to confirm the registration
	 * @return success of the operation
	 */
	public boolean tryConfirmRegistration(String userID, String password, String registrationCode) {
		//already connected, with tryLogin() method
		boolean success = false;
		
		try {
			loggedUser = stub.confirmRegistration(userID, password, registrationCode);
			success = (loggedUser != null);
		} catch(RemoteException e) {
			ClientApplication.showConnectionAlert();
		} catch(WrongConfirmationCodeException e) {
			ClientApplication.showAccessErrorAlert("Il codice di conferma inserito è errato");
		} catch (NotExistingUserIDException | WrongPasswordException | UserNotConfirmedException e) {
			//not expected
			ClientApplication.showAccessErrorAlert("La password inserita è errata");
		}
		
		if(success) {
			String suburb = loggedUser.getSuburb();
			String city = loggedUser.getCity();
			setSuburbAndCity(suburb, city);
			setSuburbHandlerName(suburb, city);
		}
		
		return success;
	}
	
	/**
	 * Tries to access the application without an account, to the suburb (and city) selected
	 * @param suburb the suburb to see in the application
	 * @param city the city to see in the application
	 * @return success of the operation
	 * @throws RemoteException if cannot connect to Watchneighbours server
	 */
	public boolean tryAccessWithoutAccount(String suburb, String city) throws RemoteException {
		if(!connectToBalancer())
			return false;		
		
		suburbHandlerName =  null;
		try {
			suburbHandlerName = stub.accessWithoutAccount(suburb, city);
			System.out.println("Client, trying access without account - stub = null = " + (stub == null));
		} catch (RemoteException e) {
			System.err.println("Client: tryAccessWithoutAccount - RemoteException caught");
			e.printStackTrace();
			disconnect();
			return false;
		}
		loggedUser = null;
		
		setSuburbAndCity(suburb, city);
		
		return true;
		
	}
	
	/**
	 * Tries to connect to another SubUrbInterface instance of Watchneighbours server, to get its reports
	 * and updates
	 * @param suburb the new suburb to add to the displayed area
	 * @param city the city of the new suburb
	 * @return SubUrbInterface instance of the new suburb's handler on the server
	 * @throws RemoteException if cannot connect to the ConnectionBalancer instance on the server
	 * @throws NotBoundException if cannot find the searched suburb
	 */
	public SubUrbInterface tryAddingSubUrbHandler(String suburb, String city) throws RemoteException, NotBoundException {
		if(!connectToBalancer())
			throw new RemoteException("Connection to balancer lost");
		
		String name = stub.accessWithoutAccount(suburb, city);
		System.out.println("Client: adding suburb = " + suburb);
		
		Registry registry = LocateRegistry.getRegistry(hostAddress, hostPort);
		SubUrbInterface result = (SubUrbInterface) registry.lookup(name);
		otherSuburbHandlers.add(result);
		
		return result;

	}
	
	/**
	 * Checks for an userID availability asking to the stub (ConnectionBalancer instance)
	 * @param userID the userID to verify
	 * @return availability of the userID
	 * @throws RemoteException if cannot connect to the ConnectionBalancer instance on the server
	 */
	public boolean checkUserIDAvailability(String userID) throws RemoteException {
		return stub.userIDAvailable(userID);
	}
	
	/**
	 * Checks an email availability asking to the stub (ConnectionBalancer instance)
	 * @param email email the email to verify
	 * @return availability of the email
	 * @throws RemoteException if cannot connect to the ConnectionBalancer instance on the server
	 */
	public boolean checkEmailAvailability(String email) throws RemoteException {
		return stub.emailAvailable(email);
	}
	
	/**
	 * Sets new connection data to find Watchneighbours server
	 * @param host the new server's host
	 * @param port the new server's port
	 */
	public void setConnectionData(String host, int port) {
		hostAddress = host;
		hostPort = port;
	}
	
	/**
	 * Tries to register a new User to Watchneighbours application
	 * @param user the new User
	 * @return success of the operation
	 * @throws RemoteException if cannot connect to the ConnectionBalancer instance on the server
	 * @throws WrongDataException if new User instance contains wrong data
	 */
	public boolean registration(User user) throws RemoteException, WrongDataException {
		return stub.register(user);
	}
	
	/**
	 * Connects the client to a SubUrbInterface instance on Watchneighbours server
	 * @return success of the operation
	 * @throws RemoteException if cannot connect to the SubUrbInterface instance
	 * @throws NotBoundException if cannot find the SubUrbInterface instance
	 */
	public boolean connectToSubUrbInterface() throws RemoteException, NotBoundException {
		
		Registry registry = LocateRegistry.getRegistry(hostAddress, hostPort);
		System.out.println("ConnectionManager - looking for SubUrbInterface named: " + suburbHandlerName);
		mainSuburbHandler = (SubUrbInterface) registry.lookup(suburbHandlerName.toLowerCase());
		
		return true;
	}
	
	
	//End of SubUrbInterface interaction methods
	
	
	
	// utils, getters and setters
	
	/**
	 * Disconnects the client from Watchneighbours server
	 */
	public void disconnect() {
		stub = null;
		if(observer != null) try { 
			mainSuburbHandler.exit(observer.getID(), observer);
			for(SubUrbInterface s : otherSuburbHandlers)
				s.exit(observer.getID(), observer);
		} catch (Exception e) {
			//nothing to do, already disconnected
		}
		mainSuburbHandler = null;
		otherSuburbHandlers.clear();
		loggedUser = null;

		suburb = null;
		suburbHandlerName = null;
		city = null;
	}
	
	/**
	 * Gets default host address 
	 * @return address default host address
	 */
	public String getDefaultHost() {
		return defaultHostAddress;
	}
	
	/**
	 * Gets default host port
	 * @return port default host port
	 */
	public int getDefaultPort() {
		return defaultHostPort;
	}
	
	/**
	 * Gets actual host address
	 * @return address actual host address
	 */
	public String getHost() {
		return hostAddress;
	}
	
	/**
	 * Gets actual host port
	 * @return port actual host port
	 */
	public int getPort() {
		return hostPort;
	}

	/**
	 * Sets a logged User instance
	 * @param user the user instance
	 */
	public void setLoggedUser(User user) {
		this.loggedUser = user;
	}

	/**
	 * Gets the logged user instance
	 * @return user the logged user
	 */
	public User getLoggedUser() {
		return loggedUser;
	}
	
	/**
	 * Tests if the client is logged
	 * @return connected true if it is connected, false if it is not
	 */
	public boolean isLogged() {
		return loggedUser != null;
	}
	
	/**
	 * Gets the main SubUrbInterface instance
	 * @return instance SubUrbInterface instance
	 */
	public SubUrbInterface getSuburbHandler() {
		return mainSuburbHandler;
	}
	
	/**
	 * Sets a new suburb handler name, the name used to search SubUrbInterface instance
	 * on the server
	 * @param suburb the suburb selected
	 * @param city the city selected
	 */
	public void setSuburbHandlerName(String suburb, String city) {
		suburbHandlerName = (city + " " + suburb).toLowerCase();
	}

	/**
	 * Sets new suburb and city values to ConnectionManager
	 * @param suburb the new suburb
	 * @param city the new city
	 */
	public void setSuburbAndCity(String suburb, String city) {
		this.suburb = suburb;
		this.city = city;
	}
	
	/**
	 * Gets actual suburb value
	 * @return suburb suburb value
	 */
	public String getSuburb() {
		return suburb;
	}
	
	/**
	 * Gets actual city value
	 * @return city actual city value
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * Sets a ReportObserver instance
	 * @param observer ReportObserver instance
	 */
	public void setReportObserver(ReportObserver observer) {
		this.observer = observer;
	}
	
}
