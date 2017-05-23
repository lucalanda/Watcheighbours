package databaseDataAnalitycs;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import databasePackage.QueryExecutor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

/**
 *The class <code>UsersViewController</code>
 *is the controller of UsersView.fxm view
 *@author Nicola Landro
 *@version 1.0
 */
public class UsersViewController implements Initializable{
	@FXML
	private TextField city;
	@FXML
	private PieChart pieChart;
	@FXML
	private CheckBox removedCheckBox;
	@FXML
	private ChoiceBox<PieChart.Data> menu;
	
	private ObservableList<PieChart.Data> pieChartData;
	
	private QueryExecutor q=null; //TMCH
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		q = BodyPageController.getQueryExecutor();
		pieChartData =
	                FXCollections.observableArrayList(
	                );
		
		pieChart.setData(pieChartData);
		
		menu.setItems(pieChartData);
	}
	

	/**
	 * add data to pie
	 */
	public void addCityToPie(){
		if(city.getText().isEmpty()){
			alertNoInformation();
			return;
		}
		cleenList();
		try {
			if(removedCheckBox.isSelected()){
					addToPieUserRemoved(city.getText());
			}
			else{
				addToPieUserExisting(city.getText());
			}
		} catch (SQLException e) {
			alertNoInformation();
		}
	}
	
	private void alertNoInformation(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		alert.setTitle("Information");
		alert.setHeaderText("Non ci sono informazioni da aggiungere al grafico con questi settaggi.");
		alert.show();
	}
	
	private void cleenList(){
			pieChartData=FXCollections.observableArrayList();
			pieChart.setData(pieChartData);
			menu.setItems(pieChartData);
	}
	
	private void addToPieUserRemoved(String city) throws SQLException {
		for(Entry<String, Integer> e : q.execute_sel_usersAlsoRemoved_number_for_suburb(city).entrySet()){
			pieChartData.add(new PieChart.Data(e.getKey(), e.getValue()));
		}
	}


	private void addToPieUserExisting(String city) throws SQLException  {
		for(Entry<String, Integer> e : q.execute_sel_users_number_for_suburb(city).entrySet()){
			pieChartData.add(new PieChart.Data(e.getKey(), e.getValue()));
		}
	}


	/**
	 * remove to list
	 */
	public void removeToPie(){
		pieChartData.remove(menu.selectionModelProperty().getValue().getSelectedItem());
	}

}
