package GUI.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ConnectionManager;
import exceptions.NoSuburbException;
import exceptions.WrongDataException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import mapEngine.MapEngine;
import netscape.javascript.JSObject;
import util.Location;
import util.LocationDataQuery;
import util.User;

/**
 * The <code> RegistrationController </code> is a controller
 * of Registration.fxml view
 * @author Luca Landa
 */
public class RegistrationController implements Initializable {
	
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
	private TextField confirmPasswordField;
	
	@FXML
	private TextField cityField;
	@FXML
	private TextField addressField;
	
	@FXML
	private WebView webView;
	private MapEngine mapEngine;
	
	private ConnectionManager connectionManager;
	private LocationDataQuery locationDataQuery;
	
	private String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private Timeline locationUpdateTimeline;

	private String name;
	private String surname;
	private String userID;
	private String email;
	private String password;
	private String confirmPassword;
	private String city;
	private String address;
	
	
	
	@Override
	/**
	 * initialize method, inherited from Initializable interface
	 */
	public void initialize(URL location, ResourceBundle resources) {
		connectionManager = ConnectionManager.getConnectionManager();
		locationDataQuery = new LocationDataQuery();
		Runnable task = new Runnable() {

			@Override
			public void run() {
				mapEngine.setDraggableMarkersBound(1);
				mapEngine.switchMapListener();
			}
			
		};
		
		mapEngine = new MapEngine(webView, task);
	}
	
	@FXML
	/**
	 * Sets the map on road view
	 */
	public void setRoadView() {
		mapEngine.viewRoad();
	}
	
	@FXML
	/**
	 * Sets the map on satellite view
	 */
	public void setSatelliteView() {
		mapEngine.viewSatellite();
	}
	
	@FXML
	/**
	 * Sets the map on hybrid view
	 */
	public void setHybridView() {
		mapEngine.viewHybrid();
	}
	
	@FXML
	/**
	 * Sets the map on terrain view
	 */
	public void setTerrainView() {
		mapEngine.viewTerrain();
	}
	
	@FXML
	/**
	 * Confirms registration with user's data inserted as input, or shows
	 * errors occurred during registration operation
	 */
	public void confirm() {
		
		if(!connectionManager.connectToBalancer())
			returnToLogInWindow();
		
		boolean correctFields = true;
		
		try {
			correctFields = checkFields();
		} catch(RemoteException e) {
			e.printStackTrace();
			returnToLogInWindow();
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
		} catch (NoSuburbException e) {
			suburb = askSuburb();
			if(suburb == null)
				return;
		} catch (IOException e) {
			showConnectionAlert();
			e.printStackTrace();
			returnToLogInWindow();
		}
		

		Location location = null;
		
		try {
			location = getMarkerLocation();
		} catch (IOException e) {
			showConnectionAlert();
			e.printStackTrace();
			returnToLogInWindow();
		}
		
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		String city = location.getCity();
		String address = location.getAddress();
		
		User user = new User(userID, email, surname, name, password, suburb, city, address, lat, lon);
		
		boolean success = false;
		try {
			success = connectionManager.registration(user);
			
		} catch (RemoteException e) {
			returnToLogInWindow();
		} catch (WrongDataException e) {
			showServerAlert(e.getMessage());
		}
		
		if(success) {
			showSuccessDialog();
			returnToLogInWindow();
		}
		
		else
			showError("Errore sconosciuto dal server");
		
		
		
		
	}
	
	
	@FXML
	/**
	 * Searches the position with location's data inserted as input
	 */
	public void searchPosition() {
		if (locationUpdateTimeline != null) locationUpdateTimeline.stop();
		
		String city = cityField.getText();
		String address = addressField.getText();
		if(city.equals("") || address.equals(""))
			return;
		
        locationUpdateTimeline = new Timeline();
        locationUpdateTimeline.getKeyFrames().add(
            new KeyFrame(new Duration(400), new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                	mapEngine.deleteAllDraggableMarkers();
                	mapEngine.goToLocation(city + " " + address);
                }
            })
        );
        locationUpdateTimeline.play();
        
	}
	
	@FXML
	/**
	 * Shows an action alert for actions those have not been implemented yet
	 */
	public void showUnimplementedActionAlert() {
		showError("Funzione non implementata");
	}
		
	@FXML
	/**
	 * Makes the application return to logIn window
	 */
	public void returnToLogInWindow() {
		ClientApplication.launchLogInWindow();
	}
	
	
	
	
	
	// utils
	
	private String askSuburb() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Quartiere non trovato");
		dialog.setHeaderText("Impossibile determinare il quartiere corrispondente all'indirizzo dato");
		dialog.setContentText("Inserisci il tuo quartiere");

		Optional<String> result = dialog.showAndWait();

		if(!result.isPresent())
			return null;
		
		else 
			return result.get();
		
	}
	
	private boolean checkFields() throws RemoteException {
		String error = "";
		
		boolean success = true;
		
		name = nameField.getText();
		surname = surnameField.getText();
		userID = userIDField.getText();
		email = emailField.getText();
		password = passwordField.getText();
		confirmPassword = confirmPasswordField.getText();
		
		city = cityField.getText();
		address = addressField.getText();
		
		if(name.equals("") || surname.equals("") || userID.equals("") || email.equals("") ||
				password.equals("") || confirmPassword.equals("") || city.equals("") || 
				address.equals("")) {
			showError("- Dati mancanti");
			return false;
		}
		
		if(!email.matches(emailPattern)){
			error += "- Email non valida \n";
			success = false;
		}
		
		if(!password.equals(confirmPassword)){
			error += "- Le password inserite non corrispondono \n";
			success = false;
		}
		
		if(!success) {
			showError(error);
			return success;
		}
		
		boolean idAvailable = connectionManager.checkUserIDAvailability(userID);
		if(!idAvailable) {
			error += "- L'userID scelto è già in uso" + "\n";
			success = false;
		}

		boolean emailAvailable = connectionManager.checkEmailAvailability(email);
		if(!emailAvailable) {
			error += "- L'indirizzo email scelto è già in uso";
			success = false;
		}
		
		if(!success)
			showError(error);
		
		return success;
		
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
	
	private void showError(String error) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Errore");
		alert.setHeaderText("");
		alert.setContentText("Sono stati riscontrati i seguenti errori:" + "\n" + error);
		alert.showAndWait();
	}
	
	private void showServerAlert(String error) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Errore registrazione");
		alert.setHeaderText("");
		alert.setContentText("Ricevuto il seguente errore dal server:" + "\n" + error);
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
	
	private void showSuccessDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Registrazione");
		alert.setHeaderText("Registrazione effettuata con successo!");
		alert.setContentText("Ti abbiamo inviato per email il codice di conferma" + "\n" + 
				"inseriscilo la prima volta che effettuerai il login");
		alert.showAndWait();
	}
	
	
}
