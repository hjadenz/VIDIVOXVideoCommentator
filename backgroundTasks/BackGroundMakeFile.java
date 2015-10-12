package backgroundTasks;
// This is the logic for creating a file that has the selected video with the soundtrack removed and the selected audio
// added instead

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import video.StartPage;

public class BackGroundMakeFile extends SwingWorker<Void, Void> {
	private String videoPath;
	private String audioPath;
	private JFrame load;
	private String videoTitle;
	private String minutes;
	private String seconds;
	
	public BackGroundMakeFile(String videoPath, String audioPath, JFrame load, String videoTitle, String minutes, String seconds){
		this.videoPath = videoPath;
		this.audioPath = audioPath;
		this.load = load;
		this.videoTitle = videoTitle;
		this.minutes = minutes;
		this.seconds = seconds;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		String cmd;
		ProcessBuilder builder;
		// Start by deleting the temporary file used last time
		removeTemp();
		// Save user's commentary to a temporary file that includes this commentary in the video
		cmd = "ffmpeg -ss 00:" + minutes + ":" + seconds + " -t 10 -i " + videoPath + " -i " + audioPath + " -map 0:v -map 1:a VIDIVOXmedia/.temporary.avi";
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
		String cmd = "rm VIDIVOXmedia/.temporary.avi";
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
		videoPath = "VIDIVOXmedia/.temporary.avi";
		StartPage.start(videoTitle, videoPath);
		load.dispose();
	}
}
