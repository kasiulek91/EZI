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
	

	public MainPanel(JTabbedPane tabbedPane) {
		fileLayout = new FileLayout(this, tabbedPane,tfidf);
	}

	private JPanel createSearchInput() {
		JPanel jPanel = new JPanel(new BorderLayout());
		searchButton = new JButton("Szukaj");
		searchFiled = new JTextField();

		searchFiled.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = searchFiled.getText();

				Vector<DocScore> results = tfidf
						.rank(convertSearchString(text));

				WordNet wordnet = new WordNet();
				wordnet.searchExtendedWords(text);

				if (results != null) {
					resultTab.addNewValues(results);
				} else {
					resultTab.clearTable();
				}
			}
		});

		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = searchFiled.getText();

				Vector<DocScore> results = tfidf
						.rank(convertSearchString(text));

				WordNet wordnet = new WordNet();
				wordnet.searchExtendedWords(text);

				if (results != null) {
					resultTab.addNewValues(results);
				} else {
					resultTab.clearTable();
				}
			}
		});

		fileLayout.setSearchFiled(searchFiled);

		jPanel.add(searchFiled, BorderLayout.CENTER);
		jPanel.add(searchButton, BorderLayout.EAST);

		return jPanel;
	}
	
	public void makeSearchPanelVisible() {
		resultTab = new ResultTable();
		searchPanel.add(createSearchInput(), BorderLayout.NORTH);
		searchPanel.add(resultTab.getScrollPane(), BorderLayout.CENTER);
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
	
	

}
