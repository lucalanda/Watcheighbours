package client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import util.IPRetriever;
import util.ResourceManager;
import util.splashScreen.SimpleSplashScreen;

/**
 * The <code> ClientApplication </code> is the main Watchneighbours client application class,
 * which manages to launch application's windows and contains the main method
 * @author Luca Landa
 */
public class ClientApplication extends Application {
	
	private static Parent root;
	private static Scene scene;
	private static Stage primaryStage;
	
	private static ConnectionManager connectionManager;
	
	private static ResourceManager resourceManager;
	

	/**
	 * Main method that starts the application
	 * @param args received launching the application
	 */
	public static void main(String[] args) {
		try {
			IPRetriever.linux_RMI_BugCorrection();
		} catch (UnknownHostException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("java.rmi.server.hostname");
			alert.setHeaderText("Warning: found linux/unix system, but was unable to set" + "\n" + 
					"'java.rmi.server.hostname' property correctly, you may experience connection issues" + "\n" +
					"due to a linux/unix os bug");
			alert.setContentText("");
		}
		
		launch(args);

	}
	
	
	/**
	 * Start method, inherited from Application class, initializes the application
	 */
	@Override
	public void start(Stage primaryStage) throws MalformedURLException, IOException {
		connectionManager = ConnectionManager.getConnectionManager();
		resourceManager = ResourceManager.getResourceManager();
		
		ClientApplication.primaryStage = primaryStage;
		primaryStage.setResizable(false);
		launchLogInWindow();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Uscire");
				alert.setHeaderText("");
				alert.setContentText("Sei sicuro di voler uscire dal programma?");
				Optional<ButtonType> result = alert.showAndWait();
				
				if(result.get() == ButtonType.OK) {
					System.out.println("Closing application..");
					exit();
				}
				else
					event.consume();
			}
			
		});
		
	}
	
	private static void runWindow(URL windowURL, URL cssURL) {
		System.out.println("ClientApplication - launching window: " + windowURL.getPath());
		primaryStage.hide();
		try {
			root = FXMLLoader.load(windowURL);
		} catch (IOException e) {
			//unexpected case
			e.printStackTrace();
		}
		scene = new Scene(root);
		if(cssURL != null)
			scene.getStylesheets().add(cssURL.getPath());
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
	}
	
	
	/**
	 * Launches the logIn window
	 */
	public static void launchLogInWindow() {
		URL url = resourceManager.getLogInWindowURL();
		runWindow(url, null);
	}
	
	/**
	 * Launches the connection settings window
	 */
	public static void launchConnectionSettingsWindow() {
		URL url = resourceManager.getConnectionSettingsWindowURL();
		runWindow(url, null);
	}
	
	/**
	 * Launches the access without account window
	 */
	public static void launchAccessWithoutAccountWindow() {
		URL url = resourceManager.getAccessWithoutAccountWindowURL();
		runWindow(url, null);
	}
	
	/**
	 * Launches registration window
	 */
	public static void launchRegistrationWindow() {
		URL url = resourceManager.getRegistrationWindowURL();
		runWindow(url, null);
	}
	
	/**
	 * Launches main window and connects the application to the SubUrbInterface instance
	 */
	public static void launchMainWindow() {

		boolean success = true;
		
		SimpleSplashScreen.startBigSplashScreen();
		
		try {
			connectionManager.connectToSubUrbInterface();
		} catch(RemoteException e) {
			showAccessErrorAlert("Errore, non è stato possibile connettersi al \n" + 
					"suburbHandler");
			success = false;
		} catch (NotBoundException e) {
			showAccessErrorAlert("Errore: non è stato trovato l'oggetto SubUrbInterface");
			success = false;
		}

		if(success) {
			System.out.println("Client: launching main window. Logged = " + (connectionManager.isLogged()));
			runWindow(resourceManager.getMainWindowURL(), null);
		}
		
		else {
			SimpleSplashScreen.stopBigSplashScreen();
			showConnectionAlert();
			
		}
		
	}
	
	/**
	 * Launches profile view window
	 */
	public static void launchProfileViewWindow() {
		URL url = resourceManager.getProfileViewWindowURL();
		runWindow(url, null);
	}
	
	
	
	
	/**
	 * Shows a connection alert dialog
	 */
	public static void showConnectionAlert() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Errore connessione");
		alert.setHeaderText("Verifica la tua connessione a internet," + "\n" + "o prova a modificare le impostazioni di" + 
				"\n" + "connessione");
		alert.setContentText("Impossibile connettersi al server");
		alert.showAndWait();
	}
	
	/**
	 * Shows a custom access error alert
	 * @param error the error to show
	 */
	public static void showAccessErrorAlert(String error) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Errore accesso");
		alert.setHeaderText(error);
		alert.setContentText("");
		alert.showAndWait();
	}
	
	/**
	 * Disconnects the client and closes the application
	 */
	public static void exit() {
		connectionManager.disconnect();
		System.exit(0);
	}
	
}
	

