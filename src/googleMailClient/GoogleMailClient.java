package googleMailClient;

import java.security.Security;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;



/**
 * The <code> GoogleMailClient </code> class sends emails to a specified address.
 * It is used to send to new registered users their confirmation code.
 * It's implemented with a Singleton pattern.  
 * @author Luca Landa
 */
public class GoogleMailClient {
	
	private static final String username = "watchneighbors";
	private static final String password = "su_LL16wn";	
	private static final String title = "Watchneighbors - registration confirm";
	private static final String message_part1 = "Thanks for signing up for Watchneighbors!" + "\n" + "Here's your temporary code:   ";
	private static final String message_part2 = "Use it the first time you log into the application to confirm your registration.";
		
	//Singleton pattern
	private static GoogleMailClient client;
	
	
	
    private GoogleMailClient() {
    	
    }
    
    /**
     * Returns the common GoogleMailClient instance
     * @return client the instance of GoogleMailClient
     */
    public static GoogleMailClient getGoogleMailClient() {
    	if(client == null)
    		client = new GoogleMailClient();
    	return client;
    }
    
    /**
     * Sends a temporary code to a specified email address
     * @param recipientEmail the user's account email
     * @param confirmationCode the user's account confirmation code
     * @throws AddressException if the email address is not correct
     * @throws MessagingException if a connection issue occurred
     */
    public void sendEmail(String recipientEmail, String confirmationCode) throws AddressException, MessagingException {
    	String message = message_part1 + confirmationCode + "\n" + message_part2;
    	send(username, password, recipientEmail, "", title, message);
    }
    
    /**
     * Sends email using GMail SMTP server.
     * @param username GMail username
     * @param password GMail password
     * @param recipientEmail TO recipient
     * @param ccEmail CC recipient. Can be empty if there is no CC recipient
     * @param title title of the message
     * @param message message to be sent
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
     */
	private void send(final String username, final String password, String recipientEmail, String ccEmail, String title, String message) throws AddressException, MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider()); //Settare nel compilatore Deprecated&Restrected -> deprecated warning
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtps.auth", "true");

        /*
        If set to false, the QUIT command is sent and the connection is immediately closed. If set 
        to true (the default), causes the transport to wait for the response to the QUIT command.

        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                http://forum.java.sun.com/thread.jspa?threadID=5205249
                smtpsend.java - demo program from javamail
        */
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(username + "@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

        if (ccEmail.length() > 0) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
        }

        msg.setSubject(title);
        msg.setText(message, "utf-8");
        msg.setSentDate(new Date());

        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

        t.connect("smtp.gmail.com", username, password);
        t.sendMessage(msg, msg.getAllRecipients());      
        t.close();
    }
}