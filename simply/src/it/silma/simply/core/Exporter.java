package it.silma.simply.core;

import it.silma.simply.utils.Constants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/*
 * Permette di esportare in linguaggi di modellazione.
 */
public class Exporter {
    // Frame da cui raccogliere i dati
    OriginalProblemFrame opf;

    public Exporter(final OriginalProblemFrame opf) {
        this.opf = opf;
    }

    /**
     * Genera i due file che descrivono il problema in AMPL: file modello con
     * estensione .mod e file di dati con estensione .dat.
     * 
     * @param opf
     *            Il <code>OriginalProblemFrame</code> da cui prendere i dati.
     * @param name
     *            Il nome con cui verranno salvati i files.
     */
    public void generateAMPL() {
        FileOutputStream amplFile;
        final AmplChooser chooser = new AmplChooser();

        final int ans = chooser.showDialog(opf, "Salva");
        if (ans == JFileChooser.APPROVE_OPTION)
            try {
                int overwrite = JOptionPane.YES_OPTION;
                if (chooser.getSelectedFile().exists()
                        || new File(chooser.getSelectedFile().getName() + ".mod").exists()
                        || new File(chooser.getSelectedFile().getName() + ".dat").exists())
                    overwrite = JOptionPane.showConfirmDialog(opf, "Il file esiste già, sovrascrivere?",
                            "Richiesta conferma", JOptionPane.YES_NO_OPTION);
                if (overwrite == JOptionPane.YES_OPTION) {
                    String pathname = chooser.getSelectedFile().getPath();
                    String name = chooser.getSelectedFile().getName();
                    if (pathname.lastIndexOf('.') != -1
                            && pathname.lastIndexOf(File.separator) < pathname.lastIndexOf('.'))
                        pathname = pathname.substring(0, pathname.lastIndexOf('.'));
                    if (name.lastIndexOf(".") != -1 && name.lastIndexOf(".") != 0)
                        name = name.substring(0, name.lastIndexOf("."));
                    // Genera .mod
                    amplFile = new FileOutputStream(pathname + ".mod");
                    final PrintStream mod = new PrintStream(amplFile);
                    mod.println("#" + name + ".mod - File del modello AMPL\n");
                    generateAMPLMod(mod);
                    amplFile.close();
                    // Genera .dat
                    amplFile = new FileOutputStream(pathname + ".dat");
                    final PrintStream dat = new PrintStream(amplFile);
                    dat.println("#" + name + ".dat - File di dati AMPL\n");
                    generateAMPLDat(dat);
                    amplFile.close();

                    JOptionPane.showMessageDialog(
                            opf,
                            "I files "
                                    + name
                                    + ".mod e "
                                    + name
                                    + ".dat sono stati creati nel percorso "
                                    + chooser
                                            .getSelectedFile()
                                            .getPath()
                                            .substring(0,
                                                    chooser.getSelectedFile().getPath().lastIndexOf(File.separator)));
                }
            } catch (final FileNotFoundException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            } catch (final IOException e) {
                e.printStackTrace();
            }
    }

    /*
     * Genera un file MPS.
     */
    public void generateMPS() {

    }

    /*
     * Genera il DAT file di AMPL.
     */
    private void generateAMPLDat(final PrintStream dat) {
        final Constants[] cts = opf.getConstraintTypes();
        final float[] blt = new float[opf.getSlackNumber()], bgt = new float[opf.getSurplusNumber()], beq = new float[opf
                .getArtificialNumber() - opf.getSurplusNumber()];
        final Coefficient[][] sorted = new Coefficient[opf.getConstraintNumber()][opf.getVariableNumber()];
        int lt = 0, gt = 0, eq = 0;
        // Riempio i tre vettori con valori che rappresentano i termini noti
        // di ciascuno dei tipi di vincoli. Servira' in seguito.
        for (int i = 0; i < cts.length; i++) {
            if (cts[i].equals(Constants.LessThan)) {
                blt[lt] = opf.knownValuesVector[i].getCoefficient();
                // I coefficienti di minoranza sono ai primi posti.
                sorted[lt] = opf.constraintCoefficients[i];
                lt++;
            }
            if (cts[i].equals(Constants.GreaterThan)) {
                bgt[gt] = opf.knownValuesVector[i].getCoefficient();
                // I coefficienti di maggioranza sono dopo quelli di minoranza.
                sorted[blt.length + gt] = opf.constraintCoefficients[i];
                gt++;
            }
            if (cts[i].equals(Constants.Equality)) {
                beq[eq] = opf.knownValuesVector[i].getCoefficient();
                // I coefficienti di uguaglianza sono agli ultimi posti.
                sorted[blt.length + bgt.length + eq] = opf.constraintCoefficients[i];
                eq++;
            }
        }
        // Coefficienti della funzione obiettivo
        dat.println("## Coefficienti della funzione obiettivo");
        for (int i = 0; i < opf.getVariableNumber(); i++) {
            if (i == 0)
                dat.println("param:\tV:\tc :=");
            dat.print("\t" + (i + 1) + "\t" + opf.objectiveCoefficientVector[i].getCoefficient());
            dat.println(i == opf.getVariableNumber() - 1 ? " ;" : "");
        }
        // Termini noti
        dat.println("## Termini noti");
        for (int i = 0; i < blt.length; i++) {
            if (i == 0)
                dat.println("param:\tLT:\tbLT :=");
            dat.print("\t" + (i + 1) + "\t" + blt[i]);
            dat.println(i == blt.length - 1 ? " ;" : "");
        }
        for (int i = 0; i < bgt.length; i++) {
            if (i == 0)
                dat.println("param:\tGT:\tbGT :=");
            dat.print("\t" + (i + 1) + "\t" + bgt[i]);
            dat.println(i == bgt.length - 1 ? " ;" : "");
        }
        for (int i = 0; i < beq.length; i++) {
            if (i == 0)
                dat.println("param:\tET:\tbET :=");
            dat.print("\t" + (i + 1) + "\t" + beq[i]);
            dat.println(i == beq.length - 1 ? " ;" : "");
        }
        // Coefficienti dei vincoli di minoranza
        if (opf.getSlackNumber() > 0) {
            dat.println("## Coefficienti dei vincoli");
            dat.println("param aLT:");
            for (int i = 0; i < blt.length + 1; i++) {
                for (int j = 0; j < opf.getVariableNumber() + 1; j++)
                    if (i == 0)
                        dat.print(j == 0 ? "" : "\t" + j);
                    else if (j == 0)
                        dat.print(i);
                    // I coefficienti di minoranza sono ai primi posti.
                    else
                        dat.print("\t" + sorted[i - 1][j - 1].getCoefficient());
                if (i == 0)
                    dat.println(" :=");
                else if (i == blt.length)
                    dat.println(" ;");
                else
                    dat.println();
            }
        }
        // Coefficienti dei vincoli di maggioranza
        if (opf.getSurplusNumber() > 0) {
            dat.println("param aGT:");
            for (int i = 0; i < bgt.length + 1; i++) {
                for (int j = 0; j < opf.getVariableNumber() + 1; j++)
                    if (i == 0)
                        dat.print(j == 0 ? "" : "\t" + j);
                    else if (j == 0)
                        dat.print(i);
                    // I coefficienti di maggioranza sono dopo quelli di
                    // minoranza.
                    else
                        dat.print("\t" + sorted[blt.length + i - 1][j - 1].getCoefficient());
                if (i == 0)
                    dat.println(" :=");
                else if (i == bgt.length)
                    dat.println(" ;");
                else
                    dat.println();
            }
        }
        // Coefficienti dei vincoli di uguaglianza
        if (beq.length > 0) {
            dat.println("param aET:");
            for (int i = 0; i < beq.length + 1; i++) {
                for (int j = 0; j < opf.getVariableNumber() + 1; j++)
                    if (i == 0)
                        dat.print(j == 0 ? "" : "\t" + j);
                    else if (j == 0)
                        dat.print(i);
                    // I coefficienti di uguaglianza sono dopo tutti gli altri.
                    else
                        dat.print("\t" + sorted[blt.length + bgt.length + i - 1][j - 1].getCoefficient());
                if (i == 0)
                    dat.println(" :=");
                else if (i == beq.length)
                    dat.println(" ;");
                else
                    dat.println();
            }
        }
    }

    /*
     * Genera il MOD file di AMPL.
     */
    private void generateAMPLMod(final PrintStream mod) {
        // Insiemi
        mod.println("## Insiemi: variabili e vincoli");
        mod.println("set V;\t\t# Insieme delle variabili decisionali");
        if (opf.getSlackNumber() > 0)
            mod.println("set LT;\t\t# Insieme dei vincoli di minoranza");
        if (opf.getSurplusNumber() > 0)
            mod.println("set GT;\t\t# Insieme dei vincoli di maggioranza");
        if (opf.getArtificialNumber() - opf.getSurplusNumber() > 0)
            mod.println("set ET;\t\t# Insieme dei vincoli di uguaglianza");
        // Parametri
        mod.println("\n## Parametri");
        mod.println("param c{V};\t\t# Coefficienti della funzione obiettivo");
        if (opf.getSlackNumber() > 0)
            mod.println("param bLT{LT};\t\t# Valori dei termini noti nei vincoli di minoranza");
        if (opf.getSurplusNumber() > 0)
            mod.println("param bGT{GT};\t\t# Valori dei termini noti nei vincoli di maggioranza");
        if (opf.getArtificialNumber() - opf.getSurplusNumber() > 0)
            mod.println("param bET{ET};\t\t# Valori dei termini noti nei vincoli di uguaglianza");
        // E' necessario dividere i vincoli nei tre tipi.
        if (opf.getSlackNumber() > 0)
            mod.println("param aLT{LT,V};\t\t# Coefficienti dei vincoli di minoranza");
        if (opf.getSurplusNumber() > 0)
            mod.println("param aGT{GT,V};\t\t# Coefficienti dei vincoli di maggioranza");
        if (opf.getArtificialNumber() - opf.getSurplusNumber() > 0)
            mod.println("param aET{ET,V};\t\t# Coefficienti dei vincoli di uguaglianza");
        // Variabili
        mod.println("\n## Variabili");
        mod.println("var x{V} >= 0;");
        // Obiettivo
        mod.println("\n## Obiettivo");
        mod.println((opf.getProblemType().equals(Constants.Maximize) ? "maximize" : "minimize") + " Z:");
        mod.println("\tsum {j in V} c[j]*x[j];");
        // Vincoli
        mod.println("## Vincoli");
        if (opf.getSlackNumber() > 0) { // Di minoranza
            mod.println("subject to lt {i in LT}:");
            mod.println("\tsum {v in V} aLT[i,v]*x[v]<=bLT[i];");
        }
        if (opf.getSurplusNumber() > 0) { // Di maggioranza
            mod.println("subject to gt {j in GT}:");
            mod.println("\tsum {v in V} aGT[j,v]*x[v]>=bGT[j];");
        }
        if (opf.getArtificialNumber() - opf.getSurplusNumber() > 0) { // Di
            // uguaglianza
            mod.println("subject to et {k in ET}:");
            mod.println("\tsum {v in V} aET[k,v]*x[v]=bET[k];");
        }
    }
}

@SuppressWarnings("serial")
class AmplChooser extends JFileChooser implements ActionListener {
    public AmplChooser() {
        super(System.getProperty("user.dir"));
        this.addChoosableFileFilter(new AmplFilter());
    }

    public void actionPerformed(final ActionEvent e) {
        System.out.println(e.getSource().toString());
    }
}

class AmplFilter extends FileFilter {
    @Override
    public boolean accept(final File file) {
        // Se è .mod o .dat, accettalo.
        final String f = file.getName();
        final int s = f.lastIndexOf(".");
        if (s != 0 && s != f.length() - 1)
            if (f.substring(s + 1).equals("mod") || f.substring(s + 1).equals("dat"))
                return true;
        // Se è una directory, accettala.
        if (file.isDirectory())
            return true;
        // Altrimenti, no.
        return false;
    }

    @Override
    public String getDescription() {
        return "AMPL Model Files and Data Files";
    }
}
