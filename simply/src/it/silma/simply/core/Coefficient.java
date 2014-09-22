package it.silma.simply.core;

import it.silma.simply.utils.Constants;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Helper class per definire un JFormattedTextField con un valore iniziale, un
 * formato, dimensioni e bordo nullo.
 */
@SuppressWarnings("serial")
class Coefficient extends JFormattedTextField implements DocumentListener {
    private final Border normalBorder = BorderFactory.createEtchedBorder();
    private final Border errorBorder = BorderFactory.createLineBorder(Color.RED, 1);
    // I coefficienti sanno in quale riga e colonna si trovano.
    protected int row, col;

    /** Crea un Coefficient vuoto. */
    public Coefficient() {
        super();

        this.setBorder(normalBorder);
        this.getDocument().addDocumentListener(this);
    }

    public Coefficient(final float value, final NumberFormat valueFormat) {
        this(value, valueFormat, 0, 0);
    }

    /** Crea un Coefficient con formato e valore specificato. */
    public Coefficient(final float value, final NumberFormat valueFormat, final int row, final int col) {
        super(valueFormat);

        this.setValue(new Float(value));
        this.setColumns(4);
        this.setBorder(normalBorder);
        this.row = row;
        this.col = col;

        this.setDisabledTextColor(Color.WHITE);

        this.getDocument().addDocumentListener(this);
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent fe) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ((JTextField) fe.getSource()).selectAll();
                    }
                });
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent me) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // Se non siamo in una situazione in cui si usano i clic
                        // del mouse per altri scopi,
                        // seleziona il testo al clic del mouse.
                        if (!Constants.STEP.equals(Constants.EnteringSelect)
                                && !Constants.STEP.equals(Constants.ExitingSelect))
                            ((JTextField) me.getSource()).selectAll();
                    }
                });
            }
        });
    }

    // Gestione dei cambiamenti nei campi testo.
    public void changedUpdate(final DocumentEvent e) {
        // Ogni volta che il testo cambia, cerca di validarlo e se non funziona,
        // notifica in rosso.
        try {
            getCoefficient();
            // Se passo oltre la prima istruzione, il numero e' valido e
            // posso ripristinare il colore di sfondo.
            this.setBorder(normalBorder);
        } catch (final NumberFormatException nfe) {
            this.setBorder(errorBorder);
        }
    }

    public void insertUpdate(final DocumentEvent e) {
        try {
            getCoefficient();
            this.setBorder(normalBorder);
        } catch (final NumberFormatException nfe) {
            this.setBorder(errorBorder);
        }
    }

    public void removeUpdate(final DocumentEvent e) {
        try {
            getCoefficient();
            this.setBorder(normalBorder);
        } catch (final NumberFormatException nfe) {
            this.setBorder(errorBorder);
        }
    }

    /** Espone il valore del Coefficiente come float. */
    protected float getCoefficient() throws NumberFormatException {
        return Float.parseFloat(this.getText());
    }

    /** Nega il valore del coefficiente. */
    protected void negate() {
        this.setValue(-this.getCoefficient());
    }

}