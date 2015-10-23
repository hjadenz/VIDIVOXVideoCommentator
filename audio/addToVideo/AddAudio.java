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

import audio.AudioPage;
import audio.LoadingFrame;
import backgroundTasks.BackgroundPreview;

import chooseFiles.FileChooser;

import time.TimeLabel;
import video.VIDIVOXstart;
import video.storage.Media;

/** AddAudio 
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class AddAudio extends JFrame {
	
	private AddAudio frame = this;
	private JPanel contentPane;
	private JPanel buttonPane;
	private JPanel optionPane;
	private JPanel timePane;
	private JPanel addPane;
	
	private JButton newCommentaryButton = new JButton("Create New mp3");
	private JButton addAudioButton = new JButton("Add Created Audio");
	
	private JButton previewButton = new JButton("Preview");
	private String audioPath;
	private JButton addButton = new JButton("Add");
	private JButton cancelButton = new JButton("Cancel");
	
	private JLabel audioLabel;
	private JCheckBox addAtPointer;
	private int positionToAddAt;
	
	private VIDIVOXstart start;
	private JSlider timeSlider;
	private int time;
	
	private TimeLabel endTime = new TimeLabel();
	private TimeLabel addTime = new TimeLabel();

	/**
	 * Create the frame.
	 */
	public AddAudio(final VIDIVOXstart start) {
		setBounds(100, 100, 500, 250);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		
		this.start = start;
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				start.createOriginalVideo();
			}
		});
		
		buttonPane = new JPanel();
		buttonPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		buttonPane.setLayout(new BorderLayout());
		
		JLabel title = new JLabel("Would you like to: ");
		title.setFont(new Font("Tahoma", Font.BOLD, 14));
		buttonPane.add(title, BorderLayout.NORTH);
		
		newCommentaryButton = new JButton("Create a new Commentary");
		newCommentaryButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		buttonPane.add(newCommentaryButton, BorderLayout.CENTER);
		
		addAudioButton = new JButton("or add audio that has already been created");
		addAudioButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		buttonPane.add(addAudioButton, BorderLayout.SOUTH);
		
		contentPane.add(buttonPane, BorderLayout.NORTH);
		
		optionPane = new JPanel(new BorderLayout());
		
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
				} else {
					timeSlider.setEnabled(true);
					positionToAddAt = start.getSliderPosition();
				}
			}
		});
		
		// Create a slider that lets the user enter a time to put the audio file ------------------------
		timePane = new JPanel();
		timePane.setLayout(new BorderLayout());
		timeSlider = new JSlider(0, this.start.getLengthOfVideo(), 0);
		
		// Add the labels that will hold the time value at which the user is adding their audio		
		timePane.add(endTime, BorderLayout.EAST);
		timePane.add(addTime, BorderLayout.WEST);
		setLengthOfSlider();
		
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
				setSliderPositionNumber();
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
			// When the slider is released this is the position that the video goes to
			@Override
			public void mouseReleased(MouseEvent e) {
				setSliderPositionNumber();
			}
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		
		timePane.add(timeSlider);
		
		optionPane.add(audioLabel, BorderLayout.NORTH);
		optionPane.add(addAtPointer, BorderLayout.SOUTH);
		optionPane.add(timePane, BorderLayout.CENTER);
		
		contentPane.add(optionPane, BorderLayout.CENTER);
		
		addPane = new JPanel();
		
		previewButton = new JButton("Preview");
		previewButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		previewButton.setEnabled(false);
		addPane.add(previewButton);
		
		contentPane.add(addPane, BorderLayout.SOUTH);
		
		addButton = new JButton("Add to Video");
		addButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		addButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
				if (addAtPointer.isSelected()) {
					positionToAddAt = start.getSliderPosition();
				} else {
					positionToAddAt = timeSlider.getValue();
				}
				
				if ((positionToAddAt + time) > start.getLengthOfVideo()) {
					positionToAddAt = start.getLengthOfVideo() - time;
				}
		    	
		    	Media m = new Media(audioPath, positionToAddAt, audioLabel.getText());
		    	start.addAudio(m);
		    	start.merge();
		    	frame.dispose();
		    }
		});
		addButton.setEnabled(false);
		addPane.add(addButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		cancelButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	start.createOriginalVideo();
		    	frame.dispose();
		    }
		});
		addPane.add(cancelButton);
		
		// Add file chooser to select audio -------------------------------------------------------------
		addAudioButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	FileChooser chooseAudio = new FileChooser();
		    	chooseAudio.addReferenceToStart(start);
		    	chooseAudio.showFileChooser(false);
		    	chooseAudio.setAudioPath(frame);
		    }
		});
		
		// Let the user create a new mp3 file, then once finished, add this as the selected audio -------
		newCommentaryButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	AudioPage audio = new AudioPage(frame);
		    	audio.setVisible(true);
		    }
		});
		
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
				
				if (addAtPointer.isSelected()) {
					positionToAddAt = start.getSliderPosition();
				} else {
					positionToAddAt = timeSlider.getValue();
				}
				
				if ((positionToAddAt + time) > start.getLengthOfVideo()) {
					positionToAddAt = start.getLengthOfVideo() - time;
				}
				
				// Run the preview of the audio through the original frame
				BackgroundPreview makeFile = new BackgroundPreview(start.getOriginalVideoPath(), audioPath, lf, start.getVideoTitle(), positionToAddAt, time);
				makeFile.addReferenceToStart(start);
				makeFile.execute();
		    }

		});
	}
	
	private void setLengthOfSlider() {
		endTime.setTimeText(timeSlider.getMaximum());
	}
	
	private void setSliderPositionNumber() {
		addTime.setTimeText(timeSlider.getValue());
	}

	public void copyPath(String path, String name) {
		this.audioPath = path;
		this.audioLabel.setText(name);
		this.setPreviewEnabled();
	}

	private void setPreviewEnabled() {
		if (audioLabel.getText() == "") {
			previewButton.setEnabled(false);
			addButton.setEnabled(false);
		} else {
			previewButton.setEnabled(true);
			addButton.setEnabled(true);
		}
		
	}
	
	public void audioFileTime() {
		//Calculate the total time of the selected audio file
		AudioFile audioFile = null;
		try {
			audioFile = AudioFileIO.read(new File(audioPath));
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e1) {
			e1.printStackTrace();
		}
		time =  audioFile.getAudioHeader().getTrackLength();
		setSliderTimeBasedOnAudio();
	}
	
	// This method changes the length of the JSlider based on the length of the audio file the user
	// has selected so that you can't choose a time that would make the audio go over the end of the 
	// video
	private void setSliderTimeBasedOnAudio() {
		int videoLength = this.start.getLengthOfVideo();
		
		timeSlider.setMaximum(videoLength - time);
		setLengthOfSlider();
		setSliderPositionNumber();
	}
}
