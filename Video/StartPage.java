package video;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSliderUI;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import backgroundTasks.BackGroundMakeFile;
import backgroundTasks.BackgroundForward;
import backgroundTasks.BackgroundRewind;
import backgroundTasks.UpdateSlider;
import audio.AddAudio;
import audio.AudioPage;
import audio.EditMediaTime;
import audio.LoadingFrame;
import audio.SavePage;

import chooseFiles.FileChooser;

import time.PositionSlider;
import time.TimeLabel;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import video.storage.Media;
import video.storage.MediaList;

/**This class creates the main frame that holds the video component and a number of buttons that allow
 * video manipulation and editing. 
 * 
 * Also contains components that remember the selected video's path and name, as well as all the 
 * information associated with any audio files that have been added to the video. 
 * 
 * @author Hannah Sampson
 */

public class StartPage {

	// Panels that contain all the buttons and components for the frame ---------------------------------
	private static StartPage startPage;
	private JPanel contentPane = new JPanel();
	private JPanel videoPane = new JPanel();
	private JPanel selectionPane = new JPanel();
	private JPanel sidePane = new JPanel();
	private JPanel audioPane = new JPanel();
	private JPanel buttonPane = new JPanel();
	private JPanel mergeButtons = new JPanel();
	private JPanel sliderPane = new JPanel();
	private JPanel audioFiles = new JPanel();

	private JFrame frame;
	
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private PositionSlider positionSlider;

	// Buttons ------------------------------------------------------------------------------------------
	private JButton selectVideo = new JButton("Select Video");
	private JButton createAudio = new JButton("Create New mp3");
	private JButton addAudioButton = new JButton("Add Audio");
	private JButton optionsButton = new JButton("Options");

	private JButton rewindButton = new JButton("Rewind");
	private JButton playAndPauseButton = new JButton("Play");
	private JButton fastForwardButton = new JButton("FastForward");
	private JButton muteButton = new JButton("Mute");
	private JButton saveButton = new JButton("Save");

	// States of the video ------------------------------------------------------------------------------
	private boolean isRewinding = false;
	private boolean isFastForwarding = false;
	private boolean isPaused = false;
	private String videoTitle;
	private String videoPath;
	private boolean isMuted;
	
	private BackgroundForward fastForward;
	private BackgroundRewind rewinding;
	
	// Audio files that have been added to the video ----------------------------------------------------
	private static MediaList audio;
	private JList<String> list;
	private DefaultListModel<String> listModel;
	
	private JButton mergeButton = new JButton("Merge");
	private JButton editButton = new JButton("Edit");
	private JButton deleteButton = new JButton("Delete");
	
	// Components that show the progression of the video ------------------------------------------------
	private UpdateSlider update;
	private TimeLabel endTime = new TimeLabel();
	private TimeLabel positionTime = new TimeLabel();
	// Set the length of the video to be 60 seconds by default
	private int lengthOfVideo = 60;
	
	// Start path that initialises the StartPage itself -------------------------------------------------
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new StartPage("Welcome to VIDIVOX", "");
					createDirectories();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/** This initial frame holds the video player, all the buttons that control the video playback
	 *  and the buttons that let you change video/audio selection etc.
	 *  
	 *  All buttons apart from "select video" and "create commentary" are greyed out upon starting to
	 *  enforce that the user needs to select a video before being able to try edit or play anything
	 */
	public StartPage(final String videoTitle, final String videoPath) {
		
		StartPage.startPage = this;
		this.videoTitle = videoTitle;
		this.videoPath = videoPath;
		
        frame = new JFrame("VIDIVOX");
        frame.setBounds(100, 100, 1000, 700);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	//releases media component and associated native resources upon closing the window
                mediaPlayerComponent.release();
                System.exit(0);
            }
        });
        
		contentPane.setLayout(new BorderLayout());
        
		// JPanel for media player ----------------------------------------------------------------------
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		videoPane.setLayout(new BorderLayout());
		videoPane.add(mediaPlayerComponent, BorderLayout.CENTER);
		
		// Add slider to control the video playback -----------------------------------------------------
		sliderPane.setLayout(new BorderLayout());
		
		positionSlider = new PositionSlider();
		positionSlider.setEnabled(true);
		
		sliderPane.add(positionSlider, BorderLayout.CENTER);
		sliderPane.add(endTime, BorderLayout.EAST);
		sliderPane.add(positionTime, BorderLayout.WEST);
		
		videoPane.add(sliderPane, BorderLayout.SOUTH);
		
		// Start the updater for the position slider (note that this is a background task)
		createUpdateSlider();
		
		contentPane.add(videoPane, BorderLayout.CENTER);
		
		
		// JPanel to contain the control panel on the lefthand side -------------------------------------
		sidePane.setLayout(new BorderLayout());
		contentPane.add(sidePane, BorderLayout.WEST);
		
		// JPanel to contain the buttons that let the user select a video or create a new commentary
		selectionPane.setLayout(new BorderLayout());
		selectionPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		sidePane.add(selectionPane, BorderLayout.SOUTH);
		// JPanel that contains ability to add a new audio, and choose options associated with adding
		// this audio to the video
		audioPane.setLayout(new BorderLayout());
		audioPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		sidePane.add(audioPane, BorderLayout.NORTH);
		
		
		// This button lets the user select a video to play
		selectVideo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		selectionPane.add(selectVideo, BorderLayout.NORTH);
		// This button lets the user create a new mp3 commentary file (without having a video selected)
		createAudio.setFont(new Font("Tahoma", Font.PLAIN, 20));
		selectionPane.add(createAudio, BorderLayout.SOUTH);
		// This either create an audio to add to the video or select an already created one
		addAudioButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		audioPane.add(addAudioButton, BorderLayout.NORTH);
		// This button lets the user create a commentary to add to the video
		optionsButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		audioPane.add(optionsButton, BorderLayout.SOUTH);
		
		
		// Add a list that contains all the audio files that the user has added to the video ------------
		audioFiles.setLayout(new BorderLayout());
		mergeButtons.setLayout(new BorderLayout());
		
		listModel = new DefaultListModel<String>();
		list = new JList<String>(listModel);
		
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		JScrollPane scroll = new JScrollPane(list);
		
		// Create action listeners for the audio manipulation buttons:
		
		// The merge button refreshes the video, i.e. if changes have been made to the audio files
		// (deleted, position in video changed) this puts these changes into place and plays the newly
		// created video
		mergeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startPage.merge();
			}
		});
		mergeButtons.add(mergeButton, BorderLayout.EAST);
		
		// edit button takes the user to a frame where they can edit the placement of the selected 
		// audio file, otherwise does nothing
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Check that something has been selected, otherwise these buttons do nothing
				if (!list.isSelectionEmpty()) {
					// Pause the video, and create a new frame that lets you change the audio position
					mediaPlayerComponent.getMediaPlayer().pause();
					EditMediaTime edit = new EditMediaTime(audio, list.getSelectedIndex(), startPage);
					edit.setVisible(true);
				}
			}
		});
		mergeButtons.add(editButton, BorderLayout.CENTER);
		
		// Delete button removes the selected audio file from the list of audio that has been added
		// to the video file, otherwise does nothing
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Check if something has been selected, otherwise these buttons do nothing
				if (!list.isSelectionEmpty()) {
					// Remove the selected audio from the list of audio files associated with this video
					audio.remove(list.getSelectedIndex());
					listModel.remove(list.getSelectedIndex());
					// If there was only one item in the list, this deletion will mean that there are
					// now no audio files added, so the edit and delete buttons are no longer active
					if (audio.size() == 0) {
						deleteButton.setEnabled(false);
						editButton.setEnabled(false);
					}
				}
			}
		});
		mergeButtons.add(deleteButton, BorderLayout.WEST);
		
		audioFiles.add(list, BorderLayout.CENTER);
		audioFiles.add(mergeButtons, BorderLayout.SOUTH);
		sidePane.add(audioFiles, BorderLayout.CENTER);

		// JPanel for video related buttons and controls ------------------------------------------------
		buttonPane.setBorder(new EmptyBorder(5, 5, 5, 5));
 
		buttonPane.add(rewindButton);
		rewindButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		buttonPane.add(playAndPauseButton);
		playAndPauseButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		buttonPane.add(fastForwardButton);
		fastForwardButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		buttonPane.add(muteButton);
		muteButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		// Note: save button is only enabled when audio is added
		saveButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		buttonPane.add(saveButton);
        
		// Add this button panel to the video frame
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.add(buttonPane, BorderLayout.SOUTH);
		

		//add action listeners for all of the buttons ---------------------------------------------------

		// Add listeners for the slider
		positionSlider.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// When the mouse is clicked the position of the mouse is recorder and used to change
				// the position of the slider
				Point p = e.getPoint();
				BasicSliderUI sliderUI = (BasicSliderUI) positionSlider.getUI();
				int value = sliderUI.valueForXPosition(p.x);
				
				positionSlider.setValue(value);
				setPositionBasedOnSlider();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				update.cancel(true);
			}
			// When the slider is released this is the position that the video goes to
			@Override
			public void mouseReleased(MouseEvent e) {
				setPositionBasedOnSlider();
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		
		// Add action listeners for the right option pane -----------------------------------------------
		selectVideo.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	FileChooser file = new FileChooser();
		    	file.addReferenceToStart(startPage);
		    	file.showFileChooser(true);
		    }
		});
		
		addAudioButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	mediaPlayerComponent.getMediaPlayer().pause();
		    	AddAudio addAudioPage = new AddAudio(startPage);
		    	addAudioPage.setVisible(true);
		    }
		});
		
		createAudio.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	AddAudio a = new AddAudio(startPage);
		    	AudioPage audio = new AudioPage(a);
		    	audio.setVisible(true);
		    }
		});
		
		optionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});

		//controls play/pause/rewind/fast forward functions ---------------------------------------------
		rewindButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	if(!isRewinding && !isFastForwarding && !isPaused) {
		    		isMuted = mediaPlayerComponent.getMediaPlayer().isMute();
	    			playAndPauseButton.setText("Play");
		    		isRewinding = true;
		    		rewinding = new BackgroundRewind(startPage, mediaPlayerComponent,mediaPlayerComponent.getMediaPlayer().getTime(), isMuted);
		    		rewinding.execute();
		    	}
		    }
		});

		fastForwardButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	if(!isRewinding && !isFastForwarding && !isPaused) {
		    		isMuted = mediaPlayerComponent.getMediaPlayer().isMute();
	    			playAndPauseButton.setText("Play");
		    		isFastForwarding = true;
		    		fastForward = new BackgroundForward(startPage, mediaPlayerComponent,mediaPlayerComponent.getMediaPlayer().getTime(), isMuted);
		    		fastForward.execute();
		    	}
		    }
		});

		playAndPauseButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	//only change the label of the button when the video is changing
		    	//between play and pause, not when fastforwarding or rewinding
		    	if(!isRewinding && !isFastForwarding){
		    		toggleIsPaused();
		    		mediaPlayerComponent.getMediaPlayer().pause();
		    	}else{
		    		//play button also cancels rewind or forward function
		    		cancelRewindForward();
		    	}
		    }
		});
		
		// Mutes and unmutes the video ------------------------------------------------------------------
		muteButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	mediaPlayerComponent.getMediaPlayer().mute();
		    }
		});
		
		// saves the current edited file as a new file --------------------------------------------------
		saveButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	//can only save if not rewinding or fastforwarding (otherwise some errors occur)
		    	if(!isRewinding && !isFastForwarding){
		    		if(!isPaused){
		    			toggleIsPaused();
		    			mediaPlayerComponent.getMediaPlayer().pause();
		    		}
		    		SavePage save = new SavePage("", false, null);
		    		save.setVisible(true);
		    	}
		    }
		});
		
		
		
		//add listeners for the media component ---------------------------------------------------------
        mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void playing(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.setTitle("VIDIVOX - " + videoTitle);
                    }
                });
            }
            
            //when finished the video will automatically play again -------------------------------------
           @Override
            public void finished(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                    	startPage.createOriginalVideo();
                    }
                });
            } 
           
           //if the video cannot play, or the audio and video ffmpeg didn't occur properly display an 
           // error message
            @Override
            public void error(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(frame, "Failed to play media.", "Error", JOptionPane.ERROR_MESSAGE
                        );
                    }
                });
            }
        });		
		
		// The created frame is titled with the name of the video
		frame.setContentPane(contentPane);
		frame.setVisible(true);
		frame.setTitle("VIDIVOX - " + videoTitle);
		
		
		// When the video frame is first created (i.e. no video is playing yet)
		// set all the buttons that are to do with controlling the video to be greyed out
		muteButton.setEnabled(false);
    	rewindButton.setEnabled(false);
    	fastForwardButton.setEnabled(false);
    	addAudioButton.setEnabled(false);
    	optionsButton.setEnabled(false);
    	playAndPauseButton.setEnabled(false);
    	positionSlider.setEnabled(false);
    	
    	mergeButton.setEnabled(false);
    	editButton.setEnabled(false);
    	deleteButton.setEnabled(false);
    	
    	saveButton.setEnabled(false);

		//video always starts with sound on
		mediaPlayerComponent.getMediaPlayer().mute(false);
	}
	
	
	
	
	
	
	
	/** Run player enables all of the video manipulation buttons and runs the video itself. */
	public void runPlayer() {
		mediaPlayerComponent.getMediaPlayer().playMedia(videoPath);
		// When a video is playing set all the buttons able to be used
    	muteButton.setEnabled(true);
    	rewindButton.setEnabled(true);
    	fastForwardButton.setEnabled(true);
    	addAudioButton.setEnabled(true);
    	optionsButton.setEnabled(true);
    	playAndPauseButton.setEnabled(true);
    	positionSlider.setEnabled(true);
    	
    	startPage.setPlayBtnText("Pause");
	}
	
	// When the video is started it is run from the current frame
	public void start(final String videoTitle, final String videoPath){
        new NativeDiscovery().discover();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                startPage.setStartPage(videoTitle, videoPath);
                startPage.runPlayer();
            }
        });
	}	
	
	// Logic for setting booleans for if the video is fastforwarding, rewinding or paused
	public void setIsRewinding(boolean b){
		isRewinding = b;
	}
	public void setIsFastForwarding(boolean b){
		isFastForwarding = b;
	}
	public void setIsPaused(boolean b){
		isPaused = b;
	}

	/** Depending on whether the video is playing or paused the text of the play/pause button changes */
	public void toggleIsPaused(){
		isPaused = (isPaused == true) ? false : true;
		String newLabel;
		newLabel = (isPaused) ? "Play" : "Pause";
		setPlayBtnText(newLabel);
	}
	
	public void setPlayBtnText(String s){
		playAndPauseButton.setText(s);
	}
	public String getVideoPath(){
		return videoPath;
	}
	public String getVideoTitle(){
		return videoTitle;
	}
	private void cancelRewindForward() {
		if(isRewinding){
			rewinding.cancel(true);
		}
		if(isFastForwarding){
			fastForward.cancel(true);
		}
	}

	/** Set the variables associated with the frame to the currently selected video */
	public void setStartPage(String videoTitle, String videoPath) {
		this.videoTitle = videoTitle;
		this.videoPath = videoPath;
	}
	/** Take the value from the slider and use this to set the position of the video
	 *  Called when a mouse event occurs for the slider
	 */
	public void setPositionBasedOnSlider() {
		float position = (float)positionSlider.getValue() / lengthOfVideo;
		setPosition(position);
	}
	/** As long as there is a video selected, use the "position" value to set the position of the
	 * 	media player
	 * 
	 * @param position
	 */
	public void setPosition(float position) {
		update.cancel(true);
		if (videoPath == "") {
			return;
		} else {
			mediaPlayerComponent.getMediaPlayer().setPosition(position);
		}
		// When the position is finalised, start the slider checher again
		createUpdateSlider();
	}
	
	
	
	/** Create the media file that stores all the files created by the media player */
	protected static void createDirectories() {
		// Create a folder to store the media we want to be able to play or listen to
		File media = new File("VIDIVOXmedia");
		if (!(media.isDirectory())) {
			media.mkdir();
		}
	}
	
	
	public void createOriginalVideo() {
		if (audio.size() == 0) {
			start(audio.getInitialVideoName(), audio.getInitialVideoPath());
		}
	}

	
	public String getOriginalVideoPath() {
		return audio.getInitialVideoPath();
	}
	
	/** This method returns the total time of the selected video file */
	public int getLengthOfVideo() {
		return lengthOfVideo;
	}
	
	
	/** This function sets the length of the slider to correspond to the length of the video
	 * it is called every time a new video is played (as otherwise it stays constant
	 * 
	 * It also sets the far right label to the total time of the video
	 */
	public void setLengthOfSlider() {
		positionSlider.setVideoLength(lengthOfVideo);
		endTime.setTimeText(lengthOfVideo);
	}
	
	/** This method creates and executes the background task that updates the slider */
	private void createUpdateSlider() {
		update = new UpdateSlider();
		update.addReferenceToStart(this);
		update.execute();
	}
	
	/** This is the method that is called to update the slider underneath the playing video
	 *  It is called by UpdateSlider (a background task which can be cancelled at any point) */
	public void updateSlider() {
		positionSlider.setValue((int) mediaPlayerComponent.getMediaPlayer().getTime()/1000);
		positionTime.setTimeText(positionSlider.getValue());
	}
	
	

	/** This function is called when a new video is selected, it resets (or sets) the list of media 
	 * so that each new video has a different set of added audio files
	 * 
	 * It also sets the length of the slider and sets the label associated with this
	 */
	public void createNewVideo(String name, String path) {
		audio = new MediaList();
		audio.addInitial(name, path);
		lengthOfVideo = audio.getLengthOfVideo();
		setLengthOfSlider();
	}
	
	/** This method is used when a new audio file has been added to the video */
	public void addAudio(Media media) {
		// Add the new audio file to the list of audio files
		audio.add(media);
		listModel.addElement(media.getName());
		// As there is now at least one audio file in the list, set all of the buttons to be enabled
		mergeButton.setEnabled(true);
		editButton.setEnabled(true);
		deleteButton.setEnabled(true);
		saveButton.setEnabled(true);
	}
	
	/** When the merge button is pressed, any changes that the user has made to the video, and it's 
	 * list of audio files is applied, and this new video is played
	 */
	public void merge() {
		//show loading screen so user knows something is happening
		LoadingFrame lf  = new LoadingFrame();
		lf.setVisible(true);
		
		// If no audio fies have been added, just play the original video
		// Disable the save function and merge funtion as there is no change from the original file
		
		// Otherwise create the new video file with the changes to the audio files applied
		if (audio.size() == 0) {
			startPage.start(audio.getInitialVideoName(), audio.getInitialVideoPath());
			mergeButton.setEnabled(false);
			saveButton.setEnabled(false);
			lf.dispose();
		} else {
			BackGroundMakeFile merge = new BackGroundMakeFile(audio.getInitialVideoPath(), audio, lf, audio.getInitialVideoName());
			merge.addReferenceToStart(startPage);
			merge.execute();
		}
	}
}
