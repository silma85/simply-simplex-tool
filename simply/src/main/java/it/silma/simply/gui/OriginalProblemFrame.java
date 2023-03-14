/**
 *
 */
package it.silma.simply.gui;

import it.silma.simply.core.Coefficient;
import it.silma.simply.core.Exporter;
import it.silma.simply.core.Matrix;
import it.silma.simply.main.Simply;
import it.silma.simply.utils.Constants;
import it.silma.simply.utils.Messages;
import it.silma.simply.utils.ResourceLoader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static it.silma.simply.utils.ResourceLoader.loadImageIcon;

/**
 * @author Silma
 *
 */
public class OriginalProblemFrame extends JFrame implements ActionListener, ChangeListener {

    private SolverFrame solverFrame;
    JTextPane lpPane;

    // Tipi di vincoli
    private final SpinnerListModel[] constraintTypeListModel = new SpinnerListModel[Constants.MAX_SIZE];

    // Spinners con i precedenti componenti
    private final JSpinner varSpinner = new JSpinner(new SpinnerNumberModel(2, Constants.MIN_SIZE, Constants.MAX_SIZE,
            1)),
            constraintSpinner = new JSpinner(new SpinnerNumberModel(2, Constants.MIN_SIZE, Constants.MAX_SIZE, 1)),
            problemTypeSpinner = new JSpinner(new SpinnerListModel(new String[]{"Max", "Min"}));
    private final JSpinner[] constraintTypeSpinner = new JSpinner[Constants.MAX_SIZE];
    // Bottoni: convalida, reimposta, procedi
    private final JButton validateButton = new JButton("Convalida"), resetButton = new JButton("Reimposta"),
            startSimplexButton = new JButton("Risolutore"), graphicButton = new JButton("Grafico");

    // Coefficienti della matrice A e del vettore t (cfr. tableau)
    private Coefficient[][] constraintCoefficients = new Coefficient[Constants.MAX_SIZE][Constants.MAX_SIZE];
    private Coefficient[] objectiveCoefficientVector = new Coefficient[Constants.MAX_SIZE];
    private Coefficient[] knownValuesVector = new Coefficient[Constants.MAX_SIZE];
    JLabel[] objectiveLabel = new JLabel[Constants.MAX_SIZE];

    /**
     * @param title Title of this frame.
     * @throws HeadlessException Swing mandated exception.
     */
    public OriginalProblemFrame(final String title) throws HeadlessException {
        super(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationByPlatform(true);

        // Settaggio layout
        final GridBagLayout rightLayout = new GridBagLayout();
        final GridBagConstraints rightConstraint = new GridBagConstraints();
        rightConstraint.fill = GridBagConstraints.HORIZONTAL;

        // Pannelli di contenuto
        final JPanel typePanel = new JPanel(new BorderLayout()), canonFormPanel = new JPanel(new BorderLayout()), welcomePanel = new JPanel(
                new BorderLayout()), coefficientsPanel = new JPanel(rightLayout);
        // Pannelli di layout
        final JPanel topPanel = new JPanel(new BorderLayout()), centerPanel = new JPanel(new BorderLayout()), buttonPanel = new JPanel(
                new BorderLayout()), contentPanel = new JPanel(new BorderLayout());

        // Colore
        contentPanel.setBackground(new Color(245, 245, 255));
        topPanel.setBackground(null);
        centerPanel.setBackground(null);
        buttonPanel.setBackground(null);
        welcomePanel.setBackground(null);
        typePanel.setBackground(null);
        canonFormPanel.setBackground(null);
        coefficientsPanel.setBackground(null);

        // Inizializzazione varie parti della UI
        initMenus();
        initConstraintCoefficient();
        addTopInputFields(typePanel, canonFormPanel, welcomePanel);
        addConstraintCoefficients(rightConstraint, coefficientsPanel);
        addButtons(buttonPanel);

        // Impone le dimensioni in dipendenza dal contenuto.
        canonFormPanel.setPreferredSize(new Dimension(getObjectiveCoefficientVector()[0].getPreferredSize().width
                * (Constants.MAX_SIZE + 2) + 30, 180));
        coefficientsPanel.setPreferredSize(new Dimension(getObjectiveCoefficientVector()[0].getPreferredSize().width
                * (Constants.MAX_SIZE + 2) + 30, getObjectiveCoefficientVector()[0].getPreferredSize().height
                * (Constants.MAX_SIZE + 3) + 36));

        // Bordi
        final Border empty = BorderFactory.createEmptyBorder(4, 4, 4, 4);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(empty, BorderFactory.createTitledBorder("Welcome")));
        typePanel.setBorder(BorderFactory.createCompoundBorder(empty,
                BorderFactory.createTitledBorder("Caratteristiche")));
        coefficientsPanel.setBorder(BorderFactory.createCompoundBorder(empty,
                BorderFactory.createTitledBorder("Coefficienti")));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
        canonFormPanel.setBorder(BorderFactory.createCompoundBorder(empty,
                BorderFactory.createTitledBorder("Forma normale")));

        // Aggiunta dei sotto-pannelli ai pannelli principali
        topPanel.add(welcomePanel, BorderLayout.CENTER);
        topPanel.add(typePanel, BorderLayout.EAST);
        centerPanel.add(canonFormPanel, BorderLayout.EAST);
        centerPanel.add(coefficientsPanel, BorderLayout.WEST);
        // Aggiunta dei pannelli principali
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.setIconImage(Objects.requireNonNull(loadImageIcon("logo.png")).getImage());
        this.setContentPane(contentPanel);
        this.pack();
    }

    /** Smista le azioni dei tre bottoni di questa finestra */
    public void actionPerformed(final ActionEvent e) {
        // Se viene premuto il bottone di inizio procedimento
        if (e.getActionCommand().equals(startSimplexButton.getActionCommand())) {
            checkNegativeKnowns();
            doArtificialRequest();
            // Crea e visualizza la finestra per l'algoritmo del simplesso
            solverFrame = new SolverFrame(this, "Risolutore");
            solverFrame.addWindowListener(new WindowAdapter() {
                @Override
                // Se il risolutore viene chiuso, riattiva tutti i bottoni.
                public void windowClosing(final WindowEvent e) {
                    if (e.getSource().equals(solverFrame)) {
                        Simply.resetGrades();
                        startSimplexButton.setEnabled(true);
                        validateButton.setEnabled(true);
                        resetButton.setEnabled(true);
                    }
                }
            });
            // Disabilita tutti i bottoni
            startSimplexButton.setEnabled(false);
            validateButton.setEnabled(false);
            resetButton.setEnabled(false);
            // Visualizza l'interfaccia per l'esecuzione del simplesso
            SwingUtilities.invokeLater(() -> solverFrame.setVisible(true));
        }

        // Se viene premuto il bottone di reset
        if (e.getActionCommand().equals(resetButton.getActionCommand()))
            for (int i = 0; i < getConstraintNumber(); i++) {
                getKnownValuesVector()[i].setValue((float) 0);
                constraintTypeSpinner[i].setValue("\u2264");
                for (int j = 0; j < getVariableNumber(); j++) {
                    if (i == 0)
                        getObjectiveCoefficientVector()[j].setValue(1F);
                    getConstraintCoefficients()[i][j].setValue((float) 0);
                }
            }

        // Se viene premuto il bottone di convalida
        if (e.getActionCommand().equals(validateButton.getActionCommand())) {
            lpPane.setText(getProblemString());
            startSimplexButton.setEnabled(true);
        }

        // Se viene premuto il bottone del grafico
        if (e.getActionCommand().equals("graphic")) {
            final GraphicDialog graph = new GraphicDialog(this);
            graph.setVisible(true);
        }
    }

    // Metodi per controllare la dimensione della tabella modificabile
    public void stateChanged(final ChangeEvent e) {
        // Cambiato il numero di vincoli
        if (constraintSpinner.equals(e.getSource())) {
            final SpinnerModel model = constraintSpinner.getModel();
            toggleTableRow((Integer) model.getValue());
        }
        // Cambiato il numero di variabili
        if (varSpinner.equals(e.getSource())) {
            final SpinnerModel model = varSpinner.getModel();
            toggleTableCol((Integer) model.getValue());
        }
    }

    /**
     * Aggiunge i bottoni al frame inferiore.
     *
     * Action table: - graphic - Disegna il grafico di un problema a 2
     * dimensioni. - validate - Richiede la convalida della tabella iniziale
     * inserita. - reset - Resetta tutti i campi al valore di default. - start -
     * Richiede l'avanzamento del programma.
     *
     * @param buttonPanel Bottone da aggiungere.
     */
    private void addButtons(final JPanel buttonPanel) {
        final JPanel left = new JPanel();
        left.setBackground(null);
        final JPanel right = new JPanel();
        right.setBackground(null);
        // Azioni e comandi
        graphicButton.setActionCommand("graphic");
        graphicButton.addActionListener(this);
        validateButton.setActionCommand("validate");
        validateButton.addActionListener(this);
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(this);
        startSimplexButton.setActionCommand("start");
        startSimplexButton.addActionListener(this);
        startSimplexButton.setEnabled(false);
        // Aspetto e posizione
        left.add(graphicButton);
        left.add(validateButton);
        left.add(resetButton);
        right.add(startSimplexButton);
        // Mix
        buttonPanel.add(left, BorderLayout.LINE_START);
        buttonPanel.add(right, BorderLayout.LINE_END);
    }

    /**
     * Aggiunge al pannello di sotto i campi per l'immissione dei coefficienti.
     *
     * @param rightConstraint Coefficiente destro.
     * @param coefficientsPanel Pannello coefficienti.
     */
    private void addConstraintCoefficients(final GridBagConstraints rightConstraint, final JPanel coefficientsPanel) {
        rightConstraint.fill = GridBagConstraints.VERTICAL;
        coefficientsPanel.setAlignmentX(RIGHT_ALIGNMENT);
        coefficientsPanel.setAlignmentY(BOTTOM_ALIGNMENT);

        // Aggiunta vettore dei coefficienti obiettivo
        for (int i = 0; i < Constants.MAX_SIZE; i++) {
            rightConstraint.gridx = i;
            rightConstraint.gridy = 0;
            coefficientsPanel.add(objectiveLabel[i], rightConstraint);
            rightConstraint.gridy = 1;
            coefficientsPanel.add(getObjectiveCoefficientVector()[i], rightConstraint);
        }
        // Con etichetta per la funzione obiettivo
        rightConstraint.gridx = Constants.MAX_SIZE;
        coefficientsPanel.add(new JLabel("<html><font face=\"serif\" size=\"4\"><i>=</i></font></html>"),
                rightConstraint);
        rightConstraint.gridx = Constants.MAX_SIZE + 1;
        coefficientsPanel.add(new JLabel("<html><font face=\"serif\" size=\"4\"><i>Z</i></font></html>"),
                rightConstraint);
        // Aggiunta matrice dei coefficienti dei vincoli
        for (int i = 0; i < Constants.MAX_SIZE; i++)
            for (int j = 0; j < Constants.MAX_SIZE; j++) {
                rightConstraint.gridy = j + 2;
                rightConstraint.gridx = i;
                coefficientsPanel.add(getConstraintCoefficients()[j][i], rightConstraint);
            }
        // Aggiunta selettori del tipo dei vincoli e termini noti
        for (int j = 0; j < Constants.MAX_SIZE; j++) {
            // Tipo di vincolo, richiede uno spazio orizzontale piu' lungo dei
            // numeri
            rightConstraint.gridwidth = 1;
            rightConstraint.gridx = Constants.MAX_SIZE;
            rightConstraint.gridy = j + 2;
            coefficientsPanel.add(constraintTypeSpinner[j], rightConstraint);
            // Resetta le impostazioni di layout e inserisce il termine noto
            rightConstraint.gridwidth = 1;
            rightConstraint.gridx = Constants.MAX_SIZE + 1;
            rightConstraint.gridy = j + 2;
            coefficientsPanel.add(getKnownValuesVector()[j], rightConstraint);
        }
    }

    /**
     * Aggiunge ai rispettivi pannelli i campi per il tipo di problema, numero
     * variabili e vincoli, forma normale e messaggio di benvenuto.
     *
     * @param typePanel Pannello del tipo di problema.
     * @param canonFormPanel Pannello della forma normale.
     * @param welcomePanel Pannello del messaggio di benvenuto.
     */
    private void addTopInputFields(final JPanel typePanel, final JPanel canonFormPanel, final JPanel welcomePanel) {
        // Pannello istruzioni
        final JTextPane instrPane = new JTextPane();
        instrPane.setContentType("text/html");
        instrPane.setEditable(false);
        instrPane.setBackground(null);
        instrPane.setPreferredSize(new Dimension(0, 140));
        instrPane.setText(Messages.HTML + Messages.INFO_WELCOME + Messages.HTML_END);
        welcomePanel.add(instrPane, BorderLayout.CENTER);

        // Pannello problema di PL
        lpPane = new JTextPane();
        lpPane.setEditable(false);
        lpPane.setContentType("text/html");
        lpPane.setBackground(new Color(245, 245, 255));
        lpPane.setText("<html><font face=\"monospaced\">Premi <b>Convalida</b>.</font></html>");
        JPanel lpBox = new JPanel(new BorderLayout());
        lpBox.add(lpPane, BorderLayout.LINE_START);
        lpBox.setBackground(new Color(245, 245, 255));
        JScrollPane lpScroll = new JScrollPane(lpBox);
        lpScroll.setBackground(new Color(245, 245, 255));
        lpScroll.setBorder(null);
        lpScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        lpScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        canonFormPanel.add(lpScroll, BorderLayout.CENTER);

        final JLabel variablesLabel = new JLabel("Numero variabili: "), constraintsLabel = new JLabel(
                "Numero vincoli: "), typeLabel = new JLabel("Tipo del problema: ");
        // Registra i ChangeListener per variabili e vincoli
        varSpinner.addChangeListener(this);
        constraintSpinner.addChangeListener(this);
        // Settaggio proprieta selettori'
        JComponent editor = varSpinner.getEditor();
        ((JSpinner.DefaultEditor) editor).getTextField().setEditable(false);
        editor = constraintSpinner.getEditor();
        ((JSpinner.DefaultEditor) editor).getTextField().setEditable(false);
        editor = problemTypeSpinner.getEditor();
        ((JSpinner.DefaultEditor) editor).getTextField().setEditable(false);

        // Selettori delle caratteristiche (numero variabili e vincoli, tipo)
        typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.PAGE_AXIS));
        final JPanel top = new JPanel(new BorderLayout()), center = new JPanel(new BorderLayout()), bottom = new JPanel(
                new BorderLayout());
        top.add(variablesLabel, BorderLayout.LINE_START);
        top.add(varSpinner, BorderLayout.LINE_END);
        top.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        top.setBackground(null);
        center.add(constraintsLabel, BorderLayout.LINE_START);
        center.add(constraintSpinner, BorderLayout.LINE_END);
        center.setBackground(null);
        center.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        bottom.add(typeLabel, BorderLayout.LINE_START);
        bottom.add(problemTypeSpinner, BorderLayout.LINE_END);
        bottom.setBackground(null);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        typePanel.add(top, Component.LEFT_ALIGNMENT);
        typePanel.add(Box.createRigidArea(new Dimension(0, 32)));
        typePanel.add(center, Component.LEFT_ALIGNMENT);
        typePanel.add(Box.createRigidArea(new Dimension(0, 32)));
        typePanel.add(bottom, Component.LEFT_ALIGNMENT);

        typePanel.setPreferredSize(new Dimension(180, 0));
    }

    /**
     * Controlla l'esistenza di termini noti negativi e se si', effettua le
     * operazioni appropriate.
     */
    private void checkNegativeKnowns() {
        final Constants[] cts = getConstraintTypes();
        boolean flag = false;
        // Itera lungo i termini noti. Se uno e' negativo, gira tutta la
        // disequazione.
        for (int i = 0; i < cts.length; i++)
            if (getKnownValuesVector()[i].getCoefficient() < 0) {
                flag = true;
                getKnownValuesVector()[i].setValue(-getKnownValuesVector()[i].getCoefficient());
                if (cts[i].equals(Constants.LessThan))
                    constraintTypeSpinner[i].getModel().setValue("≥");
                else if (cts[i].equals(Constants.GreaterThan))
                    constraintTypeSpinner[i].getModel().setValue("≤");
                for (int j = 0; j < getConstraintCoefficients()[i].length; j++)
                    getConstraintCoefficients()[i][j].setValue(-getConstraintCoefficients()[i][j].getCoefficient());
            }
        // Messaggio di avvertimento
        if (flag)
            JOptionPane.showMessageDialog(this, Messages.HTML + Messages.INFO_NEGATIVE_KNOWN + Messages.HTML_END,
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Metodi per controllare se il problema deve essere risolto con un problema
     * artificiale.
     */
    private void doArtificialRequest() {
        final int ans = JOptionPane.showConfirmDialog(this, Messages.QUESTION_ARTIFICIAL, "Rispondi alla domanda",
                JOptionPane.YES_NO_OPTION);
        if (isArtificialNeeded())
            if (ans == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, Messages.HTML + "<b>" + Messages.INFO_CORRECT_ANS + "</b>"
                        + Messages.INFO_ARTIFICIAL_OK, Messages.INFO_CORRECT_ANS, JOptionPane.INFORMATION_MESSAGE);
                Simply.addBinaryMark(true);
            } else {
                JOptionPane.showMessageDialog(this, Messages.HTML + "<font color=\"red\"><b>" + Messages.INFO_WRONG_ANS
                                + "</b></font>" + Messages.INFO_ARTIFICIAL_OK, Messages.INFO_WRONG_ANS,
                        JOptionPane.ERROR_MESSAGE);
                Simply.addBinaryMark(false);
            }
        else if (ans == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(this, Messages.HTML + "<b>" + Messages.INFO_CORRECT_ANS + "</b>"
                    + Messages.INFO_NOT_ARTIFICIAL_HIT, Messages.INFO_CORRECT_ANS, JOptionPane.INFORMATION_MESSAGE);
            Simply.addBinaryMark(true);
        } else {
            JOptionPane.showMessageDialog(this, Messages.HTML + "<font color=\"red\"><b>" + Messages.INFO_WRONG_ANS
                            + "</b></font>" + Messages.INFO_NOT_ARTIFICIAL_MISS, Messages.INFO_WRONG_ANS,
                    JOptionPane.ERROR_MESSAGE);
            Simply.addBinaryMark(false);
        }
        // La variabile di stato mi servira' per sapere l'esito della scelta.
        Constants.STATUS = isArtificialNeeded() ? Constants.ArtificialPhaseOne : Constants.Simple;
    }

    /**
     * Inizializza coefficienti dei vincoli e della funzione obiettivo, oltre ai
     * versi dei vincoli e al vettore dei termini noti.
     *
     * Inizializza le matrici e i vettori vari cosi' che
     * objectiveCoefficientVector[i] sia il coeff. della i-esima variabile
     * decisionale e constraintCoefficients[i][] sia il vincolo i-esimo (di cui
     * constraintCoefficients[i][j] e' il coeff. della j-esima variabile
     * decisionale).
     */
    private void initConstraintCoefficient() {
        // Inizializzazione matrici e vettori
        for (int i = 0; i < Constants.MAX_SIZE; i++) {
            // Verso dei vincoli
            constraintTypeListModel[i] = new SpinnerListModel(new String[]{"\u2264", "\u2265", "="});
            constraintTypeSpinner[i] = new JSpinner(constraintTypeListModel[i]);
            final JComponent editor = constraintTypeSpinner[i].getEditor();
            ((JSpinner.DefaultEditor) editor).getTextField().setEditable(false);
            // Coefficienti della funzione obiettivo (con etichette)
            getObjectiveCoefficientVector()[i] = new Coefficient(1, Constants.valueFormat);
            getObjectiveCoefficientVector()[i].setPreferredSize(new Dimension(24, 24));
            getObjectiveCoefficientVector()[i].setBackground(Color.yellow);
            objectiveLabel[i] = new JLabel("<html><font face=\"serif\" size=\"4\"><i>x</i><font size=\"3\"><sub>"
                    + (i + 1) + "</sub></font></font></html>");
            // Coefficienti dei vari vincoli
            for (int j = 0; j < Constants.MAX_SIZE; j++)
                getConstraintCoefficients()[i][j] = new Coefficient(0, Constants.valueFormat);
            // Termini noti
            getKnownValuesVector()[i] = new Coefficient(0, Constants.valueFormat);
            getKnownValuesVector()[i].setBackground(Color.cyan);
        }

        for (int i = 0; i < Constants.MAX_SIZE; i++) {
            constraintTypeSpinner[i].setVisible(i == 0 || i == 1);
            getObjectiveCoefficientVector()[i].setVisible(i == 0 || i == 1);
            objectiveLabel[i].setVisible(i == 0 || i == 1);
            getKnownValuesVector()[i].setVisible(i == 0 || i == 1);
            for (int j = 0; j < Constants.MAX_SIZE; j++)
                getConstraintCoefficients()[i][j].setVisible((i == 0 || i == 1) && (j == 0 || j == 1));
        }
    }

    /**
     * Inizializza i menu
     */
    private void initMenus() {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = new JMenu("File"), helpMenu = new JMenu("Aiuto");

        // File->Esporta in AMPL
        final JMenuItem exportAmplFileMenu = new JMenuItem("Esporta in AMPL...");
        final Exporter expo = new Exporter(this);
        exportAmplFileMenu.addActionListener(e -> expo.generateAMPL());
        fileMenu.setMnemonic(KeyEvent.VK_A);
        fileMenu.add(exportAmplFileMenu);
        fileMenu.addSeparator();

        // File->Esci
        final JMenuItem exitFileMenu = new JMenuItem("Esci");
        exitFileMenu.addActionListener(e -> System.exit(0));
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(exitFileMenu);

        // Aiuto->Guida rapida
        final JDialog helpDialog = new JDialog(this, "Guida rapida");
        final JMenuItem infoHelpMenu = new JMenuItem("Guida rapida");
        infoHelpMenu.addActionListener(e -> {
            if (!helpDialog.isVisible()) {
                final JPanel contentPane = new JPanel(new BorderLayout());
                // Testo della guida
                final JTextPane helpPane = new JTextPane();
                helpPane.setContentType("text/html");
                try {
                    URL url = ResourceLoader.loadURL("readme.html");
                    helpPane.setPage(url);
                } catch (final IOException ie) {
                    Simply.onError(ie.getMessage());
                }
                helpPane.setEditable(false);
                helpPane.setBackground(null);
                helpPane.setBorder(BorderFactory.createEmptyBorder(0, 8, 16, 8));
                helpPane.setPreferredSize(new Dimension(400, 480));
                helpPane.setBackground(new Color(245, 245, 255));
                JScrollPane helpScroll = new JScrollPane(helpPane);
                helpScroll.setBorder(null);
                helpScroll.setBackground(new Color(245, 245, 255));
                helpScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                // Bottone di chiusura
                final JButton closeButton = new JButton("Chiudi");
                closeButton.addActionListener(e1 -> {
                    helpDialog.setVisible(false);
                    helpDialog.dispose();
                });
                final JPanel closePanel = new JPanel(new FlowLayout());
                closePanel.setAlignmentY(CENTER_ALIGNMENT);
                closePanel.setBackground(null);
                closePanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
                closePanel.add(closeButton);
                contentPane.setBackground(new Color(245, 245, 255));
                contentPane.add(closePanel, BorderLayout.SOUTH);
                contentPane.add(new JSeparator(), BorderLayout.CENTER);
                contentPane.add(helpScroll, BorderLayout.NORTH);
                // Shake & serve cold
                helpDialog.setContentPane(contentPane);
                helpDialog.setLocationByPlatform(true);
                helpDialog.setResizable(false);
                helpDialog.pack();
                SwingUtilities.invokeLater(() -> helpDialog.setVisible(true));
            } else
                helpDialog.toFront();
        });
        helpMenu.setMnemonic(KeyEvent.VK_F1);
        helpMenu.add(infoHelpMenu);
        helpMenu.addSeparator();

        // Aiuto->Informazioni su
        final JMenuItem aboutHelpMenu = new JMenuItem("Informazioni su Simply");
        final AboutDialog about = new AboutDialog(this, "Informazioni su Simply");
        aboutHelpMenu.addActionListener(e -> about.setVisible(true));
        helpMenu.setMnemonic(KeyEvent.VK_F12);
        helpMenu.add(aboutHelpMenu);

        // Aggiunta menu alla barra
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);
    }

    private void toggleTableCol(final int variableNumber) {
        // Se il numero e' maggiore di due, disattiva la possibilita' del
        // grafico.
        if (variableNumber > 2)
            graphicButton.setEnabled(false);
        if (variableNumber <= 2)
            graphicButton.setEnabled(true);

        // Tutte le righe fino all'attuale numero di vincoli
        for (int i = 0; i < Constants.MAX_SIZE; i++) {
            if (i < variableNumber) {
                getObjectiveCoefficientVector()[i].setVisible(true);
                objectiveLabel[i].setVisible(true);
            } else {
                getObjectiveCoefficientVector()[i].setVisible(false);
                objectiveLabel[i].setVisible(false);
            }
            // Tutte le colonne fino a questa
            for (int j = 0; j < Constants.MAX_SIZE; j++)
                // Rendi accessibile
                getConstraintCoefficients()[i][j].setVisible(i < getConstraintNumber() && j < variableNumber);
        }

        // Trucchetto per far modificare la visualizzazione anche qui;
        // sembra che la tabella venga aggiornata solo se modifico gli spinners!
        constraintTypeSpinner[1].setVisible(false);
        constraintTypeSpinner[1].setVisible(true);
        this.validate();
    }

    private void toggleTableRow(final int constraintNumber) {
        // Tutte le righe fino a questa
        for (int i = 0; i < Constants.MAX_SIZE; i++) {
            if (i < constraintNumber) {
                getKnownValuesVector()[i].setVisible(true);
                constraintTypeSpinner[i].setVisible(true);
            } else {
                getKnownValuesVector()[i].setVisible(false);
                constraintTypeSpinner[i].setVisible(false);
            }
            // Tutte le colonne fino all'attuale numero di variabili
            for (int j = 0; j < Constants.MAX_SIZE; j++)
                // Rendi accessibile
                getConstraintCoefficients()[i][j].setVisible(j < getVariableNumber() && i < constraintNumber);
        }
        this.validate();
    }

    /** Espone il numero di variabili artificiali. */
    public int getArtificialNumber() {
        int a = 0;
        final Constants[] cts = getConstraintTypes();
        for (final Constants ct : cts)
            if (ct.equals(Constants.Equality) || ct.equals(Constants.GreaterThan))
                a++;
        return a;
    }

    /** Restituisce il tableau iniziale (artificiale) come <code>Matrix</code>. */
    public Matrix getArtificialTableau() {
        final Constants[] cts = getConstraintTypes();
        final int v = getVariableNumber(),
                // Il numero di vincoli e' pari ad y + s.
                y = getSlackNumber(), s = getSurplusNumber(), a = getArtificialNumber();
        // Il numero di righe del tableau e' pari a y + a + 1;
        // il numero di colonne, a v + y + s + a + 1.
        final int tr = y + a + 1, tc = v + y + s + a + 1;
        final Matrix tableauMatrix = new Matrix(tr, tc, 0);
        // Inizializzo la prima riga.
        for (int i = 0; i < tc; i++)
            // Variabili decisionali, slack e surplus vanno a 0.
            if (i < v + y + s)
                tableauMatrix.setElement(0, i, 0);
                // Le variabili artificiali hanno 1 nella prima riga.
            else
                tableauMatrix.setElement(0, i, 1);
        // Sistemo il valore obiettivo a 0
        tableauMatrix.setElement(0, tc - 1, 0);
        // Sistemo le variabili artificiali
        int z = 0;
        while (z < a)
            for (int i = 0; i < cts.length; i++)
                if (cts[i].equals(Constants.GreaterThan) || cts[i].equals(Constants.Equality))
                    tableauMatrix.setElement(i + 1, v + y + s + z++, 1);
        // Inizializzo la matrice dei coefficienti
        for (int i = 0; i < y + a; i++)
            for (int j = 0; j < v; j++)
                tableauMatrix.setElement(i + 1, j, getConstraintCoefficients()[i][j].getCoefficient());
        // Inizializzo la matrice slack/surplus
        z = 0;
        while (z < y + s)
            for (int i = 0; i < cts.length; i++)
                if (cts[i].equals(Constants.LessThan))
                    tableauMatrix.setElement(i + 1, v + z++, 1);
                else if (cts[i].equals(Constants.GreaterThan))
                    tableauMatrix.setElement(i + 1, v + z++, -1);
        // Inizializzo la colonna dei termini noti
        for (int i = 0; i < y + a; i++)
            tableauMatrix.setElement(i + 1, tc - 1, getKnownValuesVector()[i].getCoefficient());
        return tableauMatrix;
    }

    /** Espone il numero selezionato di vincoli. */
    public int getConstraintNumber() {
        final JComponent editor = constraintSpinner.getEditor();
        return Integer.parseInt(((JSpinner.DefaultEditor) editor).getTextField().getText());
    }

    /** Crea e riempie una Matrix con i coefficienti dei vincoli. */
    @SuppressWarnings("unused")
    public Matrix getConstraintsAsMatrix() {
        // Crea la matrice e riempila con coefficienti
        final Matrix constraintMatrix = new Matrix(getVariableNumber(), getConstraintNumber());
        constraintMatrix.fillWithCoefficients(getConstraintCoefficients());
        return constraintMatrix;
    }

    /**
     * Crea un vettore di ConstraintType's con i tipi dei vincoli alla giusta
     * posizione.
     */
    public Constants[] getConstraintTypes() {
        final Constants[] cts = new Constants[getConstraintNumber()];
        for (int i = 0; i < getConstraintNumber(); i++) {
            if ("=".equals(constraintTypeSpinner[i].getValue()))
                cts[i] = Constants.Equality;
            if ("\u2264".equals(constraintTypeSpinner[i].getValue()))
                cts[i] = Constants.LessThan;
            if ("\u2265".equals(constraintTypeSpinner[i].getValue()))
                cts[i] = Constants.GreaterThan;
        }
        return cts;
    }

    /** Restituisce il tableau iniziale (semplice) come <code>Matrix</code>. */
    public Matrix getInitialTableau() {
        final int tr = getConstraintNumber(), tc = getVariableNumber();
        // Il tableau ha righe pari al numero di vincoli piu' uno,
        // colonne pari al numero di variabili, piu' il numero di vincoli, piu'
        // uno.
        final Matrix tableauMatrix = new Matrix(tr + 1, tr + tc + 1);
        final Matrix id = Matrix.identity(tr);
        // Casella di Z (scalare)
        tableauMatrix.setElement(0, tr + tc, 0);
        // Caselle di z - c (vettore)
        for (int i = 0; i < tc; i++)
            tableauMatrix.setElement(0, i, (getProblemType().equals(Constants.Maximize) ? -1 : 1)
                    * getObjectiveCoefficientVector()[i].getCoefficient());
        for (int i = 0; i < tr; i++) {
            // Caselle dei termini noti (vettore colonna)
            tableauMatrix.setElement(i + 1, tr + tc, getKnownValuesVector()[i].getCoefficient());
            // Caselle dei coeff. slack (a 0 all'inizio!) (vettore)
            tableauMatrix.setElement(0, i + tc, 0);
            for (int j = 0; j < tc; j++)
                tableauMatrix.setElement(i + 1, j, getConstraintCoefficients()[i][j].getCoefficient());
            for (int j = 0; j < tr; j++)
                tableauMatrix.setElement(i + 1, tc + j, id.getElement(i, j));
        }
        return tableauMatrix;
    }

    public String getProblemString() {
        float current;
        StringBuilder lp = new StringBuilder("<html><font face=\"monospaced\" size=\"4\">");
        final String xf = "<i>x</i><font size=\"3\"><sub>";
        final String xl = "</sub></font>";
        lp.append(getProblemType().equals(Constants.Maximize) ? "Max. " : "Min. ");
        lp.append("<i>Z = </i>");
        for (int i = 0; i < getVariableNumber(); i++) {
            current = getObjectiveCoefficientVector()[i].getCoefficient();
            lp.append(i == 0 && current < 0 ? "-" : "");
            lp.append(Math.abs(current) != 1 ? Constants.valueFormat.format(Math.abs((double) current))
                    : "").append(xf).append(i + 1).append(xl).append(i == getVariableNumber() - 1 ? ""
                    : (getObjectiveCoefficientVector()[i + 1].getCoefficient() >= 0 ? " + " : " - "));
        }
        lp.append("<br>s.t.<br>");
        boolean allZeroes;
        for (int i = 0; i < getConstraintNumber(); i++) {
            lp.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            allZeroes = true; // Significa: "tutti 0 all'inizio del vincolo"
            for (int j = 0; j < getVariableNumber(); j++) {
                allZeroes = allZeroes && getConstraintCoefficients()[i][j].getCoefficient() == 0;
                // Segno "-" sul primo elemento
                lp.append(j == 0 && getConstraintCoefficients()[i][j].getCoefficient() < 0 ? "- " : "");
                // Scrivo il coefficiente se non e' 1 o 0
                lp.append(Math.abs(getConstraintCoefficients()[i][j].getCoefficient()) != 1
                        && getConstraintCoefficients()[i][j].getCoefficient() != 0 ? Constants.valueFormat.format(Math
                        .abs(getConstraintCoefficients()[i][j].getCoefficient())) : "");
                // Scrivo la variabile se il coeff. non e' 0
                lp.append(getConstraintCoefficients()[i][j].getCoefficient() != 0 ? xf + (j + 1) + xl : "");
                // A meno che non si sia all'ultimo posto, scrivo
                // l'operatore per il prossimo
                // coefficiente, se il prossimo e' diverso da 0, e scrivo
                // "+" (nel caso) solo
                // se i precedenti coefficienti non sono tutti 0
                lp.append(j == getVariableNumber() - 1 ? ""
                        : (getConstraintCoefficients()[i][j + 1].getCoefficient() > 0 ? (!allZeroes ? " + " : "")
                        : (getConstraintCoefficients()[i][j + 1].getCoefficient() == 0 ? "" : " - ")));
            }
            if (!allZeroes)
                lp.append(" ").append(constraintTypeSpinner[i].getModel().getValue().toString()).append(" ").append(Constants.valueFormat.format(getKnownValuesVector()[i].getCoefficient()));
            lp.append("<br>");
        }
        lp.append("</font></html>");
        return lp.toString();
    }

    /** Restituisce il tipo del problema (di massimo o minimo). */
    public Constants getProblemType() {
        if (problemTypeSpinner.getModel().getValue().equals("Max"))
            return Constants.Maximize;
        else
            return Constants.Minimize;
    }

    /** Espone il numero di variabili slack. */
    public int getSlackNumber() {
        int y = 0;
        final Constants[] cts = getConstraintTypes();
        for (final Constants ct : cts)
            if (ct.equals(Constants.LessThan))
                y++;
        return y;
    }

    /** Espone il numero di variabili surplus. */
    public int getSurplusNumber() {
        int s = 0;
        final Constants[] cts = getConstraintTypes();
        for (final Constants ct : cts)
            if (ct.equals(Constants.GreaterThan))
                s++;
        return s;
    }

    /** Espone il numero selezionato di variabili. */
    public int getVariableNumber() {
        final JComponent editor = varSpinner.getEditor();
        return Integer.parseInt(((JSpinner.DefaultEditor) editor).getTextField().getText());
    }

    public boolean isArtificialNeeded() {
        Constants[] cts;
        // Itera lungo i tipi dei vincoli. Se trova un vincolo diverso
        // dal canonico minore-o-uguale, c'e' bisogno del metodo artificiale.
        cts = getConstraintTypes();
        for (Constants ct : cts)
            if (!ct.equals(Constants.LessThan))
                return true;
        // Altrimenti, non c'e' bisogno del metodo artificiale.
        return false;
    }

    public Coefficient[] getObjectiveCoefficientVector() {
        return objectiveCoefficientVector;
    }

    @SuppressWarnings("unused")
    public void setObjectiveCoefficientVector(Coefficient[] objectiveCoefficientVector) {
        this.objectiveCoefficientVector = objectiveCoefficientVector;
    }

    public Coefficient[] getKnownValuesVector() {
        return knownValuesVector;
    }

    @SuppressWarnings("unused")
    public void setKnownValuesVector(Coefficient[] knownValuesVector) {
        this.knownValuesVector = knownValuesVector;
    }

    public Coefficient[][] getConstraintCoefficients() {
        return constraintCoefficients;
    }

    @SuppressWarnings("unused")
    public void setConstraintCoefficients(Coefficient[][] constraintCoefficients) {
        this.constraintCoefficients = constraintCoefficients;
    }
}
