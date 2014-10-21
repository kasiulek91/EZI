package gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import searchEngine.Stemmer;
import searchEngine.TFIDFSol;

public class FileLayout {

	private TFIDFSol tfidf;
	private Stemmer st = new Stemmer();
	private JTabbedPane tabbedPane;
	private JTextField searchFiled;
	private Vector<String> db;
	private Vector<String> titles;
	private Vector<String> keywords;
	private int documentsReady = 0;

	public FileLayout(JTabbedPane tabbedPane, TFIDFSol tfidf) {
		this.tabbedPane = tabbedPane;
		this.tfidf = tfidf;
	}

	public void setSearchFiled(JTextField searchFiled) {
		this.searchFiled = searchFiled;
	}

	public JComponent createLoadFileLayout(final String title) {
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
					documentsReady++;
					if (documentsReady == 2) {
						documentsReady = 0;
						tfidf.init();
						searchFiled.setEnabled(true);
					}
					// else {
					// JOptionPane
					// .showMessageDialog(fc,
					// "Najpierw wczytaj plik z dokumentami oraz z termami!");
					// }
				} else {
					jText.setForeground(Color.RED);
					jText.setText("Nie wybrano pliku!");
				}
			}
		});

		return panelFiles;
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

	private void clearFields() {
		tabbedPane.getComponentAt(0);
		// resultTab.clearTable();
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
}
