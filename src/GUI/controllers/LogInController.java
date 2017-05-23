package GUI.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ConnectionManager;
import exceptions.UserNotConfirmedException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

/**
 * The <code> LogInController </code> is the controller
 * of LogIn.fxml view
 * @author Luca Landa
 */
public class LogInController implements Initializable {

	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	
	private ConnectionManager connectionManager;
	
	
	@Override
	/**
	 * Initialize method, inherited from Initializable
	 */
	public void initialize(URL location, ResourceBundle resources) {
		connectionManager = ConnectionManager.getConnectionManager();
	}
	
	
	
	@FXML
	/**
	 * Performs a logIn operation with the user's account data inserted as input
	 * @throws IOException in case of connection issues
	 */
	public void logIn() throws IOException {
		String userID = username.getText();
		String userPassword = password.getText();
		
		boolean success = false;
		try {
			success = connectionManager.tryLogin(userID, userPassword);
		} catch (UserNotConfirmedException e) {
			success = tryConfirmRegistration(userID, userPassword);
		}
		
		if(success)
			launchMainWindowLogged();
		
		else
			System.err.println("LogInControl - error while trying to communicate with server");
	}
	
	
	@FXML
	/**
	 * Launches registration window
	 */
	public void launchRegistration() {
		if(!connectionManager.connectToBalancer())
			return;			
		
		ClientApplication.launchRegistrationWindow();
	}

	@FXML
	/**
	 * Launches ConnectionSettings window
	 */
	public void launchConnectionSettings() {
		ClientApplication.launchConnectionSettingsWindow();
	}
	
	
	@FXML
	/**
	 * Launches the window for accessing the application without an user account
	 */
	public void launchApplicationNotLogged() {
		System.out.println("LogInController - launching main window without account");

		if(!connectionManager.connectToBalancer())
			return;
		
		ClientApplication.launchAccessWithoutAccountWindow();
		
	}
	
	
	
	//utils
	
	private boolean tryConfirmRegistration(String userID, String password) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Conferma registrazione");
		dialog.setHeaderText("Conferma la tua registrazione a WatchNeighbors");
		dialog.setContentText("Inserisci il codice di conferma ricevuto per email:");
		Optional<String> result = dialog.showAndWait();
		if(!result.isPresent())
			return false;
		
		String code = result.get();
		
		return connectionManager.tryConfirmRegistration(userID, password, code);
		
	}
	
	private void launchMainWindowLogged() throws MalformedURLException, IOException {
		System.out.println("LogInController - successful login, launching main window");
		ClientApplication.launchMainWindow();
		
	}
}
