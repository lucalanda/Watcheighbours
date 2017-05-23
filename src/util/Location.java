package util;

import java.io.Serializable;

/**
 * The <code> Location </code> instance contains a map location's data
 * @author Luca Landa
 */
public class Location implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String city;
	private String suburb;
	private String address;
	private double latitude;
	private double longitude;
	
	
	
	
	/**
	 * Constructs a Location instance with the specified data
	 * @param city of the location
	 * @param suburb of the location
	 * @param address of the location
	 * @param latitude of the location
	 * @param longitude of the location
	 */
	public Location(String city, String suburb, String address, double latitude, double longitude) {
		this.city = city;
		this.suburb = suburb;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		
	}
	
	
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Location [city=" + city + ", suburb=" + suburb + ", address=" + address + "]";
	}
	
	/**
	 * Gets Location's city
	 * @return city of the location
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * Gets Location's suburb
	 * @return suburb of the location
	 */
	public String getSuburb() {
		return suburb;
	}
	
	/**
	 * Gets Location's address
	 * @return address of the location
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Gets Location's latitude
	 * @return latitude of the location
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Gets Location's longitude
	 * @return longitude of the location
	 */
	public double getLongitude() {
		return longitude;
	}
	
	
}
