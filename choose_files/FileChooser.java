package choose_files;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import audio.add_to_video.AddAudioToVideo;
import video.VIDIVOXstart;

/**
 * This class is a new frame that lets the user select a file, and easily
 * navigate (visually) around their directories. It works for both audio and
 * video files (based on a boolean property)
 * 
 * @author Hannah Sampson
 */

public class FileChooser {

	private String name;
	private String path;

	private JFileChooser chooser = new JFileChooser();

	public FileChooser(boolean videoSelected, VIDIVOXstart start,
			AddAudioToVideo audio) {
		// Set the default directory to be the one that the program creates
		// itself
		File workingDirectory = new File(System.getProperty("user.dir")
				+ "/VIDIVOXmedia");
		chooser.setCurrentDirectory(workingDirectory);
		FileNameExtensionFilter filter;
		// If the boolean indicates that we have come from a video selection
		// page, set the filters up
		// to reflect this and vice versa
		if (videoSelected) {
			filter = new FileNameExtensionFilter("avi videos", "avi");
		} else {
			filter = new FileNameExtensionFilter("mp3 audios", "mp3");
		}
		chooser.setFileFilter(filter);

		// If the option is selected, copy this back into the appropriate frame
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			name = chooser.getSelectedFile().getName();
			path = chooser.getSelectedFile().getAbsolutePath();
			if (videoSelected) {
				start.start(name, path);
				start.createNewVideo(name, path);
			} else {
				audio.copyPath(path, name);
				audio.audioFileTime();
			}
		}
	}
}
