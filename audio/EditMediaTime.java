package audio;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import video.StartPage;
import video.storage.MediaList;
import backgroundTasks.BackgroundPreview;

@SuppressWarnings("serial")
public class EditMediaTime extends JFrame {

	private EditMediaTime frame = this;
	private JPanel contentPane;
	private JPanel optionPane;
	private JPanel timePane;
	private JPanel addPane;
	
	private JLabel audioLabel;
	private JCheckBox addAtPointer;
	
	private JSlider timeSlider;
	private int time;
	
	private MediaList audio;
	private int position;
	private StartPage start;
	
	private JButton previewButton;
	private JButton addButton;
	private JButton cancelButton;
	
	public EditMediaTime(final MediaList audio, final int position, final StartPage start) {
		setBounds(100, 100, 500, 250);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		
		this.audio = audio;
		this.position = position;
		
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
				}
				
			}
		});
		
		// Create a slider that lets the user enter a time to put the audio file ------------------------
		timePane = new JPanel();
		timeSlider = new JSlider(0, this.audio.getLengthOfVideo() - time, 0);
		
		timePane.add(timeSlider);
		
		optionPane.add(audioLabel, BorderLayout.NORTH);
		optionPane.add(addAtPointer, BorderLayout.SOUTH);
		optionPane.add(timePane, BorderLayout.CENTER);
		
		contentPane.add(optionPane, BorderLayout.CENTER);
		
		addPane = new JPanel();
		
		previewButton = new JButton("Preview");
		previewButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		addPane.add(previewButton);
		
		contentPane.add(addPane, BorderLayout.SOUTH);
		
		// The add button just adjusts the time value associated with th selected audio file
		// User must press merge for changes to take effect
		addButton = new JButton("Add to Video");
		addButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		addButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	audio.editTime(position, timeSlider.getValue());
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
				
				BackgroundPreview makeFile = new BackgroundPreview(audio.getInitialVideoPath(), audio.getAudioPath(position), lf, audio.getInitialVideoName(), timeSlider.getValue(), time);
				makeFile.addReferenceToStart(start);
				makeFile.execute();
		    }

		});
	}
	
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
}
