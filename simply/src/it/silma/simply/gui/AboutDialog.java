package it.silma.simply.gui;

import it.silma.simply.main.Simply;
import it.silma.simply.utils.Messages;
import it.silma.simply.utils.ResourceLoader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	public AboutDialog(final Frame parent, final String title) {
		super(parent, title);

		final JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBackground(new Color(245, 245, 255));

		final JPanel text = new JPanel();
		text.setLayout(new BoxLayout(text, BoxLayout.PAGE_AXIS));
		text.add(new JLabel(ResourceLoader.loadImageIcon("logo.png")), Component.RIGHT_ALIGNMENT);
		text.add(new JLabel(Messages.ABOUT_ME), Component.CENTER_ALIGNMENT);
		text.add(Box.createRigidArea(new Dimension(0, 16)));

		JLabel mailMeLabel = createHyperlinkLabel(Messages.CONTACT_ME, Messages.EMAIL);
		text.add(mailMeLabel, Component.CENTER_ALIGNMENT);
		text.add(Box.createRigidArea(new Dimension(0, 16)));

		JLabel visitMeLabel = createHyperlinkLabel(Messages.VISIT_ME, Messages.WEBSITE);
		text.add(visitMeLabel, Component.CENTER_ALIGNMENT);
		text.add(Box.createRigidArea(new Dimension(0, 16)));

		text.add(new JLabel(Messages.INFORM_ME), Component.CENTER_ALIGNMENT);
		text.add(Box.createRigidArea(new Dimension(0, 16)));
		text.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		text.setBackground(null);

		final JButton closeButton = new JButton("Chiudi");
		final AboutDialog about = this;
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				about.setVisible(false);
				about.dispose();
			}
		});
		final JPanel close = new JPanel();
		close.add(closeButton);
		close.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
		close.setBackground(null);

		contentPane.add(text, BorderLayout.NORTH);
		contentPane.add(new JSeparator(), BorderLayout.CENTER);
		contentPane.add(close, BorderLayout.SOUTH);

		this.setContentPane(contentPane);
		this.setLocationByPlatform(true);
		this.setResizable(false);
		this.pack();
	}

	private JLabel createHyperlinkLabel(String content, final String href) {
		JLabel visitMeLabel = new JLabel(content);
		visitMeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		visitMeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() > 0) {
					if (Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						try {
							URI uri = new URI(href);
							desktop.browse(uri);
						} catch (URISyntaxException use) {
							Simply.onError(use.getMessage());
						} catch (IOException ioe) {
							Simply.onError(ioe.getMessage());
						}
					}
				}
			}
		});
		return visitMeLabel;
	}
}
