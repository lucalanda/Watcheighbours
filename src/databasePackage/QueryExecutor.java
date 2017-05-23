package databasePackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import javafx.scene.control.TextArea;
import util.Report;
import util.User;
import util.UserWithTimestamp;

/**
 *The class <code>QueryExecutor</code> realize the connection tu DBMS.
 *@author Nicola Landro
 *@version 0.0
 */
public class QueryExecutor {
	private Connection con;
	
	/**
	 * Construct an instance of <code>QueryExecutor</code>
	 * @param url of database 
	 * @param user autorized to use the database
	 * @param password of database user
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public QueryExecutor(String url, String user, String password) throws SQLException{
		con = openConnection(url, user, password);
		System.out.println("Connesso al db" + con.getCatalog());
	}
	/**
	 * retun an istance of <code>QueryExecutor</code> from a file
	 * @param filePath the path of file with configurations
	 * @return QueryExecutor
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 * @throws IOException if the file does not exists
	 */
	public static QueryExecutor constructFromFile(String filePath) throws SQLException, IOException{
		BufferedReader dbConfig = new BufferedReader(new FileReader(new File(filePath)));
		QueryExecutor q = new QueryExecutor(dbConfig.readLine(), dbConfig.readLine(), dbConfig.readLine());
		dbConfig.close();
		return q;
	}

	/**
	 * Print the sql exception in standard output as err
	 * @param ex
	 */
	private static void printSQLException(SQLException ex){
		System.err.println("SQLState: "+ ex.getSQLState());
		System.err.println("Error Code: "+ ex.getErrorCode());
		System.err.println("Message: "+ ex.getMessage());
			
	}
	
	/**
	 * print sql exception to a text area
	 * @param ex exception to print
	 * @param results where it will be printed
	 */
	private static void printSQLException(SQLException ex, TextArea results){
		results.setText(
				"SQLState: "+ ex.getSQLState()
				+"\n"+"Error Code: "+ ex.getErrorCode()
				+"\n"+"Message: "+ ex.getMessage()
				);
	}
		
	private Connection openConnection(String url, String user, String password) throws SQLException{
		Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", password);
			
		Connection con = DriverManager.getConnection(url, props);
			
		return con;
	}
	
//insert
	/**
	 * execute insert user to database
	 * @param user to insert
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_insert_user(User user) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.insert_table_query.get(PredefinedSQLCode.Insert.USER));
			stat.setString(1, user.getUserId());
			stat.setString(2, user.getEmail());
			stat.setString(3, user.getName());
			stat.setString(4, user.getSurname());
			stat.setString(5, user.getPassword());
			stat.setString(6, user.getSuburb());
			stat.setString(7, user.getAddress());
			stat.setString(8, user.getCity());
			stat.setDouble(9, user.getLatitude());
			stat.setDouble(10, user.getLongitude());
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
	/**
	 * execute insert historic of user registration to database
	 * @param user registered
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_insert_user_registration_historic(User user) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.insert_table_query.get(PredefinedSQLCode.Insert.USER_REGISTRATION));
			stat.setString(1, user.getUserId());
			stat.setString(2, user.getEmail());
			stat.setString(3, user.getName());
			stat.setString(4, user.getSurname());
			stat.setString(5, user.getPassword());
			stat.setString(6, user.getSuburb());
			stat.setString(7, user.getAddress());
			stat.setString(8, user.getCity());
			stat.setDouble(9, user.getLatitude());
			stat.setDouble(10, user.getLongitude());
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
	/**
	 * execute insert report to database 
	 * @param report the report to insert to database
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_Insert_Report(Report report) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.insert_table_query.get(PredefinedSQLCode.Insert.REPORT));
			stat.setString(1, report.getReporting());
			stat.setString(2, report.getSuburb());
			stat.setString(3, report.getCity());
			stat.setString(4, report.getUserInCharge());
			stat.setString(5, report.getStatus().toString().toLowerCase());
			stat.setString(6, report.getMotive());
			stat.setDouble(7, report.getReportingLat());
			stat.setDouble(8, report.getReportingLon());
			stat.setDouble(9, report.getLatReport());
			stat.setDouble(10, report.getLonReport());
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
	
	/**
	 * execute insert registration code to database
	 * @param code to insert
	 * @param user that will use this registration code
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_insert_registration_code(String code, User user) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.insert_table_query.get(PredefinedSQLCode.Insert.REGISTRATION_CODE));
			stat.setString(1, code);
			stat.setString(2, user.getUserId());
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
	/**
	 * execute insert of user profile modification to database
	 * @param modification xml that specifie modifications
	 * @param user that make modifications
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_insert_user_profile_modification(String modification, User user) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.insert_table_query.get(PredefinedSQLCode.Insert.USER_PROFILE_MODIFICATION));
			stat.setString(1, user.getUserId());
			stat.setString(2, modification);
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
	
	/**
	 * execute insert to historic of report to database
	 * @param report the report to trace
	 * @param xml with report modification
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_insert_report_historic(Report report, String xml) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.insert_table_query.get(PredefinedSQLCode.Insert.REPORT_HISTORIC));
			stat.setString(1, xml);
			stat.setTimestamp(2, report.getTimestamp());
			stat.setString(3, report.getReporting());
			stat.setString(4, report.getSuburb());
			stat.setString(5, report.getCity());
			stat.setString(6, report.getUserInCharge());
			stat.setString(7, report.getStatus().toString().toLowerCase());
			stat.setString(8, report.getMotive());
			stat.setString(9, report.getResult());
			stat.setDouble(10, report.getReportingLat());
			stat.setDouble(11, report.getReportingLon());
			stat.setDouble(12, report.getLatReport());
			stat.setDouble(13, report.getLonReport());
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
	
//delete	
	/**
	 * execute delete user to database
	 * @param user to delete
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_delete_user(User user) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.delete_table_query.get(PredefinedSQLCode.Delete.USER));
			stat.setString(1, user.getUserId());
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
	/**
	 * execute delete report to database
	 * @param report to delete
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_delete_report(Report report) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.delete_table_query.get(PredefinedSQLCode.Delete.REPORT));
			stat.setString(1, report.getSuburb());
			stat.setString(2, report.getCity());
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
	/**
	 * execute delete last user profile modification to database
	 * @param user to delete
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_delete_last_user_profile_modification(User user) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.delete_table_query.get(PredefinedSQLCode.Delete.USER_PROFILE_MODIFICATION));
			stat.setString(1, user.getUserId());
			stat.setString(2, user.getUserId());
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
	/**
	 * execute delete registration code
	 * @param user that own the code
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_delete_registration_code(User user) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.delete_table_query.get(PredefinedSQLCode.Delete.REGISTRATION_CODE));
			stat.setString(1, user.getUserId());
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
//Update
	/**
	 * execute update data of user to database
	 * @param userid of user
	 * @param user updated
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_Update_User(String userid,User user) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.update_table_query.get(PredefinedSQLCode.Update.USER));
			stat.setString(1, user.getUserId());
			stat.setString(2, user.getEmail());
			stat.setString(3, user.getName());
			stat.setString(4, user.getSurname());
			stat.setString(5, user.getPassword());
			stat.setString(6, user.getSuburb());
			stat.setString(7, user.getAddress());
			stat.setString(8, user.getCity());
			stat.setDouble(9, user.getLatitude());
			stat.setDouble(10, user.getLongitude());
			stat.setString(11, userid);
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
	
	/**
	 * execute update query to database in order to take charge of a report
	 * @param report to take charge of
	 * @param user that take charge of
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_update_take_in_charge(Report report,User user) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.update_table_query.get(PredefinedSQLCode.Update.TAKE_IN_CHARGE));
			stat.setString(1, user.getUserId());
			stat.setString(2, report.getSuburb());
			stat.setString(3, report.getCity());
			stat.setTimestamp(4, report.getTimestamp());
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
	/**
	 * execute update query to database in order to close a report
	 * @param report to close
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_update_close_report(Report report) throws SQLException{
		PreparedStatement stat=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.update_table_query.get(PredefinedSQLCode.Update.CLOSE));
			stat.setString(1, report.getResult());
			stat.setString(2, report.getSuburb());
			stat.setString(3, report.getCity());
			stat.setTimestamp(4, report.getTimestamp());
			stat.executeUpdate();
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}

//Select
	/**
	 * execute selection query to database in order to obtain all users from that particular suburb on city
	 * @param suburb the suburb of interest
	 * @param city the city witch contains the suburb interested
	 * @param date after witch you wont to know the users
	 * @return ArrayList of users
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public ArrayList<UserWithTimestamp> execute_sel_users_from_suburb_city (String suburb, String city,Timestamp date) throws SQLException{
		PreparedStatement stat=null;
		ArrayList<UserWithTimestamp> out= new ArrayList<UserWithTimestamp>();
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.USERS_FROM_CITY_AND_SUBURB));
			stat.setString(1, city);
			stat.setString(2, suburb);
			stat.setTimestamp(3, date);
			ResultSet rs = stat.executeQuery();
			while(rs.next()){
				out.add(readUserFromHistoric(rs));
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	
	/**
	 * execute selection query to database in order to obtain all users from that particular city
	 * @param city the city of interest
	 * @param date the date after witch you wont to obain data
	 * @return ArrayList of users
	 * @throws SQLException  if something went wrong with the database or with the closing of connection 
	 */
	public ArrayList<UserWithTimestamp> execute_sel_users_from_city (String city,Timestamp date) throws SQLException{
		PreparedStatement stat=null;
		ArrayList<UserWithTimestamp> out= new ArrayList<UserWithTimestamp>();
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.USERS_FROM_CITY));
			stat.setString(1, city);
			stat.setTimestamp(2, date);
			ResultSet rs = stat.executeQuery();
			while(rs.next()){
				out.add(readUserFromHistoric(rs));
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	
	private UserWithTimestamp readUserFromHistoric(ResultSet rs) throws SQLException {
		UserWithTimestamp u = new UserWithTimestamp();
		u.setTimestamp(rs.getTimestamp(1));
		u.setUserId(rs.getString(2));
		u.setEmail(rs.getString(3));
		u.setName(rs.getString(4));
		u.setSurname(rs.getString(5));
		u.setPassword(rs.getString(6));
		u.setSuburb(rs.getString(7));
		u.setAddress(rs.getString(8));
		u.setCity(rs.getString(9));
		u.setLatitude(rs.getDouble(10));
		u.setLongitude(rs.getDouble(11));
		return u;
}
	/**
	 * execute selection query to database in order to know the number of closed report on a particular suburb of the city
	 * @param suburb the suburb of interest
	 * @param city the city witch contains the sunburb of interest
	 * @return number of closed report
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public int execute_sel_num_closed_report (String suburb, String city) throws SQLException{
		PreparedStatement stat=null;
		int out=-1;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.NUM_CLOSED_REPORT));
			stat.setString(1, suburb);
			stat.setString(2, city);
			ResultSet rs = stat.executeQuery();
			if(rs.next()){
				out = rs.getInt(1);
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	/**
	 * execute selection quary to database to know if exist a view with of that city
	 * @param city the city of interest
	 * @return the name of view of the city target
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public String execute_sel_existing_view_on_city (String city) throws SQLException{
		PreparedStatement stat=null;
		String out=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.EXISTS_VIEW));
			stat.setString(1, removeSpaces(city+"_users"));
			ResultSet rs = stat.executeQuery();
			if(rs.next()){
				out = rs.getString(1);
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	/**
	 * execute selection query to database in order to obtain the confirmation code of a user
	 * @param id the userid of person about you want to obtain confirmation code
	 * @return the confirmation code
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public String execute_sel_confirm_code (String id) throws SQLException{
		PreparedStatement stat=null;
		String out=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.CONFIRM_CODE));
			stat.setString(1, id);
			ResultSet rs = stat.executeQuery();
			if(rs.next()){
				out = rs.getString(1);
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	/**
	 * execute selection query to database in order to obtain the number of users not removed from every suburb
	 * @param city the city of interest
	 * @return hash table with name of sububr and number of users of the suburb targhet
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public Hashtable<String, Integer> execute_sel_users_number_for_suburb (String city) throws SQLException{
		PreparedStatement stat=null;
		Hashtable<String, Integer> out= new Hashtable<String, Integer>();
		try{
			stat = con.prepareStatement(PredefinedSQLCode.create_num_users_query(removeSpaces(city)));
			ResultSet rs = stat.executeQuery();
			while(rs.next()){
				out.put(rs.getString(1), rs.getInt(2));
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	/**
	 * execute selection query to database in order to obtain the number of users (also removed) from every suburb
	 * @param city the city of interest
	 * @return hash table with name of sububr and number of users of the suburb targhet
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public Hashtable<String, Integer> execute_sel_usersAlsoRemoved_number_for_suburb (String city) throws SQLException{
		PreparedStatement stat=null;
		Hashtable<String, Integer> out= new Hashtable<String, Integer>();
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.USERS_REMOVED_NUMBERS_FOR_SUBURB));
			stat.setString(1, city);
			ResultSet rs = stat.executeQuery();
			while(rs.next()){
				out.put(rs.getString(1), rs.getInt(2));
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	
	/**
	 * execute selection query to database in order to know if an email is used
	 * @param email the email about you wont to know if it is available
	 * @return true if the email is just in the database
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public boolean execute_sel_mail_available (String email) throws SQLException{
		PreparedStatement stat=null;
		boolean out=true;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.EMAIL_AVAILABLE));
			stat.setString(1, email);
			ResultSet rs = stat.executeQuery();
			if(rs.next()){
				out = false;
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	
	/**
	 * execute selection query to database in order to autenticate a user 
	 * @param id of user interested in
	 * @return password of user
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public String execute_sel_autentication (String id) throws SQLException{
		PreparedStatement stat=null;
		String out=null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.AUTENTICATION));
			stat.setString(1, id);
			ResultSet rs = stat.executeQuery();
			if(rs.next()){
				out = rs.getString(1);
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	/**
	 * execute selection query to database in order to obtain the data of a particular user
	 * @param id of user inttrested in
	 * @return the user
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public User execute_sel_user_data (String id) throws SQLException{
		PreparedStatement stat=null;
		User out = new User();
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.PERSONAL_DATA));
			stat.setString(1, id);
			ResultSet rs = stat.executeQuery();
			if(rs.next()){
				out.setUserId(rs.getString(1));
				out.setEmail(rs.getString(2));
				out.setName(rs.getString(3));
				out.setSurname(rs.getString(4));
				out.setPassword(rs.getString(5));
				out.setSuburb(rs.getString(6));
				out.setAddress(rs.getString(7));
				out.setCity(rs.getString(8));
				out.setLatitude(rs.getDouble(9));
				out.setLongitude(rs.getDouble(10));
			}
			else{
				out = null;
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}

	private Report reportFromStatement(ResultSet rs) throws SQLException{
		Report r = new Report();
		r.setTimestamp(rs.getTimestamp(1));
		r.setReporting(rs.getString(2));
		r.setSuburb(rs.getString(3));
		r.setCity(rs.getString(4));
		r.setUserInCharge(rs.getString(5));
		r.setStatus(rs.getString(6));
		r.setMotive(rs.getString(7));
		r.setResult(rs.getString(8));
		r.setReportingLat(rs.getDouble(9));
		r.setReportingLon(rs.getDouble(10));
		r.setLatReport(rs.getDouble(11));
		r.setLonReport(rs.getDouble(12));
		return r;
	}
	/**
	 * execute selection query to database in order to obtain the active report on a particular suburb of city
	 * @param suburb the suburb of interest
	 * @param city the city wicth contain the suburb of interest
	 * @return Arraylist of Report
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public ArrayList<Report> execute_sel_active_report (String suburb, String city) throws SQLException{
		PreparedStatement stat=null;
		ArrayList<Report> out = new ArrayList<Report>();
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.ACTIVE_REPORTS_ON_SUBURB));
			stat.setString(1, suburb);
			stat.setString(2, city);
			ResultSet rs = stat.executeQuery();
			while(rs.next()){
				Report r = reportFromStatement(rs);
				out.add(r);
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	
	/**
	 * execute selection query to database in order to obtain the closed report on particular suburb of city
	 * @param suburb the suburb of interest
	 * @param city the city witch contains the suburb of interest
	 * @return ArrayList of report
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public ArrayList<Report> execute_sel_closed_report (String suburb, String city) throws SQLException{
		PreparedStatement stat=null;
		ArrayList<Report> out = new ArrayList<Report>();
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.CLOSED_REPORTS_ON_SUBURB));
			stat.setString(1, suburb);
			stat.setString(2, city);
			ResultSet rs = stat.executeQuery();
			while(rs.next()){
				Report r = reportFromStatement(rs);
				out.add(r);
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	
	/**
	 * execute selection query to database in order to obtain the last closed report
	 * @param suburb the suburb of interest
	 * @param city the city witch contains the suburb of interest
	 * @return Report
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public Report execute_sel_Last_closed_report (String suburb, String city) throws SQLException{
		PreparedStatement stat=null;
		Report out = null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.LAST_CLOSED_REPORT));
			stat.setString(1, suburb);
			stat.setString(2, city);
			stat.setString(3, suburb);
			stat.setString(4, city);
			ResultSet rs = stat.executeQuery();
			while(rs.next()){
				Report r = reportFromStatement(rs);
				out=r;
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	
	/**
	 * execute selection query to database in order to obtain a specific report
	 * @param existingReport an existing report
	 * @return Report
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public Report execute_sel_report(Report existingReport) throws SQLException {
		PreparedStatement stat=null;
		Report out = null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.REPORT));
			stat.setTimestamp(1, existingReport.getTimestamp());
			stat.setString(2, existingReport.getCity());
			stat.setString(3, existingReport.getSuburb());
			ResultSet rs = stat.executeQuery();
			while(rs.next()){
				Report r = reportFromStatement(rs);
				out=r;
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	
	/**
	 * execute selection query to database in order to obtain the last added report
	 * @param report a report without timestamp
	 * @return Report
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public Report execute_sel_last_added_report (Report report) throws SQLException{
		PreparedStatement stat=null;
		Report out = null;
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.NEW_INSERT_REPORT));
			stat.setString(1, report.getSuburb());
			stat.setString(2, report.getCity());
			stat.setString(3, report.getReporting());
			stat.setDouble(4, report.getReportingLat());
			stat.setDouble(5, report.getReportingLon());
			stat.setDouble(6, report.getLatReport());
			stat.setDouble(7, report.getLonReport());
			
			stat.setString(8, report.getSuburb());
			stat.setString(9, report.getReporting());
			stat.setDouble(10, report.getReportingLat());
			stat.setDouble(11, report.getReportingLon());
			stat.setDouble(12, report.getLatReport());
			stat.setDouble(13, report.getLonReport());
			ResultSet rs = stat.executeQuery();
			while(rs.next()){
				Report r = reportFromStatement(rs);
				out=r;
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	
	/**
	 * execute selection query to database in order to obtain the in charge report from a particular suburb of city
	 * @param suburb the suburb of interest
	 * @param city the city witch contains the suburb of interest
	 * @return ArrayList of Report
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public ArrayList<Report> execute_sel_In_Charge_Reports (String suburb, String city) throws SQLException{
		PreparedStatement stat=null;
		ArrayList<Report> out = new ArrayList<Report>();
		try{
			stat = con.prepareStatement(PredefinedSQLCode.select_table_query.get(PredefinedSQLCode.Select.ACTIVE_IN_CHARGE_REPORTS_ON_SUBURB));
			stat.setString(1, suburb);
			stat.setString(2, city);
			ResultSet rs = stat.executeQuery();
			while(rs.next()){
				Report r = reportFromStatement(rs);
				out.add(r);
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		return out;
	}
	
//generic query to output
	/**
	 * execute a query to database from a sql string code and get the output to console
	 * @param sqlCode the code sql of query
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_sel_Query(String sqlCode) throws SQLException{
		Statement stat=null;
		try{
			stat = con.createStatement();
			ResultSet rs = stat.executeQuery(sqlCode);
			System.out.println(sqlCode);
			while(rs.next()){
				int numCol = rs.getMetaData().getColumnCount();
				String row = "";
				for(int i = 1; i<=numCol;i++){
					int type = rs.getMetaData().getColumnType(i);
					String cell="";
					switch(type){
					case java.sql.Types.BIGINT:
					case java.sql.Types.INTEGER:
					case java.sql.Types.SMALLINT:
					case java.sql.Types.TINYINT:
					case java.sql.Types.NUMERIC:
						cell = cell + " " + rs.getLong(i);
						break;
					case java.sql.Types.DATE:
						cell = cell + " " + rs.getDate(i).toString();
						break;
					case java.sql.Types.VARCHAR:
					case java.sql.Types.CHAR:
						cell = cell + " " + rs.getString(i);
						break;
					case java.sql.Types.TIMESTAMP:
						cell = cell + " " +rs.getTimestamp(i).toString();
					case java.sql.Types.SQLXML:
						cell = cell + " " +rs.getSQLXML(i).getString();
					}
					row = row + "\t" + cell;
				}
				System.out.println(row);
			}
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}
	/**
	 * execute query to database from a sql string code
	 * @param sqlCode the code sql of query
	 * @param results text area where you wish to display output
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void execute_sel_Query(String sqlCode, TextArea results) throws SQLException {
		Statement stat=null;
		results.setText("");
		try{
			stat = con.createStatement();
			ResultSet rs = stat.executeQuery(sqlCode);
			while(rs.next()){
				int numCol = rs.getMetaData().getColumnCount();
				String row = "";
				for(int i = 1; i<=numCol;i++){
					int type = rs.getMetaData().getColumnType(i);
					String cell="";
					switch(type){
					case java.sql.Types.BIGINT:
					case java.sql.Types.INTEGER:
					case java.sql.Types.SMALLINT:
					case java.sql.Types.TINYINT:
					case java.sql.Types.NUMERIC:
						cell = cell + " | " + rs.getLong(i);
						break;
					case java.sql.Types.FLOAT:
					case java.sql.Types.DOUBLE:
						cell = cell + " | " + rs.getDouble(i);
						break;
					case java.sql.Types.DATE:
						cell = cell + " | " + rs.getDate(i).toString();
						break;
					case java.sql.Types.VARCHAR:
					case java.sql.Types.CHAR:
						cell = cell + " | " + rs.getString(i);
						break;
					case java.sql.Types.TIMESTAMP:
						cell = cell + " " +rs.getTimestamp(i).toString();
					case java.sql.Types.SQLXML:
						if(rs.getSQLXML(i)!=null){
						cell = cell + " | " +rs.getSQLXML(i).getString();
						}
						else{
							cell = cell + " | null";
						}
					}
					row = row + cell;
				}
				results.setText(results.getText()+"\n"+row.substring(1));
			}
		}
		catch(SQLException e){
			printSQLException(e,results);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
		
	}
	
//create view
	private String removeSpaces(String city){
		String wSpacesString = "";
		StringTokenizer t = new StringTokenizer(city, " ");
		while(t.hasMoreElements()){
			wSpacesString += t.nextToken();
		}
		return wSpacesString;
	}
	
	/**
	 * create a view of the specified city
	 * @param city the city about witch you want create the view
	 * @throws SQLException if something went wrong with the database or with the closing of connection 
	 */
	public void create_vew(String city) throws SQLException{
		Statement stat=null;
		try{
			stat = con.createStatement();
			stat.executeUpdate(PredefinedSQLCode.create_view_expression(removeSpaces(city)));
		}
		catch(SQLException e){
			printSQLException(e);
			throw e;
		}
		finally{
			if(stat != null){
				stat.close();
			}
		}
	}	

}
