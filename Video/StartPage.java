package Video;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

public class StartPage {

	private StartPage playVid = this;
	private JPanel contentPane;
	private JPanel buttonPane;
	private JPanel sidePane;
	private JPanel titlePane;
	private JPanel audioPane;
	private JPanel buttonPanel;

	private final JFrame frame;
	
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;

	private JButton selectVideo = new Button("Select Video");
	private JButton createAudio = new Button("Create Commentary");
	private JButton addAudioButton = new Button("Add Audio");
	private JButton optionsButton = new Button("Options");

	private JButton rewindButton;
	private JButton playAndPauseButton;
	private JButton fastForwardButton;
	private JButton muteButton;
	private JButton saveButton;

	private boolean isRewinding = false;
	private boolean isFastForwarding = false;
	private boolean isPaused = false;
	private String videoTitle;
	private String videoPath;
	private boolean isMuted;
	
	// Start path that initialises the StartPage itself
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartPage frame = new StartPage("Welcome to VIDIVOX", "");
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
		contentPane.add(mediaPlayerComponent, BorderLayout.CENTER);
		
		// JPanel to contain the control panel on the lefthand side
		sidePane = new JPanel();
		sidePane.setLayout(new BorderLayout());
		contentPane.add(sidePane, BorderLayout.WEST);
		
		// JPanel to contain the buttons
		buttonPane = new JPanel();
		buttonPane.setLayout(new BorderLayout());
		sidePane.add(buttonPane, BorderLayout.SOUTH);

		audioPane = new JPanel();
		audioPane.setLayout(new BorderLayout());
		sidePane.add(audioPane, BorderLayout.NORTH);
		

		// This button lets the user select a video to play
		selectVideo = new Button("Select Video");
		selectVideo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		buttonPane.add(selectVideo, BorderLayout.NORTH);
		// This button lets the user create a commentary to add to the video
		createAudio = new Button("Create Commentary");
		createAudio.setFont(new Font("Tahoma", Font.PLAIN, 20));
		buttonPane.add(createAudio, BorderLayout.SOUTH);
		
		// This button lets the user select a video to play
		addAudioButton = new Button("Add Audio");
		addAudioButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		audioPane.add(addAudioButton, BorderLayout.NORTH);
		// This button lets the user create a commentary to add to the video
		optionsButton = new Button("Options");
		optionsButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		audioPane.add(optionsButton, BorderLayout.SOUTH);
		

		// JPanel for video related buttons and controls
		buttonPanel = new JPanel();
 
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


		//controls play/pause/rewind/fast forward functions
		rewindButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	if(!isRewinding && !isFastForwarding && !isPaused) {
		    		isMuted = mediaPlayerComponent.getMediaPlayer().isMute();
	    			playAndPauseButton.setText("Play");
		    		isRewinding = true;
		    		// TODO: add logic to rewind the video
		    	}
		    }
		});

		fastForwardButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	if(!isRewinding && !isFastForwarding && !isPaused) {
		    		isMuted = mediaPlayerComponent.getMediaPlayer().isMute();
	    			btnPlayPause.setText("Play");
		    		isFastForwarding = true;
		    		// TODO: add logic to fastforward the video
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
		btnSave.addActionListener(new ActionListener() {
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
                    	// TODO: add "play again" functionality
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
                        goBack();
                    }
                });
            }
        });		
		
		// The created frame is titled with the name of the video
		frame.setContentPane(contentPane);
		frame.setVisible(true);
		frame.setTitle("VIDIVOX - " + videoTitle);

		//video always starts with mute not on playing
		mediaPlayerComponent.getMediaPlayer().mute(false);
	}
	
	public void runPlayer() {
		mediaPlayerComponent.getMediaPlayer().playMedia(videoPath);
	}
	
	// When the video is started it is run from the current frame
	public static void start(final String videoTitle, final String videoPath){
        new NativeDiscovery().discover();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                this.SetStartPage(videoTitle, videoPath);
                this.runPlayer();
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
	public String getVideoPath(){
		return videoPath;
	}
	private void cancelRewindForward() {
		if(isRewinding){
			// TODO: ability to cancel rewinding
		}
		if(isFastForwarding){
			// TODO: ability to cancel rewinding 
		}
	}

	public void setStartPage(videoTitle, videoPath) {
		this.videoTitle = videoTitle;
		this.videoPath = videoPath;
	}
}

