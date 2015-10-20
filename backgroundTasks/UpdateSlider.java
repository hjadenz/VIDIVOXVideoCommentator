package backgroundTasks;

import javax.swing.SwingWorker;

import video.StartPage;

public class UpdateSlider extends SwingWorker<Void, Void> {
	
	private StartPage start;
	
	public void addReferenceToStart(StartPage start) {
		this.start = start;
	}

	@Override
	protected Void doInBackground() throws Exception {
		while (!isCancelled()) {
			start.updateSlider();
		}
		return null;
	}

}
