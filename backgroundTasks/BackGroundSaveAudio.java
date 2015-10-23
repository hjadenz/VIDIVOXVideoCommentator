package backgroundTasks;

/**This class saves the commentary file that the user creates using a new thread that the main thread
 * executes. It save the text as a .wav format, then converts the .wav to a .mp3 file, and deletes the
 * superfluous .wav file. 
 * 
 * @author: Hannah Sampson
 */

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class BackgroundSaveAudio extends SwingWorker<Void,Void> {
	private String inputText;
	private String filename;
	
public BackgroundSaveAudio(String inputText, String filename){
	this.inputText = inputText;
	this.filename = filename;
}


@Override
protected Void doInBackground() throws Exception {
	// Take text and turn it into a .wav file using text2wave
	String cmd = "echo " + inputText + " | text2wave - -o VIDIVOXmedia/" + filename + ".wav";

	ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
	try {
		Process process = builder.start();
		process.waitFor();
		// Turn the .wav file into a .mp3 file using ffmpeg
		cmd = "ffmpeg -i VIDIVOXmedia/" + filename + ".wav -f mp3 VIDIVOXmedia/" + filename + ".mp3";
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		process = builder.start();
		process.waitFor();
		// Then delete the existing .wav file as this is no longer needed
		cmd = "rm VIDIVOXmedia/" + filename + ".wav";
		builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		builder.start();
		
	} catch (IOException | InterruptedException e) {
		e.printStackTrace();
	}
	return null;
}

//lets the user know if the file was saved successfully
protected void done(){
	String cmd = "[ -e VIDIVOXmedia/" + filename +".mp3 ]";
	try {
	ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
	Process process;

	process = builder.start();

	process.waitFor();

	//if exists corresponding error message comes up
	if (process.exitValue() == 0) {
		JOptionPane.showMessageDialog(null, "The file " + filename + ".mp3 was saved successfully");
		System.out.println("yay save");
	}else{
		JOptionPane.showMessageDialog(null, "Error creating " + filename + ".mp3");
	}
	} catch (IOException | InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}
}