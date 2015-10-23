package backgroundTasks;

import javax.swing.SwingWorker;

/** This class updates the slider contained on the start page (connected to the video player) and
 *  constantly checks (on a different thread) whether the slider need to be updated
 *  
 *  @author Hannah Sampson
 */

import video.VIDIVOXstart;

public class UpdateSlider extends SwingWorker<Void, Void> {
	
	private VIDIVOXstart start;
	
	public void addReferenceToStart(VIDIVOXstart start) {
		this.start = start;
	}

	/** While the updating of the slider hasn't been cancelled, constantly check whether or not the 
	 * slider (and associated labels) need updating
	 */
	@Override
	protected Void doInBackground() throws Exception {
		while (!isCancelled()) {
			start.updateSlider();
		}
		return null;
	}

}
