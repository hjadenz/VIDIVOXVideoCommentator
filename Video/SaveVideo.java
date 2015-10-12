package video;

import java.io.IOException;

public class SaveVideo {
	private String filename;
	
	public SaveVideo(String newfilename){
		this.filename = newfilename;
	}
	
	public void saveVideo(){
		//copy the temporary video file to the file the user specified
		
		String cmd = "cp VIDIVOXmedia/.temporary.avi " + "VIDIVOXmedia/"+ filename + ".avi";
		
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
