package video;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import backgroundTasks.BackgroundForward;
import backgroundTasks.BackgroundRewind;
import audio.AddAudio;

import chooseFiles.FileChooser;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class StartPage {

	private static StartPage startPage;
	private JPanel contentPane;
	private JPanel videoPane;
	private JPanel buttonPane;
	private JPanel sidePane;
	private JPanel audioPane;
	private JPanel buttonPanel;

	private static JFrame frame;
	
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JSlider positionSlider;

	private JButton selectVideo = new JButton("Select Video");
	private JButton addAudioButton = new JButton("Add Audio");
	private JButton optionsButton = new JButton("Options");

	private JButton rewindButton;
	private JButton playAndPauseButton;
	private JButton fastForwardButton;
	private JButton muteButton;
	private JButton saveButton;

	private boolean isRewinding = false;
	private boolean isFastForwarding = false;
	private boolean isPaused = false;
	private static String videoTitle;
	private static String videoPath;
	private boolean isMuted;
	
	private BackgroundForward fastForward;
	private BackgroundRewind rewinding;
	
	// Start path that initialises the StartPage itself
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
	
	/**
	 * Create the frame. This frame includes the video player and option buttons
	 */
	public StartPage(final String videoTitle, final String videoPath) {
		
		StartPage.startPage = this;
		this.videoTitle = videoTitle;
		this.videoPath = videoPath;
		
        frame = new JFrame("VIDIVOX");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	//releases media component and associated native resources
            	//upon closing the window
                mediaPlayerComponent.release();
                System.exit(0);
            }
        });
        
		// JPanel for media player
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		videoPane = new JPanel();
		videoPane.setLayout(new BorderLayout());
		videoPane.add(mediaPlayerComponent, BorderLayout.CENTER);
		
		// Add slider to control the video playback
		positionSlider = new JSlider(0,1000,0);
		positionSlider.setEnabled(true);
		
		videoPane.add(positionSlider, BorderLayout.SOUTH);
		
		contentPane.add(videoPane, BorderLayout.CENTER);
		
		// JPanel to contain the control panel on the lefthand side
		sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		contentPane.add(sidePane, BorderLayout.WEST);
		
		// JPanel to contain the buttons
		buttonPane = new JPanel();
		buttonPane.setLayout(new BorderLayout());
		buttonPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		sidePane.add(buttonPane, BorderLayout.SOUTH);

		audioPane = new JPanel();
		audioPane.setLayout(new BorderLayout());
		audioPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		sidePane.add(audioPane, BorderLayout.NORTH);
		

		// This button lets the user select a video to play
		selectVideo = new JButton("Select Video");
		selectVideo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		buttonPane.add(selectVideo, BorderLayout.NORTH);
		// This either create an audio to add to the video or select an already created one
		addAudioButton = new JButton("Add Audio");
		addAudioButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		audioPane.add(addAudioButton, BorderLayout.NORTH);
		// This button lets the user create a commentary to add to the video
		optionsButton = new JButton("Options");
		optionsButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		audioPane.add(optionsButton, BorderLayout.SOUTH);
		

		// JPanel for video related buttons and controls
		buttonPanel = new JPanel();
		buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
 
		rewindButton = new JButton("Rewind");
		buttonPanel.add(rewindButton);
		rewindButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		playAndPauseButton = new JButton("Pause");
		buttonPanel.add(playAndPauseButton);
		playAndPauseButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		fastForwardButton = new JButton("FastForward");
		buttonPanel.add(fastForwardButton);
		fastForwardButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		muteButton = new JButton("Mute");
		buttonPanel.add(muteButton);
		muteButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		
		// Only show the save button if audio has been added (i.e. if a different file has been created)
		saveButton = new JButton("Save");
		if(videoPath.equals("VIDIVOXmedia/.temporary.avi")){
			buttonPanel.add(saveButton);
		}
		saveButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		

		//add action listeners for all of the buttons

		// Add listeners for the slider
		positionSlider.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {// TODO Auto-generated method stub
			}
			@Override
			public void mousePressed(MouseEvent e) {// TODO Auto-generated method stub
			}
			// When the slider is released this is the position that the video goes to
			@Override
			public void mouseReleased(MouseEvent e) {
				setPositionBasedOnSlider();
			}

			@Override
			public void mouseEntered(MouseEvent e) {// TODO Auto-generated method stub
			}
			@Override
			public void mouseExited(MouseEvent e) {// TODO Auto-generated method stub
			}
		});
		
		// Add action listeners for the right option pane
		selectVideo.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	new FileChooser(true);
		    }
		});
		
		addAudioButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	mediaPlayerComponent.getMediaPlayer().pause();
		    	AddAudio addAudioPage = new AddAudio();
		    	addAudioPage.setVisible(true);
		    }
		});

		//controls play/pause/rewind/fast forward functions
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
		
		// Mutes and unmutes the video
		muteButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	mediaPlayerComponent.getMediaPlayer().mute();
		    }
		});
		
		// saves the current edited file as a new file
		saveButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	//can only save if not rewinding or fastforwarding (otherwise some errors occur)
		    	if(!isRewinding && !isFastForwarding){
		    		if(!isPaused){
		    			toggleIsPaused();
		    			mediaPlayerComponent.getMediaPlayer().pause();
		    		}
		    		// TODO: add ability to save files
		    	}
		    }
		});
		
		//add listeners for the media component
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
            
            //when finished an option will come up asking the user if they want to play the video again
           @Override
            public void finished(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                    	// When the video finishes set the buttons to be greyed out
                    	startPage.setPlayBtnText("Play");
                    	muteButton.setEnabled(false);
                    	rewindButton.setEnabled(false);
                    	fastForwardButton.setEnabled(false);
                    	addAudioButton.setEnabled(false);
                    	optionsButton.setEnabled(false);

                    	// let the play button restart the video
                    	playAndPauseButton.addActionListener(new ActionListener() {
                		    @Override
                		    public void actionPerformed(ActionEvent e) {
                		    	startPage.runPlayer();
                		    }
                		});
                    }
                });
            }
           
           //if the video cannot play, or the audio and video ffmpeg didn't occur properly display an error message
            @Override
            public void error(MediaPlayer mediaPlayer) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(
                            frame,
                            "Failed to play media.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
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

		//video always starts with mute not on playing
		mediaPlayerComponent.getMediaPlayer().mute(false);
	}
	
	public void runPlayer() {
		mediaPlayerComponent.getMediaPlayer().playMedia(videoPath);
		// When a video is playing set all the buttons able to be used
    	muteButton.setEnabled(true);
    	rewindButton.setEnabled(true);
    	fastForwardButton.setEnabled(true);
    	addAudioButton.setEnabled(true);
    	optionsButton.setEnabled(true);
    	playAndPauseButton.setEnabled(true);
    	
    	startPage.setPlayBtnText("Pause");
	}
	
	// When the video is started it is run from the current frame
	public static void start(final String videoTitle, final String videoPath){
        new NativeDiscovery().discover();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                startPage.setStartPage(videoTitle, videoPath);
                startPage.runPlayer();
            }
        });
	}	
	
	public void setIsRewinding(boolean b){
		isRewinding = b;
	}
	public void setIsFastForwarding(boolean b){
		isFastForwarding = b;
	}
	public void setIsPaused(boolean b){
		isPaused = b;
	}

	// Depending on whether the video is playing or paused the text of the play/pause button changes
	public void toggleIsPaused(){
		isPaused = (isPaused == true) ? false : true;
		String newLabel;
		newLabel = (isPaused) ? "Play" : "Pause";
		setPlayBtnText(newLabel);
	}
	
	public void setPlayBtnText(String s){
		playAndPauseButton.setText(s);
	}
	public static String getVideoPath(){
		return videoPath;
	}
	public static String getVideoTitle(){
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

	public void setStartPage(String videoTitle, String videoPath) {
		StartPage.videoTitle = videoTitle;
		StartPage.videoPath = videoPath;
	}
	
	public void setPositionBasedOnSlider() {
		if (videoPath == "") {
			return;
		} else {
			float position = (float)positionSlider.getValue() / 1000;
			mediaPlayerComponent.getMediaPlayer().setPosition(position);
		}
	}
	
	protected static void createDirectories() {
		// Create a folder to store the media we want to be able to play or listen to
		File media = new File("VIDIVOXmedia");
		if (!(media.isDirectory())) {
			media.mkdir();
		}
	}
}
