package server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

import databasePackage.QueryExecutor;
import exceptions.WrongDataException;
import interfaces.SubUrbInterface;
import interfaces.observer.RemoteObserver;
import server.observable.ConcreteAddReportServices;
import server.observable.ConcreteDeleteReportService;
import server.observable.ConcreteUpdateReportService;
import util.Report;
import util.User;

/**
 *The class <code>RMI_Server_SubUrb</code>
 *is the part of server with witch the client interacts 
 *when it wants to access to data about a particular suburb.
 *@author Nicola Landro
 *@version 1.0
 */
public class RMI_Server_SubUrb implements SubUrbInterface{
	private String suburb;
	private String city;
	private QueryExecutor q = null; //TMCH
	private Hashtable<String, RemoteObserver> observers;
	private LinkedBlockingQueue<RemoteObserver> obs;
	private ConcreteAddReportServices addReportService;
	private ConcreteDeleteReportService deleteReportService;
	private ConcreteUpdateReportService updateReportService;
	
	
	/**
	 * Construct an instance that satisfie the client request 
	 * about report or other thing after the login or access without account
	 * @param suburb the suburb that this instance will satisfie
	 * @param city the city in wicth there is the suburb target
	 */
	public RMI_Server_SubUrb(String suburb, String city) {
		this.suburb = suburb;
		this.city = city;
		this.observers = new Hashtable<String, RemoteObserver>();
		this.obs = new LinkedBlockingQueue<RemoteObserver>();
		this.addReportService = new ConcreteAddReportServices();
		this.deleteReportService = new ConcreteDeleteReportService();
		this.updateReportService = new ConcreteUpdateReportService();
		this.addReportService.setObserversList(obs);
		this.deleteReportService.setObserversList(obs);
		this.updateReportService.setObserversList(obs);
		
		try {
			q = QueryExecutor.constructFromFile("configuration_Server/DB.config");
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	private ArrayList<Report> getAllReports(){
		ArrayList<Report> reports=null;
		try {
			reports = q.execute_sel_active_report(suburb, city);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return reports;
	}
	
	@Override
	public boolean sendReport(Report newReport) throws RemoteException{
		boolean result = false;
		try {
			q.execute_Insert_Report(newReport);
			result = true;
			Report completeReport = q.execute_sel_last_added_report(newReport);
			q.execute_insert_report_historic(completeReport, null);
			addReportService.notityAllObservers(completeReport);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public synchronized boolean takeChargeOf(Report existingReport, User user) throws RemoteException{
		boolean result = false; 
		
		String xml = null;
		try {
			if(q.execute_sel_report(existingReport).getStatus().equals(Report.StatusType.IN_CHARGE)){
				return false;
			}
			
			q.execute_update_take_in_charge(existingReport, user);
			result = true;
			existingReport.setStatus("in charge");
			existingReport.setUserInCharge(user.getUserId());
			xml = createTakeChargeReportXML(existingReport);
			q.execute_insert_report_historic(existingReport, xml);
			updateReportService.notityAllObservers(existingReport);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void exit(String id, RemoteObserver observer) throws RemoteException {
		this.obs.remove(this.observers.get(id));
		this.observers.remove(id);
	}


	@Override
	public boolean closeReport(Report existingReport, User user,String result) throws RemoteException, WrongDataException{
		boolean res = false;
		String xml = null;
		try {
			if(q.execute_sel_report(existingReport).getStatus().equals(Report.StatusType.CLOSED)){
				return res;
			}
			if(result.length()>500){
				throw new WrongDataException("L'esito è più lungo di 500 caratteri.");
			}
				existingReport.setStatus("closed");
				existingReport.setResult(result);
				q.execute_update_close_report(existingReport);
				res = true;
				xml = createCloseReportXML(existingReport);
				q.execute_insert_report_historic(existingReport, xml);
				updateReportService.notityAllObservers(existingReport);
				if(q.execute_sel_num_closed_report(suburb,city)>10){
					Report lastReport = q.execute_sel_Last_closed_report(user.getSuburb(), user.getCity());
					updateReportService.notityAllObservers(existingReport);
					deleteReportService.notityAllObservers(lastReport);
					q.execute_delete_report(lastReport);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return res;
	}
	
	private String createTakeChargeReportXML(Report report){
		String xml= "<modifications>";
			xml+="<TakeChargeOf>";
				xml+="<UserInCharge>";
					xml+=report.getUserInCharge();
				xml+="</UserInCharge>";
			xml+="</TakeChargeOf>";
		xml+="</modifications>";
		return xml;
	}
	
	private String createCloseReportXML(Report report){
		String xml= "<modifications>";
		xml+="<Close>";
			xml+="<Results>";
				xml+=report.getResult();
			xml+="</Results>";
		xml+="</Close>";
	xml+="</modifications>";
	return xml;
	}

	private String createUserProfileModificationXML(User oldUser, User newUser){
		String xml= 
		"<modifications>";
			if(!oldUser.getUserId().equals(newUser.getUserId())){
				xml+="<UserId>";
					xml+="<oldUserId>"+oldUser.getUserId()+"</oldUserId>";
					xml+="<newUserId>"+newUser.getUserId()+"</newUserId>";
				xml+="</UserId>";
			}
			if(!oldUser.getEmail().equals(newUser.getEmail())){
				xml+="<Email>";
					xml+="<oldEmail>"+oldUser.getEmail()+"</oldEmail>";
					xml+="<newEmail>"+newUser.getEmail()+"</newEmail>";
				xml+="</Email>";
			}
			if(!oldUser.getName().equals(newUser.getName())){
				xml+="<Name>";
					xml+="<oldName>"+oldUser.getName()+"</oldName>";
					xml+="<newName>"+newUser.getName()+"</newName>";
				xml+="</Name>";
			}
			if(!oldUser.getSurname().equals(newUser.getSurname())){
				xml+="<Surname>";
					xml+="<oldSurname>"+oldUser.getSurname()+"</oldSurname>";
					xml+="<newSurname>"+newUser.getSurname()+"</newSurname>";
				xml+="</Surname>";
			}
			if(!oldUser.getPassword().equals(newUser.getPassword())){
				xml+="<Password>";
					xml+="<oldPassword>"+oldUser.getPassword()+"</oldPassword>";
					xml+="<newPassword>"+newUser.getPassword()+"</newPassword>";
				xml+="</Password>";
			}
			if(!oldUser.getAddress().equals(newUser.getAddress())){
				xml+="<Address>";
					xml+="<oldAddress>"+oldUser.getAddress()+"</oldAddress>";
					xml+="<newAddress>"+newUser.getAddress()+"</newAddress>";
				xml+="</Address>";
			}
			if(!oldUser.getCity().equals(newUser.getCity())){
				xml+="<City>";
					xml+="<oldCity>"+oldUser.getCity()+"</oldCity>";
					xml+="<newCity>"+newUser.getCity()+"</newCity>";
				xml+="</City>";
			}
			if(!oldUser.getSuburb().equals(newUser.getSuburb())){
				xml+="<Suburb>";
					xml+="<oldSuburb>"+oldUser.getSuburb()+"</oldSuburb>";
					xml+="<newSuburb>"+newUser.getSuburb()+"</newSuburb>";
				xml+="</Suburb>";
			}
			if((!(oldUser.getLatitude()==newUser.getLatitude()))||(!(oldUser.getLongitude()==newUser.getLongitude()))){
				xml+="<Latitude>";
					xml+="<oldLatitude>"+oldUser.getLatitude()+"</oldLatitude>";
					xml+="<newLatitude>"+newUser.getLatitude()+"</newLatitude>";
				xml+="</Latitude>";
				xml+="<Longitude>";
					xml+="<oldLongitude>"+oldUser.getLongitude()+"</oldLongitude>";
					xml+="<newLongitude>"+newUser.getLongitude()+"</newLongitude>";
				xml+="</Longitude>";
			}
		xml+="</modifications>";
		return xml;
	}
	
	@Override
	public boolean updateProfile(User oldUser, User newUser) throws WrongDataException, RemoteException {
		String xml = createUserProfileModificationXML(oldUser, newUser);
		try {
			q.execute_Update_User(oldUser.getUserId(), newUser);
			q.execute_insert_user_profile_modification(xml, newUser);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public ArrayList<Report> addObserver(String id, RemoteObserver observer) throws RemoteException {
		this.observers.put(id, observer);
		this.obs.add(observer);
//the code over this is not usefull because all this three object have a puntator to same list 'obs'
//		addReportService.addObserver(observer);
//		deleteReportService.addObserver(observer);
//		updateReportService.addObserver(observer);	
		ArrayList<Report> reports=getAllReports();
		return reports;
	}



	@Override
	public boolean removeUser(User user) throws RemoteException {
		boolean ret = true;
		try {
			q.execute_delete_user(user);
		} catch (SQLException e) {
			ret = false;
			e.printStackTrace();
		}
		return ret;
	}

}
