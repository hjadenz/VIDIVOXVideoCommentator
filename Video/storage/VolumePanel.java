package video.storage;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * This class extends JPanel, but is a JPanel that is already set up with all
 * its components It holds the components to change the volume for each
 * different file in the selected video's list
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class VolumePanel extends JPanel {

	private Media audioFile;
	private VolumePanel panel = this;

	private JSlider volumeSlider;
	private JLabel fileName;
	private JLabel volume;

	public VolumePanel(Media a) {
		this.audioFile = a;

		panel.setLayout(new BorderLayout());
		
		fileName = new JLabel(audioFile.getName());
		panel.add(fileName, BorderLayout.NORTH);

		setUpSlider();

		volume = new JLabel(audioFile.getVolume() + "%");
		panel.add(volume, BorderLayout.EAST);
	}

	/**
	 * Set up the volume slider Make sure that the volume slider (when changed)
	 * can changed the volume component of the media object
	 */
	private void setUpSlider() {
		volumeSlider = new JSlider(0, 100, audioFile.getVolume());
		volumeSlider.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// When the mouse is clicked the position of the mouse is
				// recorder and used to change
				// the position of the slider
				Point p = e.getPoint();
				BasicSliderUI sliderUI = (BasicSliderUI) volumeSlider.getUI();
				int value = sliderUI.valueForXPosition(p.x);

				volumeSlider.setValue(value);
				audioFile.setVolume(volumeSlider.getValue());
				volume.setText(volumeSlider.getValue() + "%");
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			// When the slider is released this is the position that the video
			// goes to
			@Override
			public void mouseReleased(MouseEvent e) {
				audioFile.setVolume(volumeSlider.getValue());
				volume.setText(volumeSlider.getValue() + "%");
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});

		panel.add(volumeSlider, BorderLayout.CENTER);
	}
}
