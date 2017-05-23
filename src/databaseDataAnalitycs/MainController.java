package databaseDataAnalitycs;

import java.net.URL;
import java.sql.SQLException;
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
 *The class <code>MainController</code>
 *is the controller of Main.fxm view
 *@author Nicola Landro
 *@version 1.0
 */
public class MainController implements Initializable{
	@FXML
	private TextField suburb;
	@FXML
	private TextField city;
	@FXML
	private PieChart pieChart;
	@FXML
	private CheckBox activeCheckBox;
	@FXML
	private CheckBox inChargeCheckBox;
	@FXML
	private CheckBox closedCheckBox;
	@FXML
	private ChoiceBox<PieChart.Data> menu;
	
	
	private ObservableList<PieChart.Data> pieChartData;
	
	private QueryExecutor q=null; //TMCH
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		q = BodyPageController.getQueryExecutor();
		pieChartData =
	                FXCollections.observableArrayList(
	                		new PieChart.Data("expected", 5)
	                );
		
		pieChart.setData(pieChartData);
		
		menu.setItems(pieChartData);
	}
	
	private void alertNoInformation(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		alert.setTitle("Information");
		alert.setHeaderText("Non ci sono informazioni da aggiungere al grafico con questi settaggi.");
		alert.show();
	}
	
	private void addToPie(String suburb, String city) throws SQLException{
		int x = 0;
		if(activeCheckBox.isSelected()){
			x += q.execute_sel_active_report(suburb, city).size();
		}
		if(inChargeCheckBox.isSelected()){
			x += q.execute_sel_In_Charge_Reports(suburb, city).size();
		}
		if(closedCheckBox.isSelected()){
			x += q.execute_sel_closed_report(suburb, city).size();
		}
		if(x == 0){
			alertNoInformation();
		}
		if(x!=0)
			pieChartData.add(new PieChart.Data(city+": "+suburb, x));
	}
	
	
	/**
	 * add data to pie
	 */
	public void addCityToPie(){
		try {
			addToPie(suburb.getText(),city.getText());
		} catch (SQLException e) {
			alertNoInformation();
		}
	}
	
	/**
	 * remove data to pie
	 */
	public void removeToPie(){
		pieChartData.remove(menu.selectionModelProperty().getValue().getSelectedItem());
	}
}
