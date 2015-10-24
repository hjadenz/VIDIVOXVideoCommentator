package backgroundTasks.createFiles;
// This is the logic for creating a file that has the selected video with the soundtrack removed and the selected audio
// added instead

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import video.VIDIVOXstart;
import video.storage.MediaList;

public class CreateVideoFile extends SwingWorker<Void, Integer> {
	private String videoPath;
	private MediaList audioPaths;
	private JFrame load;
	private String videoTitle;
	
	private VIDIVOXstart start;
	
	public CreateVideoFile(String videoPath, MediaList audioPaths, JFrame load, String videoTitle){
		this.videoPath = videoPath;
		this.audioPaths = audioPaths;
		this.load = load;
		this.videoTitle = videoTitle;
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
		
		String audioString = "";
		String filters = "";
		String inputs = "";
		String count = "";
		for (int i = 1; i <= audioPaths.size(); i++) {
			audioString = audioString + " -i " + audioPaths.getAudioPath(i - 1);
			filters = filters + "[" + i + ":a]adelay=" + (audioPaths.getAudioPosition(i-1)*1000+1) + "[auddelay" + i + "];[auddelay" + i + "]volume=" + (audioPaths.getAudioVolume(i-1)/20) + "[aud" + i + "];";
			inputs = inputs + "[aud" + i + "]";
			count = Integer.toString(i + 1);
		}
		
		// Save user's commentary to a temporary file that includes this commentary in the video
		// Note the code for this ffmpeg command was adapted from http://superuser.com/questions/716320/ffmpeg-placing-audio-at-specific-location-with-complex-filters
		cmd = "ffmpeg -y -i " + videoPath + audioString + " -filter_complex \"" + filters + "[0:a]volume=" + (audioPaths.getSoundtrackVolume()/20) + "[aud0];" + inputs  + "[aud0]amix=inputs=" + count + "\" VIDIVOXmedia/.temporary.avi";
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

	/** Remove the .temporary.avi that was created the last time a file was created */
	private void removeTemp() {
		String cmd = "rm VIDIVOXmedia/.temporary.avi";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			builder.start();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	/** Upon completion of the video being created we want the altered video to play */
	@Override
	public void done(){
		//play the video and dispose the load screen
		videoPath = "VIDIVOXmedia/.temporary.avi";
		start.start(videoTitle, videoPath);
		load.dispose();
	}
}
