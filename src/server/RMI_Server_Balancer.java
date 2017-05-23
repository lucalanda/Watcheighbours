package server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import databasePackage.QueryExecutor;
import util.User;
import exceptions.NotExistingUserIDException;
import exceptions.UserNotConfirmedException;
import exceptions.WrongConfirmationCodeException;
import exceptions.WrongDataException;
import exceptions.WrongPasswordException;
import googleMailClient.GoogleMailClient;
import interfaces.ConnectionBalancerInterface;
import interfaces.SubUrbInterface;

/**
 *The class <code>RMI_Server_Balancer</code>
 *is the part of server with witch 
 *the client interacts immediately before login.
 *@author Nicola Landro
 *@version 1.0
 */
public class RMI_Server_Balancer implements ConnectionBalancerInterface{
	private QueryExecutor q=null; //TMCH
	private final static String mailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private final static int MIN_PORT_NUMBER = 1024;
	private final static int MAX_PORT_NUMBER = 65535;
	
	/**
	 * Create an istance that cath the request of client when it start.
	 */
	public RMI_Server_Balancer() {
		try {
			q = QueryExecutor.constructFromFile("configuration_Server/DB.config");
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks to see if a specific port is available.
	 *
	 * @param port the port to check for availability
	 * @return true if the port is available
	 */
	public static boolean available(int port) {

	    if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
	        throw new IllegalArgumentException("Invalid start port: " + port);
	    }

	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }

	    return false;
	}

	@Override
	public synchronized User logIn(String userID, String password) throws NotExistingUserIDException, WrongPasswordException,
			UserNotConfirmedException, RemoteException {
		User user = null;
		boolean notConfirmed = false;
		try {
			if(q.execute_sel_confirm_code(userID)!=null){
				notConfirmed = true;
			}
			User u = q.execute_sel_user_data(userID);
			user = u;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(user==null){
			throw new NotExistingUserIDException();
		}
		else if(notConfirmed){
			throw new UserNotConfirmedException();
		}
		else if(user.getPassword().equals(password)){
			createSuburbHandler(user.getSuburb(), user.getCity());
			return user;
		}
		else{
			throw new WrongPasswordException();
		}
	}
	
	private boolean idLessThen20(User newUser){
		return newUser.getUserId().length()<=20;
	}
	private boolean nameLessThen20(User newUser){
		return newUser.getName().length()<=20;
	}
	private boolean surnameLessThen20(User newUser){
		return newUser.getSurname().length()<=20;
	}
	private boolean passwordLessThen24(User newUser){
		return newUser.getPassword().length()<=24;
	}
	private boolean emailunique(User newUser) throws SQLException{
		boolean mailUnique=false;
		mailUnique = q.execute_sel_mail_available(newUser.getEmail());
		return !mailUnique;
	}
	private boolean mailPath(User newUser){
		return newUser.getEmail().matches(mailPattern);
	}
	private boolean mailLessThen255(User newUser){
		return newUser.getEmail().length()<=255;
	}
	private boolean addressLessThen100(User newUser){
		return newUser.getAddress().length()<=100;
	}
	private boolean cityLessThen50(User newUser){
		return newUser.getCity().length()<=50;
	}
	private boolean suburbLessThen20(User newUser){
		return newUser.getSuburb().length()<=20;
	}
	
	private String wrongData(User newUser) throws SQLException{
		StringBuilder s = new StringBuilder();
		if(!idLessThen20(newUser)){
			s.append("Lo userId inserito deve essere lungo massimo 20 caratteri.\n");
		}
		if(!passwordLessThen24(newUser)){
			s.append("La password inserita deve essere lunga massimo 24 caratteri.\n");
		}
		if(!nameLessThen20(newUser)){
			s.append("Il nome inserito deve essere lungo massimo 20 caratteri.\n");
		}
		if(!surnameLessThen20(newUser)){
			s.append("Il cognome inserito deve essere lungo massimo 20 caratteri.\n");
		}
		if(emailunique(newUser)){
			s.append("L'e-mail inserita è già stata utilizzata da un'altro utente.\n");
		}
		if(!mailLessThen255(newUser)){
			s.append("L'e-mail inserita deve essere lunga massimo 255 caratteri.\n");
		}
		if(!mailPath(newUser)){
			s.append("L'e-mail inserita è scorretta.\n");
		}
		if(!suburbLessThen20(newUser)){
			s.append("Il quartiere deve essere lungo massimo 20 caratteri.\n");
		}
		if(!addressLessThen100(newUser)){
			s.append("La residenza deve essere lunga al massimo 100 caratteri.\n");
		}
		if(!cityLessThen50(newUser)){
			s.append("La città deve essere lunga al massimo 50 caratteri.\n");
		}
		return s.toString();
	}
	
	private String codeGenerator(){
		StringBuilder code = new StringBuilder();
		SecureRandom r = new SecureRandom();
		for(int i = 0; i<10;i++){
			if(Math.random()>0.647){ //this make all equiprobable
				code.append((char)(r.nextInt(55)+40)); //alphabet letters and numbers and some special character, without ( ` : 96)
			}
			else{
				code.append((char)(r.nextInt(30)+97));
			}
			
		}
		return code.toString();
	}
	
	@Override
	public synchronized boolean register(User newUser) throws WrongDataException, RemoteException {
		boolean noErr = false;
		try{
			String wrongData = wrongData(newUser);
			if(!wrongData.isEmpty()){
				throw new WrongDataException(wrongData);
			}
			else{
				q.execute_insert_user(newUser);
				String code = codeGenerator();
				q.execute_insert_registration_code(code, newUser);
				q.execute_insert_user_registration_historic(newUser);
				
				GoogleMailClient sender = GoogleMailClient.getGoogleMailClient();
				sender.sendEmail(newUser.getEmail(), code);
				noErr = true;
				
				if(q.execute_sel_existing_view_on_city(newUser.getCity().toLowerCase())==null){
					q.create_vew(newUser.getCity());
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return noErr;
	}

	@Override
	public boolean userIDAvailable(String userID) throws RemoteException {
		try {
			User u = q.execute_sel_user_data(userID);
			if(u!=null){
				return false;
			}
			else{
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; //TMCH
	}

	@Override 
	public User confirmRegistration(String userID, String password, String registrationCode) throws RemoteException,WrongConfirmationCodeException, NotExistingUserIDException, WrongPasswordException, UserNotConfirmedException {
		User user = null;
		boolean WrongConfirmation = true;
		
		try {
			
			String confirmationCode = q.execute_sel_confirm_code(userID);
			if(confirmationCode.equals(registrationCode)){
				WrongConfirmation = false;
				user = new User();
				user.setUserId(userID);
				q.execute_delete_registration_code(user);
				try{
					user = logIn(userID, password);
				}
				catch(Exception e){
					q.execute_insert_registration_code(registrationCode, user); //I must reinsert because the password or other was wrong, so the user is not confirmed
					WrongConfirmation = true;
					throw e;
				}
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(WrongConfirmation){
			throw new WrongConfirmationCodeException();
		}
		
		return user;
	}

	@Override 
	public boolean emailAvailable(String email) throws RemoteException {
		boolean available = false;
		try {
			available = q.execute_sel_mail_available(email);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return available;
	}
	/**
	 * 
	 * @param suburb
	 * @param city
	 * @throws RemoteException
	 */
	private void createRMI_ServerSubUrb(String suburb, String city) throws RemoteException{
		RMI_Server_SubUrb obj= new RMI_Server_SubUrb(suburb, city) ;
		
		for(int i = MIN_PORT_NUMBER; i<MAX_PORT_NUMBER;i++){
			try{
				SubUrbInterface stub = (SubUrbInterface) UnicastRemoteObject.exportObject(obj,i) ;
				Registry r = LocateRegistry.getRegistry( ) ;
				r.rebind((city+" "+suburb).toLowerCase(),stub);
				System.out.println("Porta corretta: "+i+", create '"+city+" "+suburb+"' in RMIRegistry"); //for server control
				break;
			}
			catch(RemoteException e){
				System.out.println("Porta Occupata: "+i); //for server control
			}
		}
	}
	
	private boolean createSuburbHandler(String suburb, String city) throws RemoteException{
		Registry r = LocateRegistry.getRegistry();
		boolean notExistSuburbHandler = true;
		for(String s : r.list()){
			if (s.equalsIgnoreCase((city+" "+suburb))){
				notExistSuburbHandler = false;
				break;
			}
		}
		if(notExistSuburbHandler){
			createRMI_ServerSubUrb(suburb, city);
		}
		return notExistSuburbHandler;
	}
	
	@Override 
	public synchronized String accessWithoutAccount(String suburb, String city) throws RemoteException {
		createSuburbHandler(suburb, city);
		return (city+" "+suburb).toLowerCase();
	}

}
