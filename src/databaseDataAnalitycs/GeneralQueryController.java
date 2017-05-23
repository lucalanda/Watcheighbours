package databaseDataAnalitycs;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import databasePackage.QueryExecutor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;

/**
 *The class <code>GeneralQueryController</code>
 *is the controller of GeneralQuery.fxm view
 *@author Nicola Landro
 *@version 1.0
 */
public class GeneralQueryController implements Initializable{
	@FXML
	private TextArea query;
	@FXML
	private TextArea results;
	
	private QueryExecutor q;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		results.setEditable(false);
		results.setFocusTraversable(false);	
		this.q = BodyPageController.getQueryExecutor();
	}
	
	private void alertNoCode(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		alert.setTitle("Information");
		alert.setHeaderText("Inserire il testo della query.");
		alert.show();
	}
	
	/**
	 * execute the query insert in the TextArea from user
	 */
	public void executeQuery(){
		if(query.getText().isEmpty()){
			alertNoCode();
			return;
		}
		try {
			q.execute_sel_Query(query.getText(),results);
		} catch (SQLException e) {
			//shouldent be catch with something because the methods show in the textArea the results
		}
	}
}
