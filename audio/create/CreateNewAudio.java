package audio.create;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.StyledDocument;

import video.VIDIVOXstart;

import audio.add_to_video.AddAudioToVideo;
import backgroundtasks.BackgroundSpeech;

/**
 * CreateNewAudio is the frame that lets the user add text for festival to speak
 * (preview) and create audio files from (.wav to .mp3)
 * 
 * It contains a textPane that has a character limit of 150 characters, as well
 * as the added festival functionality that lets the user pick a speed for the
 * speech
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class CreateNewAudio extends JFrame {

	private JPanel contentPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	private JPanel titlePanel = new JPanel();
	private CreateNewAudio frame = this;
	private JPanel speedPanel = new JPanel();
	private JPanel textPanel = new JPanel();

	// Create the text pane which will hold the text the user inputs and wants
	// to speak
	// Note that this text pane will have an enforced max character limit of 150
	// characters
	private JTextPane text;
	private AbstractDocument document;
	static final int MAX_CHARACTERS = 150;

	BackgroundSpeech speech = null;
	private List<Integer> pids = new ArrayList<Integer>();

	// Add the components that show additional festival functionality - speed
	// --------------------------------------------------------------------------------------------------
	private JLabel speedTitle = new JLabel(
			"How fast would you like the speech?");
	private JSlider speedSlider;
	private JLabel slowLabel = new JLabel("slow");
	private JLabel fastLabel = new JLabel("fast");
	// Set speed to default speed
	private double speed = 1.0;

	private AddAudioToVideo audio;
	private VIDIVOXstart start;

	/**
	 * This frame allows the user to add text, play this text back, and change
	 * the speed of the text
	 */
	public CreateNewAudio(AddAudioToVideo audio, VIDIVOXstart start) {
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// If the window is closed the preview stops
				// festival processes are cancelled
				if (speech != null) {
					speech.cancel(true);
				}
				frame.dispose();
			}
		});
		setBounds(100, 100, 300, 200);

		this.audio = audio;
		this.start = start;

		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout());

		setUpSpeedPanel();
		setUpTitlePanel();
		setUpTextField();
		setUpViewButtons();
		contentPanel.add(textPanel, BorderLayout.CENTER);
	}

	// PIDS associated with the festival speech
	public void pidsAdd(int i) {
		pids.add(i);
	}

	public List<Integer> getPids() {
		return pids;
	}

	// ---------------------------------------------------------------------------------------------------
	// The following methods set up the frame
	// ---------------------------------------------------------------------------------------------------

	/** Add the slider that controls the speed of the audio playback */
	private void setUpSpeedPanel() {
		textPanel.setLayout(new BorderLayout());

		speedPanel.setLayout(new BorderLayout());
		speedPanel.add(speedTitle, BorderLayout.NORTH);

		// Let the duration that the user can set go from 0.1 to 4.0
		speedSlider = new JSlider(1, 40, 30);

		speedPanel.add(slowLabel, BorderLayout.WEST);
		speedPanel.add(speedSlider, BorderLayout.CENTER);
		speedPanel.add(fastLabel, BorderLayout.EAST);

		textPanel.add(speedPanel, BorderLayout.SOUTH);
	}

	/**
	 * This method adds the text to the top of the frame that helps the user
	 * understand what they are required to do
	 */
	private void setUpTitlePanel() {
		titlePanel.setLayout(new BorderLayout());

		JLabel title = new JLabel("Please enter your commentary:");
		title.setFont(new Font("Tahoma", Font.BOLD, 14));
		titlePanel.add(title, BorderLayout.NORTH);

		// 150 characters represents the approximate length of a compound
		// sentence of reasonable length. Any sentence
		// longer than this can easily be (and probably should be) split into
		// multiple smaller sentences.
		JLabel limit = new JLabel("Note there is a max of 150 characters");
		limit.setFont(new Font("Tahoma", Font.PLAIN, 12));
		titlePanel.add(limit, BorderLayout.CENTER);

		contentPanel.add(titlePanel, BorderLayout.NORTH);
	}

	/**
	 * This method sets up the text field (and its limit) that the user inputs
	 * their text into
	 */
	private void setUpTextField() {
		text = new JTextPane();
		text.setBounds(25, 90, 230, 30);
		// Indicates where the user can type text to make it easier
		text.setText("Add text here ...");

		// Implement an enforced character limit for the text box

		// Note this method to create a character limit for the text pane comes
		// from
		// docs.oracle.come/javase/tutorial/uiswing/components/generaltext.html#filter
		StyledDocument style = text.getStyledDocument();
		if (style instanceof AbstractDocument) {
			document = (AbstractDocument) style;
			document.setDocumentFilter(new JTextAreaDocumentFilter(
					MAX_CHARACTERS));
		}

		textPanel.add(text, BorderLayout.CENTER);
	}

	/** Sets up the buttons that let the user preview and save their text */
	private void setUpViewButtons() {
		// The preview button takes the text the user has typed and plays the
		// commentary out loud so that they can
		// check it is what they want
		JButton preview = new JButton("Preview");
		preview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				speed = 4.0 - ((double) speedSlider.getValue() / 10);
				// Remove the characters that will cause issues in the BASH
				// process
				String removedText = text.getText().replaceAll("\'|\"", "");
				speech = new BackgroundSpeech(removedText, frame, speed);
				speech.execute();
			}
		});
		preview.setFont(new Font("Tahoma", Font.BOLD, 10));
		buttonPanel.add(preview);

		// To save the text, pass the required text through to the save page
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				speed = 4.0 - ((double) speedSlider.getValue() / 10);
				// Remove the characters that will cause issues in the BASH
				// process
				String removedText = text.getText().replaceAll("\'|\"", "");
				SaveAudioOrVideo save = new SaveAudioOrVideo(start,
						removedText, true, audio, speed);
				save.setVisible(true);
				frame.dispose();
			}
		});
		save.setFont(new Font("Tahoma", Font.BOLD, 10));
		save.setBounds(100, 150, 80, 30);
		buttonPanel.add(save);

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// If the cancel button is pressed the preview stops
				// festival processes are cancelled
				if (speech != null) {
					speech.cancel(true);
				}
				frame.dispose();
			}
		});
		cancel.setFont(new Font("Tahoma", Font.BOLD, 10));
		cancel.setBounds(185, 150, 80, 30);
		buttonPanel.add(cancel);

		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
	}
}