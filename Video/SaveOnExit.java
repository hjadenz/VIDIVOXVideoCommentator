package video;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import audio.create.SaveAudioOrVideo;

/**
 * This class is a frame that comes up when the user tries to exit the
 * application and hasn't saved their most recent changes.
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class SaveOnExit extends JFrame {

	private JPanel contentPanel = new JPanel();
	private JPanel titlePanel = new JPanel();
	private JPanel buttonPanel = new JPanel();

	private JLabel label = new JLabel("Your changes have not been saved.");
	private JLabel question = new JLabel("Would you like to save them?");

	private SaveOnExit frame = this;
	private VIDIVOXstart start;

	private JButton saveButton = new JButton("Save");
	private JButton exitButton = new JButton("Don't Save");
	private JButton cancelButton = new JButton("Cancel");

	public SaveOnExit(VIDIVOXstart s) {
		// Set it so that if the user just exits the window, the frame closes
		// but the application doesn't
		// exit
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.start = s;

		setBounds(200, 200, 300, 100);
		contentPanel.setLayout(new BorderLayout());
		setContentPane(contentPanel);

		titlePanel.setLayout(new BorderLayout());

		titlePanel.add(label, BorderLayout.NORTH);
		titlePanel.add(question, BorderLayout.SOUTH);
		contentPanel.add(titlePanel, BorderLayout.NORTH);

		buttonPanel.add(saveButton);
		buttonPanel.add(exitButton);
		buttonPanel.add(cancelButton);
		contentPanel.add(buttonPanel, BorderLayout.SOUTH);

		buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		setButtonFunctionality();
	}

	private void setButtonFunctionality() {
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SaveAudioOrVideo save = new SaveAudioOrVideo(null, null, false,
						null, 0.0);
				save.setVisible(true);
				frame.dispose();
				start.setSaved(true);
			}
		});

		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// releases media component and associated native resources upon
				// closing the window
				start.releaseMediaPlayer();
			}
		});

		// Just let the user go back to the previous frame
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
	}
}
