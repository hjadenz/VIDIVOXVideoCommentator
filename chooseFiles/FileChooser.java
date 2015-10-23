package chooseFiles;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import audio.addToVideo.AddAudio;
import video.VIDIVOXstart;

/** This class is a new frame that lets the user select a file, and easily navigate (visually) around
 * their directories. It works for both audio and video files (based on a boolean property)
 * 
 * @author Hannah Sampson
 */

public class FileChooser {
	
	private String name;
	private String path;
	private VIDIVOXstart start;
	
	private JFileChooser chooser = new JFileChooser();
	
	/**This method is created after the reference back to the start page has been initialised (if video)
	 * It takes the file the user selects and extracts the name, the absolute path, and feeds this back
	 * 
	 * @param videoSelected - a boolean to indicate whether the user is selecting an audio or a video
	 */
	public void showFileChooser(boolean videoSelected) {
		// Set the default directory to be the one that the program creates itself
	    File workingDirectory = new File(System.getProperty("user.dir")+ "/VIDIVOXmedia");
	    chooser.setCurrentDirectory(workingDirectory);
	    FileNameExtensionFilter filter;
	    // If the boolean indicates that we have come from a video selection page, set the filters up
	    // to reflect this and vice versa
	    if(videoSelected){
	    	filter = new FileNameExtensionFilter("avi videos", "avi");
	    }else{
		    filter = new FileNameExtensionFilter("mp3 audios", "mp3");
	    }
	    chooser.setFileFilter(filter);
	    
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	name = chooser.getSelectedFile().getName();
	    	path = chooser.getSelectedFile().getAbsolutePath();
	    	if(videoSelected){
	    		start.start(name, path);
	    		start.createNewVideo(name, path);
	    	}
	    }
	}
	
	// Copy the information from the audio selected back into the audio page and work out how long it is
	public void setAudioPath(AddAudio audioPage) {
		audioPage.copyPath(path, name);
		audioPage.audioFileTime();
	}
	
	// Create a link back to the original page so that video files can be easily played
	public void addReferenceToStart(VIDIVOXstart s) {
		this.start = s;
	}
}
