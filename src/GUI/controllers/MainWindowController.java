package GUI.controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ConnectionManager;
import client.ReportObserver;
import exceptions.WrongDataException;
import interfaces.SubUrbInterface;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import mapEngine.MapEngine;
import netscape.javascript.JSObject;
import util.Report;
import util.Report.StatusType;
import util.ResourceManager;
import util.User;
import util.splashScreen.SimpleSplashScreen;

/**
 * The <code> MainWindowController </code> is the controller
 * of MainWindow.fxml view
 * @author Luca Landa
 */
public class MainWindowController implements Initializable {
	
	@FXML
	private TextField searchField;
	
	@FXML
	private WebView webView;
	private MapEngine mapEngine;
	
	@FXML
	private FlowPane reportViewsPane;
	@FXML
	private CheckBox userReportsCheckBox;
	@FXML
	private TextArea noReportsWarning;
	
	@FXML
	private Button buttonConfirmNewReport;
	@FXML
	private Button buttonAbortNewReport;
	@FXML
	private Button buttonProfile;
	@FXML
	private Button buttonExit;
	
	private Timeline locationUpdateTimeline;
	
	private ObservableList<Node> reportPanesList;
	private ArrayList<Report> reportsList;
	
	private EventHandler<ActionEvent> actionEventHandler;
	private EventHandler<ActionEvent> seeOnMapEventHandler;
	private EventHandler<MouseEvent> newReportClickEventHandler;
	
	private ConnectionManager connectionManager;
	private ResourceManager resourceManager;
	
	private SubUrbInterface suburbHandler;
	private ReportObserver observer;
	private boolean logged = false;
	private User loggedUser;
	private String suburb;
	private String city;
	
	private String newReportMotive;

	private static Stage profileViewStage;
	
	
	
	@Override
	/**
	 * initialize method, inherited from Initialize interface
	 */
	public void initialize(URL location, ResourceBundle resources) {
		connectionManager = ConnectionManager.getConnectionManager();
		resourceManager = ResourceManager.getResourceManager();
		
		buttonConfirmNewReport.setVisible(false);
		buttonConfirmNewReport.setStyle("-fx-background-color: green");
		buttonAbortNewReport.setVisible(false);
		buttonAbortNewReport.setStyle("-fx-background-color: red");
		noReportsWarning.setMouseTransparent(true);
		
		loggedUser = connectionManager.getLoggedUser();
		logged = (loggedUser != null);
		
		suburb = connectionManager.getSuburb();
		city = connectionManager.getCity();
		
		reportViewsPane.setFocusTraversable(false);
		reportPanesList = reportViewsPane.getChildren();
		reportsList = new ArrayList<Report>();
		
		if(!logged) {
			buttonProfile.setDisable(true);
			buttonExit.setText("Esci");
			userReportsCheckBox.setDisable(true);
		}
		
		suburbHandler = connectionManager.getSuburbHandler();

		generateEventHandlers();
		
		try {
			String id = loggedUser != null ?
					loggedUser.getUserId() : null;
			observer = new ReportObserver(this, id);
			connectionManager.setReportObserver(observer);

		} catch (RemoteException e) {
			System.err.println("RemoteException while trying to create Observer object");
			exit();
			return;
		}

		Runnable endLoadingTask = new Runnable() {
			
			@Override
			public void run() {
				List<Report> initReportsList = null;
				
				try {
					initReportsList = suburbHandler.addObserver(observer.getID(), observer);
					
					if(initReportsList != null)
						for(Report rep : initReportsList)
							if(!reportsList.contains(rep))
								addReport(rep);

				} catch (RemoteException e) {
					System.err.println("RemoteException while trying to add the observer and get reportsList");
					showConnectionError();
					exit();
					return;
				} finally {
					SimpleSplashScreen.stopBigSplashScreen();
				}
				
			}
		};
		
		if(logged)
			mapEngine = new MapEngine(webView, endLoadingTask, loggedUser.getLatitude(), loggedUser.getLongitude());
		
		else
			mapEngine = new MapEngine(webView, endLoadingTask, "italia " + city);
		
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
	 * Search the map's position with the location string inserted as input
	 */
	public void searchPosition() {
		if (locationUpdateTimeline != null) locationUpdateTimeline.stop();
		
		String searchString = searchField.getText();
		if(searchString.equals(""))
			return;
		
        locationUpdateTimeline = new Timeline();
        locationUpdateTimeline.getKeyFrames().add(
            new KeyFrame(new Duration(400), new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                	mapEngine.goToLocation(searchString);
                }
            })
        );                
        locationUpdateTimeline.play();
	}
	
	@FXML
	/**
	 * Makes the map return to initial location
	 */
	public void centerSuburb() {
		searchField.setText("");
		if(logged)
			mapEngine.panToCoordinates(loggedUser.getLatitude(), loggedUser.getLongitude());
		else
			mapEngine.goToLocation("italia " + city);
	}
	
	@FXML
	/**
	 * Starts the creation of a new report
	 */
	public void createNewReport() {
		if(!logged) {
			String errorTitle = "Segnalazione";
			String errorContext = "Devi essere loggato per mandare una segnalazione!";
			showErrorMessage(errorTitle, errorContext);
			return;
		}
		
		mapEngine.setDraggableMarkersType("grn-pushpin");
		mapEngine.deleteAllDraggableMarkers();
		hideAllReports();
		mapEngine.switchMapListener();
		mapEngine.setDraggableMarkersBound(0);
		
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("");
		dialog.setHeaderText("Segnalazione");
		dialog.setContentText("Qual'è il motivo della segnalazione?");
		Optional<String> result = dialog.showAndWait();
		
		if(!result.isPresent()) {
			endReportCreation();
			return;
		}
		
		newReportMotive = result.get();
		while(newReportMotive.length() < 4 || newReportMotive.contains("\"")) {
			dialog.setTitle("Errore");
			dialog.setHeaderText(newReportMotive.length() < 4 ? 
					"Il motivo deve avere almeno 4 caratteri" : "Il motivo non può contenere il carattere: \"");
			dialog.setContentText("Inserisci il motivo della segnalazione");
			result = dialog.showAndWait();
			if(!result.isPresent()) {
				endReportCreation();
				return;
			}
			newReportMotive = result.get();

		}
		
		webView.addEventHandler(MouseEvent.MOUSE_CLICKED, newReportClickEventHandler);
		buttonConfirmNewReport.setVisible(true);
		buttonAbortNewReport.setVisible(true);
		mapEngine.setDraggableMarkersBound(2);
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("");
		alert.setHeaderText("Clicca sulla mappa per inserire due segnalatori, uno che indica" + "\n" + 
				"la tua posizione (verde), e uno che indica la posizione" + "\n" +
				"dell'evento che vuoi segnalare (rosso)" + "\n");
		alert.setContentText("Utilizza i bottoni in alto (Conferma) e (Annulla) per confermare" + "\n" + 
				"o annullare la nuova segnalazione");
		alert.showAndWait();
		
	}
	
	@FXML
	/**
	 * Aborts the creation of a new report
	 */
	public void abortReportCreation() {
		endReportCreation();
	}
	
	@FXML
	/**
	 * Confirm the creation of the new report
	 */
	public void confirmReportCreation() {
		JSObject markers = mapEngine.getAllDraggableMarkers();
		Object firstObject = markers.getSlot(0);
		Object secondObject = markers.getSlot(1);
		
		if(secondObject == null || !(firstObject instanceof JSObject && secondObject instanceof JSObject)) {
			showErrorMessage("Segnalatori", "Non hai ancora inserito entrambi i segnalatori richiesti");
			return;
		}
		
		JSObject firstMarker = (JSObject) firstObject;
		JSObject secondMarker = (JSObject) secondObject;
		String firstMarkerCoords = ((JSObject) firstMarker.getMember("position")).toString();
		String secondMarkerCoords = ((JSObject) secondMarker.getMember("position")).toString();
		
		double reportingLat = Double.parseDouble(firstMarkerCoords.substring(1, firstMarkerCoords.indexOf(',')));
		double reportingLon = Double.parseDouble(firstMarkerCoords.substring(firstMarkerCoords.indexOf(' '), firstMarkerCoords.length() - 1));
		double latReport = Double.parseDouble(secondMarkerCoords.substring(1, secondMarkerCoords.indexOf(',')));
		double lonReport = Double.parseDouble(secondMarkerCoords.substring(secondMarkerCoords.indexOf(' '), secondMarkerCoords.length() - 1));
		
		String reporting = loggedUser.getUserId();
		String suburb = loggedUser.getSuburb();
		String city = loggedUser.getCity();
		String status = "active";
		Report report = new Report(reporting, city, suburb, null, status, newReportMotive, reportingLat, reportingLon, latReport, lonReport);
		
		tryCreatingNewReport(report);
		endReportCreation();
	}
	
	@FXML
	/**
	 * Switches reports' view, between user's reports (if logged)
	 * and all reports
	 */
	public void switchReportsView() {
		boolean tick = userReportsCheckBox.isSelected();
		
		if(tick) {
			String loggedUserName = loggedUser.getUserId();
			for(Report report: reportsList) {
				String reporting = report.getReporting();
				if(!loggedUserName.equals(reporting)) {
					hideReport(report);
				}
			}
			
		}
		
		else 
			showAllReports();
	
	}
	
	@FXML
	/**
	 * Adds a new suburb's reports and asks Watchneighbours server to receive 
	 * report updates about it
	 */
	public void addSuburb() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Aggiunta quartiere");
		dialog.setHeaderText("Inserisci il quartiere di cui vuoi visualizzare le segnalazioni");
		dialog.setContentText("");
		Optional<String> result = dialog.showAndWait();
		
		if(!result.isPresent() || result.equals(""))
			return;
		
		String suburb = result.get();
		try {
			SubUrbInterface newSuburbHandler = connectionManager.tryAddingSubUrbHandler(suburb, city);
			List<Report> newSuburbReports = newSuburbHandler.addObserver(observer.getID(), observer);
			System.out.println("MainWindow, new suburbHandler added: received " + newSuburbReports.size() + " reports");
			for(Report r : newSuburbReports)
				addReport(r);
			showSuccessMessage("Aggiunta quartiere", "Ricevute " + newSuburbReports.size() + " segnalazioni");
		} catch (RemoteException e) {
			returnToLogInMenu();
		} catch (NotBoundException e) {
			showErrorMessage("Errore", "Impossibile ottenere le segnalazioni del quartiere richiesto");
		}
	}
	
	@FXML
	/**
	 * Shows logged user profile, if logged
	 * @throws IOException if cannot load the ProfileView window
	 */
	public void showProfile() throws IOException {
		//creation of a new window
		profileViewStage = new Stage();
		Parent h = FXMLLoader.load(resourceManager.getProfileViewWindowURL());
		Scene ss = new Scene(h);
		profileViewStage.setScene(ss);
		profileViewStage.setResizable(false);
		profileViewStage.showAndWait();
		profileViewStage = null;

		//code executed after the ProfileView window closes or gets closed by user
		if(!(loggedUser == connectionManager.getLoggedUser())) {

			String actualSuburb = suburb;
			if(connectionManager.getSuburb() == null) {
				showConnectionError();
				exit();
				return;
			}
			
			else if(!actualSuburb.equalsIgnoreCase(connectionManager.getSuburb()) || 
					!loggedUser.getUserId().equalsIgnoreCase(connectionManager.getLoggedUser().getUserId())){
				
				try {
					reportsList.clear();
					reportPanesList.clear();
					suburbHandler.exit(observer.getID(), observer);
					ClientApplication.launchMainWindow();
				} catch (RemoteException e) {
					showConnectionError();
					exit();
					return;
				}
				
			}
			
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
	 * Makes the application return to logIn window and disconnect from Watchneighbours server
	 */
	public void exit() {
		System.out.println("MainViewControl: exiting / logging out");
		if(profileViewStage != null)
			profileViewStage.close();

		connectionManager.disconnect();
		
		SimpleSplashScreen.stopBigSplashScreen();
		returnToLogInMenu();
		
	}
		
	
	
	
	
	
	
	
	
	//methods used by observer's update thread
	
	/**
	 * Adds a report to the view
	 * @param report the report to add
	 */
	public synchronized void addReport(Report report) {
		if(reportsList.contains(report)) 
			return;
		
		reportsList.add(report);

		boolean right = logged ?
				report.getSuburb().equalsIgnoreCase(loggedUser.getSuburb()) : false;
		ReportPane rw = new ReportPane(report, right, logged, actionEventHandler, seeOnMapEventHandler);
		reportPanesList.add(rw);
		
		double lat = report.getLatReport();
		double lon = report.getLonReport();
		String title = report.getMotive();
		String type = null;
		
		String[] markerTypes = mapEngine.getSupportedMarkerTypes();
		switch(report.getStatus()) {
		case ACTIVE:
			type = markerTypes[0];
			break;
		
		case IN_CHARGE:
			type = markerTypes[1];
			break;
			
		case CLOSED: default:
			type = markerTypes[2];
			break;
		
		}
		
		String infoWindowText = generateInfoWindowHTML(report);
		mapEngine.setMarkerToCoordinates(lat, lon, infoWindowText, title, type);
		
		switchReportsView();
		checkNoReportsLabel();
	}
	
	/**
	 * Updates an existing report's data
	 * @param updatedReport the updated report
	 */
	public synchronized void updateReport(Report updatedReport) {
		int index = reportsList.indexOf(updatedReport);
		if(index == -1) {
			addReport(updatedReport);
			return;
		}
		
		StatusType newStatus = updatedReport.getStatus();
		String newMarkerType = null;
		String[] markerTypes = mapEngine.getSupportedMarkerTypes();
		
		switch(newStatus) {
		case ACTIVE:
			newMarkerType = markerTypes[0];
			break;
			
		case IN_CHARGE:
			newMarkerType = markerTypes[1];
			break;
			
		case CLOSED: default:
			newMarkerType = markerTypes[2];
			break;
		}
		
		mapEngine.switchMarkerType(index, newMarkerType);
		reportsList.remove(index);
		reportsList.add(index, updatedReport);
		
		ReportPane reportPane = (ReportPane) reportPanesList.get(index);
		String suburb = updatedReport.getSuburb();
		boolean right = logged ? 
				suburb.equalsIgnoreCase(loggedUser.getSuburb()) : false; 
		reportPane.updateReport(updatedReport, right);
		mapEngine.changeInfowindowText(index, generateInfoWindowHTML(updatedReport));
		
		checkNoReportsLabel();
		
	}
		
	/**
	 * Deletes a report from the view
	 * @param report the report to delete
	 */
	public synchronized void deleteReport(Report report) {
		int index = reportsList.indexOf(report);
		if(index == -1)
			return;
		
		reportsList.remove(index);
		reportPanesList.remove(index);
		mapEngine.deleteMarkerAtIndex(index);
		checkNoReportsLabel();
	}
	
	
	
	
	
	
	
	
	//methods used by GUI to interact with the server
	
	private void tryCreatingNewReport(Report newReport) {
		
		boolean success = false;

		try {
			success = suburbHandler.sendReport(newReport);
		} catch (RemoteException e) {
			showConnectionError();
			exit();
			return;
		}
		
		if(success)
			showSuccessMessage("Successo", "Invio della segnalazione effettuato correttamente");
		
		else
			showErrorMessage("Errore", "Errore durante l'invio della segnalazione");
	}
	
	private void tryTakingChargeOfReport(int index) {
		Report report = reportsList.get(index);
		if(index == -1)
			throw new RuntimeException("Error while trying to take charge of a report: report not found");
		
		boolean success = false;
		try {
			System.out.println("MainWindowControl - trying to take charge of report nr." + index);
			success = suburbHandler.takeChargeOf(report, loggedUser);
		} catch (RemoteException e) {
			showConnectionError();
			exit();
			return;
		}
		
		if(!success) {
			String errorTitle = "Presa in carico segnalazione";
			String errorHeader = "Errore nel tentativo di prendere in carico una segnalazione" + "\n" + 
					"Qualcun altro l'ha appena presa in carico, oppure non puoi prenderla in carico" + "\n" +
					"tu stesso.";
			showErrorMessage(errorTitle, errorHeader);
		}
		
		else
			showSuccessMessage("Successo", "Segnalazione presa in carico");
	}
	
	private void tryClosingReport(int index) {
		Report report = reportsList.get(index);
		if(index == -1)
			throw new RuntimeException("Error while trying to take charge of a report: report not found");
		
		boolean success = false;
			
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Chiusura segnalazione");
		dialog.setHeaderText("Come si è risolta la segnalazione?");
		dialog.setContentText("Inserisci l'esito: ");
		Optional<String> result = dialog.showAndWait();
		if(!result.isPresent())
			return;

		try {
			success = suburbHandler.closeReport(report, loggedUser, result.get());
		} catch (RemoteException e) {
			showConnectionError();
			exit();
			return;
		} catch (WrongDataException e) {
			showErrorMessage("Errore", e.getMessage());
			return;
		}
		
		if(!success) {
			String errorTitle = "Chiusura segnalazione";
			String errorContext = "Errore di sincronizzazione con il server" + "\n" + 
					"qualcun altro potrebbe aver già chiuso la segnalazione, o potrebbe essersi" + "\n" + 
					"verificato un errore di comunicazione";
			showErrorMessage(errorTitle, errorContext);
		}
		
		else 
			showSuccessMessage("Successo", "La segnalazione è stata chiusa");
	}
	
	
	
	
	
	//utils
	
	private void generateEventHandlers() {
		actionEventHandler = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Object obj = event.getSource();
				ReportPane source = (ReportPane) ((Button) obj).getParent();
				int index = reportPanesList.indexOf(source);
				
				Report selectedReport = reportsList.get(index);
				StatusType reportStatus = selectedReport.getStatus();
				if(reportStatus == StatusType.ACTIVE)
					tryTakingChargeOfReport(index);
				
				else if(reportStatus == StatusType.IN_CHARGE)
						tryClosingReport(index);
			
			}
			
		};
		
		seeOnMapEventHandler = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Object obj = event.getSource();
				ReportPane source = (ReportPane) ((Button) obj).getParent();
				int index = reportPanesList.indexOf(source);
				
				Report selectedReport = reportsList.get(index);
				double lat = selectedReport.getLatReport();
				double lon = selectedReport.getLonReport();
				
				mapEngine.panToCoordinates(lat, lon);
				mapEngine.animateMarker(index);
				
			}
			
		};
		
		newReportClickEventHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() == MouseButton.PRIMARY) {
					mapEngine.setDraggableMarkersType("red-pushpin");
					webView.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
				}
				
			}
			
		};
		
	}
	
	private void endReportCreation() {
		newReportMotive = null;
		mapEngine.switchMapListener();
		buttonConfirmNewReport.setVisible(false);
		buttonAbortNewReport.setVisible(false);
		mapEngine.deleteAllDraggableMarkers();
		showAllReports();
		
	}
	
	private void hideReport(Report report) {
		int index = reportsList.indexOf(report);
		ReportPane rp = (ReportPane) reportPanesList.get(index);
		rp.setInvisible(true);
		mapEngine.hideMarkerAtIndex(index);
		
	}
	
	private void hideAllReports() {
		for(Report report : reportsList)
			hideReport(report);
	}
	
	private void showAllReports() {
		for(Node rp : reportPanesList) {
			try {
				((ReportPane) rp).setInvisible(false);
			} catch (ClassCastException e) {
				continue;
			}
		
		}
		mapEngine.showAllMarkers();
	}
	
	
	private String generateInfoWindowHTML(Report report) {
		String result = 
			"<div id='report'>" +
			"<h2>" + report.getMotive() + "</h3>" + 
			"<p> Città: " + report.getCity() + "</p>" + 
			"<p> Quartiere: " + report.getSuburb() + "</p>" +
			"<p> Stato: " + report.getStatus() + "</p>" +
			"<p> Utente segnalante: " + report.getReporting() + "</p>" +
			"<p> Lat. utente: " + report.getReportingLat() + "</p>" + 
			"<p> Lon. utente: " + report.getReportingLon() + "</p>" +
			"<p> Lat. segnalazione: " + report.getLatReport() + "</p>" + 
			"<p> Lon. segnalazione: " + report.getLonReport() + "</p>" +
			"<p> Timestamp: " + report.getTimestamp() + "</p>" +
			"</div>";
		
		return result;
	}
	
	static Stage getProfileViewStage() {
		return profileViewStage;
	}
	
	private void checkNoReportsLabel() {
		boolean noReports = reportsList.isEmpty();
		if(noReports)
			reportPanesList.add(noReportsWarning);
		
		else
			reportPanesList.remove(noReportsWarning);
		
	}
	
	private void showSuccessMessage(String title, String context) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(context);
		alert.setContentText("");
		alert.showAndWait();
	}
	
	private void showErrorMessage(String title, String header) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText("");
		alert.showAndWait();
	}
	
	private void showConnectionError() {
		String title = "Errore";
		String message = "Connessione persa, torno al menu' principale.";
		showErrorMessage(title, message);
	}
	
	private void returnToLogInMenu() {
		connectionManager.disconnect();
		ClientApplication.launchLogInWindow();
	}
	
}















