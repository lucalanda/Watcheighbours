package util;

import java.io.Serializable;

/**
 *The class <code>User</code>
 *rapresent a user of watchneighbours
 *@author Nicola Landro
 *@author Luca Landa
 *@version 1.0
 */
public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	private String id;
	private String email;
	private String surname;
	private String name;
	private String password;
	private String suburb;
	private String city;
	private String address;
	private double latitude;
	private double longitude;
	
	
	/**
	 * construct a void user
	 */
	public User(){
		
	}
	
	/**
	 * construct a user
	 * @param id the user id
	 * @param email the user email
	 * @param surname the user surname
	 * @param name the user name
	 * @param password the user password
	 * @param suburb the user suburb
	 * @param address the user address
	 * @param city the user city
	 * @param latitude the user's house latitude
	 * @param longitude the user's house longitude
	 */
	public User(String id, String email, String surname, String name, String password, String suburb, String city, String address,
			double latitude, double longitude) {
		this.id = id;
		this.email = email;
		this.surname = surname;
		this.name = name;
		this.password = password;
		this.suburb = suburb;
		this.setCity(city);
		this.setAddress(address);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public boolean equals(Object user) {
		if(user instanceof User)
			return id.equals(((User) user).id);
			
		else 
			return false;
	}
	
	
	
	/**
	 * is a deep equals from two users
	 * @param user with witch you want compare
	 * @return true if the users are equals
	 */
	public boolean deepEquals(Object user) {
		if(!(user instanceof User))
			return false;
		
		User u = (User) user;
		
		return 
				this.id.equalsIgnoreCase(u.id) &&
			    this.email.equals(u.email) &&
			    this.surname.equals(u.surname) &&
			    this.name.equals(u.name) &&
			    this.password.equals(u.password) &&
			    this.suburb.equals(u.suburb) &&
			    this.city.equals(u.city) &&
			    this.address.equals(u.address) &&
			    this.latitude == u.latitude &&
			    this.longitude == u.longitude;
	}


	/**
	 * @return the id
	 */
	public String getUserId() {
		return id;
	}




	/**
	 * @param id the id to set
	 */
	public void setUserId(String id) {
		this.id = id;
	}




	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}




	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}




	/**
	 * @return the surname
	 */
	public String getSurname() {
		return surname;
	}




	/**
	 * @param surname the surname to set
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}




	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}




	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}




	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}




	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}




	/**
	 * @return the suburb
	 */
	public String getSuburb() {
		return suburb;
	}




	/**
	 * @param suburb the suburb to set
	 */
	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}




	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}




	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}




	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}




	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", surname=" + surname + ", name=" + name + ", password="
				+ password + ", suburb=" + suburb + ", city=" + city + ", address=" + address + ", latitude=" + latitude
				+ ", longitude=" + longitude + "]";
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	
	
}
