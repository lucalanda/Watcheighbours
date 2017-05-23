package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import exceptions.NoSuburbException;

/**
 * The <code> LocationDataQuery </code> instance provides information
 * about a location specified with coordinates, using OpenStreetMaps and
 * GoogleMaps services
 * @author Luca Landa
 */
public class LocationDataQuery {
	
	private final static String OSM_URL_BASE = "http://nominatim.openstreetmap.org/reverse?format=json&";
	private final static String OSM_URL_END = "&zoom=18&addressdetails=1";
	private final static String GOOGLE_URL_BASE = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
	private final static String GOOGLE_KEY = "AIzaSyBdO0zLYsi5T5jDze0MdvZsrdj5E9yJbPg";
	
	
	

	
	/**
	 * Constructs a new LocationDataQuery instance
	 */
	public LocationDataQuery() {
		
	}
	
	
	
	
	/**
	 * Gets the suburb of a specified location
	 * @param latitude of the location
	 * @param longitude of the location
	 * @return suburb of the location
	 * @throws IOException if a connection issue occurred
	 * @throws NoSuburbException if was not able to find the location suburb
	 */
	public String getSuburb(double latitude, double longitude) throws IOException, NoSuburbException {
		String res = getOSMResult(latitude, longitude);
		res = extractSuburbFromOSM(res);
		if(res == null) {
			throw new NoSuburbException();
		}
		else
			return res.toLowerCase();
		
	}
	
	/**
	 * Gets the address of a specified location
	 * @param latitude of the location
	 * @param longitude of the location
	 * @return address of the location
	 * @throws IOException if a connection issue occurred
	 */
	public String getAddress(double latitude, double longitude) throws IOException {
		String res = getGoogleResult(latitude, longitude);
		res = extractAddressFromGoogle(res);
		return res.toLowerCase();
	}
	
	/**
	 * Gets the city of a specified location
	 * @param latitude of the location
	 * @param longitude of the location
	 * @return city of the location
	 * @throws IOException if a connection issue occurred
	 */
	public String getCity(double latitude, double longitude) throws IOException {
		String res = getGoogleResult(latitude, longitude);
		res = extractCityFromGoogle(res);
		return res.toLowerCase();
		
	}
	
	/**
	 * Gets a <code> Location </code> instance, which contains data of the
	 * location specified with coordinates
	 * @param latitude of the location
	 * @param longitude of the location
	 * @return Location instance, which contains location data
	 * @throws IOException if a connection issue occurred
	 */
	public Location getLocation(double latitude, double longitude) throws IOException {
		String osmResult = getOSMResult(latitude, longitude);
		String suburb = extractSuburbFromOSM(osmResult).toLowerCase();
		
		String googleResult = getGoogleResult(latitude, longitude);
		
		String city = extractCityFromGoogle(googleResult).toLowerCase();
		String address = extractAddressFromGoogle(googleResult).toLowerCase();
		
		Location l = new Location(city, suburb, address, latitude, longitude);
		return l;
		
	}
	
	
	
	// utils
	
	private String getOSMResult(double latitude, double longitude) throws IOException {
		URL url = new URL(OSM_URL_BASE + "lat=" + latitude + "&lon=" + longitude + OSM_URL_END);
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		String res = br.readLine();
		br.close();
		return res;
	}
	
	private String getGoogleResult(double latitude, double longitude) throws IOException {
		URL url = new URL(GOOGLE_URL_BASE + latitude + ',' + longitude + "&key=" + GOOGLE_KEY);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String r = in.readLine();
		while(!r.contains("formatted_address")) {
			r = in.readLine();
		}
		in.close();
		
		String res = r.substring(r.indexOf(':') + 3, r.length() - 2);
		
		return res.toString();
	}
	
	private String extractSuburbFromOSM(String queryResult) {
		if(queryResult.contains("suburb")) {
			int index = queryResult.indexOf("suburb");
			queryResult = queryResult.substring(index + 6 + 3);
			return queryResult.substring(0, queryResult.indexOf('"'));
		}
		else if(queryResult.contains("neighbourhood")) {
			int index = queryResult.indexOf("neighbourhood");
			queryResult = queryResult.substring(index + 13 + 3);
			return queryResult.substring(0, queryResult.indexOf('"'));
		}
		else
			return null;
	}
	

	private String extractCityFromGoogle(String googleResult) {
		String[] data = googleResult.split(", ");
		int index = data.length < 4 ? 1 : 2;
		
		String[] t = data[index].split(" ");
		String city = "";
		for(int i = 1; i < t.length - 1; i++)
			city += t[i] + (i + 1 < t.length - 1 ? " " : "");
		
		return city;
	}
	
	private String extractAddressFromGoogle(String googleResult) {
		String[] data = googleResult.split(", ");
		String address = data[0] + " " + (data.length < 4 ?
				"" : data[1]);
		
		return address;
	}
	
	
	
}
