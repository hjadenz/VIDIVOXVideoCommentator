package audio.create;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import backgroundTasks.SaveAudio;
import backgroundTasks.SaveVideo;

import audio.NameTaken;
import audio.addToVideo.AddAudioToVideo;

import video.VIDIVOXstart;

/** This class is a frame that allows the user to choose a filename for the audio or video that 
 *  they are trying to save
 *  
 *  It checks whether the file exists, and if it does, asks the user if they want to rename or override
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class SaveAudioOrVideo extends JFrame {

	private JPanel contentPane;
	private JFrame frame = this;
	private boolean saveAudio;
	private AddAudioToVideo audio;
	private double speed;
	private String inputText;
	private VIDIVOXstart start;

	/**
	 * Create the frame.
	 */
	public SaveAudioOrVideo(VIDIVOXstart start, String inputText, boolean saveAudio, AddAudioToVideo audio, double speed) {
		
		this.audio = audio;
		this.saveAudio = saveAudio;
		this.speed = speed;
		this.inputText = inputText;
		this.start = start;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setBounds(100, 100, 300, 250);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		setUp();
	}
	
	/** Check whether the file you were trying to create already exists
	 *  if it does, prompt the user to select whether they want to rename the file, or overwrite the
	 *  existing one.
	 * 
	 * @param filename
	 * @param inputText
	 */
	private void doesFileExist(String filename, String inputText) {
		//figure out extension, depending on if audio or video
		String mediaTypeExt;
		mediaTypeExt = (saveAudio) ? ".mp3" : ".avi";
		
		//check if file exists
		String cmd = "[ -e VIDIVOXmedia/" + filename + mediaTypeExt+" ]";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();
			//if taken, prompt the user to overwrite or not
			if (process.exitValue() == 0) {
				NameTaken taken = new NameTaken(filename, inputText,saveAudio, audio, speed, start);
				taken.setVisible(true);
				frame.dispose();
			//filename is good, go and save the media
			} else {
				if(saveAudio){
					SaveAudio saveAudio = new SaveAudio(inputText, filename, speed, audio);
					saveAudio.execute();
				}else{
					SaveVideo save = new SaveVideo(filename, start);
					save.execute();
				}
				frame.dispose();
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// --------------------------------------------------------------------------------------------------
	// This method sets up the frame
	// --------------------------------------------------------------------------------------------------
	
	private void setUp() {
		String mediaType;
		mediaType = (saveAudio) ? "Audio" : "Video";
		
		JLabel title = new JLabel("Please give this "+mediaType+" file a name:");
		title.setFont(new Font("Tahoma", Font.BOLD, 14));
		title.setBounds(15, 20, 280, 30);
		contentPane.add(title);
		
		JLabel limit = new JLabel("Please use only a-z, A-Z and 0-9 to name");
		limit.setFont(new Font("Tahoma", Font.BOLD, 10));
		limit.setBounds(15, 40, 270, 30);
		contentPane.add(limit);
		
		JLabel limit2 = new JLabel("No special characters (e.g. .,/!?)");
		limit2.setFont(new Font("Tahoma", Font.BOLD, 10));
		limit2.setBounds(15, 60, 270, 30);
		contentPane.add(limit2);
		
		final JTextField text = new JTextField();
		text.setBounds(25, 100, 230, 30);
		contentPane.add(text);
		text.setColumns(10);
		
		text.setText("My"+mediaType+"File");
		
		// If the user confirms their name choice, and is made up only of digits or characters:
		JButton confirm = new JButton("Confirm");
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Check if the file exits and try to save it
				String filename = text.getText();
				if (filename.matches("[a-zA-Z0-9]+")) {
					doesFileExist(filename, inputText);
				}else{
					JOptionPane.showMessageDialog(null, "Please enter a-z or 0-9 characters only.");
				}
			}
		});
		confirm.setFont(new Font("Tahoma", Font.BOLD, 10));
		confirm.setBounds(15, 150, 120, 30);
		contentPane.add(confirm);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		cancel.setFont(new Font("Tahoma", Font.BOLD, 10));
		cancel.setBounds(145, 150, 120, 30);
		contentPane.add(cancel);
	}

}