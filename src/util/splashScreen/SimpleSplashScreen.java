package util.splashScreen;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The <code> SimpleSplashScreen </code> class provides 
 * methods to show a simple splash screen on a javafx application
 * @author Luca Landa
 */
public class SimpleSplashScreen {
	
	private static Stage bigSplashScreenStage;
	private static final String bigSplashScreenViewPath = "/util/splashScreen/BigSplashScreenView.fxml";

	
	
	
	
	/**
	 * Starts the splash screen
	 */
	public static void startBigSplashScreen() {
		bigSplashScreenStage = start(bigSplashScreenStage, bigSplashScreenViewPath);
	}
	
	/**
	 * Stops the splash screen
	 */
	public static void stopBigSplashScreen() {
		if(bigSplashScreenStage != null)
			bigSplashScreenStage.close();
		
		bigSplashScreenStage = null;
	}
	
	/**
	 * Returns a boolean representing whether the splash screen is
	 * running or not
	 * @return running <code> true </code> if the splash screen is running,
	 * <code> false </code> if it's not.
	 */
	public static boolean isBigSplashScreenRunning() {
		return bigSplashScreenStage != null;
	}

	
	
	
	
	//util
	
	private static Stage start(Stage stage, String path) {
		if(stage == null)
			stage = new Stage();
		
		try {
			Parent p = FXMLLoader.load(SimpleSplashScreen.class.getResource(path));
			Scene scene = new Scene(p);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.setAlwaysOnTop(true);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return stage;
	}
	
	
	
}
