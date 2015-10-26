package backgroundtasks.create_files;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import video.VIDIVOXstart;
import video.storage.MediaList;

/**
 * This class uses the audio files associated with the selected video file to
 * build up a BASH command that mixes all these inputs according to the users
 * specifications
 * 
 * It then executes this BASH command (all done as a separate thread to maintain
 * usability) to create a new temporary file that stores the edited video, and
 * then plays this edited video to the user
 * 
 * @author Hannah Sampson
 */

public class CreateVideoFile extends SwingWorker<Void, Integer> {
	private String videoPath;
	private MediaList audioPaths;
	private JFrame load;
	private String videoTitle;

	private VIDIVOXstart start;

	public CreateVideoFile(String videoPath, MediaList audioPaths, JFrame load,
			String videoTitle) {
		this.videoPath = videoPath;
		this.audioPaths = audioPaths;
		this.load = load;
		this.videoTitle = videoTitle;
	}

	public void addReferenceToStart(VIDIVOXstart start) {
		this.start = start;
	}

	@Override
	protected Void doInBackground() throws Exception {
		String cmd;
		ProcessBuilder builder;
		// Start by deleting the temporary file used last time
		removeTemp();

		// Initialise the strings
		String audioString = "";
		String filters = "";
		String inputs = "";
		String count = "";
		// For every audio file associated with the selected video file, cycle
		// through and add to the string, so that each audio file is added,
		// delayed to the position the user chose before, and has its volume
		// altered to whatever has been set for it
		for (int i = 1; i <= audioPaths.size(); i++) {
			// Add audio file path
			audioString = audioString + " -i \'" + audioPaths.getAudioPath(i - 1) + "\'";
			// Add the delay and volume change
			filters = filters + "[" + i + ":a]adelay="
					+ (audioPaths.getAudioPosition(i - 1) * 1000 + 1)
					+ "[auddelay" + i + "];[auddelay" + i + "]volume="
					+ (audioPaths.getAudioVolume(i - 1) / 20) + "[aud" + i
					+ "];";
			// Make sure that ffmpeg knows which audios to merge
			inputs = inputs + "[aud" + i + "]";
			count = Integer.toString(i + 1);
		}

		// Save user's commentary to a temporary file that includes this
		// commentary in the video
		
		// Note the code for this ffmpeg command was adapted from
		// http://superuser.com/questions/716320/ffmpeg-placing-audio-at-specific-location-with-complex-filters
		cmd = "ffmpeg -y -i \'" + videoPath + "\'" + audioString + " -filter_complex \""
				+ filters + "[0:a]volume="
				+ (audioPaths.getSoundtrackVolume() / 20) + "[aud0];" + inputs
				+ "[aud0]amix=inputs=" + count
				+ "\" VIDIVOXmedia/.temporary.avi";
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);

		try {
			Process process = builder.start();
			process.waitFor();
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
			load.dispose();
		}
		return null;
	}

	/**
	 * Remove the .temporary.avi that was created the last time a file was
	 * created
	 */
	private void removeTemp() {
		String cmd = "rm VIDIVOXmedia/.temporary.avi";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			builder.start();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * Upon completion of the video being created we want the altered video to
	 * play
	 */
	@Override
	public void done() {
		// play the video and dispose the load screen
		videoPath = "VIDIVOXmedia/.temporary.avi";
		start.start(videoTitle, videoPath);
		load.dispose();
	}
}
