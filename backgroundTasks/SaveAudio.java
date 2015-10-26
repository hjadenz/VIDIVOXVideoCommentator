package backgroundtasks;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import audio.add_to_video.AddAudioToVideo;

/**
 * This class saves the commentary file that the user creates using a new thread
 * that the main thread executes. It save the text as a .wav format, then
 * converts the .wav to a .mp3 file, and deletes the superfluous .wav file.
 * 
 * @author: Hannah Sampson
 */

public class SaveAudio extends SwingWorker<Void, Void> {
	private String inputText;
	private String filename;
	private double speed;
	private AddAudioToVideo audio;

	public SaveAudio(String inputText, String filename, double speed,
			AddAudioToVideo audio) {
		this.inputText = inputText;
		this.filename = filename;
		this.speed = speed;
		this.audio = audio;
	}

	@Override
	protected Void doInBackground() throws Exception {
		// Take text and turn it into a .wav file using text2wave
		String cmd = "echo \"" + inputText + "\" | text2wave -o VIDIVOXmedia/"
				+ filename + ".wav -eval \"(Parameter.set \'Duration_Stretch "
				+ speed + ")\"";

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();
			// Turn the .wav file into a .mp3 file using ffmpeg
			cmd = "ffmpeg -i VIDIVOXmedia/" + filename
					+ ".wav -f mp3 VIDIVOXmedia/" + filename + ".mp3";
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			process = builder.start();
			process.waitFor();
			// Then delete the existing .wav file as this is no longer needed
			cmd = "rm VIDIVOXmedia/" + filename + ".wav";
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			builder.start();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method lets the user know whether or not the audio file saved
	 * successfully
	 */
	protected void done() {
		String cmd = "[ -e VIDIVOXmedia/" + filename + ".mp3 ]";
		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process;

			process = builder.start();

			process.waitFor();

			// if exists corresponding error message comes up
			if (process.exitValue() == 0) {
				JOptionPane.showMessageDialog(null, "The file " + filename
						+ ".mp3 was saved successfully");
				// If the audio was saved successfully give the name back,
				// otherwise do nothing

				// Check whether the audio being saved has been called from the
				// main frame or from an AddAudio page (AddAudio will require a
				// reference to the chosen audio's path sent back)
				if (audio != null) {
					audio.copyPath("VIDIVOXmedia/" + filename + ".mp3",
							filename + ".mp3");
					audio.audioFileTime();
				}
			} else {
				JOptionPane.showMessageDialog(null, "Error creating "
						+ filename + ".mp3");
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}