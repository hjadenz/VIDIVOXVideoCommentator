package audio;

import java.io.IOException;

import backgroundTasks.BackGroundSaveAudio;

public class SaveAudio {
	private String filename;
	private String inputText;
	
	public SaveAudio(String filename, String inputText) {
		this.filename = filename;
		this.inputText = inputText;
	}

	public void saveFile(AddAudio audio) {
		// Note that saving the file like this will automatically overwrite the file
		BackGroundSaveAudio bgsa = new BackGroundSaveAudio(inputText, filename);
		bgsa.execute();
		
		audio.copyPath("VIDIVOXmedia/"+filename+".mp3", filename);
	}

}