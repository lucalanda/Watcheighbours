package mapEngine;

import java.util.ArrayList;
import java.util.HashSet;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 * The <code> MapEngine </code> class provides a map view on the specified 
 * <code> WebView </code> received as argument in its constructors.
 * It uses Google Maps javascript API, and provides several methods to handle the map,
 * map modalities and markers
 * @author Nicola Landro
 * @author Luca Landa
 */
public class MapEngine {
	
	private WebView webview;
	private WebEngine engine;

	private String[] markerTypes;
	private HashSet<String> markerTypesTable;
	private String defaultMarkerType;
	private ArrayList<JSObject> markersList;
	
	private boolean clickToAddMarker_MapListener = false;
	
	
	/**
	 * Constructs a MapEngine instance, viewable on the specified
	 * WebView instance given as argument. When the map finishes loading,
	 * if not null, the <code> endLoadingTask </code> will be executed.
	 * @param webView the WebView which will display the map
	 * @param endLoadingTask the task executed after the map finishes loading
	 */
	public MapEngine(WebView webView, Runnable endLoadingTask){
		webview = webView;
		engine = webview.getEngine();
		engine.load(getClass().getResource("googlemap.html").toString());
		markersList = new ArrayList<JSObject>();
		
		ChangeListener<State> listener = new ChangeListener<State>() {

			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if(newValue == State.SUCCEEDED) {
					initializeMarkerTypesData();
					if(endLoadingTask != null)
						endLoadingTask.run();
					engine.getLoadWorker().stateProperty().removeListener(this);
				}
					
			}
			
		};
		
		engine.getLoadWorker().stateProperty().addListener(listener);
		
	}
	
	/**
	 * Constructs a MapEngine instance, viewable on the specified
	 * WebView instance given as argument. When the map finishes loading,
	 * it will be set to coordinates given as arguments, and if not null, 
	 * the <code> endLoadingTask </code> will be executed.
	 * @param webView the WebView which will display the map
	 * @param endLoadingTask the task executed after the map finishes loading
	 * @param startLatitude the latitude to set the map at
	 * @param startLongitude the longitude to set the map at
	 */
	public MapEngine(WebView webView, Runnable endLoadingTask, double startLatitude, double startLongitude) {
		this(webView, null);
		
		webView.setVisible(false);

		ChangeListener<State> listener = new ChangeListener<State>(){

			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if (newValue == State.SUCCEEDED) {
                	panToCoordinates(startLatitude, startLongitude);
                	webView.setVisible(true);
                	if(endLoadingTask != null)
                		endLoadingTask.run();
                	engine.getLoadWorker().stateProperty().removeListener(this);
                }
			}
			
		};
		
		engine.getLoadWorker().stateProperty().addListener(listener);
		
	}
	
	/**
	 * Constructs a MapEngine instance, viewable on the specified
	 * WebView instance given as argument. When the map finishes loading,
	 * it will be set at the result of a location search with the given
	 * <code> searchString </code> given as argument, and if not null, 
	 * the <code> endLoadingTask </code> will be executed.
	 * @param webView the web view that load the html
	 * @param endLoadingTask a task that inform when the load end
	 * @param searchString the position in witch you want to load the map
	 */
	public MapEngine(WebView webView, Runnable endLoadingTask, String searchString) {
		this(webView, null);
		
		webView.setVisible(false);

		ChangeListener<State> listener = new ChangeListener<State>(){

			@Override
			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
				if (newValue == State.SUCCEEDED) {
					goToLocation(searchString);
					webView.setVisible(true);
					if(endLoadingTask != null) {
						endLoadingTask.run();
					}
					engine.getLoadWorker().stateProperty().removeListener(this);
                }
			}
			
		};
		engine.getLoadWorker().stateProperty().addListener(listener);
	}
	
	
	
	
	
	/**
	 * Reloads the map
	 */
	public void reload(){
		engine.load(getClass().getResource("googlemap.html").toString());
	}
	
	/**
	 * Zooms in the map
	 */
	public void zoomIn(){
		engine.executeScript("document.zoomIn()");
	}
	
	/**
	 * Zooms out the map
	 */
	public void zoomOut(){
		engine.executeScript("document.zoomOut()");
	}

	/**
	 * Sets the road view to the map
	 */
	public void viewRoad(){
		engine.executeScript("document.setMapTypeRoad()");
	}
	
	/**
	 * Sets the satellite view to the map
	 */
	public void viewSatellite(){
		engine.executeScript("document.setMapTypeSatellite()");
	}
	
	/**
	 * Sets the hybrid view to the map
	 */
	public void viewHybrid(){
		engine.executeScript("document.setMapTypeHybrid()");
	}
	
	/**
	 * Sets the terrain view to the map
	 */
	public void viewTerrain(){
		engine.executeScript("document.setMapTypeTerrain()");
	}

	/**
	 * Sets map position to the result of a search performed with the
	 * given <code> searchString </code>
	 * @param searchString the String used in the search
	 * @return coordinates a String containing the resulting coordinates
	 */
	public String goToLocation(String searchString){
		engine.executeScript("document.goToLocation(\"" + searchString + "\")");
		String s = (String) engine.executeScript("document.getCenter()");
		return s;
	}
	
	/**
	 * Makes the map pan too given coordinates, if possible, or just sets the
	 * position to them 
	 * @param latitude the latitude to pan to
	 * @param longitude the longitude to pan to
	 * @return coordinates a String containing the resulting coordinates
	 */
	public String panToCoordinates(double latitude, double longitude) {
		engine.executeScript("document.panTo(" + latitude + "," + longitude + ")");
		String s = (String) engine.executeScript("document.getCenter()");
		return s;
	}
	
	/**
	 * Enables or disables the map <code> ClickToMarkMode </code>
	 * When it is active, clicks on map will position markers at mouse cursor's position 
	 */
	public void switchMapListener() {
		setClickToAddMarker_MapListener((boolean) engine.executeScript("switchClickToMarkMode()"));
	}
	
	/**
	 * Delete all draggable markers on the map
	 */
	public void deleteAllDraggableMarkers() {
		engine.executeScript("deleteAllDraggableMarkers()");
	}
	
	/**
	 * Gets draggable markers limit number
	 * @return limit the draggable markers limit number
	 */
	public int getDraggableMarkersBound() {
		return (int) engine.executeScript("getDraggableMarkersBound()");
	}
	
	/**
	 * Sets a new draggable markers limit number
	 * @param bound the new limit number
	 * @return bound new draggable markers limit number
	 */
	public int setDraggableMarkersBound(int bound) {
		return (int) engine.executeScript("setDraggableMarkersBound(" + bound + ")");
	}
	
	/**
	 * Sets the type for new draggable markers inserted
	 * @param markerType the new draggable markers type
	 */
	public void setDraggableMarkersType(String markerType) {
		if(!checkMarkerType(markerType))
			markerType = "grn-pushpin.png";
		
		engine.executeScript("setDraggableMarkerType(\"" + markerType + "\")");
	}
	
	/**
	 * Sets a non-draggable marker to the location result of the search, performed
	 * with the searchString given as argument
	 * @param searchString the string used to search for the location
	 * @param markerTitle the title of the new marker
	 * @param markerType the type of the new marker
	 * @return coordinates a string containing the coordinates of the new marker
	 */
	public String setMarkerToLocation(String searchString, String markerTitle, String markerType){
		if(!checkMarkerType(markerType))
			markerType = defaultMarkerType;
		
		engine.executeScript("document.markerToLocation(\"" + searchString + "\", \"" + markerTitle + "\", \"" + markerType + "\")");

		String s = (String) engine.executeScript("document.getCenter()");
		return s;
	}
	
	/**
	 * Sets a non-draggable marker to the specified coordinates
	 * @param latitude new marker latitude
	 * @param longitude new marker longitude
	 * @param infoWindowText the text displayed while clicking on the marker
	 * @param markerTitle the title of the new marker
	 * @param markerType the type of the new marker
	 * @return coordinates a string containing the coordinates of the new marker
	 */
	public String setMarkerToCoordinates(double latitude, double longitude, String infoWindowText, String markerTitle, String markerType) {
		if(!checkMarkerType(markerType))
			markerType = defaultMarkerType;
		
		JSObject newMarker = (JSObject) engine.executeScript("document.setMarker(" + latitude + "," + longitude + ", \"" + infoWindowText + "\", \"" + markerTitle + "\", \"" + markerType + "\")");
		markersList.add(newMarker);
		
		String s = (String) engine.executeScript("document.getCenter()");
		return s;
	}
	
	/**
	 * Changes the type of a marker
	 * @param index the number to which the marker was added
	 * @param newMarkerType the new marker type
	 * @return success of the operation
	 */
	public boolean switchMarkerType(int index, String newMarkerType) {
		if(!checkMarkerType(newMarkerType))
			throw new IllegalArgumentException("Switching marker type error: invalid marker type");
		
		if(markersList.size() <= index)
			return false;
		
		JSObject newMarker = (JSObject) engine.executeScript("switchMarkerType(" + index + ", \"" + newMarkerType + "\")");
		markersList.remove(index);
		markersList.add(index, newMarker);
		
		return true;
	}
	
	/**
	 * Changes the text displayed while clicking on the marker 
	 * @param index the number to which the marker was added
	 * @param newContent new text displayed on the marker
	 * @return success of the operation 
	 */
	public boolean changeInfowindowText(int index, String newContent) {
		if(index >= markersList.size())
			throw new NullPointerException();
		
		Object result = engine.executeScript("changeInfowindowText(" + index + ",\"" + newContent + "\")");
		
		return result != null;
	}

	/**
	 * Animates a marker
	 * @param index the number to which the marker was added
	 * @return success of the operation
	 */
	public boolean animateMarker(int index) {
		if(markersList.size() <= index)
			return false;
		
		engine.executeScript("animateMarker(" + index + ")");
		return true;
	}
	
	/**
	 * Deletes a marker
	 * @param index the number to which the marker was added
	 * @return success of the operation
	 */
	public boolean deleteMarkerAtIndex(int index) {
		if(markersList.size() <= index)
			return false;

		markersList.remove(index);
		engine.executeScript("deleteMarker(" + index + ")");
		
		return true;
	}
	
	/**
	 * Hides a marker
	 * @param index the number to which the marker was added
	 * @return success of the operation
	 */
	public boolean hideMarkerAtIndex(int index) {
		if(markersList.size() == 0)
			return false;
		
		engine.executeScript("hideMarkerAtIndex(" + index + ")");
		
		return true;
	}
	
	/**
	 * Hides all markers
	 * @return success of the operation
	 */
	public boolean hideAllMarkers() {
		if(markersList.size() == 0)
			return false;
		
		engine.executeScript("hideAllMarkers()");
		
		return true;
		
	}
	
	/**
	 * Shows a marker, with no effect if the marker was not hidden before
	 * @param index the number to which the marker was added
	 * @return success of the operation
	 */
	public boolean showMarkerAtIndex(int index) {
		if(markersList.size() <= index)
			return false;
		
		engine.executeScript("showMarkerAtIndex(" + index + ")");
		
		return true;
	}
	
	/**
	 * Shows all markers
	 * @return success of the operation
	 */
	public boolean showAllMarkers() {
		if(markersList.size() == 0)
			return false;
		
		engine.executeScript("showAllMarkers()");
		
		return true;
	}

	/**
	 * Gets all non-draggable markers that have been set
	 * @return JSObject instance containing the non-draggable markers
	 */
	public JSObject getAllMarkers() {
		JSObject jsObj = (JSObject) engine.executeScript("getAllMarkers()");
		
		return jsObj;
	}
	
	/**
	 * Gets all draggable markers that have been set
	 * @return JSObject instance containing the draggable markers
	 */
	public JSObject getAllDraggableMarkers() {
		JSObject jsObj = (JSObject) engine.executeScript("getAllDraggableMarkers()");
		
		return jsObj;
	}
	
	/**
	 * Gets latitude of the map center
	 * @return latitude of the map center
	 */
	public double getLatitude(){
		String coord = (String) engine.executeScript("document.getCenter()");
		int startIndex = 1;
		int endIndex = coord.indexOf(',');
		
		String result = coord.substring(startIndex, endIndex);
		return Double.parseDouble(result);
	}
	
	/**
	 * Gets longitude of the map center
	 * @return longitude of the map center
	 */
	public double getLongitude(){
		String coord = (String) engine.executeScript("document.getCenter()");
		int startIndex = coord.indexOf(',') + 2;
		int endIndex = coord.indexOf(')');
		
		String result = coord.substring(startIndex, endIndex);
		
		return Double.parseDouble(result);
	}
	
	/**
	 * Gets all supported types of marker
	 * @return types of marker supported
	 */
	public String[] getSupportedMarkerTypes() {
		return markerTypes;
		
	}

	/**
	 * Gets a boolean of the ClickToMarkMode
	 * If true, the listener is active and clicks on the map will 
	 * set draggable markers on mouse cursor's position 
	 * @return true if addMarker is clicked
	 */
	public boolean getClickToAddMarker_MapListener() {
		return clickToAddMarker_MapListener;
	}

	/**
	 * Enables or disables the ClickToMarkMode
	 * If enabled, clicks on map will set draggable markers on mouse
	 * cursor's position
	 * @param clickToAddMarker_MapListener the settings you want to applicate
	 */
	public void setClickToAddMarker_MapListener(boolean clickToAddMarker_MapListener) {
		this.clickToAddMarker_MapListener = clickToAddMarker_MapListener;
	}
	
	
	
	
	//utils
	
	private void initializeMarkerTypesData() {
		markerTypesTable = new HashSet<String>();
		
		JSObject supportedMarkerTypes = (JSObject) engine.executeScript("getSupportedMarkerTypes()");
		markerTypes = supportedMarkerTypes.toString().split(",");
		
		for(String type : markerTypes)
			markerTypesTable.add(type);
		
		defaultMarkerType = markerTypes[0];
		
	}
	
	private boolean checkMarkerType(String markerType) {
		if(markerType == null)
			return false;
		
		return markerTypesTable.contains(markerType);

	}
}
