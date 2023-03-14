package it.silma.simply.core;

import it.silma.simply.utils.Constants;

import java.text.NumberFormat;

/**
 * 
 * @author Silma
 * 
 *         Implementa una matrice con alcune operazioni associate. Ovvero, somma
 *         di righe o colonne ad altre righe o colonne, prodotto righe per
 *         colonne, trasposizione, moltiplicazione o divisione di una riga per
 *         uno scalare.
 * 
 */
public class Matrix {
    /**
     * Genera una matrice identita' di ordine n.
     * 
     * Genera una matrice quadrata di lato n, con gli elementi sulla diagonale
     * pari a 1 e tutti gli altri pari a 0.
     * 
     * @param n
     *            La dimensione della matrice.
     * @return La matrice identità.
     */
    public static Matrix identity(final int n) {
        final Matrix id = new Matrix(n, n);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                // Se i == j, allora 1, altrimenti 0
                id.table[i][j] = i == j ? 1 : 0;
        return id;
    }

    final int MAX_ROWS = Constants.MAX_SIZE + 1;

    final int MAX_COLS = Constants.MAX_SIZE * 3 + 1;

    private final int nr, nc;

    private final float[][] table;

    /**
     * Crea una matrice con specificati numero di righe e colonne, ma senza
     * valori.
     * 
     * @param n
     *            Numero di righe
     * @param m
     *            Numero di colonne
     */
    public Matrix(int n, int m) {
        if (n > MAX_ROWS)
            n = MAX_ROWS;
        if (m > MAX_COLS)
            m = MAX_COLS;
        nr = n;
        nc = m;

        table = new float[nr][nc];
    }

    /**
     * Crea una matrice di dimensioni specificate, con tutti gli elementi al
     * valore specificato.
     * 
     * @param n
     *            Righe della matrice.
     * @param m
     *            Colonne della matrice.
     * @param v
     *            Valore a cui inizializzare la matrice.
     */
    public Matrix(int n, int m, final float v) {
        if (n > MAX_ROWS)
            n = MAX_ROWS;
        if (m > MAX_COLS)
            m = MAX_COLS;
        nr = n;
        nc = m;

        table = new float[nr][nc];
        for (int i = 0; i < nr; i++)
            for (int j = 0; j < nc; j++)
                table[i][j] = v;
    }

    /**
     * Aggiunge una linea ad un'altra linea, n volte.
     * 
     * @param n
     *            Il numero di volte per cui aggiungere la linea.
     * @param src
     *            La linea da aggiungere.
     * @param dst
     *            La linea a cui aggiungere la \c src.
     */
    public void addTimesLineToLine(final float n, final int src, final int dst) {
        for (int i = 0; i < nc; i++)
            table[dst][i] += n * table[src][i];
    }

    /**
     * Divide una linea per uno scalare.
     * 
     * @param f
     *            Lo scalare per cui dividere la linea.
     * @param dst
     *            L'indice della linea da dividere.
     */
    public void divideLineByScalar(final float f, final int dst) {
        for (int i = 0; i < nc; i++)
            table[dst][i] /= f;
    }

    /** @return Il numero di colonne */
    public int getNc() {
        return nc;
    }

    /** @return Il numero di righe */
    public int getNr() {
        return nr;
    }

    /**
     * Stampa su standard output.
     */
    @SuppressWarnings("unused")
    public void printToStdOut() {
        for (int i = 0; i < nr; i++) {
            for (int j = 0; j < nc; j++)
                System.out.print(table[i][j] + "\t");
            System.out.println();
        }
        System.out.println();
    }

    public void setElement(final int row, final int col, final double val) {
        table[row][col] = (float) val;
    }

    /**
     * @param row
     *            Riga dell'elemento desiderato.
     * @param col
     *            Colonna dell'elemento desiderato.
     * @param val
     *            Valore da inserire nella posizione specificata.
     */
    public void setElement(final int row, final int col, final float val) {
        table[row][col] = val;
    }

    public double delta() {
        if (this.nc != this.nr || this.nc > 2)
            throw new IllegalArgumentException("Sorry, square 2x2 only.");
        else if (this.nc == 1)
            return this.getElement(0, 0);
        else
            return this.getElement(0, 0) * this.getElement(1, 1) - (this.getElement(0, 1) * this.getElement(1, 0));
    }

    public void fillWithCoefficients(final Coefficient[][] origin) {
        fillWithCoefficients(origin, 0, nr, 0, nc);
    }

    public void fillWithCoefficients(final Coefficient[][] origin, final int sr, final int er, final int sc,
            final int ec) {
        for (int i = 0; i < (er - sr); i++)
            for (int j = 0; j < (ec - sc); j++)
                this.setElement(i, j, origin[i + sr][j + sc].getCoefficient());
    }

    @SuppressWarnings("unused")
    public Coefficient[][] getCoeffficients(final NumberFormat valueFormat) {
        // Temporary Coefficient Matrix
        final Coefficient[][] teCoMa = new Coefficient[nr][nc];
        // Riempimento
        for (int i = 0; i < nr; i++)
            for (int j = 0; j < nc; j++)
                teCoMa[i][j] = new Coefficient(this.getElement(i, j), valueFormat, i, j);

        return teCoMa;
    }

    /**
     * @param row
     *            Riga dell'elemento desiderato.
     * @param col
     *            Colonna dell'elemento desiderato.
     * @return L'elemento cercato.
     */
    public float getElement(final int row, final int col) {
        return table[row][col];
    }

    /**
     * Moltiplica una matrice per un'altra compatibile.
     * 
     * @param b
     *            La seconda matrice da moltiplicare.
     * @return Il risultato, sotto forma di matrice.
     * @throws Exception
     *             Lancia un'eccezione se le matrici non sono moltiplicabili
     *             riga-per-colonna.
     */
    @SuppressWarnings("unused")
    protected Matrix multiply(final Matrix b) throws Exception {
        if (nc != b.getNr())
            throw new Exception(
                    "Il numero di colonne della prima matrice dev'essere pari al numero di righe della seconda.");

        final Matrix result = new Matrix(nr, b.getNc());
        float current = 0.0f;

        // Per ogni riga
        for (int i = 0; i < nr; i++)
            // E per ogni colonna (quindi per ogni elemento)
            for (int j = 0; j < b.getNc(); j++) {
                // Per tutta la riga (e tutta la colonna della seconda matrice)
                for (int z = 0; z < nc; z++)
                    // Calcola la somma dei prodotti degli elementi.
                    current += getElement(i, z) * b.getElement(z, j);
                // Il risultato e' l'elemento di posto corrente, che viene
                // resettato.
                result.setElement(i, j, current);
                current = 0.0f;
            }
        return result;
    }
}
