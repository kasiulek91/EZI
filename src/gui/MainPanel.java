package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import searchEngine.DocScore;
import searchEngine.Stemmer;
import searchEngine.TFIDFSol;
import searchEngine.WordNet;

public class MainPanel {

	private TFIDFSol tfidf = new TFIDFSol();
	private JTextField searchFiled;
	private JButton searchButton;
	private ResultTable resultTab;
	private FileLayout fileLayout;
	private JPanel searchPanel;
	private ExpandPanel expandPanel;

	public MainPanel(JTabbedPane tabbedPane) {
		fileLayout = new FileLayout(this, tabbedPane, tfidf);
	}

	private JPanel createSearchInput() {
		JPanel jPanel = new JPanel(new BorderLayout());
		searchButton = new JButton("Szukaj");
		searchFiled = new JTextField();

		searchFiled.addActionListener(new MyActionListener());
		searchButton.addActionListener(new MyActionListener());

		fileLayout.setSearchFiled(searchFiled);

		jPanel.add(searchFiled, BorderLayout.CENTER);
		jPanel.add(searchButton, BorderLayout.EAST);

		return jPanel;
	}

	public void makeSearchPanelVisible() {
		resultTab = new ResultTable();
		searchPanel.add(createSearchInput(), BorderLayout.NORTH);
		searchPanel.add(resultTab.getScrollPane(), BorderLayout.CENTER);
		searchPanel.add(createExpandPanel(), BorderLayout.SOUTH);
	}

	private JComponent createExpandPanel() {
		expandPanel = new ExpandPanel(this);
		return expandPanel;
	}

	public void makeExtansion(Vector<String> words) {
		expandPanel.removeAll();
		if (words.size() == 0) {
			expandPanel.addEnableWord("Brak znalezionych s³ów!");
		} else {
			expandPanel.addEnableWord("Spróbuj tak¿e: ");
			for (int i = 0; i < Math.min(5, words.size()); i++) {
				expandPanel.addNewWord(words.elementAt(i));
			}
		}
	}

	public JComponent createSearchPanel() {
		searchPanel = new JPanel(new BorderLayout());
		return searchPanel;
	}

	public JComponent createButtonPanel() {
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		buttonPanel.add(fileLayout.createLoadFileLayout("documents"));
		buttonPanel.add(fileLayout.createLoadFileLayout("keywords"));
		return buttonPanel;
	}

	private String convertSearchString(String text) {
		Stemmer st = new Stemmer();
		String newString = "";
		text = text.toLowerCase().replaceAll("[^a-z ]", "").trim()
				.replaceAll(" +", " ");
		String[] arrayString = text.split(" ");
		for (int i = 0; i < arrayString.length; i++) {
			newString += st.stemString(arrayString[i]) + " ";
		}
		return newString;
	}

	public void setSearchField(String searchText) {
		searchFiled.setText(searchText);
		searchButton.doClick();
	}

	public class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String text = searchFiled.getText();

			Vector<DocScore> results = tfidf.rank(convertSearchString(text));
			WordNet wordnet = new WordNet();

			if (results != null) {
				resultTab.addNewValues(results);

				searchPanel.revalidate();
				searchPanel.repaint();
			} else {
				resultTab.clearTable();
			}
			makeExtansion(wordnet.searchExtendedWords(text));

		}

	}

}
