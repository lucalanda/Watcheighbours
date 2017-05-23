package databaseDataAnalitycs;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import databasePackage.QueryExecutor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

/**
 *The class <code>BodyPageController</code>
 *is the controller of BodyPage.fxm view, it are the body of the application.
 *@author Nicola Landro
 *@version 1.0
 */
public class BodyPageController implements Initializable{
	@FXML
	private AnchorPane container;
	private static QueryExecutor q = null; //TMCH
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			q = QueryExecutor.constructFromFile("configuration_Server/DB.config");
		} catch (SQLException | IOException e) {
			alertNoDBConnection();
		}
		
		main();
	}
	/**
	 * return the query executor to use from other controller
	 * @return the query executor
	 */
	protected static QueryExecutor getQueryExecutor(){
		return q;
	}
	
	private void alertIOException(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		alert.setTitle("Errore");
		alert.setHeaderText("C'è stato un errore nel caricamento dell'inferfaccia grafica");
		alert.show();
	}
	private void alertNoDBConnection(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		alert.setTitle("Errore");
		alert.setHeaderText("C'è stato un errore nella connessione al database");
		alert.show();
	}
	
	/**
	 * give information about writer of program
	 */
	public void aboutUs(){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		alert.setTitle("About...");
		alert.setHeaderText("Questa applicazione è stata scritta da LL e NL");
		alert.show();
	}
	
	/**
	 * give help information
	 */
	public void help(){
		loadView("Help.fxml");
	}
	
	/**
	 * exit from application
	 */
	public void quit(){
		System.exit(0);
	}
	
	/**
	 * load the geleralQuery view
	 */
	public void generaleQuery(){
		loadView("GeneralQuery.fxml");
	}
	
	/**
	 * load the main view
	 */
	public void main(){
		loadView("Main.fxml");
	}
	/**
	 * load the users view
	 */
	public void usersView(){
		loadView("UsersView.fxml");
	}
	/**
	 * load the subscriptions view
	 */
	public void subscriptions(){
		loadView("Subscriptions.fxml");
	}
	private void loadView(String path){
		try {
			container.getChildren().set(0,FXMLLoader.load(getClass().getResource(path)));
		} catch (IOException e) {
			alertIOException();
		}
	}
}
