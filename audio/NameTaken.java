package audio;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import video.VIDIVOXstart;

import backgroundtasks.SaveAudio;
import backgroundtasks.SaveVideo;

import audio.add_to_video.AddAudioToVideo;
import audio.create.SaveAudioOrVideo;

/**
 * NameTaken is a frame that comes up when the user tries to save a file using
 * the name of a file that already exists in the VIDIVOXmedia folder.
 * 
 * This frame prompts the user to rename the file they're are trying to save, or
 * to overwrite the existing file
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class NameTaken extends JFrame {

	private JPanel contentPanel = new JPanel();
	private JPanel titlePanel = new JPanel();
	private JPanel buttonPanel = new JPanel();

	private JFrame frame = this;

	private boolean saveAudio;
	private String filename;
	private String input;
	private AddAudioToVideo audio;
	private double speed;
	private VIDIVOXstart start;

	public NameTaken(String filename, String input, boolean saveAudio,
			AddAudioToVideo audio, double speed, VIDIVOXstart start) {

		this.saveAudio = saveAudio;
		this.filename = filename;
		this.input = input;
		this.audio = audio;
		this.speed = speed;
		this.start = start;

		setBounds(100, 100, 300, 250);
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout());

		createTitle();
		createButtons();
	}

	// --------------------------------------------------------------------------------------------------
	// Set up the components of the frame
	// --------------------------------------------------------------------------------------------------

	private void createTitle() {
		titlePanel.setLayout(new BorderLayout());

		JLabel title = new JLabel(filename + " already exists.");
		title.setFont(new Font("Tahoma", Font.BOLD, 14));
		titlePanel.add(title, BorderLayout.NORTH);

		JLabel limit = new JLabel(
				"Would you like to overwrite the file or rename?");
		limit.setFont(new Font("Tahoma", Font.BOLD, 10));
		titlePanel.add(limit, BorderLayout.SOUTH);

		contentPanel.add(titlePanel, BorderLayout.CENTER);
	}

	private void createButtons() {
		// If overwrite, delete the existing file and save the new file
		JButton confirm = new JButton("Overwrite");
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// specify whether a video or an audio
				String mediaTypeExt;
				mediaTypeExt = (saveAudio) ? ".mp3" : ".avi";

				String cmd = "rm VIDIVOXmedia/" + filename + mediaTypeExt;
				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c",
						cmd);
				try {
					builder.start();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// saving audio and videos are done differently, call the
				// desired object depending on
				// whether it's video or audio
				if (saveAudio) {
					SaveAudio saveAudio = new SaveAudio(input, filename, speed,
							audio);
					saveAudio.execute();
				} else {
					SaveVideo saveVideo = new SaveVideo(filename, start);
					saveVideo.execute();
				}

				frame.dispose();
			}
		});
		confirm.setFont(new Font("Tahoma", Font.BOLD, 10));
		buttonPanel.add(confirm);

		// Rename takes you back to the previous screen to save your text again
		JButton cancel = new JButton("Rename");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// creates the SavePage depending if video or audio
				SaveAudioOrVideo save = new SaveAudioOrVideo(start, input,
						saveAudio, audio, speed);
				save.setVisible(true);
				frame.dispose();
			}
		});
		cancel.setFont(new Font("Tahoma", Font.BOLD, 10));
		cancel.setBounds(145, 150, 120, 30);
		buttonPanel.add(cancel);

		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
	}
}