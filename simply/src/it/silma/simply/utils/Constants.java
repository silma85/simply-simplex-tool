/**
 * 
 */
package it.silma.simply.utils;

import java.text.NumberFormat;
import java.util.Locale;

public enum Constants {
	// Tipi dei vincoli
	Equality, GreaterThan, LessThan,
	// Tipi del problema
	Maximize, Minimize,
	// Passi del problema
	PreprocessPhaseOne, InitialTableau, BfsCompute, OptimalityTest, TransitionPhaseTwo, EnteringSelect, ExitingSelect, PivotOperations, Wait,
	// Stati del problema
	Simple, ArtificialPhaseOne, ArtificialPhaseTwo, Unfeasible, Unbounded, Solved;

	// Formato dei numeri (identico lungo tutto il programma, quindi va qui)
	public static final NumberFormat valueFormat = NumberFormat.getNumberInstance(Locale.US);

	// Stato del problema
	public static Constants STATUS = Constants.Wait;
	public static Constants STEP = Constants.InitialTableau;

	// Numero valutazioni, valutazione corrente, totale (va mediata alla fine) e
	// peso degli errori.
	public static float NVALS = 0;
	public static float MARK = 30.0f;
	public static float GRADE = 0.0f;
	public static float ERROR = 1.0f;

	public final static int MAX_SIZE = 7;
	public final static int MIN_SIZE = 1;

	// Internals
	public final static String pathToRes = "it/silma/simply/res/";
}