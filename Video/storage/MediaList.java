package video.storage;

import java.util.ArrayList;

/** This class is a container for any media added to the original video
 * 		Holds the original name of the video and video path
 * 		Also all added audio files and their timings are held here
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class MediaList extends ArrayList<Media> {
	
	private ArrayList<Media> media = this;
	private String initialName;
	private String initialPath;
	
	/**Add the details of the original video to the MediaList component
	 * 
	 * @param videoName
	 * @param videoPath
	 */
	public void addInitial(String videoName, String videoPath) {
		this.initialName = videoName;
		this.initialPath = videoPath;
	}

	// Return details of the original video
	public String getInitialVideoPath() {
		return initialPath;
	}
	public String getInitialVideoName() {
		return initialName;
	}
	
	// Return the time and path associated with a particular added audio
	public String getAudioPath(int order) {
		return media.get(order).getPath();
	}
	public int getAudioPosition(int order) {
		return media.get(order).getTime();
	}
}
