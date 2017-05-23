package databaseDataAnalitycs;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import databasePackage.QueryExecutor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import util.UserWithTimestamp;

/**
 * Controller for the Subscriptions.fxml view
 * @author Luca Landa
 */
public class SubscriptionsController implements Initializable {
	
	@FXML
	private TableView<UserWithTimestamp> table;
	@FXML
	private TableColumn<UserWithTimestamp, String> timestampColumn;
	@FXML
	private TableColumn<UserWithTimestamp, String> userIDColumn;
	@FXML
	private TableColumn<UserWithTimestamp, String> emailColumn;
	@FXML
	private TableColumn<UserWithTimestamp, String> nameColumn;
	@FXML
	private TableColumn<UserWithTimestamp, String> surnameColumn;
	@FXML
	private TableColumn<UserWithTimestamp, String> addressColumn;

	@FXML
	private CheckBox suburbCheckBox;
	
	@FXML
	private Label numResultsLabel;
	
	@FXML
	private TextField cityField;
	@FXML
	private TextField suburbField;
	@FXML
	private DatePicker datePicker;
	
	private QueryExecutor queryExecutor;
	private ObservableList<UserWithTimestamp> resultsList;
	
	
	
	@Override
	/**
	 * initialize method, inherited from Initialize interface
	 */
	public void initialize(URL location, ResourceBundle resources) {
		queryExecutor = BodyPageController.getQueryExecutor();
		resultsList = FXCollections.observableArrayList();
		setColumnsType();
		table.setItems(resultsList);
	}
	
	@FXML
	/**
	 * Queries the database with the input data inserted and outputs the results
	 */
	public void search() {
		boolean cityCheck = !cityField.getText().equals("");
		boolean suburbCheck = suburbCheckBox.isSelected() && (!suburbField.getText().equals(""));
		
		if(!cityCheck) {
			showAlert("Città", "Inserire una città per la ricerca");
			return;
		}
		
		String city = cityField.getText();
		String suburb = suburbCheck ? suburbField.getText() : null;
		
		LocalDate localDate = datePicker.getValue();
		if(localDate == null) {
			showAlert("Data", "Inserire una data di inizio ricerca");
			return;
		}
		//java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
		Timestamp sqlDate = new Timestamp(java.sql.Date.valueOf(localDate).getTime());
		
		List<UserWithTimestamp> results = null;

		try {
			if(suburbCheckBox.isSelected()){
				results = queryExecutor.execute_sel_users_from_suburb_city(suburb, city, sqlDate);
			}
			else{
				results = queryExecutor.execute_sel_users_from_city(city, sqlDate);
			}

			for(UserWithTimestamp u : results){
				System.out.println("User: " + u);
			}
			
			setResults(results);
		} catch (SQLException e) {
			e.printStackTrace();
			showDatabaseConnectionError();
		}
		
	}
	
	
	
	//utils

	private void setColumnsType() {
		timestampColumn.setCellValueFactory(
				new PropertyValueFactory<UserWithTimestamp, String>("timestamp")
		);
		
		userIDColumn.setCellValueFactory(
				new PropertyValueFactory<UserWithTimestamp, String>("userId")
		);
		
		emailColumn.setCellValueFactory(
				new PropertyValueFactory<UserWithTimestamp, String>("email")
		);
		
		nameColumn.setCellValueFactory(
				new PropertyValueFactory<UserWithTimestamp, String>("name")
		);
		
		surnameColumn.setCellValueFactory(
				new PropertyValueFactory<UserWithTimestamp, String>("surname")
		);
		
		addressColumn.setCellValueFactory(
				new PropertyValueFactory<UserWithTimestamp, String>("address")
		);
	}
	
	private void setResults(List<UserWithTimestamp> users) {
		resultsList.clear();
		for(UserWithTimestamp u : users)
			resultsList.add(u);
		
	}
	
	private void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		alert.setTitle(title);
		alert.setHeaderText(message);
		alert.setContentText("");
		alert.showAndWait();
	}
	
	private void showDatabaseConnectionError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		alert.setTitle("Errore");
		alert.setHeaderText("Errore nella connessione con il database");
		alert.setContentText("");
		alert.showAndWait();
	}

}
