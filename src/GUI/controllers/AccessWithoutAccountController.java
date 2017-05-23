package GUI.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ConnectionManager;
import exceptions.NoSuburbException;
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

/**
 * The <code> AccessWithoutAccountController </code> is the controller of 
 * the AccessWithoutAccount.fxml view
 * @author Luca Landa
 *
 */
public class AccessWithoutAccountController implements Initializable{
	
	@FXML
	private TextField cityField;
	@FXML
	private TextField addressOrSuburbField;
	
	@FXML
	private WebView webView;
	private MapEngine mapEngine;
	
	private ConnectionManager connectionManager;
	private LocationDataQuery locationDataQuery;
	
	private Timeline locationUpdateTimeline;
	

	
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
	 * Centers the view's map on the searched position
	 */
	public void searchPosition() {
		if (locationUpdateTimeline!=null) locationUpdateTimeline.stop();
		
		String city = cityField.getText();
		String addressOrSuburb = addressOrSuburbField.getText();
		if(city.equals(""))
			return;
		
        locationUpdateTimeline = new Timeline();
        locationUpdateTimeline.getKeyFrames().add(
            new KeyFrame(new Duration(400), new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                	mapEngine.deleteAllDraggableMarkers();
            		mapEngine.goToLocation(city + " " + addressOrSuburb);
                	
                }
            })
        );                
        locationUpdateTimeline.play();
	}
	
	@FXML
	/**
	 * Tries to perform the access operation with the input data inserted
	 */
	public void confirm() {
		String suburb = null;
		String city = null;
		
		JSObject marker = getMarker();
		if(marker == null) {
			showMarkerAlert();
			return;
		}
		
		try {
			String latLon = marker.getMember("position").toString();
			double markerLat = Double.parseDouble(latLon.substring(1, latLon.indexOf(',')));
			double markerLon = Double.parseDouble(latLon.substring(latLon.indexOf(' '), latLon.length() - 1));
			suburb = locationDataQuery.getSuburb(markerLat, markerLon);
			
		} catch (IOException e) {
			showConnectionAlert();
			e.printStackTrace();
			returnToLogInMenu();
		} catch (NoSuburbException e) {
			suburb = askSuburb();
			if(suburb == null)
				return;
		}

		
		Location location = null;
		try {
			location = getMarkerLocation();
		} catch (IOException e) {
			showConnectionAlert();
			e.printStackTrace();
			returnToLogInMenu();
		}
		
		boolean success = false;
		suburb = location.getSuburb();
		city = location.getCity();
		try {
			success = connectionManager.tryAccessWithoutAccount(suburb, city);
			if(success) {
				launchMainWindow();
				return;
			}
		} catch (RemoteException e) {
			returnToLogInMenu();
		} catch (IOException e) {
			//not expected case
			showConnectionAlert();
			e.printStackTrace();
			returnToLogInMenu();
		} 
		
		if(!success) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Errore");
			alert.setHeaderText("");
			alert.setContentText("Si è verificato un errore durante la comunicazione con il server" + "\n" + 
					"Ritorno al menù principale");
			returnToLogInMenu();
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
	 * Makes the application return to the logIn window
	 */
	public void returnToLogInMenu() {
		ClientApplication.launchLogInWindow();
	}
	
	
	
	//utils
	
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
	
	private Location getMarkerLocation() throws IOException {
		JSObject marker = getMarker();
		String coords = marker.getMember("position").toString();
		double latitude = Double.parseDouble(coords.substring(1, coords.indexOf(',')));
		double longitude = Double.parseDouble(coords.substring(coords.indexOf(' '), coords.length() - 1));
		
		Location result = locationDataQuery.getLocation(latitude, longitude);
		
		return result;
	}
	
	
	
	
	
	private void showMarkerAlert() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Segnalatore");
		alert.setHeaderText("Clicca sulla mappa per posizionare un segnalatore e" + "\n" + 
				"trascinalo sulla tua posizione, prima di confermare");
		alert.setContentText("");
		alert.showAndWait();
	}
	
	private String askSuburb() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Quartiere");
		dialog.setHeaderText("Impossibile trovare il quartiere corrispondente all'indirizzo dato");
		dialog.setContentText("Inserisci il quartiere");

		Optional<String> result = dialog.showAndWait();

		if(!result.isPresent())
			return null;
		
		else 
			return result.get();
		
	}
	
	private void showConnectionAlert() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Connessione persa");
		alert.setHeaderText("Connessione a internet persa");
		alert.setContentText("Ritorno al menù principale");
		alert.showAndWait();
	}
	
	private void showErrorMessage(String title, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.setContentText("");
		alert.showAndWait();
	}

	private void launchMainWindow() throws MalformedURLException, IOException {
		System.out.println("AccessWithoutAccountControl: accessing without account succeeded");
		ClientApplication.launchMainWindow();
	}
	
}
