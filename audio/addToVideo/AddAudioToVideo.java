package audio.addToVideo;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSliderUI;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import audio.LoadingFrame;
import audio.create.CreateNewAudio;
import backgroundTasks.createFiles.CreatePreviewFile;

import chooseFiles.FileChooser;

import time.TimeLabel;
import video.VIDIVOXstart;
import video.storage.Media;

/** AddAudio is a frame that gives the user options to choose an audio to add to the selected video
 * 	It contains a slider that lets the user change where the audio goes, and a checkbox that lets the 
 *  user decide whether they want to use the slider or the video's current position
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class AddAudioToVideo extends JFrame {
	
	// Create the contentPanes to hold all the components -----------------------------------------------
	private AddAudioToVideo frame = this;
	private JPanel contentPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	private JPanel optionPanel = new JPanel();
	private JPanel timePanel = new JPanel();
	private JPanel addPanel = new JPanel();
	private JPanel finalPositionPanel = new JPanel();
	
	// Create buttons -----------------------------------------------------------------------------------
	private JButton newCommentaryButton = new JButton("Create New mp3");
	private JButton addAudioButton = new JButton("Add Created Audio");
	
	private JButton previewButton = new JButton("Preview");
	private JButton addButton = new JButton("Add");
	private JButton cancelButton = new JButton("Cancel");
	
	// Add the components that hold the logic for asking the user whether they want to add the audio
	// based on the slider or based on where the video is -----------------------------------------------
	private JLabel audioLabel = new JLabel("Add audio at current video position");
	private JCheckBox addAtPointer;
	private int positionToAddAt;
	
	private TimeLabel labelPositionToAddAt = new TimeLabel();
	private JLabel finalPositionLabel = new JLabel("Exact position the audio will be added: ");
	
	// Add the components that indicate what time the user will be adding the audio ---------------------
	private VIDIVOXstart start;
	private JSlider timeSlider;
	private int time;
	
	private TimeLabel endTime = new TimeLabel();
	private TimeLabel addTime = new TimeLabel();
	
	// The audio file that the user chooses -------------------------------------------------------------
	private AudioFile audioFile;
	private String audioPath;

	public AddAudioToVideo(VIDIVOXstart s) {
		setBounds(100, 100, 500, 250);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout());
		
		this.start = s;
		this.positionToAddAt = start.getSliderPosition();
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				start.merge();
			}
		});
		
		// Set up the frame -----------------------------------------------------------------------------
		createAudioSelectionButtons();
		addSliderToSelectPosition();
		createViewButtons();
	}

	/** Set the label that shows the user the maximum time they can add the audio, without going over
	 *  the end of the video
	 */
	private void setLabelForLengthOfSlider() {
		endTime.setTimeText(timeSlider.getMaximum());
	}
	/** Set the label to the left of the slider that shows the exact time the user has selected */
	private void setPositionNumberLabel() {
		addTime.setTimeText(timeSlider.getValue());
	}

	/** Add the details of the chosen audio to the frame so that the user can easily see what the choice is */
	public void copyPath(String path, String name) {
		this.audioPath = path;
		this.audioLabel.setText(name);
		this.setPreviewEnabled();
	}

	/** If an audio has been selected, enable the preview button */
	private void setPreviewEnabled() {
		if (audioLabel.getText() == "") {
			previewButton.setEnabled(false);
			addButton.setEnabled(false);
		} else {
			previewButton.setEnabled(true);
			addButton.setEnabled(true);
		}
		
	}
	
	/** Given a particular audio file, work out it's duration */
	public void audioFileTime() {
		//Calculate the total time of the selected audio file
		try {
			audioFile = AudioFileIO.read(new File(audioPath));
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e1) {
			e1.printStackTrace();
		}
		time =  audioFile.getAudioHeader().getTrackLength();
		setSliderTimeBasedOnAudio();
	}
	
	/** This method changes the length of the JSlider based on the length of the audio file the user
	 * has selected so that you can't choose a time that would make the audio go over the end of the 
	 * video
	 */
	private void setSliderTimeBasedOnAudio() {
		int videoLength = this.start.getLengthOfVideo();
		
		timeSlider.setMaximum(videoLength - time);
		setLabelForLengthOfSlider();
		setPositionNumberLabel();
	}
	
	
	
	//---------------------------------------------------------------------------------------------------
	// The following methods are primarily concerned with setting up the frame
	//---------------------------------------------------------------------------------------------------
	
	/** Create the buttons that let the user select:
	 *  	an audio that has already been created
	 *   	an audio that they will create from text
	 */
	private void createAudioSelectionButtons() {
		buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		buttonPanel.setLayout(new BorderLayout());
		
		JLabel title = new JLabel("What would you like to do: ");
		title.setFont(new Font("Tahoma", Font.BOLD, 14));
		buttonPanel.add(title, BorderLayout.NORTH);
		
		newCommentaryButton = new JButton("Create Commentary");
		// Let the user create a new mp3 file, then once finished, add this as the selected audio
		newCommentaryButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	CreateNewAudio audio = new CreateNewAudio(frame, start);
		    	audio.setVisible(true);
		    }
		});
		newCommentaryButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		buttonPanel.add(newCommentaryButton, BorderLayout.CENTER);
		
		addAudioButton = new JButton("Add Existing Audio");
		addAudioButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	new FileChooser(false, null, frame);
		    }
		});
		addAudioButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		buttonPanel.add(addAudioButton, BorderLayout.SOUTH);
		
		contentPanel.add(buttonPanel, BorderLayout.NORTH);
	}
	
	/** Add the slider that lets the user choose where they want their chosen audio to be placed
	 *  Also add the checkbox that lets the user decide whether they want to add their audio using
	 *  the slider or using the position of the video in the original media player
	 */
	private void addSliderToSelectPosition() {
		optionPanel.setLayout(new BorderLayout());
		
		audioLabel = new JLabel("");
		audioLabel.setHorizontalAlignment(SwingConstants.CENTER);
		addAtPointer = new JCheckBox("Add Audio at Pointer");
		addAtPointer.setHorizontalAlignment(SwingConstants.CENTER);
		addAtPointer.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				toggleTimeEnabled();
			}
			private void toggleTimeEnabled() {
				if (timeSlider.isEnabled()) {
					timeSlider.setEnabled(false);
					labelPositionToAddAt.setTimeText(positionToAddAt);
				} else {
					timeSlider.setEnabled(true);
					labelPositionToAddAt.setTimeText(timeSlider.getValue());
				}
			}
		});
		
		// Create a slider that lets the user enter a time to put the audio file ------------------------
		timePanel.setLayout(new BorderLayout());
		timeSlider = new JSlider(0, this.start.getLengthOfVideo(), 0);
		
		// Add the labels that will hold the time value at which the user is adding their audio		
		timePanel.add(endTime, BorderLayout.EAST);
		timePanel.add(addTime, BorderLayout.WEST);
		setLabelForLengthOfSlider();
		
		// Set up the label that holds the final time that the audio will be added at
		finalPositionPanel.add(finalPositionLabel);
		finalPositionPanel.add(labelPositionToAddAt);
		
		timePanel.add(finalPositionPanel, BorderLayout.SOUTH);
		
		// Add the ability to update the label that states the time the slider is on
		timeSlider.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// When the mouse is clicked the position of the mouse is recorder and used to change
				// the position of the slider
				Point p = e.getPoint();
				BasicSliderUI sliderUI = (BasicSliderUI) timeSlider.getUI();
				int value = sliderUI.valueForXPosition(p.x);
				
				timeSlider.setValue(value);
				setPositionNumberLabel();
				// If the slider is enabled, set the value to be the value on the slider
				labelPositionToAddAt.setTimeText(timeSlider.getValue());
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			// When the slider is released this is the position that the video goes to
			@Override
			public void mouseReleased(MouseEvent e) {
				setPositionNumberLabel();
				labelPositionToAddAt.setTimeText(timeSlider.getValue());
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		
		timePanel.add(timeSlider);
		
		optionPanel.add(audioLabel, BorderLayout.NORTH);
		optionPanel.add(addAtPointer, BorderLayout.SOUTH);
		optionPanel.add(timePanel, BorderLayout.CENTER);
		
		contentPanel.add(optionPanel, BorderLayout.CENTER);
	}
	
	/** This method creates and adds action listeners for the buttons that let the user preview where
	 * 	the audio will be placed, once they're happy add this to the video properly
	 * 	It also lets the user cancel the action which just removes the frame and no further action
	 * 	is required
	 */
	private void createViewButtons() {
		previewButton = new JButton("Preview");
		// Show the user a small amount of the film, starting from where the user has selected to place
		// the audio ------------------------------------------------------------------------------------
		previewButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {	
				//show loading screen so user knows something is happening
				LoadingFrame lf  = new LoadingFrame();
				lf.setVisible(true);
				
				// Get the total time of the selected audio file (make sure its up to date)
				audioFileTime();
				
				int position = 0;
				
				if (addAtPointer.isSelected()) {
					position = positionToAddAt;
				} else {
					position = timeSlider.getValue();
				}
				
				// Make sure that the audio won't go over the end of the video
				if ((position + time) > start.getLengthOfVideo()) {
					position = start.getLengthOfVideo() - time;
				}
					
				// Run the preview of the audio through the original frame
				CreatePreviewFile preview = new CreatePreviewFile(start.getOriginalVideoPath(), audioPath, lf, start.getVideoTitle(), position, time);
				preview.addReferenceToStart(start);
				preview.execute();
		    }
		});
		previewButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		previewButton.setEnabled(false);
		addPanel.add(previewButton);
		
		addButton = new JButton("Add to Video");
		addButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		addButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	
		    	int position = 0;
		    	
				if (addAtPointer.isSelected()) {
					position = positionToAddAt;
				} else {
					position = timeSlider.getValue();
				}
				
				// Make sure that the audio won't go over the end of the video
				if ((position + time) > start.getLengthOfVideo()) {
					position = start.getLengthOfVideo() - time;
				}
		    	
				// Create a new media object that will be associated with the selected video
		    	Media m = new Media(audioPath, position, audioLabel.getText(), 50);
		    	start.addAudio(m);
		    	// Run the video with the newest changes in place
		    	start.merge();
		    	frame.dispose();
		    }
		});
		addButton.setEnabled(false);
		addPanel.add(addButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		cancelButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	start.merge();
		    	frame.dispose();
		    }
		});
		addPanel.add(cancelButton);
		
		contentPanel.add(addPanel, BorderLayout.SOUTH);
	}
}
