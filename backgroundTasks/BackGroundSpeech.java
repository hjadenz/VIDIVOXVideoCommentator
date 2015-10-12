package backgroundTasks;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.SwingWorker;
import audio.AudioPage;

public class BackGroundSpeech extends SwingWorker<Void, Void> {
	private String removedText;
	private AudioPage ap;
	private boolean isSpeaking = true;
	
	public BackGroundSpeech(String removedText, AudioPage ap){
		this.removedText = removedText;
		this.ap= ap;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		String cmd = "echo " + removedText + " | festival --tts";
		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			int pid = getProcessID(process);
			ap.pidsAdd(pid);
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
	private void cancel(){
		List<Integer> pids = ap.getPids();
 		for (int i : pids){
			String cmdKILL = "kill $(pstree -pA "+ i +"|sed 's/[+`-]\\+/\\n/g'|sed -n 's/.*(\\([0-9]\\+\\)).*/\\1/p')";
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmdKILL);
			try {
				Process process = builder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
