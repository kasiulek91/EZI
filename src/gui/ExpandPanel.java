package gui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ExpandPanel extends JPanel{
	
	public ExpandPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	}
	
	public void addEnableWord(String word) {
		JTextField newWord = new JTextField(word);
		newWord.setEditable(false);
		newWord.setEnabled(false);
		this.add(newWord);
	}
	public void addNewWord(String word) {
		JTextField newWord = new JTextField(word);
		newWord.setEditable(false);
		this.add(newWord);
	}
}
