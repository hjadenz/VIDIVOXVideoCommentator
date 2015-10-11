package audio;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import video.StartPage;

public class AddAudio extends JFrame {
	
	private JFrame frame = this;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public AddAudio() {
		setBounds(100, 100, 500, 110);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		
		JLabel title = new JLabel("Would you like to: ");
		title.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.add(title, BorderLayout.NORTH);
		
		JButton newCommentaryButton = new JButton("Create a new Commentary");
		newCommentaryButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.add(newCommentaryButton, BorderLayout.CENTER);
		
		JButton addAudioButton = new JButton("or add audio that has already been created");
		addAudioButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.add(addAudioButton, BorderLayout.SOUTH);
	}
}
