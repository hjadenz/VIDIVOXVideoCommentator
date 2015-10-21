package audio;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import backgroundTasks.BackgroundPreview;

import chooseFiles.FileChooser;

import video.StartPage;
import video.storage.Media;

@SuppressWarnings("serial")
public class AddAudio extends JFrame {
	
	private AddAudio frame = this;
	private JPanel contentPane;
	private JPanel buttonPane;
	private JPanel optionPane;
	private JPanel timePane;
	private JPanel addPane;
	
	private JButton newCommentaryButton;
	private JButton addAudioButton;
	
	private JButton previewButton;
	private String audioPath;
	private String audioName;
	private JButton addButton;
	private JButton cancelButton;
	
	private JLabel audioLabel;
	private JCheckBox addAtPointer;
	
	private StartPage start;
	private JSlider timeSlider;

	/**
	 * Create the frame.
	 */
	public AddAudio(final StartPage start) {
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
				}
				
			}
		});
		
		// Create a slider that lets the user enter a time to put the audio file ------------------------
		timePane = new JPanel();
		timeSlider = new JSlider(0, this.start.getLengthOfVideo(), 0);
		
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
		    	Media m = new Media(audioPath, timeSlider.getValue(), audioLabel.getText());
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
		
		// Add file chooser to select audio
		addAudioButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	FileChooser chooseAudio = new FileChooser();
		    	chooseAudio.addReferenceToStart(start);
		    	chooseAudio.showFileChooser(false);
		    	chooseAudio.setAudioPath(frame);
		    }
		});
		
		// Add file chooser to select audio
		newCommentaryButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	AudioPage audio = new AudioPage(frame);
		    	audio.setVisible(true);
		    }
		});
		
		previewButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {	
				//show loading screen so user knows something is happening
				LoadingFrame lf  = new LoadingFrame();
				lf.setVisible(true);
				
				//if the user has selected commentary to save
				//go into background task to create it
				AudioFile audioFile = null;
				try {
					audioFile = AudioFileIO.read(new File(audioPath));
				} catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int time = audioFile.getAudioHeader().getTrackLength();
				
				BackgroundPreview makeFile = new BackgroundPreview(start.getOriginalVideoPath(), audioPath, lf, start.getVideoTitle(), timeSlider.getValue(), time);
				makeFile.addReferenceToStart(start);
				makeFile.execute();
		    }

		});
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
}
