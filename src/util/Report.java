package util;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The <code> Report </code> instance represents user
 * reports on Watchneighbours application.
 * @author Nicola Landro
 * @author Luca Landa
 */
public class Report implements Serializable{
	
	private static final long serialVersionUID = 1L;

	/**
	 * The <code> StatusType </code> enum is used to represent
	 * the report status
	 * @author Nicola Landro
	 * @author Luca Landa
	 */
	public enum StatusType { ACTIVE, IN_CHARGE, CLOSED }
	
	private String reporting;
	private String city;
	private String suburb;
	private String userInCharge;
	private String motive;
	private StatusType status;
	private double reportingLat;
	private double reportingLon;
	private double latReport;
	private double lonReport;
	private String result;
	private Timestamp timestamp;

	
	private static String[] statusTypes = { "active", "in charge", "closed" };
	
	
	
	/**
	 * Constructs a Report instance with the specified data
	 * @param reporting the user which created the report
	 * @param suburb the suburb where the report is located
	 * @param city the city where the report is located
	 * @param userInCharge the user (if exists) who took charge of the report
	 * @param status of the report
	 * @param motive of the report
	 * @param reportingLat latitude of the reporting user
	 * @param reportingLon longitude of the reporting user
	 * @param latReport latitude of the report
	 * @param lonReport longitude of the report
	 */
	public Report(String reporting, String city, String suburb, String userInCharge, String status, String motive,
			double reportingLat, double reportingLon, double latReport, double lonReport) {
		this.reporting = reporting;
		this.city = city;
		this.suburb = suburb;
		this.userInCharge = userInCharge;
		this.motive = motive;
		this.reportingLat = reportingLat;
		this.reportingLon = reportingLon;
		this.latReport = latReport;
		this.lonReport = lonReport;
		
		switch(status.toLowerCase()) {
		case "active":
			this.status = StatusType.ACTIVE;
			break;
			
		case "in charge": case "in_charge":
			this.status = StatusType.IN_CHARGE;
			break;
			
		case "closed":
			this.status = StatusType.CLOSED;
			break;
			
		default:
			throw new IllegalArgumentException("Error, invalid status (must be <active>, <in charge>, <in_charge> or <closed>)");
		}
	}
	
	



	/**
	 * Constructs an empty Report instance
	 */
	public Report() {
		
	}

	@Override
	/* (non-Javadoc)
	 * @see java.lang.Object#equals()
	 */
	public boolean equals(Object obj) {
		if(!(obj instanceof Report))
			return false;
		
		Report rep = (Report) obj;
		return (rep.getReporting().equals(reporting) && timestamp.equals(rep.timestamp));
		
	}

	/**
	 * Gets the report's reporting user
	 * @return reporting userID
	 */
	public String getReporting() {
		return reporting;
	}

	/**
	 * Sets the report's reporting user
	 * @param reporting the reporting userID to set
	 */
	public void setReporting(String reporting) {
		this.reporting = reporting;
	}

	/**
	 * Gets report suburb
	 * @return suburb of the report
	 */
	public String getSuburb() {
		return suburb;
	}

	/**
	 * Sets the report suburb
	 * @param suburb the suburb to set
	 */
	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}

	/**
	 * Gets the ID of the user who took charge of the report
	 * @return ID of the user in charge
	 */
	public String getUserInCharge() {
		return userInCharge;
	}

	/**
	 * Sets the ID of the user who took charge
	 * @param userInCharge the userInCharge to set
	 */
	public void setUserInCharge(String userInCharge) {
		this.userInCharge = userInCharge;
	}

	/**
	 * Gets the report status
	 * @return status of the report
	 */
	public StatusType getStatus() {
		return status;
	}

	/**
	 * Sets the report status
	 * @param status the status to set
	 */
	public void setStatus(StatusType status) {
		this.status = status;
	}

	/**
	 * Sets the report status, receiving a string parameter which must
	 * be "active", "in charge", "in_charge" or "closed".
	 * If it is different from these 4 strings (letters' case does not matter)
	 * an IllegalArgumentException will be thrown
	 * @param status the status to set
	 * @throws IllegalArgumentException if the given string status is not valid
	 */
	public void setStatus(String status) {
		switch(status.toLowerCase()) {
		case "active":
			this.status = StatusType.ACTIVE;
			break;
			
		case "in charge": case "in_charge":
			this.status = StatusType.IN_CHARGE;
			break;
			
		case "closed":
			this.status = StatusType.CLOSED;
			break;
			
		default:
			throw new IllegalArgumentException("Error, invalid status (must be <active>, <in charge>, <in_charge> or <closed>)");
		}
	}

	/**
	 * Gets report motive
	 * @return motive of the report
	 */
	public String getMotive() {
		return motive;
	}

	/**
	 * Sets report motive
	 * @param motive the motive to set
	 */
	public void setMotive(String motive) {
		this.motive = motive;
	}

	/**
	 * Gets latitude of the reporting user
	 * @return the reportingLat
	 */
	public double getReportingLat() {
		return reportingLat;
	}

	/**
	 * Sets latitude of the reporting user
	 * @param reportingLat the reporting user latitude to set
	 */
	public void setReportingLat(double reportingLat) {
		this.reportingLat = reportingLat;
	}

	/**
	 * Gets longitude of the reporting user
	 * @return reportingLon longitude of the reporting user
	 */
	public double getReportingLon() {
		return reportingLon;
	}

	/**
	 * Sets longitude of the reporting user
	 * @param reportingLon the reporting user longitude to set
	 */
	public void setReportingLon(double reportingLon) {
		this.reportingLon = reportingLon;
	}

	/**
	 * Gets latitude of the report
	 * @return latitude of the report
	 */
	public double getLatReport() {
		return latReport;
	}

	/**
	 * Sets latitude of the report
	 * @param latReport the report latitude to set
	 */
	public void setLatReport(double latReport) {
		this.latReport = latReport;
	}

	/**
	 * Gets report longitude
	 * @return longitude of the report
	 */
	public double getLonReport() {
		return lonReport;
	}

	/**
	 * Sets report longitude
	 * @param lonReport the report longitude to set
	 */
	public void setLonReport(double lonReport) {
		this.lonReport = lonReport;
	}

	/**
	 * Gets report timestamp
	 * @return timestamp of the report
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets report timestamp
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Report [reporting=" + reporting + ", suburb=" + suburb + ", userInCharge=" + userInCharge + ", status="
				+ status.toString().toLowerCase() + ", motive=" + motive + ", reportingLat=" + reportingLat + ", reportingLon=" + reportingLon
				+ ", latReport=" + latReport + ", lonReport=" + lonReport + ", timestamp=" + timestamp + "]";
	}

	/**
	 * Gets all status types allowed
	 * @return types allowed for the report status
	 */
	public static String[] getStatusTypes() {
		return statusTypes;
	}

	/**
	 * Gets report result
	 * @return result of the report
	 */
	public String getResult() {
		return result;
	}

	/**
	 * Sets report result
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * Gets report city
	 * @return city of the report
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Sets report city
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	

}
