package audio;
// Author: Catherine Shanly and Hannah Sampson
// a loading frame when the user has selected an audio file to be added and played
// as creating the temporary file can take a while

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LoadingFrame extends JFrame {
	
	public LoadingFrame(){
		setBounds(200, 200, 300, 95);
		JLabel label = new JLabel("Loading...");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(label, BorderLayout.CENTER);
		setBounds(200, 200, 300, 95);
		setContentPane(contentPane);
	}
}
