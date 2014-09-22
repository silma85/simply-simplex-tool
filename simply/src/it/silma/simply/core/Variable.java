package it.silma.simply.core;

/**
 * Una variabile del problema. Contiene informazioni sulla propria posizione,
 * sull'essere slack/surplus/artificiale, sull'essere di base e sul proprio
 * coefficiente. <strong>Non sul valore</strong>, che e' contenuto nell'array
 * BFS.
 * 
 * @author Silma
 * 
 */
public class Variable {
    // Se e' slack, surplus o artificiale
    protected boolean isSlack = false, isSurplus = false, isArtificial = false;
    // Se e' di base oppure no
    protected boolean isBasic = false;

    // Identificativo: per prime vengono le variabili decisionali,
    // poi le variabili slack/surplus ordinate secondo i vincoli,
    // poi le variabili artificiali.
    // ATTENZIONE: Mappato direttamente sul vettore BFS.
    protected int id;
    // Valore del coefficiente
    private float c;

    /** Identificativo di riga. -1 per le variabili non di base. */
    protected int rowId = -1;

    /**
     * Crea una variabile con id specificata, coefficiente specificato.
     * 
     * @param id
     *            Il numero identificativo della variabile, mappato sul vettore
     *            bfs.
     * @param coeff
     *            Il valore iniziale della variabile.
     * @param basic
     *            Stabilisce se una variabile nasce in base oppure no.
     * @param flag
     *            Utilizzata in combinazione a <code>basic</code> per stabilire
     *            se una variabile e' slack, surplus o artificiale.
     */
    public Variable(final int id, final float coeff, final boolean basic, final boolean flag) {
        this.id = id;
        c = coeff;
        isBasic = basic;
        isSlack = flag && basic;
        isSurplus = flag && !basic;
        isArtificial = basic && !flag;
    }

    protected float getCoefficient() {
        return c;
    }

    protected void setCoefficient(final float n) {
        this.c = n;
    }

    protected void toggleBasic() {
        isBasic = !isBasic;
    }
}