package audio;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import backgroundTasks.BackGroundMakeFile;

import chooseFiles.FileChooser;

import video.StartPage;

@SuppressWarnings("serial")
public class AddAudio extends JFrame {
	
	private AddAudio frame = this;
	private JPanel contentPane;
	private JPanel buttonPane;
	private JPanel optionPane;
	private JPanel timePane;
	
	private JButton newCommentaryButton;
	private JButton addAudioButton;
	
	private JButton previewButton;
	private String audioPath;
	
	private JLabel audioLabel;
	private JCheckBox addAtPointer;
	private JTextField minutes;
	private JTextField seconds;

	/**
	 * Create the frame.
	 */
	public AddAudio() {
		setBounds(100, 100, 500, 250);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		
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
/*		addAtPointer = new JCheckBox("Add Audio at Pointer");
		addAtPointer.setHorizontalAlignment(SwingConstants.CENTER);
		addAtPointer.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				toggleTimeEnabled();
			}

			private void toggleTimeEnabled() {
				if (minutes.isEnabled()) {
					minutes.setEnabled(false);
					seconds.setEnabled(false);
				} else {
					minutes.setEnabled(true);
					seconds.setEnabled(true);
				}
				
			}
		});
*/		
		timePane = new JPanel();
		minutes = new JTextField("00");
		seconds = new JTextField("00");
		
		timePane.add(minutes);
		timePane.add(new JLabel(":"));
		timePane.add(seconds);
		
		optionPane.add(audioLabel, BorderLayout.NORTH);
//		optionPane.add(addAtPointer, BorderLayout.SOUTH);
		optionPane.add(timePane, BorderLayout.CENTER);
		
		contentPane.add(optionPane, BorderLayout.CENTER);
		
		previewButton = new JButton("Preview");
		previewButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		previewButton.setEnabled(false);
		contentPane.add(previewButton, BorderLayout.SOUTH);
		
		// Add file chooser to select audio
		addAudioButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	FileChooser chooseAudio = new FileChooser(false);
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
				
				BackGroundMakeFile makeFile = new BackGroundMakeFile(StartPage.getVideoPath(), audioPath, lf, StartPage.getVideoTitle(), minutes.getText(), seconds.getText());
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
		} else {
			previewButton.setEnabled(true);
		}
		
	}
}
