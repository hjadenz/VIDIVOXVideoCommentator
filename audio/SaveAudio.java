package audio;

import java.io.IOException;

import audio.addToVideo.AddAudio;
import backgroundTasks.BackgroundSaveAudio;

public class SaveAudio {
	private String filename;
	private String inputText;
	
	public SaveAudio(String filename, String inputText) {
		this.filename = filename;
		this.inputText = inputText;
	}

	public void saveFile(AddAudio audio) {
		// Note that saving the file like this will automatically overwrite the file
		BackgroundSaveAudio bgsa = new BackgroundSaveAudio(inputText, filename);
		bgsa.execute();
		
		audio.copyPath("VIDIVOXmedia/"+filename+".mp3", filename);
	}

}