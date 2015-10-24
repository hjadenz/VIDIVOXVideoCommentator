package backgroundTasks.videoControl;

import javax.swing.SwingWorker;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/**
 * This class fastforwards the video without freezing the GUI, using SwingWorker
 *
 */
public class BackgroundForward extends SwingWorker<Void, Void> {
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	long time;
	private boolean isMuted;
	
	public BackgroundForward(EmbeddedMediaPlayerComponent mediaPlayerComponent,long time, boolean isMuted){
		this.mediaPlayerComponent = mediaPlayerComponent;
		this.time = time/1000;
		this.isMuted = isMuted;
	}
	
	@Override
	protected Void doInBackground() {
		long length = (mediaPlayerComponent.getMediaPlayer().getLength())/1000;
		// Set the video to mute while fastforwarding
		mediaPlayerComponent.getMediaPlayer().mute(true);
		
			while(time < length){
				if(!isCancelled()){
					time = (mediaPlayerComponent.getMediaPlayer().getTime())/1000;
					mediaPlayerComponent.getMediaPlayer().skip(10);
				}else{
					break;
				}
			}
			// If the video was muted before the user starting fastforwarding: keep the video muted upon resuming
			// Otherwise we want to unmute
			if (isMuted == false) {
				mediaPlayerComponent.getMediaPlayer().mute(false);
			}
		
		return null;
	}
}
