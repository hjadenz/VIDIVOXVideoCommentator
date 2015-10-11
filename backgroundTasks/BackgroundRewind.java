package backgroundTasks;

import javax.swing.SwingWorker;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import video.StartPage;


/**
 * This class rewinds the video and implements SwingWorker so that the GUI doesn't freeze
 *
 */

public class BackgroundRewind extends SwingWorker<Void, Void> {
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private long time;
	private StartPage startPage;
	private boolean isMuted;
	
	public BackgroundRewind(StartPage startPage, EmbeddedMediaPlayerComponent mediaPlayerComponent,long time, boolean isMuted){
		this.startPage = startPage;
		this.mediaPlayerComponent = mediaPlayerComponent;
		this.time = time/1000;
		this.isMuted = isMuted;
	}
	
	
	@Override
	protected Void doInBackground() {
		// Set the audio to be muted while rewinding
		mediaPlayerComponent.getMediaPlayer().mute(true);
		
			while(time > 0){
				if(!isCancelled()){
					time = (mediaPlayerComponent.getMediaPlayer().getTime())/1000;
					mediaPlayerComponent.getMediaPlayer().skip(-10);
				}else{
					break;
				}
			}
			//set the buttons to display the correct images
			//and set booleans to the correct value
			startPage.setPlayBtnText("Pause");
			startPage.setIsRewinding(false);
			// If the video was muted before the user pressed rewind the audio will still be muted when we play again
			// Otherwise this turns the audio back on
			if (isMuted == false) {
				mediaPlayerComponent.getMediaPlayer().mute(false);
			}
		
		return null;
	}
}
