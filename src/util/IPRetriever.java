package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * The <code> IPRetriever </code> class finds out
 * the real machine IP addresses, both local and not
 * @author Luca Landa
 */
public class IPRetriever {

	/**
	 * Gets the machine ip address, requesting it to
	 * http://bot.whatismyipaddress.com
	 * @return address the ip address
	 * @throws ConnectException if a connection error occurred
	 */
	public static String getMyIP() throws ConnectException {
		URL url = null;
        BufferedReader in = null;
        String ipAddress = "";
        try {
            url = new URL("http://bot.whatismyipaddress.com");
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            
            //IF not connected to internet, it will return one empty String
            ipAddress = in.readLine().trim();
            
            if (!(ipAddress.length() > 0)) {
                try {
                    InetAddress ip = InetAddress.getLocalHost();
                    ipAddress = (ip.getHostAddress()).trim();
                } catch(Exception exp) {
                    ipAddress = "ERROR";
                }
            }
        } catch (Exception ex) {
            // This try will give the Private IP of the Host.
            try {
                InetAddress ip = InetAddress.getLocalHost();
                ipAddress = (ip.getHostAddress()).trim();
            } catch(Exception exp) {
                ipAddress = "ERROR";
            }

        }
		
        if(ipAddress.equals("127.0.0.1") || ipAddress.equals("ERROR"))
        	throw new ConnectException();
        
        else
        	return ipAddress;
        
	}
	
	/**
	 * Gets the machine real localhost ip
	 * It's particularly useful on UNIX/LINUX machines, where an attempt to 
	 * get it from <code> InetAddress </code> could just return a loopback address,
	 * due to a system bug / feature
	 * @return address localhost ip address
	 * @throws UnknownHostException if cannot find machine's localhost ip
	 */
	public static String getLocalHostIP() throws UnknownHostException {
		
		try {
	        InetAddress candidateAddress = null;

	        for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();

	            for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {

	                    if (inetAddr.isSiteLocalAddress()) {
	                        // Found non-loopback site-local address.
	                    	String result = inetAddr.toString();
	                    	if(result.startsWith("/"))
	                    		result = result.substring(1);
	                        return result;
	                    }
	                    else if (candidateAddress == null) {
	                        // Found a non-loopback address, but not necessarily site-local.
	                        candidateAddress = inetAddr;
	                    }
	                }
	            }
	        }
	        if (candidateAddress != null) {
	            /*
	             * Did not find a site localhost address, but found some other non-loopback address.
	             * Return this non-loopback candidate address.
	             */
	        	String result = candidateAddress.toString();
	        	if(result.startsWith("/"))
	        		result = result.substring(1);
	            return result;
	        }
	        /* 
	         * Didn't find a non-loopback address.
	         * Return InetAddress.getLocalHost() result.
	         */
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        if (jdkSuppliedAddress == null) {
	            throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
	        }
	        String result = jdkSuppliedAddress.toString();
	        if(result.startsWith("/"))
	        	return result.substring(1);
	        return result;
	    }
	    catch (Exception e) {
	        UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
	        unknownHostException.initCause(e);
	        throw unknownHostException;
	    }
	
	}
	
	/**
	 * Checks for a LINUX/UNIX system to correct a java rmi bug which leads to have
	 * remote references that points to a loopback address, instead of the real localhost address,
	 * cause of connection issues
	 * @throws UnknownHostException if could not find the real localhost ip
	 */
	public static void linux_RMI_BugCorrection() throws UnknownHostException {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("linux") || os.contains("unix"))
			System.setProperty("java.rmi.server.hostname", getLocalHostIP());
			
	}
}
