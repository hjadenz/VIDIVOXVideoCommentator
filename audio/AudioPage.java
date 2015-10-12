package audio;

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
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import backgroundTasks.BackGroundSpeech;

import javax.swing.JTextField;

// This class is required to set a characted limit for the text field on the audio screen
class JTextFieldLimit extends PlainDocument {
	  private int limit;
	  JTextFieldLimit(int limit) {
		  super();
		  this.limit = limit;
	  }

	  JTextFieldLimit(int limit, boolean upper) {
		  super();
		  this.limit = limit;
	  }

	  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		  if (str == null) {
			  return;
		  }

		  if ((getLength() + str.length()) <= limit) {
			  super.insertString(offset, str, attr);
		  }
	  }
}


public class AudioPage extends JFrame {

	private JPanel contentPane;
	private JPanel buttonPane;
	private AudioPage frame = this;
	private JTextField text;
	BackGroundSpeech bgs = null;
	private List<Integer> pids = new ArrayList<Integer>();

	/**
	 * Create the frame.
	 */
	public AudioPage(final AddAudio audio) {
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	// If the window is closed the preview stops
				// festival processes are cancelled
				if(bgs != null){
					bgs.cancel(true);
				}
                System.exit(0);
            }
        });
		setBounds(100, 100, 300, 250);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout());
		
		JLabel title = new JLabel("Please enter your commentary:");
		title.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.add(title, BorderLayout.NORTH);
		
		// 150 characters represents the approximate length of a compound sentence of reasonable length. Any sentence
		// longer than this can easily be (and probably should be) split into multiple smaller sentences.
		JLabel limit = new JLabel("Note there is a max of 150 characters");
		limit.setFont(new Font("Tahoma", Font.BOLD, 10));
		contentPane.add(limit, BorderLayout.CENTER);
		
		text = new JTextField();
		text.setBounds(25, 90, 230, 30);
		contentPane.add(text, BorderLayout.CENTER);
		text.setColumns(10);
		text.setDocument(new JTextFieldLimit(150));
		// Indicates where the user can type text to make it easier
		text.setText("Add text here ...");
		
		buttonPane = new JPanel();
		
		// The preview button takes the text the user has typed and plays the commentary out loud so that they can 
		// check it is what they want
		JButton preview = new JButton("Preview");
		preview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					String removedText = text.getText().replaceAll("\'|\"", "");
					bgs = new BackGroundSpeech(removedText, frame);
					bgs.execute();
			}
		});
		preview.setFont(new Font("Tahoma", Font.BOLD, 10));
		buttonPane.add(preview);
		
		// To save the text, pass the required text through to the save page
		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String removedText = text.getText().replaceAll("\'|\"", "");
				SavePage save = new SavePage(removedText,true, audio);
				save.setVisible(true);
				frame.dispose();
			}
		});
		save.setFont(new Font("Tahoma", Font.BOLD, 10));
		save.setBounds(100, 150, 80, 30);
		buttonPane.add(save);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// If the cancel button is pressed the preview stops
				// festival processes are cancelled
				if(bgs != null){
					bgs.cancel(true);
				}
				frame.dispose();
			}
		});
		cancel.setFont(new Font("Tahoma", Font.BOLD, 10));
		cancel.setBounds(185, 150, 80, 30);
		buttonPane.add(cancel);
		
		contentPane.add(buttonPane, BorderLayout.SOUTH);
	}
	public void pidsAdd(int i){
		pids.add(i);
	}
	public List<Integer> getPids(){
		return pids;
	}

}
