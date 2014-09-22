/**
 * 
 */
package it.silma.simply.core;

import it.silma.simply.main.Simply;
import it.silma.simply.utils.Constants;
import it.silma.simply.utils.Messages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

/**
 * @author Silma
 * 
 */
@SuppressWarnings("serial")
public class SolverFrame extends JFrame implements ActionListener {
    /** Frame contentente il problema e tutti i dati iniziali. */
    protected OriginalProblemFrame sourceProblem;
    /** Solutore per l'attuale problema. */
    protected Solver solver;

    // Pannelli
    private final JPanel topPanel;
    // Pannelli
    private final JPanel centerPanel;
    // Pannelli
    private final JPanel bfsPanel;
    // Pannelli
    private final JPanel buttonPanel;
    // Pannelli
    private final JPanel inputPanel;
    // Aree dati
    private JTextPane requestsPane;
    private JScrollPane requestsScrollArea;
    private JLabel statusArea;
    // Bottoni
    private JButton validateButton;
    // Bottoni
    private JButton solutionButton;
    // Bottoni
    private JButton stepButton;
    // Il tableau "visualizzabile" e' uno solo; conosco
    // i valori delle partizioni abbastanza per poterlo dividere
    // al momento in cui ce ne sia bisogno.
    protected Coefficient[][] extTableau;
    JLabel[] topRowLabel;
    // Anche la BFS e' resa interattiva e confrontabile
    protected Coefficient[] extBFS;
    private int tableauRows;
    private int tableauCols;
    final int constraintNumber;
    final int variableNumber;
    // Furbazzo
    private boolean solution = false;

    public SolverFrame(final OriginalProblemFrame sourceProblem, final String title) throws HeadlessException {
        super(title);
        this.setResizable(false);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(new Color(245, 245, 255));

        this.sourceProblem = sourceProblem;
        this.constraintNumber = sourceProblem.getConstraintNumber();
        this.variableNumber = sourceProblem.getVariableNumber();

        // Crea il solutore per questo problema
        solver = new Solver(sourceProblem.getVariableNumber(), sourceProblem.getConstraintNumber(), this);

        // Layout dei pannelli
        topPanel = new JPanel(new GridLayout());
        centerPanel = new JPanel();
        bfsPanel = new JPanel(new GridLayout(2, 0));
        buttonPanel = new JPanel(new BorderLayout());
        inputPanel = new JPanel(new BorderLayout());

        // Colori
        topPanel.setBackground(null);
        bfsPanel.setBackground(null);
        buttonPanel.setBackground(null);
        inputPanel.setBackground(null);
        centerPanel.setBackground(null);

        // Bordi dei pannelli
        final Border emptBorder = BorderFactory.createEmptyBorder(4, 4, 4, 4);
        topPanel.setBorder(BorderFactory.createCompoundBorder(emptBorder,
                BorderFactory.createTitledBorder("Informazioni")));
        centerPanel.setBorder(emptBorder);
        bfsPanel.setBorder(emptBorder);
        buttonPanel.setBorder(emptBorder);
        inputPanel.setBorder(emptBorder);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(emptBorder,
                BorderFactory.createTitledBorder("Tableau e BFS")));

        // Inizializza gli elementi dei vari pannelli e aggiunge
        // all'interfaccia
        initMenus();
        initBottomAndTopPanel();
        initCenterAndBfsPanel();

        // Aggiunge i componenti principali
        inputPanel.add(centerPanel, BorderLayout.CENTER);
        inputPanel.add(bfsPanel, BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.PAGE_START);
        this.add(inputPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);

        // Finally
        this.pack();
        this.setLocationByPlatform(true);
        this.setAlwaysOnTop(true);
    }

    /**
     * Sommario delle signature dei bottoni: validateButton: "evaluate"
     * solutionButton: "solution" stepButton: "next_step"
     */
    public void actionPerformed(final ActionEvent e) {
        // E' stato premuto il bottone "Convalida"
        if (e.getActionCommand().equals(validateButton.getActionCommand()))
            doEvaluation();
        // E' stato premuto il bottone "Soluzione"
        else if (e.getActionCommand().equals(solutionButton.getActionCommand())) {
            doSolutionRequest();
            // Non piu' di una volta per volta.
            solutionButton.setEnabled(false);
        }
        // E' stato premuto il bottone "Procedi"
        else if (e.getActionCommand().equals(stepButton.getActionCommand()))
            setNextStep();
    }

    private void doEvaluation() {
        if (solver.isTableauCorrect()) {
            this.setRequestText(Messages.TABLEAU_GOOD);
            stepButton.setEnabled(true);
            solutionButton.setEnabled(false);
            validateButton.setEnabled(false);
        } else {
            this.setRequestText(Messages.TABLEAU_BAD);
            solutionButton.setEnabled(true);
        }
        // Aggiunge il voto (aggiustato da isTableauCorrect()) e ripristina
        // a
        // 30.
        // Se e' stata richiesta la soluzione, uno non merita voto.
        if (!solution)
            Simply.addMark();
        solution = false;
    }

    private boolean doFeasibleQuestion() {
        final int ans = JOptionPane.showConfirmDialog(this, Messages.HTML + Messages.QUESTION_FEASIBLE
                + Messages.HTML_END, "Ammissibilita'", JOptionPane.YES_NO_OPTION);
        if (solver.isFeasible()) {
            // Se abbiamo risposto si' ad un tableau ammissibile
            if (ans == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, Messages.HTML + Messages.INFO_CORRECT_ANS + Messages.INFO_FEASIBLE
                        + Messages.HTML_END, Messages.INFO_CORRECT_ANS, JOptionPane.INFORMATION_MESSAGE);
                Simply.addBinaryMark(true);
            } else {
                // Se abbiamo risposto no ad un tableau ammissibile
                JOptionPane.showMessageDialog(this, Messages.HTML + Messages.INFO_WRONG_ANS + Messages.INFO_FEASIBLE
                        + Messages.HTML_END, Messages.INFO_WRONG_ANS, JOptionPane.ERROR_MESSAGE);
                Simply.addBinaryMark(false);
            }
        } else // Se abbiamo risposto si' ad un tableau non ammissibile
        if (ans == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, Messages.HTML + Messages.INFO_WRONG_ANS + Messages.INFO_UNFEASIBLE
                    + Messages.HTML_END, Messages.INFO_WRONG_ANS, JOptionPane.ERROR_MESSAGE);
            Simply.addBinaryMark(false);
        } else {
            // Se abbiamo risposto no ad un tableau non ammissibile
            JOptionPane.showMessageDialog(this, Messages.HTML + Messages.INFO_CORRECT_ANS + Messages.INFO_UNFEASIBLE
                    + Messages.HTML_END, Messages.INFO_CORRECT_ANS, JOptionPane.INFORMATION_MESSAGE);
            Simply.addBinaryMark(true);
        }
        return solver.isFeasible();
    }

    private void doShowGrade() {
        final float g = Simply.getGrade();
        String comment = Messages.HTML + "Il tuo voto (in trentesimi) &egrave; <b>" + Constants.valueFormat.format(g)
                + "</b>.";
        if (g < 12)
            comment += "<br><br>Hai <b>numerose lacune</b> e hai fatto molti errori,"
                    + "<br>oppure hai premuto tasti non richiesti. <b>Impegnati"
                    + "<br>di pi&ugrave; per la prossima esecuzione.";
        else if (g < 18)
            comment += "<br><br>Hai <b>qualche lacuna</b>, da colmare con pi&ugrave; esercizi"
                    + "<br>svolti. Continua a studiare e vedrai che potrai" + "<br>ottenere un buon risultato.";
        else if (g < 24)
            comment += "<br><br>(da 18 a 24 escluso)";
        else if (g < 30)
            comment += "<br><br>(da 23 a 30 escluso)";
        else
            comment += "<br><br><b>Perfetto</b>, non hai commesso neanche un errore."
                    + "Puoi andare a dare una mano ai compagni. <b>Continua cos&igrave;!</b>.";
        comment += Messages.HTML_END;
        JOptionPane.showMessageDialog(this, comment, "Valutazione", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doSolutionRequest() {
        solution = true;
        solver.getCoefficientTableau(extTableau);
        solver.getCoefficientBFS(extBFS);
        this.setRequestText(Messages.TABLEAU_GIVEUP);
        // Pessimo voto.
        Simply.setMark(0);
        Simply.addMark();
    }

    private void doUnboundedQuestion() {
        // La domanda chiede se il problema e' limitato.
        final int ans = JOptionPane.showConfirmDialog(this, Messages.QUESTION_UNBOUNDED, "Rispondi alla domanda",
                JOptionPane.YES_NO_OPTION);
        if (solver.isUnbounded()) {
            // Se ho risposto si' ad un problema non limitato
            if (ans == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, Messages.INFO_UNBOUNDED, Messages.INFO_WRONG_ANS,
                        JOptionPane.ERROR_MESSAGE);
                Simply.addBinaryMark(false);
            } else {
                // Se ho risposto no ad un problema non limitato
                JOptionPane.showMessageDialog(this, Messages.INFO_UNBOUNDED, Messages.INFO_CORRECT_ANS,
                        JOptionPane.INFORMATION_MESSAGE);
                Simply.addBinaryMark(true);
            }
        } else // Se ho risposto si' ad un problema limitato
        if (ans == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, Messages.INFO_BOUNDED, Messages.INFO_CORRECT_ANS,
                    JOptionPane.INFORMATION_MESSAGE);
            Simply.addBinaryMark(true);
        } else {
            // Se ho risposto no ad un problema limitato
            JOptionPane.showMessageDialog(this, Messages.INFO_BOUNDED, Messages.INFO_WRONG_ANS,
                    JOptionPane.ERROR_MESSAGE);
            Simply.addBinaryMark(false);
        }
        // Termina il tutto in caso di problema illimitato.
        if (solver.isUnbounded()) {
            setRequestText(Messages.TABLEAU_UNBOUNDED + Constants.valueFormat.format(Simply.getGrade()) + "</b>."
                    + Messages.HTML_END);
            setStatusText(Messages.STATUS_FINISHED);
            validateButton.setEnabled(false);
            solutionButton.setEnabled(false);
            stepButton.setEnabled(false);
            // Il momento del voto.
            doShowGrade();
        }
    }

    /** Bottoni e la barra di stato. */
    private void initBottomAndTopPanel() {
        // Area delle domande
        requestsPane = new JTextPane();
        requestsPane.setContentType("text/html");
        if (Constants.STATUS.equals(Constants.Simple))
            setRequestText(Messages.TABLEAU_PREPROCESS);
        else
            setRequestText(Messages.TABLEAU_PHASE_ONE_BEGIN);
        requestsPane.setEditable(false);
        requestsPane.setBackground(this.getContentPane().getBackground());
        requestsScrollArea = new JScrollPane(requestsPane);
        requestsScrollArea.setPreferredSize(new Dimension(0, 150));
        requestsScrollArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        requestsScrollArea.setBackground(this.getContentPane().getBackground());
        requestsScrollArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        topPanel.add(requestsScrollArea);

        // Barra di stato iniziale (problema di massimo o minimo)
        statusArea = new JLabel(Constants.STATUS.equals(Constants.Simple) ? (sourceProblem.getProblemType().equals(
                Constants.Maximize) ? Messages.STATUS_MAXIMIZE : Messages.STATUS_MINIMIZE) : Messages.STATUS_PHASE_ONE);
        statusArea.setAlignmentX(LEFT_ALIGNMENT);
        statusArea.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        buttonPanel.add(statusArea, BorderLayout.AFTER_LAST_LINE);

        // Pulsanti: convalida, richiedi soluzione, procedi con l'algoritmo
        validateButton = new JButton("Convalida");
        validateButton.addActionListener(this);
        validateButton.setActionCommand("evaluate");
        buttonPanel.add(validateButton, BorderLayout.CENTER);

        solutionButton = new JButton("Soluzione");
        solutionButton.addActionListener(this);
        solutionButton.setActionCommand("solution");
        solutionButton.setEnabled(false);
        buttonPanel.add(solutionButton, BorderLayout.WEST);

        stepButton = new JButton("Procedi");
        stepButton.addActionListener(this);
        stepButton.setActionCommand("next_step");
        stepButton.setEnabled(false);
        buttonPanel.add(stepButton, BorderLayout.EAST);
    }

    /**
     * Il tableau ha colonne pari alla somma del numero di variabili e del
     * numero di vincoli piu' la colonna dei termini noti, righe pari al numero
     * di vincoli piu' la riga della funzione obiettivo. Ma attenzione: le
     * colonne aumentano se si tratta di un problema artificiale.<br>
     * <br>
     * <b>Nota:</b> Il comportamento dei campi per la scelta delle variabili
     * entranti o uscenti, e' definito qui mediante ActionListener interni.
     * 
     */
    private void initCenterAndBfsPanel() {
        tableauCols = variableNumber + sourceProblem.getSlackNumber() + sourceProblem.getSurplusNumber()
                + sourceProblem.getArtificialNumber() + 1;
        tableauRows = 1 + constraintNumber;
        centerPanel.setLayout(new GridLayout(tableauRows + 1, tableauCols + 1));
        extTableau = new Coefficient[tableauRows][tableauCols];
        // Etichette del tableau
        topRowLabel = new JLabel[tableauCols];
        // Imposto la gravita' dell'errore.
        Simply.setErrorRatio(tableauRows * tableauCols);

        for (int i = 0; i < tableauRows; i++) {
            // Etichette della prima riga
            if (i == 0)
                for (int j = 0; j < tableauCols; j++) {
                    if (j < variableNumber)
                        topRowLabel[j] = new JLabel("<html><font face=\"serif\" size=\"4\"><i>x</i></font>"
                                + "<font size=\"3\"><sub>" + (1 + j) + "</sub></font<</html>");
                    else if (j < variableNumber + sourceProblem.getSlackNumber() + sourceProblem.getSurplusNumber())
                        topRowLabel[j] = new JLabel("<html><font face=\"serif\" size=\"4\"><i>s</i></font>"
                                + "<font size=\"3\"><sub>" + (1 + j - variableNumber) + "</sub></font<</html>");
                    else if (j < variableNumber + sourceProblem.getSlackNumber() + sourceProblem.getSurplusNumber()
                            + sourceProblem.getArtificialNumber())
                        topRowLabel[j] = new JLabel(
                                "<html><font face=\"serif\" size=\"4\"><i>a</i></font>"
                                        + "<font size=\"3\"><sub>"
                                        + (1 + j - variableNumber - sourceProblem.getSlackNumber() - sourceProblem
                                                .getSurplusNumber()) + "</sub></font<</html>");
                    else
                        topRowLabel[j] = new JLabel("<html><font face=\"serif\" size=\"4\"><i>"
                                + (sourceProblem.getProblemType().equals(Constants.Maximize) ? "" : "-")
                                + "Z</i></font></html>");
                    centerPanel.add(topRowLabel[j]);
                }
            for (int j = 0; j < tableauCols; j++) {
                extTableau[i][j] = new Coefficient(0, Constants.valueFormat, i, j);
                // Registra un listener per i clic per la prima riga.
                if (i == 0 && j < tableauCols - 1)
                    extTableau[i][j].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(final MouseEvent me) {
                            if (Solver.pvtCol != -1 && Constants.STEP.equals(Constants.EnteringSelect)) {
                                // Le prossime sette righe controllano
                                // l'evidenziazione di una sola colonna alla
                                // volta.
                                for (int i = 0; i < tableauCols - 1; i++) {
                                    final Color color = i < variableNumber ? Color.YELLOW
                                            : (i >= variableNumber + sourceProblem.getSlackNumber()
                                                    + sourceProblem.getSurplusNumber() ? Color.PINK : Color.WHITE);
                                    extTableau[0][i].setBackground(color);
                                }
                                for (int i = 0; i < tableauCols - 1; i++)
                                    for (int j = 1; j < tableauRows; j++)
                                        extTableau[j][i].setBackground(Color.WHITE);
                                for (int i = 0; i < tableauRows; i++)
                                    extTableau[i][((Coefficient) me.getSource()).col].setBackground(new Color(180, 220,
                                            180));
                                // Le righe seguenti controllano la
                                // correttezza
                                // della scelta.
                                if (((Coefficient) me.getSource()).col == Solver.pvtCol) {
                                    setRequestText(Messages.TABLEAU_ENTERING_GOOD);
                                    // Attivo il bottone per la fase
                                    // successiva.
                                    stepButton.setEnabled(true);
                                    Constants.STEP = Constants.ExitingSelect;
                                } else {
                                    Simply.setMark(Simply.getMark() / 2);
                                    // Se la variabile selezionata non e'
                                    // quella
                                    // candidata ad uscire
                                    if (((Coefficient) me.getSource()).getCoefficient() < 0)
                                        setRequestText(Messages.TABLEAU_ENTERING_NO_GOOD);
                                    else
                                        setRequestText(Messages.TABLEAU_ENTERING_BAD);
                                }
                            }
                        }
                    });
                // Registra un listener per i clic per la prima colonna.
                if (i > 0 && j == 0)
                    extTableau[i][j].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(final MouseEvent me) {
                            if (Solver.pvtRow != -1 && Constants.STEP.equals(Constants.ExitingSelect)) {
                                // Le prossime cinque righe controllano
                                // l'evidenziazione di una sola riga alla
                                // volta.
                                for (int i = 1; i < tableauRows; i++)
                                    for (int j = 0; j < tableauCols - 1; j++)
                                        if (j != Solver.pvtCol)
                                            extTableau[i][j].setBackground(Color.WHITE);
                                for (int i = 0; i < tableauCols - 1; i++)
                                    extTableau[((Coefficient) me.getSource()).row][i].setBackground(new Color(180, 220,
                                            180));
                                // Le righe seguenti controllano la
                                // correttezza
                                // della scelta.
                                if (((Coefficient) me.getSource()).row == Solver.pvtRow) {
                                    setRequestText(Messages.TABLEAU_EXITING_GOOD);
                                    // Attivo il bottone per la fase
                                    // successiva.
                                    stepButton.setEnabled(true);
                                    Constants.STEP = Constants.PivotOperations;
                                } else {
                                    Simply.setMark(Simply.getMark() / 2);
                                    // Se la variabile selezionata non e'
                                    // quella
                                    // candidata ad uscire
                                    setRequestText(Messages.TABLEAU_EXITING_BAD);
                                }
                            }
                        }
                    });
                // Giallo per i coeff. di costo ridotto
                if (i == 0 && j < variableNumber)
                    extTableau[i][j].setBackground(Color.YELLOW);
                // Ciano per i termini noti
                if (i != 0 && j == tableauCols - 1)
                    extTableau[i][j].setBackground(Color.CYAN);
                // Se presente, rosa per le variabili artificiali
                if (Constants.STATUS.equals(Constants.ArtificialPhaseOne))
                    if (i == 0
                            && j >= variableNumber + sourceProblem.getSlackNumber() + sourceProblem.getSurplusNumber())
                        extTableau[i][j].setBackground(Color.PINK);
                // Violetto per il valore obiettivo
                if (i == 0 && j == tableauCols - 1)
                    extTableau[i][j].setBackground(new Color(200, 140, 200));

                centerPanel.add(extTableau[i][j]);
            }
        }

        // Pannello della BFS, aggiunge variabili nel numero adeguato
        bfsPanel.setAlignmentX(BOTTOM_ALIGNMENT);
        extBFS = new Coefficient[tableauCols - 1];
        for (int i = 0; i < tableauCols - 1; i++)
            bfsPanel.add(new JLabel(topRowLabel[i].getText()));
        for (int i = 0; i < tableauCols - 1; i++) {
            extBFS[i] = new Coefficient(0, Constants.valueFormat, 0, i);
            extBFS[i].setBackground(new Color(180, 220, 180));
            bfsPanel.add(extBFS[i]);
        }
    }

    /** Inizializzazione menu. */
    private void initMenus() {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = new JMenu("File"), helpMenu = new JMenu("Aiuto");
        // File->Esci
        final JMenuItem exitFileMenu = new JMenuItem("Esci");
        exitFileMenu.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(exitFileMenu);
        // Aiuto->Legenda
        final JDialog legendDialog = new JDialog(this, "Legenda");
        final JMenuItem legendHelpMenu = new JMenuItem("Legenda");
        legendHelpMenu.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final JPanel contentPane = new JPanel(new BorderLayout());
                contentPane.setBackground(new Color(245, 245, 255));
                // Rettangolino colorato
                JTextField text = new JTextField();
                // Lettere x, s, a
                final String x = "<html><font face=\"serif\" size=\"4\"><i>x</i><sub><font size=\"3\">i</font></sub></font></html>";
                final String s = "<html><font face=\"serif\" size=\"4\"><i>s</i><sub><font size=\"3\">i</font></sub></font></html>";
                final String a = "<html><font face=\"serif\" size=\"4\"><i>a</i><sub><font size=\"3\">i</font></sub></font></html>";
                final String Z = "<html><font face=\"serif\" size=\"4\"><i>Z</i></font></html>";
                // Simboli della legenda
                final JPanel legendPane = new JPanel(new FlowLayout());
                final JPanel left = new JPanel(new GridLayout(9, 1, 0, 4)); // Pannello
                // con
                // colori,
                // variabili
                left.setPreferredSize(new Dimension(40, 270));
                left.setBackground(null);
                text.setBackground(Color.YELLOW);
                text.setEnabled(false);
                left.add(text); // Rettangolo giallo
                text = new JTextField();
                text.setBackground(Color.CYAN);
                text.setEnabled(false);
                left.add(text); // Rettangolo azzurro
                text = new JTextField();
                text.setBackground(Color.PINK);
                text.setEnabled(false);
                left.add(text); // Rettangolo rosa
                text = new JTextField();
                text.setBackground(new Color(200, 140, 200));
                text.setEnabled(false);
                left.add(text); // Rettangolo violetto
                text = new JTextField();
                text.setBackground(new Color(180, 220, 180));
                text.setEnabled(false);
                left.add(text); // Rettangolo verdino
                left.add(new JLabel(x)); // Variabile x
                left.add(new JLabel(s)); // Variabile s
                left.add(new JLabel(a)); // Variabile a
                left.add(new JLabel(Z)); // Valore Z
                // Testo della legenda
                final JPanel right = new JPanel(new GridLayout(9, 1, 0, 4)); // Pannello
                // delle
                // spiegazioni
                right.setPreferredSize(new Dimension(300, 270));
                right.setBackground(null);
                right.add(new JLabel("Coefficiente della funzione obiettivo"));
                right.add(new JLabel("Termine noto"));
                right.add(new JLabel("Coefficiente di una variabile artificiale"));
                right.add(new JLabel("Valore della funzione obiettivo"));
                right.add(new JLabel("Valore di una variabile nella BFS, oppure riga o colonna pivot"));
                right.add(new JLabel("Variabile decisionale"));
                right.add(new JLabel("Variabile slack o surplus"));
                right.add(new JLabel("Variabile artificiale"));
                right.add(new JLabel("Etichetta del valore obiettivo (con segno - se minimizzazione)"));
                // Aggiungo i pannelli di destra e sinistra al pannello
                // della
                // legenda
                legendPane.add(left);
                legendPane.add(right);
                legendPane.setBackground(null);
                legendPane.setOpaque(true);
                // Bottone di chiusura
                final JButton closeButton = new JButton("Chiudi");
                closeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        legendDialog.setVisible(false);
                        legendDialog.dispose();
                    }
                });
                final JPanel closePanel = new JPanel(new FlowLayout());
                closePanel.setAlignmentY(CENTER_ALIGNMENT);
                closePanel.setBackground(null);
                closePanel.add(closeButton);
                // Shake & serve cold
                contentPane.add(legendPane, BorderLayout.CENTER);
                contentPane.add(closePanel, BorderLayout.SOUTH);
                legendDialog.setResizable(false);
                legendDialog.setContentPane(contentPane);
                legendDialog.setLocationByPlatform(true);
                legendDialog.pack();
                legendDialog.setVisible(true);
            }
        });
        helpMenu.add(legendHelpMenu);
        helpMenu.addSeparator();
        // Aiuto->Informazioni su
        final JMenuItem aboutHelpMenu = new JMenuItem("Informazioni su Simply");
        final AboutBox about = new AboutBox(this, "Informazioni su Simply");
        aboutHelpMenu.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                about.setVisible(true);
            }
        });
        helpMenu.setMnemonic(KeyEvent.VK_F1);
        helpMenu.add(aboutHelpMenu);
        // Aggiunta alla barra
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);
    }

    private void setNextStep() {
        // Ripristina lo stato dei bottoni: solo "Convalida" e' attivo
        validateButton.setEnabled(true);
        solutionButton.setEnabled(false);
        stepButton.setEnabled(false);
        // Selezione del passo
        if (Constants.STEP.equals(Constants.PreprocessPhaseOne)) {
            this.setRequestText(Messages.TABLEAU_PHASE_ONE_NORMALIZE);
            solver.normalizeTableau();
            // Il prossimo passo sara' il test di ottimalita'.
            Constants.STEP = Constants.OptimalityTest;
        } else if (Constants.STEP.equals(Constants.TransitionPhaseTwo)) {
            this.setRequestText(Messages.TABLEAU_PHASE_TWO);
            this.setStatusText(Messages.STATUS_PHASE_TWO);
            solver.normalizeTableau();
            // Il prossimo passo sara' il test di ottimalita'.
            Constants.STEP = Constants.OptimalityTest;
        } else if (Constants.STEP.equals(Constants.OptimalityTest))
            doOptimalityTest(); // setEntering() e' chiamata qui, come anche
        // setPhaseTwoTableau().
        else if (Constants.STEP.equals(Constants.EnteringSelect)) {
            // Nulla.
        } else if (Constants.STEP.equals(Constants.ExitingSelect)) {
            Simply.addMark();
            doUnboundedQuestion();
            if (!solver.isUnbounded()) {
                this.setRequestText(Messages.TABLEAU_SELECT_EXITING);
                validateButton.setEnabled(false);
                // Internamente, scegliamo la variabile uscente.
                solver.setExiting();
            }
        } else if (Constants.STEP.equals(Constants.PivotOperations)) {
            // Il passo successivo sono le operazioni di pivot.
            this.setRequestText(Messages.TABLEAU_PIVOT_FIRST
                    + Constants.valueFormat.format(solver.tableau.getElement(Solver.pvtRow, Solver.pvtCol))
                    + Messages.TABLEAU_PIVOT_SECOND);
            Constants.STEP = Constants.PivotOperations;
            // Internamente, effettuo le operazioni di pivot.
            solver.pivotAround();
            // Il passo successivo e' il test di ottimalita'.
            Constants.STEP = Constants.OptimalityTest;
        } else
            // Stato sconosciuto (non dovrebbe mai essere "Wait" se e'
            // possibile
            // premere "Procedi")
            System.out.println("Stato inconsistente a seguito della pressione di \"Procedi\"");
    }

    private void setPhaseTwoCenterAndBfsPanel() {
        // Rimuove le variabili artificiali dal tableau interno.
        solver.setInternalPhaseTwoTableau();
        // Rimuove le variabili artificiali dal tableau esterno e dalla BFS
        // esterna.
        final Coefficient[][] newTableau = new Coefficient[tableauRows][tableauCols
                - sourceProblem.getArtificialNumber()];
        // Le colonne nel tableau sono una in piu' delle variabili nella
        // BFS!
        final Coefficient[] newBFS = new Coefficient[tableauCols - sourceProblem.getArtificialNumber() - 1];
        topRowLabel[tableauCols - sourceProblem.getArtificialNumber() - 1] = topRowLabel[tableauCols - 1];
        for (int i = 0; i < tableauRows; i++) {
            for (int j = 0; j < tableauCols - sourceProblem.getArtificialNumber(); j++) {
                newTableau[i][j] = extTableau[i][j];
                if (i == 0 && j < variableNumber)
                    newTableau[i][j].setBackground(Color.YELLOW);
                else
                    newTableau[i][j].setBackground(Color.WHITE);
            }
            // Il ciclo for precedente ignora il fatto che l'ultima colonna
            // sia
            // rilevante. Correggo.
            newTableau[i][tableauCols - sourceProblem.getArtificialNumber() - 1] = extTableau[i][tableauCols - 1];
            if (i > 0)
                newTableau[i][tableauCols - sourceProblem.getArtificialNumber() - 1].setBackground(Color.CYAN);
        }
        for (int i = 0; i < newBFS.length; i++)
            // Dovrebbe andare bene cosi' com'e'
            newBFS[i] = extBFS[i];
        tableauCols -= sourceProblem.getArtificialNumber();
        extTableau = newTableau;
        extBFS = newBFS;
        // Imposto il nuovo rateo di errore
        Simply.setErrorRatio(tableauRows * tableauCols);
        // Sistemo la grafica
        centerPanel.removeAll();
        centerPanel.setLayout(new GridLayout(2 + constraintNumber, 0));
        for (int i = 0; i < tableauRows; i++) {
            if (i == 0)
                for (int j = 0; j < tableauCols; j++)
                    centerPanel.add(topRowLabel[j]);
            for (int j = 0; j < tableauCols; j++)
                centerPanel.add(extTableau[i][j]);
        }
        bfsPanel.removeAll();
        bfsPanel.setLayout(new GridLayout(2, 0));
        for (int j = 0; j < tableauCols - 1; j++)
            bfsPanel.add(new JLabel(topRowLabel[j].getText()));
        for (int j = 0; j < tableauCols - 1; j++)
            bfsPanel.add(extBFS[j]);
        // Ridisegna.
        this.repaint();
        this.pack();
    }

    /**
     * Esegue il test interattivo di ottimalita' (e.g. e' l'utente a ipotizzare
     * se il tableau sia ottimo oppure no) e, se siamo alla fine della Fase Uno,
     * richiama il test di ammissibilita'.
     */
    protected void doOptimalityTest() {
        final boolean optimum = solver.isTableauOptimum();
        if (JOptionPane.showConfirmDialog(this, Messages.QUESTION_OPTIMUM, "Rispondi alla domanda",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
            if (optimum) {
                // La risposta e' giusta, il tableau e' ottimo.
                JOptionPane
                        .showMessageDialog(
                                this,
                                Messages.HTML
                                        + (Constants.STATUS.equals(Constants.ArtificialPhaseOne) ? Messages.INFO_OPTIMUM_PHASE_ONE
                                                : Messages.INFO_OPTIMUM_HIT) + Messages.HTML_END,
                                Messages.INFO_CORRECT_ANS, JOptionPane.INFORMATION_MESSAGE);
                Simply.addBinaryMark(true);
            }
            // L'utente ha risposto "si'" davanti ad un tableau non ottimo.
            else {
                JOptionPane.showMessageDialog(this, Messages.HTML + "<font color=\"red\"><b>Sbagliato!</b></font> "
                        + Messages.INFO_NOT_OPTIMUM + Messages.HTML_END, Messages.INFO_WRONG_ANS,
                        JOptionPane.ERROR_MESSAGE);
                Simply.addBinaryMark(false);
            }
        // Se la risposta e' stata "no"
        else if (optimum) {
            // L'utente ha risposto "no" davanti ad un tableau ottimo.
            JOptionPane.showMessageDialog(this, Messages.HTML + Messages.INFO_OPTIMUM_MISS + Messages.HTML_END,
                    Messages.INFO_WRONG_ANS, JOptionPane.ERROR_MESSAGE);
            Simply.addBinaryMark(false);
        } else {
            // La risposta e' giusta, il tableau non e' ottimo.
            JOptionPane.showMessageDialog(this, Messages.HTML + "<b>Esatto! </b>" + Messages.INFO_NOT_OPTIMUM
                    + Messages.HTML_END, Messages.INFO_CORRECT_ANS, JOptionPane.INFORMATION_MESSAGE);
            Simply.addBinaryMark(true);
        }
        // Se l'ottimo e' stato raggiunto, disabilita tutte le
        // funzionalita'.
        if (optimum) {
            if (Constants.STATUS.equals(Constants.Simple) || Constants.STATUS.equals(Constants.ArtificialPhaseTwo)) {
                Constants.STATUS = Constants.Solved;
                validateButton.setEnabled(false);
                stepButton.setEnabled(false);
                solutionButton.setEnabled(false);
                setRequestText(Messages.TABLEAU_SOLVED + Constants.valueFormat.format(Simply.getGrade()) + "</b>."
                        + Messages.HTML_END);
                setStatusText(Messages.STATUS_FINISHED);
                extTableau[0][tableauCols - 1].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                // Il momento del voto.
                doShowGrade();
            } else // Siamo nel passaggio dalla fase 1 alla fase 2.
            if (!doFeasibleQuestion()) {
                // Se non ammissibile, il problema termina qui.
                Constants.STATUS = Constants.Unfeasible;
                setRequestText(Messages.TABLEAU_UNFEASIBLE + Constants.valueFormat.format(Simply.getGrade()) + "</b>."
                        + Messages.HTML_END);
                setStatusText(Messages.STATUS_FINISHED);
                validateButton.setEnabled(false);
                stepButton.setEnabled(false);
                solutionButton.setEnabled(false);
                // Il momento del voto.
                doShowGrade();
            } else {
                Constants.STATUS = Constants.ArtificialPhaseTwo;
                Constants.STEP = Constants.TransitionPhaseTwo;
                setRequestText(Messages.TABLEAU_PHASE_ONE_OVER);
                setStatusText(Messages.STATUS_WAITING);
                // Sistema il tableau (e campi BFS).
                setPhaseTwoCenterAndBfsPanel();
                // Riattiva il pulsante di convalida.
                validateButton.setEnabled(true);
                Solver.pvtRow = -1;
                Solver.pvtCol = -1;
            }
        } else {
            // Altrimenti, prepara la prossima fase.
            Constants.STEP = Constants.EnteringSelect;
            setRequestText(Messages.TABLEAU_SELECT_ENTERING);
            setStatusText(Messages.STATUS_WAITING);
            validateButton.setEnabled(false);
            Solver.pvtRow = -1;
            Solver.pvtCol = -1;
            // Internamente, il solutore sceglie la variabile entrante.
            solver.setEntering();
        }
    }

    protected void setRequestText(final String request) {
        requestsPane.setText(Messages.HTML + request + Messages.HTML_END);
    }

    protected void setStatusText(final String text) {
        statusArea.setText(Messages.HTML + text + Messages.HTML_END);
    }
}
