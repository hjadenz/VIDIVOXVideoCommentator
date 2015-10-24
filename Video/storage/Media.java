package video.storage;

/**The Media class is to store any audio files that are added to the video
 * They are stored as the audio's path and the time at which the user wants them added to the video
 * 
 * @author Hannah Sampson
 */

public class Media {
	
	private String audioPath;
	private int time;
	private String audioName;
	private int volume;
	
	public Media(String audioPath, int time, String audioName, int volume) {
		this.audioPath = audioPath;
		this.time = time;
		this.audioName = audioName;
		this.volume = volume;
	}
	
	// Return values associated with media file
	public String getPath() {
		return audioPath;
	}
	public int getTime() {
		return time;
	}
	public String getName() {
		return audioName;
	}
	public int getVolume() {
		return volume;
	}

	// Set values associated with media file (used when editing added audio)
	public void setTime(int time) {
		this.time = time;		
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
}
