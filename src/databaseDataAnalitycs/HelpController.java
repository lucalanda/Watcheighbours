package databaseDataAnalitycs;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 *The class <code>HelpController</code>
 *is the controller of Help.fxm view, it show the user manual.
 *@author Nicola Landro
 *@version 1.0
 */
public class HelpController implements Initializable{
	@FXML
	private WebView webview;
	private WebEngine engine;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		engine = webview.getEngine();
		engine.load(getClass().getResource("DBAnalizerManualeUtente.html").toString());
		
	}

}
