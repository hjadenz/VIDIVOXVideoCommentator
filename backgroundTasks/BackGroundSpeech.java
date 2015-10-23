package backgroundTasks;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.SwingWorker;

import audio.create.AudioPage;

/** This class sets up a festival process to run in the background when "preview" is pressed
 * 
 */

public class BackgroundSpeech extends SwingWorker<Void, Void> {
	private String text;
	private AudioPage audio;
	private boolean isSpeaking = true;
	private double stretch;
	
	public BackgroundSpeech(String text, AudioPage a, double s){
		this.text = text;
		this.audio= a;
		this.stretch = s;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		String cmd = "echo \"(Parameter.set \'Duration_Stretch " + stretch + ") (SayText \\\"" + text + "\\\")\" | festival";
		
		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			int pid = getProcessID(process);
			audio.pidsAdd(pid);
			while(isSpeaking){
				// If the preview is cancelled
				if(isCancelled()){
					cancel();
					isSpeaking = false;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//get the process ID using reflection
	private int getProcessID(Process process) throws NoSuchFieldException, IllegalAccessException {
		int PID = -1;
		if(process.getClass().getName().equals("java.lang.UNIXProcess")){
			Field f = process.getClass().getDeclaredField("pid");
			f.setAccessible(true);
			return f.getInt(process);
		}
		return PID;
	}
		
	// Goes through all process ID's associated with this frame and kills them
	// i.e. when the frame is closed or cancel is pressed, the voices created by the preview stop
	private void cancel(){
		List<Integer> pids = audio.getPids();
 		for (int i : pids){
			String cmdKILL = "kill $(pstree -pA "+ i +"|sed 's/[+`-]\\+/\\n/g'|sed -n 's/.*(\\([0-9]\\+\\)).*/\\1/p')";
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmdKILL);
			try {
				builder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
