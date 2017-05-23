package GUI.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ConnectionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * The <code> ConnectionSettingsController </code> is the controller
 * of the ConnectionSettings.fxml view
 * @author Luca Landa
 *
 */
public class ConnectionSettingsController implements Initializable {
	
	@FXML
	private TextField hostAddress;
	@FXML
	private TextField portNumber;
	@FXML
	private Label connectionStatus;
	@FXML
	private Button applyButton;
	@FXML
	private Button defaultButton;
	@FXML
	private Button okButton;
	
	private ConnectionManager connectionManager;
	
	private String actualHostAddress;
	private int actualHostPort;
	
	private static final String IP_ADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +	"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	private static final String INTEGER_PATTERN = "[0-9]*[0-9]";
	
	
	
	@Override
	/**
	 * initialize method, inherited from Initializable interface
	 */
	public void initialize(URL location, ResourceBundle resources) {
		connectionManager = ConnectionManager.getConnectionManager();
		actualHostAddress = connectionManager.getHost();
		actualHostPort = connectionManager.getPort();
		
		hostAddress.setText(actualHostAddress);
		portNumber.setText(Integer.toString(actualHostPort));
		
		testConnection();
	}
	
	
	@FXML
	/**
	 * Applies connection data settings given as input
	 */
	public void applySettings() {
		if(!checkData())
			return;
		
		
		String host = hostAddress.getText();
		int numPort = Integer.parseInt(portNumber.getText());
		
		
		if(!host.equals(connectionManager.getHost()) || numPort != (connectionManager.getPort())){
			
			connectionManager.setConnectionData(host, numPort);
			actualHostAddress = host;
			actualHostPort = numPort;
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setHeaderText("L'indirizzo del server è stato modificato");
			alert.showAndWait();
		
		}
		
	}
	
	
	@FXML
	/**
	 * Resets connection data to default settings
	 */
	public void setDefaultSettings() {
		String defaultHost = connectionManager.getDefaultHost();
		int defaultPort = connectionManager.getDefaultPort();
		
		hostAddress.setText(defaultHost);
		portNumber.setText(Integer.toString(defaultPort));
		
	}
	
	
	@FXML
	/**
	 * Makes application to return to logIn window and connect to ConnectionBalancer instance
	 * on Watchneighbours server
	 */
	public void ok() {
		ClientApplication.launchLogInWindow();
		connectionManager.connectToBalancer();

	}
	
	@FXML
	/**
	 * Tests connection to Watchneighbours server with connection data inserted as input
	 */
	public void testConnection() {
		if(!checkData())
			return;
		
		String host = hostAddress.getText();
		int port = Integer.parseInt(portNumber.getText());
		
		System.out.println("ConnectionSettingsControl: testing connection " + host + " " + port);
		
		boolean success = connectionManager.testCustomConnection(host, port);
		connectionStatus.setStyle("-fx-text-fill: white;" + (success ? 
				"-fx-background-color: green;" : "-fx-background-color: red"));
		connectionStatus.setText("Stato: " + (success ? 
				"ok" : "ko"));
		
	}

	
	//utils 
	
	private boolean checkData() {
		String host = hostAddress.getText();
		String port = portNumber.getText();
		
		boolean addressCorrect = host.matches(IP_ADDRESS_PATTERN) || host.equals("localhost");
		boolean portCorrect = port.matches(INTEGER_PATTERN);
		
		if(!addressCorrect || !portCorrect) {
			String header = "";
						
			if(!addressCorrect) 
				header += "Indirizzo ip non corretto." + "\n";
			
			if(!portCorrect)
				header += "Errore, numero di porta errato.";
				
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Errore");
			alert.setHeaderText(header);
			alert.showAndWait();
			return false;
		}
		
		return true;
	}
	
}
