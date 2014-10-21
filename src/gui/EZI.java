package gui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class EZI {

	private JComponent createMainPanel() {
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		MainPanel mainTab = new MainPanel(tabbedPane);
		mainPanel.add(mainTab.createButtonPanel());
		mainPanel.add(mainTab.createSearchPanel());

		tabbedPane.addTab("Panel Glowny", mainPanel);
		tabbedPane.addTab("documents", new JPanel(new BorderLayout()));
		tabbedPane.addTab("keywords", new JPanel(new BorderLayout()));

		tabbedPane.setEnabledAt(1, false);
		tabbedPane.setEnabledAt(2, false);

		return tabbedPane;
	}

	private static void createAndShowGUI() {
		final EZI instance = new EZI();
		final JFrame frame = new JFrame("EZI - projekt 1");
		frame.getContentPane().add(instance.createMainPanel());
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
