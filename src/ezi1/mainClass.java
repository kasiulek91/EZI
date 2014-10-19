package ezi1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class mainClass {

	private TFIDFSol tfidf = new TFIDFSol();
	private Vector<String> db;
	private Vector<String> titles;
	private Vector<String> keywords;
	private Stemmer st = new Stemmer();
	private JTextField searchFiled;
	private ResultTable resultTab;
	private JTabbedPane tabbedPane;

	private JComponent createLoadFileLayout(final String title) {
		final JPanel panelFiles = new JPanel(new FlowLayout());

		final JButton buttonOpen = new JButton("Wybierz plik - " + title);
		panelFiles.add(buttonOpen);

		final JLabel jText = new JLabel();

		panelFiles.add(jText);

		buttonOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				int chosedFile = fc.showOpenDialog(panelFiles);
				if (chosedFile == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (title.equals("documents")) {
						readDocumentsFile(file);
						Vector<String> stem = stemDBVecotr(st);
						tfidf.setDb(stem, titles);
						tabbedPane.setEnabledAt(1, true);
						createDocumentPanel(1, stem);
						clearFields();
					} else {
						readKeywordsFile(file);
						Vector<String> stem = st.stemVector(keywords);
						tfidf.setKeywords(stem);
						tabbedPane.setEnabledAt(2, true);
						createDocumentPanel(2, stem);
						clearFields();
					}
					jText.setForeground(Color.BLACK);
					jText.setText("Wybrano plik " + file.getPath());
				} else {
					jText.setForeground(Color.RED);
					jText.setText("Nie wybrano pliku!");
				}
			}
		});

		return panelFiles;
	}
	
	private void clearFields() {
		resultTab.clearTable();
		searchFiled.setText("");
	}

	private void readDocumentsFile(File document) {
		db = new Vector<String>();
		titles = new Vector<String>();
		try (InputStream in = Files.newInputStream(document.toPath());
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in))) {
			String line = null;
			String row = "";
			while ((line = reader.readLine()) != null) {
				if (row.equals("")) {
					titles.add(line);
				}
				row += line + " ";
				if (line.equals("")) {
					row = row.toLowerCase().replaceAll("[^a-z ]", "").trim()
							.replaceAll(" +", " ");
					db.add(row);
					row = "";
				}

			}
			row = row.toLowerCase().replaceAll("[^a-z ]", "").trim()
					.replaceAll(" +", " ");
			db.add(row);

			row = "";
		} catch (IOException x) {
			System.err.println(x);
		}

	}

	private void readKeywordsFile(File keywordsFile) {
		keywords = new Vector<String>();
		try (InputStream in = Files.newInputStream(keywordsFile.toPath());
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				keywords.add(line);

			}
		} catch (IOException x) {
			System.err.println(x);
		}
	}

	private Vector<String> stemDBVecotr(Stemmer st) {
		Vector<String> newDB = new Vector<String>();
		for (int i = 0; i < db.size(); i++) {
			String split[] = db.get(i).split("\\s");
			Vector<String> tmpV = new Vector<String>();
			tmpV.addAll(Arrays.asList(split));
			tmpV = st.stemVector(tmpV);
			String tmpS = "";
			for (String s : tmpV) {
				tmpS += s + " ";
			}
			newDB.add(tmpS);
		}
		return newDB;
	}

	private JComponent createButtonPanel() {
		final JPanel buttonPanel = new JPanel(new GridLayout(3, 0, 5, 5));
		buttonPanel.setPreferredSize(new Dimension(400, 120));
		buttonPanel.add(createLoadFileLayout("documents"));
		buttonPanel.add(createLoadFileLayout("keywords"));
		final JButton buttonLoad = new JButton("Zaladuj dane");
		buttonLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (db != null && keywords != null) {
					tfidf.init();
					searchFiled.setEnabled(true);
				} else {
					JOptionPane
							.showMessageDialog(buttonPanel,
									"Najpierw wczytaj plik z dokumentami oraz z termami!");
				}
			}
		});
		buttonPanel.add(buttonLoad);
		return buttonPanel;
	}

	private String convertSearchString(String text) {
		String newString = "";
		text = text.toLowerCase().replaceAll("[^a-z ]", "").trim()
				.replaceAll(" +", " ");
		String[] arrayString = text.split(" ");
		for (int i = 0; i < arrayString.length; i++) {
			newString += st.stemString(arrayString[i])+" ";
		}
		return newString;
	}

	private JComponent createSearchPanel() {
		final JPanel jPanel = new JPanel(new BorderLayout());
		searchFiled = new JTextField("Szukaj...");
		jPanel.add(searchFiled, BorderLayout.NORTH);
		// final JTextArea searchResults = new JTextArea(2, 200);
		resultTab = new ResultTable();
		searchFiled.setEnabled(false);
		searchFiled.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				if (searchFiled.getText().equals("")) {
					searchFiled.setText("Szukaj...");
				}
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				searchFiled.setText("");
			}
		});
		searchFiled.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = searchFiled.getText();

				Vector<DocScore> results = tfidf
						.rank(convertSearchString(text));
				//---
				WordNet wordnet = new WordNet();
				
				wordnet.searchExtendedWords(text);
				//----
				if (results != null) {
					resultTab.addNewValues(results);
				} else {
					resultTab.clearTable();
				}

			}
		});
		jPanel.add(resultTab.getScrollPane(), BorderLayout.CENTER);
		return jPanel;
	}

	private void createDocumentPanel(int tabIndex, Vector<String> stemDocument) {
		final JPanel mainPanel = (JPanel) tabbedPane.getComponentAt(tabIndex);
		mainPanel.removeAll();
		final JTextArea documentText = new JTextArea();
		documentText.setLineWrap(true);
		documentText.setWrapStyleWord(true);
		documentText.setEditable(false);
		String text = "";
		for (String s : stemDocument) {
			text += s + "\n\n";
		}
		documentText.setText(text);
		mainPanel.add(new JScrollPane(documentText));
	}

	private JComponent createMainPanel() {
		tabbedPane = new JTabbedPane();
		final JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(createButtonPanel(), BorderLayout.NORTH);
		mainPanel.add(createSearchPanel(), BorderLayout.CENTER);

		tabbedPane.addTab("Panel Glowny", mainPanel);
		tabbedPane.addTab("documents", new JPanel(new BorderLayout()));
		tabbedPane.addTab("keywords", new JPanel(new BorderLayout()));

		tabbedPane.setEnabledAt(1, false);
		tabbedPane.setEnabledAt(2, false);

		return tabbedPane;
	}

	private static void createAndShowGUI() {
		final mainClass instance = new mainClass();

		final JFrame frame = new JFrame("EZI - projekt 1");
		frame.getContentPane().add(instance.createMainPanel());
		/* frame.setJMenuBar(instance.createMainMenuBar()); */
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 400);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	}

}
