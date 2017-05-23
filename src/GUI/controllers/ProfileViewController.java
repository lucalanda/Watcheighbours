package GUI.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.ResourceBundle;

import client.ConnectionManager;
import exceptions.NoSuburbException;
import exceptions.UserNotConfirmedException;
import exceptions.WrongDataException;
import interfaces.SubUrbInterface;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import mapEngine.MapEngine;
import netscape.javascript.JSObject;
import util.Location;
import util.LocationDataQuery;
import util.User;

/**
 * The <code> ProfileViewController </code> is a controller
 * of ProfileView.fxml view 
 * @author Luca Landa
 */
public class ProfileViewController implements Initializable {

	@FXML
	private TextField nameField;
	@FXML
	private TextField surnameField;
	@FXML
	private TextField userIDField;
	@FXML
	private TextField emailField;
	@FXML
	private TextField passwordField;
	
	@FXML
	private TextField cityField;
	@FXML
	private TextField addressField;
	
	@FXML
	private Label messageLabel;
	
	@FXML
	private Button modifyButton;
	@FXML
	private Button exitButton;
	
	@FXML
	private WebView webView;
	
	private Stage ownStage;
	
	private ConnectionManager connectionManager;
	private LocationDataQuery locationDataQuery;
	
	private SubUrbInterface suburbHandler;
	private User loggedUser;
	private boolean editMode = false;

	private String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private MapEngine mapEngine;
	private Timeline locationUpdateTimeline;
	
	private String name;
	private String surname;
	private String email;
	private String password;
	private String city;
	private String address;
	
	
	
	@Override
	/**
	 * initialize method, inherited from Initializable interface
	 */
	public void initialize(URL location, ResourceBundle resources) {
		connectionManager = ConnectionManager.getConnectionManager();
		locationDataQuery = new LocationDataQuery();
		
		loggedUser = connectionManager.getLoggedUser();
		if(loggedUser == null)
			throw new RuntimeException("ProfileView error: no logged user");
		
		suburbHandler = connectionManager.getSuburbHandler();
		ownStage = MainWindowController.getProfileViewStage();
		resetAndDisableModify();
		System.out.println("ProfileViewController - loggedUser:" + loggedUser + "\n" + 
				"location:" + loggedUser.getCity() + " " + loggedUser.getSuburb());
		
		Runnable task = new Runnable() {

			@Override
			public void run() {
				mapEngine.setDraggableMarkersBound(1);
				mapEngine.switchMapListener();
			}
			
		};
		
		mapEngine = new MapEngine(webView, task, loggedUser.getLatitude(), loggedUser.getLongitude());
		
		userIDField.setFocusTraversable(false);
	}
	
	@FXML
	/**
	 * Searches a map's position with data inserted as input
	 */
	public void searchPosition() {
		if (locationUpdateTimeline!=null) locationUpdateTimeline.stop();
		
		String city = cityField.getText();
		if(city.equals(""))
			return;
		String a = addressField.getText().toLowerCase();
		if(!a.startsWith("via") || !a.startsWith("viale") || !a.startsWith("v.le") || !a.startsWith("p.zza") || 
				!a.startsWith("piazza"))
			a = "via " + a;
		String address = a;
		
        locationUpdateTimeline = new Timeline();
        locationUpdateTimeline.getKeyFrames().add(
            new KeyFrame(new Duration(400), new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                	mapEngine.deleteAllDraggableMarkers();
                	mapEngine.goToLocation("italia" + " " + city + " " + address);
                }
            })
        );                
        locationUpdateTimeline.play();
        
	}

	@FXML
	/**
	 * Enables the modify mode if not enabled, or elaborates the new data and confirms the
	 * success or the operation, or shows the error occurred during profile update operation 
	 */
	public void modify() {
		if(!editMode) {
			editMode = true;
			enableModify();
			modifyButton.setText("Conferma");
			exitButton.setText("Annulla");
		}
		 
		else {
			boolean correctFields = false;
			try {
				correctFields = checkFields();
			} catch (RemoteException e) {
				exit(true);
				return;
			}
			
			if(!correctFields)
				return;
			
			String suburb = null;
			
			JSObject marker = getMarker();
			if(marker == null) {
				showMarkerAlert();
				return;
			}
			
			try {
				String latLon = marker.getMember("position").toString();
				double markerLat = Double.parseDouble(latLon.substring(1, latLon.indexOf(',')));
				double markerLon = Double.parseDouble(latLon.substring(latLon.indexOf(' '), latLon.length() - 1));
				
				/*
				 * first we get the suburb, and later the Location object, instead of just getting a Location object,
				 * because getting just the suburb from LocationDataQuery instance is 4X faster than getting
				 * a Location instance
				 */
				suburb = locationDataQuery.getSuburb(markerLat, markerLon);
			} catch (IOException e) {
				showConnectionAlert();
				exit(true);
				return;
			} catch (NoSuburbException e) {
				suburb = askForSuburb();
				if(suburb == null) {
					editMode = false;
					resetAndDisableModify();
					return;
				}
			}
			
			Location location = null;
			
			try {
				location = getMarkerLocation();
			} catch (IOException e) {
				showConnectionAlert();
				e.printStackTrace();
				exit(true);
				return;
			}
			
			suburb = location.getSuburb();
			
			if(!suburb.equalsIgnoreCase(loggedUser.getSuburb())) {
				boolean confirm = confirmChange(suburb);
				if(!confirm)
					return;
			}
			
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			city = location.getCity();
			address = location.getAddress();
			//user will not be able to change his userID
			User newUser = new User(loggedUser.getUserId(), email, surname, name, password, suburb, city, address, latitude, longitude);
			
			if(loggedUser.deepEquals(newUser)) {
				showError("Modifica profilo", "", "Attenzione: non hai modificato alcun campo.");
				return;
			}
				
			boolean success = false;
			try {
				success = suburbHandler.updateProfile(loggedUser, newUser);
			} catch (RemoteException e) {
				exit(true);
				return;
			} catch (WrongDataException e) {
				showError("Errore", "", e.getMessage());
				return;
			}
			
			if(success) {
				loggedUser = newUser;
				resetAndDisableModify();
				connectionManager.setLoggedUser(newUser);
				connectionManager.setSuburbAndCity(suburb, city);
				connectionManager.setSuburbHandlerName(suburb, city);
				try {
					success = connectionManager.tryLogin(loggedUser.getUserId(), password);
				} catch (UserNotConfirmedException e) {
					//case not expected
					e.printStackTrace();
					exit(true);
					return;
				}
				showUpdateSuccessAlert();
			}
		}
	}
	
	@FXML
	/**
	 * Deletes user's account
	 */
	public void deleteProfile() {
		if(!confirmDelete())
			return;
		
		boolean success = false;
		
		try {
			success = suburbHandler.removeUser(loggedUser);
			if(success)
				showDeleteSuccessAlert();
				
		} catch (RemoteException e) {
			
		} finally {
			connectionManager.disconnect();
			exit(!success);
		}
		
		
	}
	
	@FXML
	/**
	 * Shows an action alert for actions those have not been implemented yet
	 */
	public void showUnimplementedActionAlert() {
		showErrorMessage("Errore", "Funzione non implementata");
	}
	 
	@FXML
	/**
	 * Disables modify mode if enabled, or closes ProfileView view else
	 */
	public void abortOrQuit() {
		if(editMode) {
			editMode = false;
			resetAndDisableModify();
		}
		
		else
			exit(false);

	}
	
	
	
	//utils
	
	private void resetAndDisableModify() {
		nameField.setText(loggedUser.getName());
		surnameField.setText(loggedUser.getSurname());
		userIDField.setText(loggedUser.getUserId());
		emailField.setText(loggedUser.getEmail());
		passwordField.setText(loggedUser.getPassword());
		
		String city = loggedUser.getCity();
		String address = loggedUser.getAddress();
		cityField.setText(city);
		addressField.setText(address);
		
		nameField.setEditable(false);
		nameField.setMouseTransparent(true);
		nameField.setFocusTraversable(false);
		
		surnameField.setEditable(false);
		surnameField.setMouseTransparent(true);
		surnameField.setFocusTraversable(false);
		
		emailField.setEditable(false);
		emailField.setMouseTransparent(true);
		emailField.setFocusTraversable(false);
		
		passwordField.setEditable(false);
		passwordField.setMouseTransparent(true);
		passwordField.setFocusTraversable(false);
		
		cityField.setEditable(false);
		cityField.setMouseTransparent(true);
		cityField.setFocusTraversable(false);
		
		addressField.setEditable(false);
		addressField.setMouseTransparent(true);
		addressField.setFocusTraversable(false);
		
		messageLabel.setVisible(false);
		
		modifyButton.setText("Modifica");
		exitButton.setText("Esci");
		
		editMode = false;
	}
	 
	private void enableModify() {
		nameField.setEditable(true);
		nameField.setMouseTransparent(false);
		nameField.setFocusTraversable(true);
		
		surnameField.setEditable(true);
		surnameField.setMouseTransparent(false);
		surnameField.setFocusTraversable(true);
		
		emailField.setEditable(true);
		emailField.setMouseTransparent(false);
		emailField.setFocusTraversable(true);
		
		passwordField.setEditable(true);
		passwordField.setMouseTransparent(false);
		passwordField.setFocusTraversable(true);
		
		cityField.setEditable(true);
		cityField.setMouseTransparent(false);
		cityField.setFocusTraversable(true);
		
		addressField.setEditable(true);
		addressField.setMouseTransparent(false);
		addressField.setFocusTraversable(true);
		
		messageLabel.setVisible(true);
		
		modifyButton.setText("Conferma");
		exitButton.setText("Annulla");
		
		editMode = true;
	}
	
	private boolean checkFields() throws RemoteException {
		name = nameField.getText();
		surname = surnameField.getText();
		email = emailField.getText();
		password = passwordField.getText();
		
		city = cityField.getText();
		address = addressField.getText();
		 
		if(name.equals("") || surname.equals("") || email.equals("") ||
				password.equals("") || city.equals("") || address.equals("")) {
				showError("Errore", "", "Dati mancanti: devi riempire tutti i campi");
				return false;
		}
		 
		if(!email.matches(emailPattern)){
			showError("Errore", "", "L'indirizzo email inserito non è valido");
			return false;
		}
			
		String oldEmail = loggedUser.getEmail();
		boolean	emailAvailable = oldEmail.equalsIgnoreCase(email) ?
				true : connectionManager.checkEmailAvailability(email);
		if(!emailAvailable) {
			showError("Errore", "", "L'indirizzo email inserito è già in uso, inseriscine uno diverso");
		 	return false;
		}
		//controlli superati
		return true;
		 
     
	}
	
	private Location getMarkerLocation() throws IOException {
		JSObject marker = getMarker();
		String coords = marker.getMember("position").toString();
		double latitude = Double.parseDouble(coords.substring(1, coords.indexOf(',')));
		double longitude = Double.parseDouble(coords.substring(coords.indexOf(' '), coords.length() - 1));
		
		Location result = locationDataQuery.getLocation(latitude, longitude);
		
		return result;
	}
	
	private JSObject getMarker() {
		JSObject draggableMarkers = mapEngine.getAllDraggableMarkers();
		
		JSObject result;
		try {
			result = (JSObject) draggableMarkers.getSlot(0);
		} catch (ClassCastException e) {
			result = null;
		}
		
		return result;
	}
	
	private String askForSuburb() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Quartiere non trovato");
		dialog.setHeaderText("Impossibile trovare il quartiere corrispondente all'indirizzo dato");
		dialog.setContentText("Inserisci il tuo quartiere");

		Optional<String> result = dialog.showAndWait();

		if(!result.isPresent())
			return null;
		
		else 
			return result.get();

	}
	 
	private void showError(String title, String header, String context) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.showAndWait();
		 
	}
	
	private void showMarkerAlert() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Segnalatore");
		alert.setHeaderText("Clicca sulla mappa per posizionare un segnalatore e" + "\n" + 
				"trascinalo sulla tua posizione, prima di confermare");
		alert.setContentText("");
		alert.showAndWait();
	}
		
	private void showConnectionAlert() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Connessione persa");
		alert.setHeaderText("Connessione a internet persa");
		alert.setContentText("Ritorno al menù principale");
		alert.showAndWait();
	}
	
	private boolean confirmChange(String newSuburb) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Modifica");
		alert.setHeaderText("Il tuo nuovo quartiere è " + newSuburb);
		alert.setContentText("vuoi continuare?");
		Optional<ButtonType> result = alert.showAndWait();
		
		return (result.isPresent() && result.get() == ButtonType.OK);
		
	}
	
	private boolean confirmDelete() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Cancellazione");
		alert.setHeaderText("");
		alert.setContentText("Sei sicuro di voler eliminare il tuo profilo?");
		Optional<ButtonType> result = alert.showAndWait();
		
		return (result.isPresent() && result.get() == ButtonType.OK);
	}
	
	private void showDeleteSuccessAlert() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Successo");
		alert.setHeaderText("Il tuo profilo è stato eliminato");
		alert.setContentText("Ritorno al menù principale");
		alert.showAndWait();
	}
	
	private void showUpdateSuccessAlert() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Successo");
		alert.setHeaderText("");
		alert.setContentText("Modifica del profilo effettuata correttamente");
		alert.showAndWait();
	}
	
	private void showErrorMessage(String title, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.setContentText("");
		alert.showAndWait();
	}
	
	private void exit(boolean connectionIssue) {
		if(connectionIssue) {
			connectionManager.disconnect();
			String title = "Connessione";
			String context = "Connessione con il server interrotta" + "\n" + "ritorno al menù principale";
			showError(title, "", context);
		}
		
		ownStage.close();
	}
	
}
