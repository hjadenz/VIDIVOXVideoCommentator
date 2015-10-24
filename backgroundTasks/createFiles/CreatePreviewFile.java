package backgroundTasks.createFiles;

import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

import video.VIDIVOXstart;

/** This class creates a small preview of the audio that the user want to add to the selected video
 * 
 *  It creates a temporary file that starts at the time the user has selected to add the audio, and
 *  just plays for the length of the audio
 * 
 * @author Hannah Sampson
 */

public class CreatePreviewFile extends SwingWorker<Void, Integer> {
	private String videoPath;
	private String audioPath;
	private JFrame load;
	private String videoTitle;
	private String hours;
	private String minutes;
	private String seconds;
	private int time;
	
	private VIDIVOXstart start;
	
	public CreatePreviewFile(String videoPath, String audioPath, JFrame load, String videoTitle, int position, int time){
		this.videoPath = videoPath;
		this.audioPath = audioPath;
		this.load = load;
		this.videoTitle = videoTitle;
		
		if (time < 1) {
			this.time = 1;
		} else {
			this.time = time;
		}
		
		if (((position/60)/60)%60 < 10) {
			this.hours = "0" + Integer.toString(((position/60)/60)%60);
		} else {
			this.hours = Integer.toString(((position/60)/60)%60);
		}
		
		if ((position/60)%60 < 10) {
			this.minutes = "0" + Integer.toString((position/60)%60);
		} else {
			this.minutes = Integer.toString((position/60)%60);
		}
		
		if (position%60 < 10) {
			this.seconds = "0" + Integer.toString(position%60);
		} else {
			this.seconds = Integer.toString(position%60);
		}
	}
	
	public void addReferenceToStart(VIDIVOXstart start) {
		this.start = start;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		String cmd;
		ProcessBuilder builder;
		// Start by deleting the temporary file used last time
		removeTemp();
		// Save user's commentary to a temporary file that includes this commentary in the video
		cmd = "ffmpeg -ss " + hours + ":" + minutes + ":" + seconds + " -t " + time + " -i " + videoPath + " -i " + audioPath + " -map 0:v -map 1:a VIDIVOXmedia/.preview.avi";
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		
		try {		
			Process process = builder.start();
			process.waitFor();
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
			load.dispose();
		}
		
		return null;
	}
	
	/** Upon completion, run the created video */
	@Override
	public void done(){
		//play the video and dispose the load screen
		videoPath = "VIDIVOXmedia/.preview.avi";
		start.start(videoTitle, videoPath);
		load.dispose();
	}
	
	/** Remove that previous temporary file (.preview.avi) that was created the last time this class was
	 *  executed
	 */
	private void removeTemp() {
		String cmd = "rm VIDIVOXmedia/.preview.avi";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			builder.start();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
}
