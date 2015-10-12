package audio;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import video.SaveVideo;
import video.StartPage;

public class SavePage extends JFrame {

	private JPanel contentPane;
	private JFrame frame = this;
	private boolean saveAudio;
	private AddAudio audio;

	/**
	 * Create the frame.
	 */
	public SavePage(final String inputText, final boolean saveAudio, AddAudio audio) {
		
		this.audio = audio;
		this.saveAudio = saveAudio;
		
		if(saveAudio){
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}else{
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		setBounds(100, 100, 300, 250);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
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
		text.setDocument(new JTextFieldLimit(50));
		text.setText("My"+mediaType+"File");
		
		JButton confirm = new JButton("Confirm");
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
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
				NameTakenPage taken = new NameTakenPage(filename, inputText,saveAudio, audio);
				taken.setVisible(true);
				frame.dispose();
			//filename is good, go and save the media
			} else {
				if(saveAudio){
					SaveAudio save = new SaveAudio(filename, inputText);
					save.saveFile(audio);

				}else{
					SaveVideo save = new SaveVideo(filename);
					save.saveVideo();
					JOptionPane.showMessageDialog(frame, "The file " + filename + mediaTypeExt+" was saved successfully");
				}

				frame.dispose();
				
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}