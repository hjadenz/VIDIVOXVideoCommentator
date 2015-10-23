package audio;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import backgroundTasks.BackgroundSaveAudio;

import audio.addToVideo.AddAudio;
import audio.create.SavePage;

import video.SaveVideo;

public class NameTakenPage extends JFrame {

	private JPanel contentPane;
	private JFrame frame = this;
	private boolean saveAudio;

	/**
	 * Create the frame.
	 */
	public NameTakenPage(final String filename, final String input,final boolean saveAudio, final AddAudio audio, final double speed) {
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
		Font font = new Font("Tahoma", Font.BOLD, 14);
		JLabel title = new JLabel(filename +" already exists.");
		title.setFont(font);
		title.setBounds(20, 20, 300, 30);
		contentPane.add(title);
		
		JLabel limit = new JLabel("Would you like to overwrite the file or rename?");
		limit.setFont(new Font("Tahoma", Font.BOLD, 10));
		limit.setBounds(15, 60, 270, 30);
		contentPane.add(limit);
		
		// If overwrite, delete the existing file and save the new file
		JButton confirm = new JButton("Overwrite");
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//specify whether a video or an audio
				String mediaTypeExt;
				mediaTypeExt = (saveAudio) ? ".mp3" : ".avi";
				
				String cmd = "rm VIDIVOXmedia/" + filename + mediaTypeExt;
				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
				try {
					builder.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//saving audio and videos are done differently, call the desired object depending on
				//whether it's video or audio
				if(saveAudio){
					BackgroundSaveAudio saveAudio = new BackgroundSaveAudio(input, filename, speed, audio);
					saveAudio.execute();
				}else{
					SaveVideo save = new SaveVideo(filename);
					save.saveVideo();
					JOptionPane.showMessageDialog(frame, "The file " + filename + mediaTypeExt+" was saved successfully");
				}
					
				frame.dispose();
			}
		});
		confirm.setFont(new Font("Tahoma", Font.BOLD, 10));
		confirm.setBounds(15, 150, 120, 30);
		contentPane.add(confirm);
		
		// Rename takes you back to the previous screen to save your text again
		JButton cancel = new JButton("Rename");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//creates the SavePage depending if video or audio
				SavePage save = new SavePage(input,saveAudio, audio, speed);
				save.setVisible(true);
				frame.dispose();
			}
		});
		cancel.setFont(new Font("Tahoma", Font.BOLD, 10));
		cancel.setBounds(145, 150, 120, 30);
		contentPane.add(cancel);
	}

}