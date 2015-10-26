package time;

import javax.swing.JLabel;

/**
 * This class extends JLabel so that it can be included in a frame like a label
 * can but is also able to represent a hours:minutes:seconds time (string) when
 * only given a number (int) that represents the number of seconds to calculate
 * a time for
 * 
 * Used by AddAudio.java and EditMediaTime.java and StartPage.java to represent
 * start and end times for a video. Used together with slider to show progress
 * through video.
 * 
 * @author Hannah Sampson
 */

@SuppressWarnings("serial")
public class TimeLabel extends JLabel {

	// When initialised the time is set to be zero
	public TimeLabel() {
		this.setText("00:00:00");
	}

	// This method breaks the integer "time" into the number of hours, minutes
	// and seconds, and formats
	// it nicely as a string in the form hh:mm:ss
	private String setTimeString(int time) {

		// Number of hours, minutes and seconds (up to 59:59:59)
		int hours = ((time / 60) / 60) % 60;
		int minutes = (time / 60) % 60;
		int seconds = time % 60;

		String timeCalculation = "";

		// If (for each of hours, minutes and seconds) there is only 1 digit,
		// add a '0' to the front in
		// the string to match proper format. Otherwise if there are 2 digits
		// just add this directly to
		// the string (add ':' between each set of digits)
		if (hours < 10) {
			timeCalculation = timeCalculation + "0" + Integer.toString(hours)
					+ ":";
		} else {
			timeCalculation = timeCalculation + Integer.toString(hours) + ":";
		}

		if (minutes < 10) {
			timeCalculation = timeCalculation + "0" + Integer.toString(minutes)
					+ ":";
		} else {
			timeCalculation = timeCalculation + Integer.toString(minutes) + ":";
		}

		if (seconds < 10) {
			timeCalculation = timeCalculation + "0" + Integer.toString(seconds);
		} else {
			timeCalculation = timeCalculation + Integer.toString(seconds);
		}

		return timeCalculation;
	}

	// When given and integer, set the text of the TimeLabel to be the string
	// given by setTimeString
	// (above)
	public void setTimeText(int value) {
		this.setText(setTimeString(value));
	}
}
