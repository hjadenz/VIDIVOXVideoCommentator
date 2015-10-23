package video;

import java.awt.EventQueue;

/**Initialises the start Frame and creates the files that store all of the files that the program creates
 * 
 * @author Hannah Sampson
 */
public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new VIDIVOXstart("Welcome to VIDIVOX", "");
					VIDIVOXstart.createDirectories();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
