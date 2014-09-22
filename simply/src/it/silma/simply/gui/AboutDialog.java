package it.silma.simply.gui;

import it.silma.simply.utils.Messages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
        text.add(new JLabel(new ImageIcon(this.getClass().getResource("../res/logo.png"))), Component.RIGHT_ALIGNMENT);
        text.add(new JLabel(Messages.ABOUT_ME), Component.CENTER_ALIGNMENT);
        text.add(Box.createRigidArea(new Dimension(0, 16)));
        text.add(new JLabel(Messages.CONTACT_ME), Component.CENTER_ALIGNMENT);
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
}
