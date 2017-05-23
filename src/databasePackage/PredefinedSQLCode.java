package databasePackage;

import java.util.Hashtable;

/**
 *The class <code>PredefinedSQLCode</code> contains the static predefined SQL query.
 *@author Nicola Landro
 *@version 1.0
 */
public class PredefinedSQLCode {
	//insert_table_query - Insert
	//select_table_query - Select
	//update_table_query - Update
	//delete_table_query - Delete
	//create view - to create view
	
	protected enum Insert{USER, REPORT, REGISTRATION_CODE, USER_PROFILE_MODIFICATION, REPORT_HISTORIC, USER_REGISTRATION}
	protected static final Hashtable<Insert, String> insert_table_query = new Hashtable<Insert, String>()
	{
		private static final long serialVersionUID = 1L;
		{
			put(
					Insert.USER,
					"INSERT INTO users ("
					+ "userid, "
					+ "email, "
					+ "name, "
					+ "surname, "
					+ "password, "
					+ "suburb, "
					+ "address, "
					+ "city," 
					+ "latitude, "
					+ "longitude)"
					+ "values(?,?,?,?,?,?,?,?,?,?) "
			);
			put(
					Insert.REPORT,
					"INSERT INTO report ( "
					+ "timestamp, "
					+ "reporting, "
					+ "suburb, "
					+ "city," 
					+ "user_in_charge, "
					+ "status, "
					+ "reason, "
					+ "reporting_lat, "
					+ "reporting_lon, "
					+ "report_lat, "
					+ "report_lon ) "
					+ "values(CURRENT_TIMESTAMP,?,?,?,?,?,?,?,?,?,?)"
					
			);
			put(
					Insert.REGISTRATION_CODE,
					"INSERT INTO registration_code("
					+ "code, "
					+ "userid)"
					+ "values(?,?)"
			);
			
			put(
					Insert.USER_PROFILE_MODIFICATION,
					"INSERT INTO user_profile_modification("
					+ "timestamp, "
					+ "userid, "
					+ "modification_backup) "
					+ "values(current_timestamp,?,xmlparse (document ?))" //current_timestamp, SQL function
			);
			
			put(
					Insert.USER_REGISTRATION,
					"INSERT INTO user_registration_historic "
					+ "(timestamp, userid, email, name, surname, password, "
					+ "suburb, address, city, latitude, longitude)"
					+ "values(current_timestamp,?,?,?,?,?,?,?,?,?,?)"
			);
			
			put(
					Insert.REPORT_HISTORIC,
					"INSERT INTO report_operation_historic "
							+ "(operation, creation_timestamp, operation_timestamp,"
							+ "reporting, suburb, city, user_in_charge, status,reason,result," 
							+ "reporting_lat,reporting_lon, report_lat, report_lon)"
							+ "values(xmlparse (document ?),?,current_timestamp,?,?,?,?,?,?,?,?,?,?,?)"
			);

		}
	};
	
	protected enum Select{AUTENTICATION, PERSONAL_DATA, ACTIVE_REPORTS_ON_SUBURB, CLOSED_REPORTS_ON_SUBURB, ACTIVE_IN_CHARGE_REPORTS_ON_SUBURB, NUM_CLOSED_REPORT, CONFIRM_CODE, EMAIL_AVAILABLE, LAST_CLOSED_REPORT, NEW_INSERT_REPORT, EXISTS_VIEW, REPORT, USERS_REMOVED_NUMBERS_FOR_SUBURB, USERS_FROM_CITY_AND_SUBURB, USERS_FROM_CITY;}
	protected static final Hashtable<Select, String> select_table_query = new Hashtable<Select, String>()
	{
		private static final long serialVersionUID = 1L;
		{
			put(
					Select.USERS_FROM_CITY_AND_SUBURB,
					"SELECT * "
					+ "FROM user_registration_historic "
					+ "WHERE city = ? AND suburb = ? "
					+ "AND timestamp > ?"
			);
			put(
					Select.USERS_FROM_CITY,
					"SELECT * "
					+ "FROM user_registration_historic "
					+ "WHERE city = ? "
					+ "AND timestamp > ?"
			);
			put(
					Select.AUTENTICATION,
					"SELECT password "
					+ "FROM users "
					+ "WHERE userid = ? "
			);
			put(
					Select.PERSONAL_DATA,
					"SELECT * "
					+ "FROM users "
					+ "WHERE userid = ? "
			);
			put(
					Select.EMAIL_AVAILABLE,
					"SELECT userid "
					+ "FROM users "
					+ "WHERE email = ? "
			);
			put(
					Select.ACTIVE_REPORTS_ON_SUBURB,
					"SELECT * "
					+ "FROM report "
					+ "WHERE suburb = ? AND city = ? AND status <> 'closed' " 
			);
			put(
					Select.CLOSED_REPORTS_ON_SUBURB,
					"SELECT * "
					+ "FROM report "
					+ "WHERE suburb = ? AND city = ? AND status = 'closed' " 
			);
			put(
					Select.LAST_CLOSED_REPORT,
					"SELECT * "
					+ "FROM report "
					+ "WHERE suburb = ? AND city = ? AND status = 'closed' " 
					+ "AND timestamp <= all("
					+ "SELECT timestamp "
					+ "FROM report "
					+ "WHERE suburb = ? AND city = ? AND status = 'closed' " 
					+ ")"
			);
			put(
					Select.NEW_INSERT_REPORT,
					"SELECT * "
					+ "FROM report as r "
					+ "WHERE "
						+ "suburb = ? "
						+ "AND city = ? " 
						+ "AND reporting = ? "
						+ "AND reporting_lat = ? "
						+ "AND reporting_lon = ? "
						+ "AND report_lat = ? "
						+ "AND report_lon =? "
						+ "AND timestamp >= all( "
							+ "SELECT timestamp "
							+ "FROM report "
							+ "WHERE suburb = ? "
								+ "AND reporting = ? "
								+ "AND reporting_lat = ? "
								+ "AND reporting_lon = ? "
								+ "AND report_lat = ? "
								+ "AND report_lon = ? "
							+ ")"
			);
			put(
					Select.ACTIVE_IN_CHARGE_REPORTS_ON_SUBURB,
					"SELECT * "
					+ "FROM report "
					+ "WHERE suburb = ? AND city = ? AND status = 'in charge' " 
			);
			put(
					Select.REPORT,
					"SELECT * "
					+ "FROM report "
					+ "WHERE timestamp = ? AND city = ? AND  suburb = ?"
			);
			put(
					Select.NUM_CLOSED_REPORT,
					"SELECT COUNT(*) "
					+ "FROM report "
					+ "WHERE suburb = ? AND city = ? AND status = 'closed' " 
			);
			put(
					Select.CONFIRM_CODE,
					"SELECT code "
					+ "FROM registration_code "
					+ "WHERE userid = ?"
			);
			put(
					Select.EXISTS_VIEW,
					"SELECT table_name "
					+ "FROM information_schema.views "
					+ "WHERE table_schema='public' "
					+ "AND table_name = ?" //city_users
			);
			put(
					Select.USERS_REMOVED_NUMBERS_FOR_SUBURB,
					"SELECT suburb, count(*)"
					+ " FROM user_registration_historic"
					+ " WHERE city = ?"
					+ " GROUP BY suburb"
			);

		}
	};
	
	protected enum Update{USER, TAKE_IN_CHARGE, CLOSE;}
	protected static final Hashtable<Update, String> update_table_query = new Hashtable<Update, String>()
	{
		private static final long serialVersionUID = 1L;
		{
			put(
					Update.USER,
					"UPDATE users "
					+ "SET "
						+ "userid = ?, "
						+ "email = ?, "
						+ "name = ?, "
						+ "surname = ?, "
						+ "password = ?, "
						+ "suburb = ?, "
						+ "address = ?, " 
						+ "city = ?," 
						+ "latitude = ?, "
						+ "longitude = ? "
					+ "WHERE userid = ? "
			);
			put(
					Update.TAKE_IN_CHARGE,
					"UPDATE report "
					+ "SET "
						+ "user_in_charge = ? ,"
						+ "status = 'in charge' "
					+ "WHERE suburb = ? AND city = ? AND timestamp =?"
			);
			put(
					Update.CLOSE,
					"UPDATE report "
					+ "SET "
						+ "status = 'closed', "
						+ "result = ?"
					+ "WHERE suburb = ? AND city = ? AND timestamp =?"

			);

		}
	};
	
	protected enum Delete{USER, REPORT, USER_PROFILE_MODIFICATION, REGISTRATION_CODE;}
	protected static final Hashtable<Delete, String> delete_table_query = new Hashtable<Delete, String>()
			{
				private static final long serialVersionUID = 1L;
				{
					put(
							Delete.USER,
							"DELETE "
							+ "FROM users "
							+ "WHERE userid = ? "
					);
					
					put(
							Delete.REPORT,
							"DELETE "
							+ "FROM report AS s "
							+ "WHERE status = 'closed' AND suburb = ? AND city = ?"
									+ "AND timestamp <= all( "
															+ "SELECT timestamp "
															+ "FROM report "
															+"WHERE status = 'closed' AND suburb = ? AND city = ? ); " 
					);
					put( 
							Delete.USER_PROFILE_MODIFICATION, 
							"DELETE "
							+ "FROM user_profile_modification "
							+ "WHERE userid = ? "
									+ "AND ( timestamp <= all ( "
																+ "SELECT timestamp "
																+ "FROM user_profile_modification "
																+ "WHERE userid = ? ) );" // the two '?' are the same id
					);
					put(
							Delete.REGISTRATION_CODE,
							"DELETE "
							+ " FROM registration_code "
							+ " WHERE userid = ? "
					);

				}
			};
			
			protected static final String[] create_view_query = {
				"CREATE VIEW ",
				//expetted "city_users"
				" AS "
				+ " SELECT userid, suburb "
				+ " FROM users "
				+ " WHERE city = "
				//expetted name of city
			};
			
			/**
			 * @param city on witch you want create a view
			 * @return the string of sql query
			 */
			protected static String create_view_expression(String city){
				return create_view_query[0] +city+"_users" + create_view_query[1]+"'"+city+"'";
			}
			/**
			 * @param city on witch you want know the number of users
			 * @return the string of sql query
			 */
			protected static String create_num_users_query(String city){
				return "SELECT suburb,count(*) FROM "+city+"_users GROUP BY suburb";
			}

}
