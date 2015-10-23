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

import time.TimeLabel;
import video.VIDIVOXstart;
import video.storage.MediaList;
import audio.LoadingFrame;
import backgroundTasks.BackgroundPreview;

/**
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class EditMediaTime extends JFrame {

	private EditMediaTime frame = this;
	private JPanel contentPane;
	private JPanel optionPane;
	private JPanel timePane;
	private JPanel addPane;
	
	private JLabel audioLabel;
	private JCheckBox addAtPointer;
	private int positionToAddAt = 0;
	
	private JSlider timeSlider;
	private int time;
	private TimeLabel endTime = new TimeLabel();
	private TimeLabel positionTime = new TimeLabel();
	
	private MediaList audio;
	private int position;
	private VIDIVOXstart start;
	
	private JButton previewButton;
	private JButton addButton;
	private JButton cancelButton;
	
	public EditMediaTime(MediaList a, int p, VIDIVOXstart s) {
		setBounds(100, 100, 500, 250);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		
		this.audio = a;
		this.position = p;
		this.start = s;
		
		this.time = this.getTotalTimeOfAudio();
		
		optionPane = new JPanel(new BorderLayout());
		
		audioLabel = new JLabel(audio.getAudioName(position));
		audioLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		addAtPointer = new JCheckBox("Move Audio to Pointer");
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
		timeSlider = new JSlider(0, this.audio.getLengthOfVideo() - time, 0);
		
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
		
		timePane.add(endTime, BorderLayout.EAST);
		timePane.add(positionTime, BorderLayout.WEST);
		timePane.add(timeSlider);
		
		setLengthOfSlider();
		
		// Add the labels that hold the audio file the user wants to edit -------------------------------
		optionPane.add(audioLabel, BorderLayout.NORTH);
		optionPane.add(addAtPointer, BorderLayout.SOUTH);
		optionPane.add(timePane, BorderLayout.CENTER);
		
		contentPane.add(optionPane, BorderLayout.CENTER);
		
		addPane = new JPanel();
		
		previewButton = new JButton("Preview");
		previewButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		addPane.add(previewButton);
		
		contentPane.add(addPane, BorderLayout.SOUTH);
		
		// The add button just adjusts the time value associated with the selected audio file
		addButton = new JButton("Add to Video");
		addButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		addButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	// If the checkbox to add the audio at the main frame's slider position has not been
		    	// checked, set the positionToAddAt value to be the value of the slider on the edit
		    	// frame
		    	if (!addAtPointer.isSelected()) {
		    		positionToAddAt = timeSlider.getValue();
		    	}
		    	// Once set, edit the audio in the list and merge the new audio so that the video
		    	// starts playing automatically with the changes in place
		    	audio.editTime(position, positionToAddAt);
		    	start.merge();
		    	frame.dispose();
		    }
		});
		addPane.add(addButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		cancelButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	frame.dispose();
		    }
		});
		addPane.add(cancelButton);
		
		// Preview button works the same as when adding audio for the first time: Just view enough of 
		// the original video to tell where the audio is being added (and only for length of audio)
		previewButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {	
				//show loading screen so user knows something is happening
				LoadingFrame lf  = new LoadingFrame();
				lf.setVisible(true);
				
				if ((positionToAddAt + time) > audio.getLengthOfVideo()) {
					positionToAddAt = audio.getLengthOfVideo() - time;
				}
				
				BackgroundPreview makeFile = new BackgroundPreview(audio.getInitialVideoPath(), audio.getAudioPath(position), lf, audio.getInitialVideoName(), positionToAddAt, time);
				makeFile.addReferenceToStart(start);
				makeFile.execute();
		    }

		});
	}
	
	/** For the chosen audio
	 * 
	 * @return
	 */
	public int getTotalTimeOfAudio() {
		//Calculate the total time of the selected audio file
		AudioFile audioFile = null;
		try {
			audioFile = AudioFileIO.read(new File(audio.getAudioPath(position)));
		} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e1) {
			e1.printStackTrace();
		}
		return audioFile.getAudioHeader().getTrackLength();
	}
	
	private void setLengthOfSlider() {
		endTime.setTimeText(timeSlider.getMaximum());
	}
	private void setSliderPositionNumber() {
		positionTime.setTimeText(timeSlider.getValue());
	}
}
