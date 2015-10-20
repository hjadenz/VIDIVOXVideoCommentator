package backgroundTasks;
// This is the logic for creating a file that has the selected video with the soundtrack removed and the selected audio
// added instead

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import video.StartPage;

public class BackgroundPreview extends SwingWorker<Void, Void> {
	private String videoPath;
	private String audioPath;
	private JFrame load;
	private String videoTitle;
	private String hours;
	private String minutes;
	private String seconds;
	private int time;
	
	private StartPage start;
	
	public BackgroundPreview(String videoPath, String audioPath, JFrame load, String videoTitle, int position, int time){
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
	
	public void addReferenceToStart(StartPage start) {
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
		}
		
		return null;
	}
	
	private void removeTemp() {
		String cmd = "rm VIDIVOXmedia/.preview.avi";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			builder.start();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	// Upon completion we want the altered video to play
	public void done(){
		//play the video and dispose the load screen
		videoPath = "VIDIVOXmedia/.preview.avi";
		start.start(videoTitle, videoPath);
		load.dispose();
	}
}
