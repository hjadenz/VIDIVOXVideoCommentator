package chooseFiles;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import audio.AddAudio;

import video.StartPage;

public class FileChooser {
	
	private String name;
	private String path;
	private StartPage start;
	
	private JFileChooser chooser = new JFileChooser();
	
	public void showFileChooser(boolean videoSelected) {
	    File workingDirectory = new File(System.getProperty("user.dir")+ "/VIDIVOXmedia");
	    chooser.setCurrentDirectory(workingDirectory);
	    FileNameExtensionFilter filter;
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
	
	public void setAudioPath(AddAudio audioPage) {
		audioPage.copyPath(path, name);
		audioPage.audioFileTime();
	}
	
	public void addReferenceToStart(StartPage s) {
		this.start = s;
	}
}
