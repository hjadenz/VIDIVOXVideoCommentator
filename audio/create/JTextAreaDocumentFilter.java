package audio.create;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/** This class is a different kind of Document Filter that is used by the textPane when the user creates
 *  their own commentaries to limit the number of characters that can be added
 *  
 *  This works to ensure that when the input string is passed to festival there are no issues with the
 *  voice, i.e. makes sure that the sentences stay short enough for the voice to cope with
 * 
 * @author Hannah Sampson
 */

public class JTextAreaDocumentFilter extends DocumentFilter {
	
	private int maxCharacters;
	
	public JTextAreaDocumentFilter(int maxCharacters) {
		this.maxCharacters = maxCharacters;
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet aSet) throws BadLocationException {
		if ((fb.getDocument().getLength() + string.length()) <= maxCharacters) {
			super.insertString(fb,  offset, string, aSet);
		}
	}
	
	@Override
	public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
		if ((fb.getDocument().getLength() + string.length()) <= maxCharacters) {
			super.replace(fb,  offset, length, string, attrs);
		}
	}
}
