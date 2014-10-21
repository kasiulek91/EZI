package gui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ExpandPanel extends JPanel {

	private MainPanel mp;

	public ExpandPanel(MainPanel mp) {
		this.mp = mp;
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	}

	public void addEnableWord(String word) {
		JTextField newWord = new JTextField(word);
		newWord.setEditable(false);
		newWord.setEnabled(false);
		this.add(newWord);
	}

	public void addNewWord(String word) {
		final JTextField newWord = new JTextField(word);
		newWord.setEditable(false);
		newWord.setForeground(Color.blue);
		newWord.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				System.out.println(newWord.getText());
				newWord.setForeground(Color.magenta);
				mp.setSearchField(newWord.getText());
			}
		});
		this.add(newWord);
	}
}
