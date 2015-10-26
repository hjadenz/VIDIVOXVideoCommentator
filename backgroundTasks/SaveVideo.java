package backgroundtasks;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import video.VIDIVOXstart;

/**
 * This class copies the temporary file that has the selected video and the
 * changes that have been made to it into a file that the user names
 * 
 * @author Hannah Sampson
 */

public class SaveVideo extends SwingWorker<Void, Void> {
	private String filename;
	private VIDIVOXstart start;

	public SaveVideo(String newfilename, VIDIVOXstart start) {
		this.filename = newfilename;
		this.start = start;
	}

	@Override
	protected Void doInBackground() throws Exception {
		String cmd = "cp VIDIVOXmedia/.temporary.avi " + "VIDIVOXmedia/"
				+ filename + ".avi";
		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	/** Check whether the file has been saved properly and let the user know */
	protected void done() {
		String cmd = "[ -e VIDIVOXmedia/" + filename + ".avi ]";
		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process;
			process = builder.start();
			process.waitFor();

			// If the file exists give the user a message that informs them of
			// this, otherwise show an error. If the file saves, set the main
			// page to recognise that the video has been saved since the last
			// changes were made to it
			if (process.exitValue() == 0) {
				JOptionPane.showMessageDialog(null, "The file " + filename
						+ ".avi was saved successfully");

				// If no start is passed through, this means that it is coming
				// from the user selecting to save on exiting the frame. Hence
				// if the video has been saved successfully, the application can
				// be exited
				if (start == null) {
					System.exit(0);
				} else {
					start.setSaved(true);
				}
			} else {
				JOptionPane.showMessageDialog(null, "Error creating "
						+ filename + ".avi. Try Again");
				// If no start is passed through, this means that it is coming
				// from the user selecting to save on exiting the frame. On not
				// saving successfully, we just want to show the user that it
				// didn't save successfully, and give them the chance to try
				// again
				if (start != null) {
					start.setSaved(false);
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
