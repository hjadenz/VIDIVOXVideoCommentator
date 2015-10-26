package backgroundtasks.video_control;

import javax.swing.SwingWorker;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/**
 * This class fastforwards the video without freezing the GUI, using SwingWorker
 * 
 * @author Hannah Sampson
 */
public class BackgroundForward extends SwingWorker<Void, Void> {
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	long time;
	private boolean isMuted;

	public BackgroundForward(EmbeddedMediaPlayerComponent mediaPlayerComponent,
			long time, boolean isMuted) {
		this.mediaPlayerComponent = mediaPlayerComponent;
		this.time = time / 1000;
		this.isMuted = isMuted;
	}

	@Override
	protected Void doInBackground() {
		long length = (mediaPlayerComponent.getMediaPlayer().getLength()) / 1000;
		// Set the video to mute while fastforwarding
		mediaPlayerComponent.getMediaPlayer().mute(true);

		// While there is still time left in the video:
		while (time < length) {
			// As long as the play button (or the rewind button) hasn't been
			// pressed the video will continue to fastforward
			if (!isCancelled()) {
				time = (mediaPlayerComponent.getMediaPlayer().getTime()) / 1000;
				mediaPlayerComponent.getMediaPlayer().skip(10);
			} else {
				break;
			}
		}
		// If the video was muted before the user starting fastforwarding: keep
		// the video muted upon resuming otherwise we want to unmute
		if (isMuted == false) {
			mediaPlayerComponent.getMediaPlayer().mute(false);
		}

		return null;
	}
}
