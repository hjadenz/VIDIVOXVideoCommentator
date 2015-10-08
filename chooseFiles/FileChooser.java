package chooseFiles;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import video.StartPage;

public class FileChooser {
	private boolean videoSelected;
	
	public FileChooser(boolean videoSelected, StartPage start){
		this.videoSelected = videoSelected;
		JFileChooser chooser = new JFileChooser();
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
	    	String name = chooser.getSelectedFile().getName();
	    	String path = chooser.getSelectedFile().getAbsolutePath();
	    	if(videoSelected){
	    		start.start(name, path);
	    	}else{
//	    		start.setAudioLabel(name);
//	    		start.setAudioPath(path);
	    	}
	    }
	}
}
