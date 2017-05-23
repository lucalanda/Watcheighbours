package util;

import java.sql.Timestamp;

/**
 *The class <code>UserWithTimestamp</code>
 *rapresent a user of watchneighbours with timestamp
 *@author Nicola Landro
 *@author Luca Landa
 *@version 1.0
 */
public class UserWithTimestamp extends User {
	
	
	private static final long serialVersionUID = 1L;
	
	private Timestamp timestamp;
	
	
	
	/**
	 * @return timestamp
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	/**
	 * @param timestamp the timestamp of user creation
	 */
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
