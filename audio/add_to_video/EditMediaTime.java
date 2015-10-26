package audio.add_to_video;

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
import backgroundtasks.create_files.CreatePreviewFile;

/**
 * Edit media time lets the user take an audio file that has already been
 * selected and added to the video, and change the position at which it is added
 * to the video
 * 
 * This position can be previewed in the position the audio wants it to be
 * before adding it
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class EditMediaTime extends JFrame {

	// Create the contentPanes to hold all the components
	// --------------------------------------------------------------------------------------------------
	private EditMediaTime frame = this;
	private JPanel contentPanel = new JPanel();
	private JPanel optionPanel = new JPanel();
	private JPanel timePanel = new JPanel();
	private JPanel addPanel = new JPanel();
	private JPanel finalPositionPanel = new JPanel();

	// Create buttons
	// --------------------------------------------------------------------------------------------------
	private JButton previewButton = new JButton("Preview");
	private JButton addButton = new JButton("Add");
	private JButton cancelButton = new JButton("Cancel");

	// Add the components that hold the logic for asking the user whether they
	// want to add the audio based on the slider or based on where the video is
	// --------------------------------------------------------------------------------------------------
	private JLabel audioLabel;
	private JCheckBox addAtPointer;
	private int positionToAddAt;

	private TimeLabel labelPositionToAddAt = new TimeLabel();
	private JLabel finalPositionLabel = new JLabel(
			"Exact position the audio will be added: ");

	// Add the components that indicate what time the user wants to add audio
	// --------------------------------------------------------------------------------------------------
	private JSlider timeSlider;
	private int time;

	private TimeLabel endTime = new TimeLabel();
	private TimeLabel addTime = new TimeLabel();

	// The audio file that the user chooses
	// --------------------------------------------------------------------------------------------------
	private VIDIVOXstart start;
	private MediaList audio;
	private int p;

	public EditMediaTime(MediaList a, final int p, VIDIVOXstart s) {
		setBounds(100, 100, 500, 180);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout());

		this.audio = a;
		this.p = p;
		this.start = s;

		this.positionToAddAt = start.getSliderPosition();

		this.time = this.getTotalTimeOfAudio();

		createTimePanel();
		createLabels();
		createButtons();

		setLengthOfSlider();
	}

	/** For the chosen audio return its total length */
	public int getTotalTimeOfAudio() {
		// Calculate the total time of the selected audio file
		AudioFile audioFile = null;
		try {
			audioFile = AudioFileIO.read(new File(audio.getAudioPath(p)));
		} catch (CannotReadException | IOException | TagException
				| ReadOnlyFileException | InvalidAudioFrameException e1) {
			e1.printStackTrace();
		}
		return audioFile.getAudioHeader().getTrackLength();
	}

	private void setLengthOfSlider() {
		endTime.setTimeText(timeSlider.getMaximum());
	}

	private void setSliderPositionNumber() {
		addTime.setTimeText(timeSlider.getValue());
	}

	// --------------------------------------------------------------------------------------------------
	// These methods deal with setting up the frame
	// --------------------------------------------------------------------------------------------------

	private void createLabels() {
		audioLabel = new JLabel(audio.getAudioName(p));
		audioLabel.setHorizontalAlignment(SwingConstants.CENTER);

		optionPanel.setLayout(new BorderLayout());

		optionPanel.add(audioLabel, BorderLayout.NORTH);
		optionPanel.add(timePanel, BorderLayout.CENTER);

		contentPanel.add(optionPanel, BorderLayout.NORTH);

		// Labels that give the user the definite value that the audio will be
		// placed at
		finalPositionPanel.add(finalPositionLabel);
		finalPositionPanel.add(labelPositionToAddAt);
		contentPanel.add(finalPositionPanel, BorderLayout.CENTER);
	}

	/**
	 * Create the components that deal with the position the audio is being
	 * moved to
	 */
	private void createTimePanel() {
		// Add a checkbox that lets the user decide whether they want to select
		// a new time or use the video position (at the time that they opened
		// the frame)
		addAtPointer = new JCheckBox("Add audio at current video position");
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

		// Create a slider that lets the user enter a time to put the audio file
		timePanel.setLayout(new BorderLayout());
		timeSlider = new JSlider(0, this.audio.getLengthOfVideo() - time, 0);

		timeSlider.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// When the mouse is clicked the position of the mouse is
				// recorder and used to change the position of the slider
				Point p = e.getPoint();
				BasicSliderUI sliderUI = (BasicSliderUI) timeSlider.getUI();
				int value = sliderUI.valueForXPosition(p.x);

				timeSlider.setValue(value);
				labelPositionToAddAt.setTimeText(timeSlider.getValue());
				setSliderPositionNumber();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			// When the slider is released this is the position that the video
			// goes to
			@Override
			public void mouseReleased(MouseEvent e) {
				setSliderPositionNumber();
				labelPositionToAddAt.setTimeText(timeSlider.getValue());
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});

		timePanel.add(endTime, BorderLayout.EAST);
		timePanel.add(addTime, BorderLayout.WEST);
		timePanel.add(timeSlider, BorderLayout.CENTER);

		timePanel.add(addAtPointer, BorderLayout.SOUTH);
	}

	/**
	 * Create the buttons and add their action listeners These action listeners
	 * are responsible for creating previews and actually editing the media
	 * object
	 */
	private void createButtons() {
		previewButton = new JButton("Preview");
		previewButton.setFont(new Font("Tahoma", Font.BOLD, 14));

		addPanel.add(previewButton);

		contentPanel.add(addPanel, BorderLayout.SOUTH);

		// The add button just adjusts the time value associated with the
		// selected audio file
		addButton = new JButton("Add to Video");
		addButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// If the checkbox to add the audio at the main frame's slider
				// position has not been checked, set the positionToAddAt value
				// to be the value of the slider on the edit frame
				if (!addAtPointer.isSelected()) {
					positionToAddAt = timeSlider.getValue();
				}
				// Once set, edit the audio in the list and merge the new audio
				// so that the video starts playing automatically with the
				// changes in place
				audio.editTime(p, positionToAddAt);
				start.merge();
				frame.dispose();
			}
		});
		addPanel.add(addButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		addPanel.add(cancelButton);

		// Preview button works the same as when adding audio for the first
		// time: Just view enough of the original video to tell where the audio
		// is being added (and only for length of audio)
		previewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// show loading screen so user knows something is happening
				LoadingFrame lf = new LoadingFrame();
				lf.setVisible(true);

				// Get the total time of the selected audio file (make sure its
				// up to date)
				getTotalTimeOfAudio();

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
				CreatePreviewFile preview = new CreatePreviewFile(start
						.getOriginalVideoPath(), audio.getAudioPath(p), lf,
						start.getVideoTitle(), position, time);
				preview.addReferenceToStart(start);
				preview.execute();
			}
		});
	}
}
