package it.silma.simply.main;

import it.silma.simply.gui.OriginalProblemFrame;
import it.silma.simply.utils.Constants;

import javax.swing.JOptionPane;

/**
 * @author Alessandro Putzu
 * @class Simply La classe base del programma.
 * 
 *        Oltre a lanciare l'interfaccia grafica primaria, contiene le risorse
 *        di testo necessarie e alcune altre variabili di pubblica utilita'.
 * 
 */
public class Simply {

	public static void onError(String message) {
        JOptionPane
        .showMessageDialog(
                null,
                "<html>Errore irreversibile: " + message + ". <br />Il programma verr&agrave; terminato.</html>");
        System.exit(1);
	}
	
    public static void main(final String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (final ClassNotFoundException e) {
            onError(e.getMessage());
        } catch (final InstantiationException e) {
            onError(e.getMessage());
        } catch (final IllegalAccessException e) {
            onError(e.getMessage());
        } catch (final javax.swing.UnsupportedLookAndFeelException e) {
            onError(e.getMessage());
        }

        try {
			System.out.println(System.getProperty("java.vm.version"));

			// Formato numerico.
			Constants.valueFormat.setGroupingUsed(false);
			Constants.valueFormat.setMaximumFractionDigits(2);
			Constants.valueFormat.setMinimumFractionDigits(0);

			// Lancia il thread che si occupera' di mostrare la GUI.
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			        showGUI();
			        /* new BetaBox(null); */
			    }
			});
		} catch (Exception e) {
            onError(e.getMessage());
		}
    }

    public static void showGUI() {
        final OriginalProblemFrame originalProblem = new OriginalProblemFrame("Simply - Inserisci un Problema");
        originalProblem.setVisible(true);
    }

    public static void addBinaryMark(final boolean passed) {
        Constants.NVALS += 1;
        Constants.GRADE += passed ? 30 : 0;
    }

    public static void addMark() {
        Constants.NVALS += 3;
        Constants.GRADE += 3 * Constants.MARK;
        // Reimposta il voto corrente.
        Constants.MARK = 30;
    }

    public static float getGrade() {
        return Constants.GRADE / Constants.NVALS;
    }

    public static float getMark() {
        return Constants.MARK;
    }

    public static void lowerMark() {
        Constants.MARK -= (Constants.MARK - Constants.ERROR > 0 ? Constants.ERROR : 0);
    }

    public static void resetGrades() {
        Constants.NVALS = 0;
        Constants.MARK = 30.0f;
        Constants.GRADE = 0.0f;
        Constants.ERROR = 1.0f;
    }

    public static void setErrorRatio(final float cells) {
        Constants.ERROR = 4 * cells / 30;
    }

    public static void setMark(final float val) {
        Constants.MARK = val;
    }
}
