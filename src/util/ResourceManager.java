package util;

import java.net.URL;

/**
 * The <code> ResourceManager </code> class handles
 * fxml resources urls.
 * Implemented with a Singleton pattern.
 * @author Luca Landa
 *
 */
public class ResourceManager {

	//Singleton pattern
	private static ResourceManager manager;
	
	private final static String LOGIN_WINDOW_PATH = "/GUI/views/LogIn.fxml";
	private final static String CONNECTION_SETTINGS_WINDOW_PATH = "/GUI/views/ConnectionSettings.fxml";
	private final static String ACCESS_WITHOUT_ACCOUNT_WINDOW_PATH = "/GUI/views/AccessWithoutAccount.fxml";
	private final static String REGISTRATION_WINDOW_PATH = "/GUI/views/Registration.fxml";
	private final static String MAIN_VIEW_WINDOW_PATH = "/GUI/views/MainWindow.fxml";
	private final static String PROFILE_VIEW_WINDOW_PATH = "/GUI/views/ProfileView.fxml";
	
	
	
	
	private ResourceManager() {
		
	}
	
	/**
	 * Gets the common instance of ResourceManager
	 * @return ResourceManager common instance
	 */
	public static ResourceManager getResourceManager() {
		if(manager == null)
			manager = new ResourceManager();
		
		return manager;
	}
	
	

	
	
	/**
	 * Gets LogIn.fxml window url
	 * @return url of LogIn.fxml resource
	 */
	public URL getLogInWindowURL() {
		return manager.getClass().getResource(LOGIN_WINDOW_PATH);
	}
	
	/**
	 * Gets ConnectionSettings.fxml window url
	 * @return url of ConnectionSettings.fxml resource
	 */
	public URL getConnectionSettingsWindowURL() {
		return manager.getClass().getResource(CONNECTION_SETTINGS_WINDOW_PATH);
	}

	/**
	 * Gets AccessWithoutAccount.fxml window url
	 * @return url of AccessWithoutAccount.fxml resource
	 */
	public URL getAccessWithoutAccountWindowURL() {
		return manager.getClass().getResource(ACCESS_WITHOUT_ACCOUNT_WINDOW_PATH);
	}
	
	/**
	 * Gets Registration.fxml window url
	 * @return url of Registration.fxml resource
	 */
	public URL getRegistrationWindowURL() {
		return manager.getClass().getResource(REGISTRATION_WINDOW_PATH);		
	}
	
	/**
	 * Gets MainWindow.fxml window url
	 * @return url of MainWindow.fxml resource
	 */
	public URL getMainWindowURL() {
		return manager.getClass().getResource(MAIN_VIEW_WINDOW_PATH);
	}

	/**
	 * Gets ProfileView.fxml window url
	 * @return url of ProfileView.fxml resource
	 */
	public URL getProfileViewWindowURL() {
		return manager.getClass().getResource(PROFILE_VIEW_WINDOW_PATH);
	}
}
