package audio;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * This frame appears whenever a video is being loaded (usually after an audio
 * file has been added to it and the video has to be created before it can be
 * played)
 * 
 * The progress bar has a small animation to make sure that the user knows that
 * the application is still working
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class LoadingFrame extends JFrame {

	private JProgressBar progress;
	private JPanel contentPane = new JPanel();
	private JLabel label = new JLabel("Loading...");
	private LoadingFrame frame = this;

	public LoadingFrame() {
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setBounds(200, 200, 300, 80);
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);

		label.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(label, BorderLayout.NORTH);

		progress = new JProgressBar();
		progress.setBorder(new EmptyBorder(10, 5, 10, 5));
		progress.setIndeterminate(true);
		contentPane.add(progress, BorderLayout.CENTER);
	}
}
