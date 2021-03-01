package application;

import java.io.File;
import java.net.SecureCacheResponse;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class SceneController implements Initializable{
	
	private MediaPlayer mediaPlayer;
	private Boolean atEndofMedia = false;
	private Boolean repeatMode = false;
	private Boolean randomMode = false;
	private Boolean playlistMode = false;
	private Boolean darkMode = true;
	private Boolean isStarted = true;
	private Boolean notice = true;
	private int playlistCount = 0;
	private static int counter = 0;
	private HashMap<String, File> uploadList = new HashMap<String, File>();
	private HashMap<String, Integer> queueList = new HashMap<String, Integer>();
	private HashMap<String, Integer> playlistMap = new HashMap<String, Integer>();
	private ObservableList<String> songList = FXCollections.observableArrayList();
	private ObservableList<String> inputListObs = FXCollections.observableArrayList();
	private ObservableList<String> outputListObs = FXCollections.observableArrayList();
	private ObservableList<Playlist> playlistListObs = FXCollections.observableArrayList();
	private HashMap<String, Image> imgMap = new HashMap<String, Image>();
	private ArrayList<File> fileList = new ArrayList<File>();

    @FXML
    private MediaView mediaView;

    @FXML
    private Button backBtn;
    
    
    @FXML
    private Button playlistBtn;
    
    @FXML
    private Button musicBtn;
    
    @FXML
    private Button uploadBtn;
    
    @FXML
    private Button repeatBtn;
    
    @FXML
    private Button optionsBtn;
    
    @FXML
    private Button randomBtn;

    @FXML
    private Button playBtn;
    
    @FXML
    private Button stopBtn;

    @FXML
    private Button pauseBtn;

    @FXML
    private Button nextBtn;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Slider progressBar;

    @FXML
    private Label durationTime;

    @FXML
    private Label maxTime;

    @FXML
    private Label currentSong;

    @FXML
    private Pane musicPane;

    @FXML
    private ListView<String> musicList;
    
    @FXML
    private ListView<String> inputList;
    
    @FXML
    private ListView<String> outputList;
    
    @FXML
    private ListView<Playlist> playlistList;

    @FXML
    private Pane playlistPane;
    
    @FXML
    private Pane creationPane;
    
    @FXML
    private Pane optionsPane;
    
    @FXML
    private Pane leftPane;
    
    @FXML
    private Pane bottomPane;
    
    @FXML
    private ImageView imgCover;
    
    @FXML
    private CheckBox cBDark;
    
    @FXML
    private Pane bgPane;
    
    @FXML
    private TextField txtTitle;

  //__________________________________FUNCTIONS______________________________________________//

    @FXML
    public void chooseFile(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Music to add");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.mp4"));
    	File file = fileChooser.showOpenDialog(null);
    	if(file == null) {
    		Alert alert = new Alert(AlertType.ERROR, "No such File Found", ButtonType.CLOSE);
    		alert.show();
    	} else {
    		createMediaPlayer(file);
    		fillUploadList(file);
    	}
    }
    
    public void createMediaPlayer(File file) {
    	setCurrentSong(file);
    	String fileString = file.toURI().toString();
    	Media media = new Media(fileString);
    	mediaPlayer = new MediaPlayer(media);
    	mediaView.setMediaPlayer(mediaPlayer);
    	//setOptions
    	mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {

			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				// TODO Auto-generated method stub
				progressBar.setValue(newValue.toSeconds());					
				double temp = Formatter.calcTime(newValue.toSeconds());
				durationTime.setText(String.valueOf(temp));
				
			}
		});
		
		progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
			}
		});
		
		progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
			}
		});
		
		mediaPlayer.setOnReady(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Duration total = media.getDuration();
				progressBar.setMax(total.toSeconds());
									
				Double temp = Formatter.calcTime(total.toSeconds());
				maxTime.setText(String.valueOf(temp));
			}
		});
		
		mediaPlayer.setOnEndOfMedia(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(repeatMode) {
					mediaPlayer.seek(Duration.ZERO);
				}else {
					if(!playBtn.isVisible()) {
						playBtn.setVisible(true);
						pauseBtn.setVisible(false);
					}
					mediaPlayer.seek(mediaPlayer.getStartTime());
					mediaPlayer.stop();
				}
				if(playlistMode) {
	    			if(queueList.size()>1) {
	    				int randomValue = new Random().nextInt(queueList.size()+0)+0;
	    				
	                	String songString = "";
	                	
	                	
	                	for (Entry<String, Integer> m : queueList.entrySet()) {
	                	    if (m.getValue() == randomValue) {
	                	        songString = m.getKey();
	                	    }
	                	}
	                	
	                	createMediaPlayer(uploadList.get(songString));
	                    if(playBtn.isVisible()) {
	                    	playBtn.setVisible(false);
	                    	pauseBtn.setVisible(true);
	                    }
	                	mediaPlayer.play();
	    				
	    			}else {}
	    		}
			}
		});
		
		volumeSlider.setValue(mediaPlayer.getVolume() * 100);
		volumeSlider.valueProperty().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				// TODO Auto-generated method stub
				mediaPlayer.setVolume(volumeSlider.getValue()/100);
			}
		});
		
		getMusicCover();
    }
    
    public void setCurrentSong(File file) {
    	String songName = file.getName();
    	currentSong.setText(songName);
    }
    
    public void createPlaylist() {
    	if(!creationPane.isVisible()) {
    		playlistPane.setVisible(false);
    		creationPane.setVisible(true);
    	}
    	txtTitle.setText("");
    	outputListObs.clear();
    	outputList.setItems(outputListObs);
    	if(inputListObs.size() < songList.size()) {
    		for (int i = 0; i < songList.size(); i++) {
    			if(!inputListObs.contains(songList.get(i))) {
        			inputListObs.add(songList.get(i));
    			}
    		}
        	inputList.setItems(inputListObs);
    	}
    }
    
    public void selectPlMusic(MouseEvent click) {
    	if(click.getClickCount() == 2) {
    		outputListObs.add(inputList.getSelectionModel().getSelectedItem());
    		outputList.setItems(outputListObs);
    	}
    }
    
    public void deletePlMusic() {
    	try {
        	playlistListObs.get(playlistList.getSelectionModel().getSelectedIndex());
        	playlistList.getItems().clear();
        	playlistList.setItems(playlistListObs);
        	playlistMap.clear();
        	playlistCount = 0;
        	
		} catch (Exception e) {
			// TODO: handle exception
    		Alert alert = new Alert(AlertType.ERROR, "No playlist found", ButtonType.CLOSE);
    		alert.show();
		}
    }
    
    public void playPlMusic(MouseEvent click) {
    	if(click.getClickCount() == 2) {
    		ArrayList<File> tempArrayList = playlistList.getSelectionModel().getSelectedItem().getList();
    		int counter = 0;
        	for(int i = 0; i < tempArrayList.size();i++) {
        		for (Entry<String, File> m : uploadList.entrySet()) {
        			if(tempArrayList.get(i) == m.getValue()) {
                	    playlistMap.put(m.getKey(),counter);
                		counter++;
        			}
        		}
        	}
        	playlistCount = 0;
    		createMediaPlayer(tempArrayList.get(0));
            if(playBtn.isVisible()) {
            	playBtn.setVisible(false);
            	pauseBtn.setVisible(true);
            }
    		mediaPlayer.play();
    		playlistMode = true;
    	}
    }
    
    public void createBtn() {
    	if(txtTitle.getText() == "") {
    		Alert alert = new Alert(AlertType.ERROR, "No title selected", ButtonType.CLOSE);
    		alert.show();
    	}else {
    		if(outputListObs.size() == 0) {
    			Alert alert = new Alert(AlertType.ERROR, "No songs selected", ButtonType.CLOSE);
        		alert.show();
    		}else {
    			for (int i = 0; i < outputListObs.size(); i++) {
    				fileList.add(uploadList.get(outputListObs.get(i)));
				}
            	Playlist pl = new Playlist(txtTitle.getText(),fileList);
            	if(creationPane.isVisible()) {
            		playlistPane.setVisible(true);
            		creationPane.setVisible(false);
            	}
            	playlistListObs.add(pl);
            	playlistList.setItems(playlistListObs);
    		}
    	}
    }
    
    public void cancelBtn() {
    	if(creationPane.isVisible()) {
    		playlistPane.setVisible(true);
    		creationPane.setVisible(false);
    	}
    }
    
    public void fillUploadList(File file) {
    	String songName = file.getName();
    	if(uploadList.containsKey(songName) == false) {
    		counter++;
    		songList.add(songName);
        	musicList.setItems(songList);
        	uploadList.put(songName, file);
        	queueList.put(songName, counter);
    	} 	
    }
    
    public void setSongFromView(MouseEvent click) {
    	if(click.getClickCount() == 2) {
			String songName = musicList.getSelectionModel().getSelectedItem();
			createMediaPlayer(uploadList.get(songName));
			if(musicPane.isVisible()) {
				musicPane.setVisible(false);
				mediaPlayer.play();
			}
			if(playBtn.isVisible()) {
				playBtn.setVisible(false);
				pauseBtn.setVisible(true);
			}
		}
    }

    @FXML
    public void pause(ActionEvent event) {
    	if(mediaPlayer != null) {
    		Status status = mediaPlayer.getStatus();
        	if(status == Status.UNKNOWN || status == Status.HALTED) {
        		return;
        	}else {
        		if(status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
        			if(atEndofMedia) {
        				mediaPlayer.seek(mediaPlayer.getStartTime());
        				atEndofMedia = false;
        			}else {
        				if(playBtn.isVisible()) {
        					playBtn.setVisible(false);
        					pauseBtn.setVisible(true);
        				}
        				mediaPlayer.play();
        			}
        		}else {
        			if(pauseBtn.isVisible()) {
        				pauseBtn.setVisible(false);
        				playBtn.setVisible(true);
        			}
        			mediaPlayer.pause();
        		}
        	}
    	}else {
    		Alert alert = new Alert(AlertType.ERROR, "No music found", ButtonType.CLOSE);
    		alert.show();
    	}
    }
    

    @FXML
    public void play(ActionEvent event) {
    	if(mediaPlayer != null) {
    		Status status = mediaPlayer.getStatus();
        	if(status == Status.UNKNOWN || status == Status.HALTED) {
        		return;
        	}else {
        		if(status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
        			if(atEndofMedia) {
        				mediaPlayer.seek(mediaPlayer.getStartTime());
        				atEndofMedia = false;
        			}else {
        				if(playBtn.isVisible()) {
        					playBtn.setVisible(false);
        					pauseBtn.setVisible(true);
        				}
        				mediaPlayer.play();
        			}
        		}else {
        			if(pauseBtn.isVisible()) {
        				pauseBtn.setVisible(false);
        				playBtn.setVisible(true);
        			}
        			mediaPlayer.pause();
        		}
        	}
    	}else {
    		Alert alert = new Alert(AlertType.ERROR, "No music found", ButtonType.CLOSE);
    		alert.show();
    	}
    	
    }
    
    public void repeat() {
    	if(repeatMode) {
        	repeatMode = false;
        	repeatBtn.setStyle("-fx-background-color: #1c1c1c; ");
    	}else {
        	repeatMode = true;
        	repeatBtn.setStyle("-fx-background-color: #454545; ");
    	}
    }
    
    public void random() {
    	if(randomMode) {
    		randomMode = false;
        	randomBtn.setStyle("-fx-background-color: #1c1c1c; ");
    	}else {
    		randomMode = true;
    		randomBtn.setStyle("-fx-background-color: #454545; ");
    	}
	}
    

    @FXML
    public void nextSong(ActionEvent event) {
    	if(!playlistMode) {
    		if(!randomMode) {
            	try {
            		String string = currentSong.getText();
                	int counter1 = queueList.get(string); //String / Integer
                	counter1++;
                	//1 counter++ -> 2
                	String songString = "";
                	
                	
                	for (Entry<String, Integer> m : queueList.entrySet()) {
                	    if (m.getValue() == counter1) {
                	        songString = m.getKey();
                	    }
                	}
                	createMediaPlayer(uploadList.get(songString));
                    if(playBtn.isVisible()) {
                    	playBtn.setVisible(false);
                    	pauseBtn.setVisible(true);
                    }
                	mediaPlayer.play();
                	
        		} catch (Exception e) {
        			// TODO: handle exception
        			Alert alert = new Alert(AlertType.ERROR, "no more tracks in queue", ButtonType.CLOSE);
            		alert.show();
        		}
    		}else {
    			if(queueList.size()>1) {
    				int randomValue = new Random().nextInt(queueList.size()+0)+0;
    				
                	String songString = "";
                	
                	
                	for (Entry<String, Integer> m : queueList.entrySet()) {
                	    if (m.getValue() == randomValue) {
                	        songString = m.getKey();
                	    }
                	}
                	
                	createMediaPlayer(uploadList.get(songString));
                    if(playBtn.isVisible()) {
                    	playBtn.setVisible(false);
                    	pauseBtn.setVisible(true);
                    }
                	mediaPlayer.play();
    				
    			}else {
        			Alert alert = new Alert(AlertType.ERROR, "no enough tracks in queue to randomize", ButtonType.CLOSE);
            		alert.show();
    			}
    		}
    	}else {
    		if(playlistList.getSelectionModel().getSelectedItem().getList().size() == 1) {
    			Alert alert = new Alert(AlertType.ERROR, "no more tracks in queue", ButtonType.CLOSE);
        		alert.show();
    		}else {
    			playlistCount++;
    			String songString = "";
            	for (Entry<String, Integer> m : playlistMap.entrySet()) {
            	    if (m.getValue() == playlistCount) {
            	    	songString = m.getKey();
            	    }
            	}

                createMediaPlayer(uploadList.get(songString));
                mediaPlayer.play();
                if(playBtn.isVisible()) {
                	playBtn.setVisible(false);
                	pauseBtn.setVisible(true);
                }

    		}
    	}    	
    }
    
    @FXML
    public void lastSong(ActionEvent event) {
    	if(!playlistMode) {
    		if(!randomMode) {
            	try {
            		String string = currentSong.getText();
                	int counter1 = queueList.get(string);
                	counter1--;
                	String songString = "";
                	
                	
                	for (Entry<String, Integer> m : queueList.entrySet()) {
                	    if (m.getValue() == counter1) {
                	        songString = m.getKey();
                	    }
                	}
                	createMediaPlayer(uploadList.get(songString));
                    if(playBtn.isVisible()) {
                    	playBtn.setVisible(false);
                    	pauseBtn.setVisible(true);
                    }
                	mediaPlayer.play();
                	
        		} catch (Exception e) {
        			// TODO: handle exception
        			Alert alert = new Alert(AlertType.ERROR, "no more tracks in queue", ButtonType.CLOSE);
            		alert.show();
        		}
    		}else {
    			if(queueList.size()>1) {
    				int randomValue = new Random().nextInt(queueList.size()+0)+0;
    				
                	String songString = "";
                	
                	
                	for (Entry<String, Integer> m : queueList.entrySet()) {
                	    if (m.getValue() == randomValue) {
                	        songString = m.getKey();
                	    }
                	}
                	
                	createMediaPlayer(uploadList.get(songString));
                    if(playBtn.isVisible()) {
                    	playBtn.setVisible(false);
                    	pauseBtn.setVisible(true);
                    }
                	mediaPlayer.play();
    				
    			}else {
        			Alert alert = new Alert(AlertType.ERROR, "no enough tracks in queue to randomize", ButtonType.CLOSE);
            		alert.show();
    			}
    		}

    	}else {
    		if(playlistList.getSelectionModel().getSelectedItem().getList().size() == 1) {
    			Alert alert = new Alert(AlertType.ERROR, "no more tracks in queue", ButtonType.CLOSE);
        		alert.show();
    		}else {
    			playlistCount--;
    			String songString = "";
            	for (Entry<String, Integer> m : playlistMap.entrySet()) {
            	    if (m.getValue() == playlistCount) {
            	    	songString = m.getKey();
            	    }
            	}

                createMediaPlayer(uploadList.get(songString));
                if(playBtn.isVisible()) {
                	playBtn.setVisible(false);
                	pauseBtn.setVisible(true);
                }
                mediaPlayer.play();

    		}
    	}    	
    }

    @FXML
    public void stop(ActionEvent event) {
    	playlistMode = false;
    	if(mediaPlayer != null) {
        	mediaPlayer.stop();
        	if(!playBtn.isVisible()) {
        		playBtn.setVisible(true);
        		pauseBtn.setVisible(false);
        	}
    	}else {
    		Alert alert = new Alert(AlertType.ERROR, "No music found", ButtonType.CLOSE);
    		alert.show();
    	}
    }

    @FXML
    public void toggleMusicPane(ActionEvent event) {
    	if(playlistPane.isVisible() ||optionsPane.isVisible()) {
    		playlistPane.setVisible(false);
    		optionsPane.setVisible(false);
    	}
    	if(musicPane.isVisible()) {
    		musicPane.setVisible(false);
    	}else {
    		musicPane.setVisible(true);
    	}
    }

    @FXML
    public void togglePlaylistPane(ActionEvent event) {
    	if(musicPane.isVisible() || optionsPane.isVisible()) {
    		musicPane.setVisible(false);
    		optionsPane.setVisible(false);
    	}
    	if(playlistPane.isVisible()) {
    		playlistPane.setVisible(false);
    	}else {
    		playlistPane.setVisible(true);
    	}
    }
    
    public void toggleOptionsPane(ActionEvent event) {
    	if(isStarted == true) {
    		cBDark.selectedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					// TODO Auto-generated method stub
					if(newValue){
						leftPane.getStylesheets().clear();
						leftPane.getStylesheets().add("/application/stylesheet.css");
						bottomPane.getStylesheets().clear();
						bottomPane.getStylesheets().add("/application/stylesheet.css");
						progressBar.getStylesheets().clear();
						progressBar.getStylesheets().add("/application/stylesheet.css");
						volumeSlider.getStylesheets().clear();
						volumeSlider.getStylesheets().add("/application/stylesheet.css");
						playlistBtn.getStylesheets().clear();
						playlistBtn.getStylesheets().add("/application/stylesheet.css");
						musicBtn.getStylesheets().clear();
						musicBtn.getStylesheets().add("/application/stylesheet.css");
						uploadBtn.getStylesheets().clear();
						uploadBtn.getStylesheets().add("/application/stylesheet.css");
						optionsBtn.getStylesheets().clear();
						optionsBtn.getStylesheets().add("/application/stylesheet.css");
						playBtn.getStylesheets().clear();
						playBtn.getStylesheets().add("/application/stylesheet.css");
						repeatBtn.getStylesheets().clear();
						repeatBtn.getStylesheets().add("/application/stylesheet.css");
						randomBtn.getStylesheets().clear();
						randomBtn.getStylesheets().add("/application/stylesheet.css");
						stopBtn.getStylesheets().clear();
						stopBtn.getStylesheets().add("/application/stylesheet.css");
						nextBtn.getStylesheets().clear();
						nextBtn.getStylesheets().add("/application/stylesheet.css");
						backBtn.getStylesheets().clear();
						backBtn.getStylesheets().add("/application/stylesheet.css");
						bgPane.getStylesheets().clear();
						bgPane.getStylesheets().add("/application/stylesheet.css");
						musicPane.getStylesheets().clear();
						musicPane.getStylesheets().add("/application/stylesheet.css");
						playlistPane.getStylesheets().clear();
						playlistPane.getStylesheets().add("/application/stylesheet.css");
						pauseBtn.getStylesheets().clear();
						pauseBtn.getStylesheets().add("/application/stylesheet.css");
						
		            }else{
						leftPane.getStylesheets().clear();
						leftPane.getStylesheets().add("/application/stylesheetTheme.css");
						bottomPane.getStylesheets().clear();
						bottomPane.getStylesheets().add("/application/stylesheetTheme.css");
						progressBar.getStylesheets().clear();
						progressBar.getStylesheets().add("/application/stylesheetTheme.css");
						volumeSlider.getStylesheets().clear();
						volumeSlider.getStylesheets().add("/application/stylesheetTheme.css");
						playlistBtn.getStylesheets().clear();
						playlistBtn.getStylesheets().add("/application/stylesheetTheme.css");
						musicBtn.getStylesheets().clear();
						musicBtn.getStylesheets().add("/application/stylesheetTheme.css");
						uploadBtn.getStylesheets().clear();
						uploadBtn.getStylesheets().add("/application/stylesheetTheme.css");
						optionsBtn.getStylesheets().clear();
						optionsBtn.getStylesheets().add("/application/stylesheetTheme.css");
						playBtn.getStylesheets().clear();
						playBtn.getStylesheets().add("/application/stylesheetTheme.css");
						repeatBtn.getStylesheets().clear();
						repeatBtn.getStylesheets().add("/application/stylesheetTheme.css");
						randomBtn.getStylesheets().clear();
						randomBtn.getStylesheets().add("/application/stylesheetTheme.css");
						stopBtn.getStylesheets().clear();
						stopBtn.getStylesheets().add("/application/stylesheetTheme.css");
						nextBtn.getStylesheets().clear();
						nextBtn.getStylesheets().add("/application/stylesheetTheme.css");
						backBtn.getStylesheets().clear();
						backBtn.getStylesheets().add("/application/stylesheetTheme.css");
						bgPane.getStylesheets().clear();
						bgPane.getStylesheets().add("/application/stylesheetTheme.css");
						musicPane.getStylesheets().clear();
						musicPane.getStylesheets().add("/application/stylesheetTheme.css");
						playlistPane.getStylesheets().clear();
						playlistPane.getStylesheets().add("/application/stylesheetTheme.css");
						pauseBtn.getStylesheets().clear();
						pauseBtn.getStylesheets().add("/application/stylesheetTheme.css");
		            }
				}	
			});
    		isStarted = false;
    	}else {
    		
    	}
    	
    	if(musicPane.isVisible() || playlistPane.isVisible()) {
    		musicPane.setVisible(false);
    		playlistPane.setVisible(false);
    	}
    	if(optionsPane.isVisible()) {
    		optionsPane.setVisible(false);
    	}else {
    		optionsPane.setVisible(true);
    	}
    }
    
    public void getMusicCover() {
    	if(mediaPlayer != null && notice == false) {
    		String songName = currentSong.getText();
    		if(imgMap.containsKey(songName)) {
    			imgCover.setImage(imgMap.get(songName));
    		}else {
        		Alert alert = new Alert(AlertType.CONFIRMATION, "This track has no Cover, do you want to upload a Cover?", ButtonType.YES, ButtonType.NO);
        		alert.showAndWait();
        		if(alert.getResult() == ButtonType.YES) {
        			FileChooser fileChooser = new FileChooser();
        	        fileChooser.setTitle("Select Cover Picture");
        	        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*jpg"));
        	        File selectedFile = fileChooser.showOpenDialog(null);
        	        File selectedFileInput = selectedFile;
        	        if(selectedFile != null) {
        	        	Image image = new Image(selectedFile.toURI().toString());
        	        	imgCover.setImage(image);
        	        	imgMap.put(songName, image);
        	        }
        		}else {
        			
        		}
    		}
    	}
    }
    
      
    //__________________________________INITIALIZE FUNCTION____________________________________//
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}

}
