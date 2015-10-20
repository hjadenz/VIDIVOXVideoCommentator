package video.storage;

/**The Media class is to store any audio files that are added to the video
 * They are stored as the audio's path and the time at which the user wants them added to the video
 * 
 * @author Hannah Sampson
 */

public class Media {
	
	private String audioPath;
	private String time;
	
	public Media(String audioPath, String time) {
		this.audioPath = audioPath;
		this.time = time;
	}
	
	public String getPath() {
		return audioPath;
	}
	public String getTime() {
		return time;
	}
}
