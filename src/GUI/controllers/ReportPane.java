package GUI.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import util.Report;

class ReportPane extends AnchorPane {
	
	private Label timestampLabel;
	private Label motiveLabel;
	private Label statusLabel;
	private Label suburbLabel;
	
	private Button actionButton;
	private Button seeOnMapButton;
	
	private ObservableList<Node> nodesList;
	
	private Report report;
	private boolean logged;
	//right: meant as the right to operate on the report, like taking charge of it or closing it
	private boolean right;
	
	
	

	
	public ReportPane(Report report, boolean right, boolean logged, EventHandler<ActionEvent> actionEventHandler, EventHandler<ActionEvent> seeOnMapEventHandler) {
		this.report = report;
		this.logged = logged;
		this.right = right;
		
		positionItems();
		
		setupLabels();
		
		setupActionButton();
		
		seeOnMapButton.setText("vedi sulla mappa");
		
		actionButton.setOnAction(actionEventHandler);
		seeOnMapButton.setOnAction(seeOnMapEventHandler);
		
	}

	
	

	
	private void positionItems() {
		nodesList = getChildren();
		setPrefWidth(300);
		setPrefHeight(100);
		
		timestampLabel = new Label();
		timestampLabel.setLayoutX(10);
		timestampLabel.setLayoutY(11);
		nodesList.add(timestampLabel);
		
		motiveLabel = new Label();
		motiveLabel.setLayoutX(10);
		motiveLabel.setLayoutY(31);
		motiveLabel.setMaxWidth(150);
		nodesList.add(motiveLabel);
		
		statusLabel = new Label();
		statusLabel.setLayoutX(10);
		statusLabel.setLayoutY(51);
		nodesList.add(statusLabel);
		
		suburbLabel = new Label();
		suburbLabel.setLayoutX(10);
		suburbLabel.setLayoutY(71);
		suburbLabel.setMaxWidth(150);
		nodesList.add(suburbLabel);
		
		actionButton = new Button();
		actionButton.setLayoutX(182);
		actionButton.setLayoutY(22);
		actionButton.setPrefWidth(119);
		actionButton.setMaxWidth(119);
		nodesList.add(actionButton);
		
		seeOnMapButton = new Button();
		seeOnMapButton.setLayoutX(171);
		seeOnMapButton.setLayoutY(57);
		nodesList.add(seeOnMapButton);
	}
	
	private void setupLabels() {
		String timestamp = report.getTimestamp().toString();
		timestampLabel.setText(timestamp.substring(0, timestamp.indexOf('.')));
		
		motiveLabel.setText(report.getMotive());
		
		String status = report.getStatus().toString().toLowerCase();
		if(status.equals("in_charge"))
			status = "in charge";
		statusLabel.setText(status);
		
		suburbLabel.setText(report.getSuburb().toLowerCase());
		
	}
	
	void setupActionButton() {
		switch(report.getStatus()){
		case ACTIVE:
			actionButton.setText("prendi in carico");
			actionButton.setDisable(!logged || !right);
			break;
			
		case IN_CHARGE:
			actionButton.setText(logged && right ?
					"chiudi" : "in carico");
			actionButton.setDisable(!logged || !right);
			break;
			
		case CLOSED:
			actionButton.setText("chiusa");
			actionButton.setDisable(true);
			break;
		}
		
	}
	
	void updateReport(Report newReport, boolean newRight) {
		this.report = newReport;
		this.right = newRight;
		setupLabels();
		setupActionButton();
	}
	
	void setInvisible(boolean invisible) {
		if(invisible) {
			setStyle("-fx-opacity: 0.5");
			actionButton.setDisable(true);
			seeOnMapButton.setDisable(true);
		}
		
		else {
			setStyle("");
			setupActionButton();
			seeOnMapButton.setDisable(false);
		}
	}
	
	
}
