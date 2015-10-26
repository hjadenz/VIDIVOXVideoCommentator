package video.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * This class is a container for any media added to the original video Holds the
 * original name of the video and video path Also all added audio files and
 * their timings are held here
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class MediaList extends ArrayList<Media> {

	private ArrayList<Media> media = this;
	private String initialName;
	private String initialPath;
	private int videoLength;
	private int originalVolume = 50;

	/**
	 * Add the details of the original video to the MediaList component
	 * 
	 * @param videoName
	 * @param videoPath
	 */
	public void addInitial(String videoName, String videoPath) {
		this.initialName = videoName;
		this.initialPath = videoPath;
		this.videoLength = calculateLengthOfVideo();
	}

	// Return details of the original video
	public String getInitialVideoPath() {
		return initialPath;
	}

	public String getInitialVideoName() {
		return initialName;
	}

	public int getSoundtrackVolume() {
		return originalVolume;
	}

	// Return the time and path associated with a particular added audio
	public String getAudioPath(int order) {
		return media.get(order).getPath();
	}

	public int getAudioPosition(int order) {
		return media.get(order).getTime();
	}

	public String getAudioName(int order) {
		return media.get(order).getName();
	}

	public int getAudioVolume(int order) {
		return media.get(order).getVolume();
	}

	public int getLengthOfVideo() {
		return videoLength;
	}

	/**
	 * This method uses linux BASH functionality to get the length of a video
	 * file
	 */
	public int calculateLengthOfVideo() {
		String cmd = "ffprobe " + this.getInitialVideoPath()
				+ " -show_format 2>&1 | sed -n 's/duration=//p' ";
		String line = null;
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();

			InputStream stdout = process.getInputStream();
			BufferedReader stdoutBuffered = new BufferedReader(
					new InputStreamReader(stdout));
			line = stdoutBuffered.readLine();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return (int) Double.parseDouble(line);
	}

	/**
	 * Edit the time value associated with a particular media object This is
	 * implemented so that the user can edit where a particular audio is placed
	 * in the video even after they have added it to the video
	 */
	public void editTime(int position, int time) {
		media.get(position).setTime(time);
	}

	public void editSoundtrackVolume(int volume) {
		originalVolume = volume;
	}
}
