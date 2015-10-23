package time;

import javax.swing.JSlider;

/**This class is a type of slider that reacts to how long the video being played is. 
 * 
 * It holds its own information on the length of the video playing, as well as the length of the audio
 * that is being viewed (if applicable)
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class PositionSlider extends JSlider {

	private int lengthOfVideo;
	
	// Initially the slider will be set to all zeros until a video is added
	public PositionSlider() {
		this.setMaximum(0);
		this.setMinimum(0);
	}
	
	// Setter methods 
	public void setVideoLength(int totalTime) {
		this.lengthOfVideo = totalTime;
		this.setMaximum(totalTime);
	}
	public void setAudioLength(int totalTime) {
		this.setMaximum(lengthOfVideo - totalTime);
	}
}
