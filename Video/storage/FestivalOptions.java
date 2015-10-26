package video.storage;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSliderUI;

import video.VIDIVOXstart;

/**
 * FestivalOptions is a frame that lets the user select different volumes for
 * each of the files associated with the selected video
 * 
 * VolumePanels are stored in a list, and have one substantiated for each audio
 * file
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class FestivalOptions extends JFrame {

	private FestivalOptions frame = this;
	private VIDIVOXstart start;

	private JPanel contentPanel = new JPanel();
	private JPanel sliderPanel = new JPanel();
	private JPanel originalPanel = new JPanel();
	private JPanel titlePanel = new JPanel();

	private JLabel title = new JLabel(
			"Changes to the volume are automatically stored");
	private JLabel instructions = new JLabel(
			"Press \"merge\" to play video from beginning with changes applied");

	private MediaList audio;
	private ArrayList<VolumePanel> volumes = new ArrayList<VolumePanel>();

	private JSlider soundtrackVolume;
	private JLabel sound;
	private JLabel percentage;

	private JButton mergeButton = new JButton("Merge");

	public FestivalOptions(MediaList a, VIDIVOXstart s) {
		setBounds(100, 100, 500, 150);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout());

		this.audio = a;
		this.start = s;

		// Set up the first slider for the original soundtrack's volume
		createSoundtrackVolumeControl();

		// Set up the sliders for each audio file
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		for (int i = 0; i < audio.size(); i++) {
			frame.setBounds(100, 100, 500, (150 + 40 * i));
			createEachSlider(i);
		}
		sliderPanel.add(mergeButton);

		titlePanel.setLayout(new BorderLayout());
		title.setFont(new Font("Tahoma", Font.BOLD, 14));
		titlePanel.add(title, BorderLayout.NORTH);
		instructions.setFont(new Font("Tahoma", Font.PLAIN, 14));
		titlePanel.add(instructions, BorderLayout.SOUTH);

		contentPanel.add(titlePanel, BorderLayout.NORTH);
		contentPanel.add(originalPanel, BorderLayout.CENTER);
		contentPanel.add(sliderPanel, BorderLayout.SOUTH);

		mergeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				start.merge();
			}
		});
	}

	/**
	 * For the number of audio files that there are, add a new volume slider to
	 * the frame
	 */
	private void createEachSlider(int i) {
		volumes.add(new VolumePanel(audio.get(i)));
		sliderPanel.add(volumes.get(i));
	}

	private void createSoundtrackVolumeControl() {
		originalPanel.setLayout(new BorderLayout());
		
		sound = new JLabel("Original Soundtrack Volume");
		originalPanel.add(sound, BorderLayout.NORTH);

		soundtrackVolume = new JSlider(0, 100, audio.getSoundtrackVolume());
		soundtrackVolume.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// When the mouse is clicked the position of the mouse is
				// recorder and used to change
				// the position of the slider
				Point p = e.getPoint();
				BasicSliderUI sliderUI = (BasicSliderUI) soundtrackVolume
						.getUI();
				int value = sliderUI.valueForXPosition(p.x);

				soundtrackVolume.setValue(value);
				audio.editSoundtrackVolume(soundtrackVolume.getValue());
				percentage.setText(soundtrackVolume.getValue() + "%");
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			// When the slider is released this is the position that the video
			// goes to
			@Override
			public void mouseReleased(MouseEvent e) {
				audio.editSoundtrackVolume(soundtrackVolume.getValue());
				percentage.setText(soundtrackVolume.getValue() + "%");
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		originalPanel.add(soundtrackVolume, BorderLayout.CENTER);

		percentage = new JLabel(audio.getSoundtrackVolume() + "%");
		originalPanel.add(percentage, BorderLayout.EAST);
	}
}
