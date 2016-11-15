package it.silma.simply.gui;

import it.silma.simply.core.Matrix;
import it.silma.simply.utils.Constants;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

@SuppressWarnings("serial")
public class GraphicDialog extends JDialog implements ActionListener {

    private OriginalProblemFrame opf;
    private Painter graph;

    private JButton closeButton = new JButton("Chiudi"), zoomIn = new JButton("+"), zoomOut = new JButton("-"),
            refresh = new JButton("Aggiorna");

    public GraphicDialog(final OriginalProblemFrame opf) {
        super(opf, "Grafico del problema");
        final GraphicDialog gv = this;
        this.setResizable(false);
        this.setLocationByPlatform(true);
        this.opf = opf;

        final JPanel contentPane = new JPanel(new BorderLayout()), buttonPanel = new JPanel(new BorderLayout()), graphPanel = new JPanel();
        contentPane.setBackground(new Color(245, 245, 255));

        initGraph(graphPanel);
        initButtons(buttonPanel);
        contentPane.add(graphPanel, BorderLayout.NORTH);
        contentPane.add(new JSeparator());
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        this.setContentPane(contentPane);
        this.pack();
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        // Se premuto il tasto di chiusura
        if (e.getSource().equals(closeButton)) {
            this.setVisible(false);
            this.dispose();
        }
        // Se premuto il tasto per aumentare lo zoom
        if (e.getSource().equals(zoomIn)) {
            graph.setZoom(2);
            if (graph.getZoom() > 40)
                zoomIn.setEnabled(false);
            if (!zoomOut.isEnabled())
                zoomOut.setEnabled(true);
            this.repaint();
        }
        // Se premuto il tasto per diminuire lo zoom
        if (e.getSource().equals(zoomOut)) {
            graph.setZoom(-2);
            if (graph.getZoom() < 10)
                zoomOut.setEnabled(false);
            if (!zoomIn.isEnabled())
                zoomIn.setEnabled(true);
            this.repaint();
        }
        // Se premuto il tasto per ridisegnare il grafico
        if (e.getSource().equals(refresh)) {
            this.repaint();
            graph.remove(graph.problemLabel);
            graph.addProblemLabel();
        }
    }

    private void initButtons(final JPanel buttonPanel) {
        // Registra le azioni
        closeButton.addActionListener(this);
        zoomIn.addActionListener(this);
        zoomOut.addActionListener(this);
        refresh.addActionListener(this);

        final JPanel left = new JPanel(), center = new JPanel(), right = new JPanel();
        // A sinistra
        JLabel l = new JLabel("In rosso, possibili funzioni obiettivo.");
        l.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        left.add(refresh);
        left.add(l);
        left.setBackground(null);
        // Al centro
        center.add(zoomOut);
        center.add(zoomIn);
        center.setBackground(null);
        // A destra
        right.add(closeButton);
        right.setBackground(null);
        // Mix
        buttonPanel.add(left, BorderLayout.LINE_START);
        buttonPanel.add(center, BorderLayout.CENTER);
        buttonPanel.add(right, BorderLayout.LINE_END);
        buttonPanel.setBackground(null);
    }

    private void initGraph(final JPanel graphPanel) {
        graphPanel.setBackground(null);

        // Qui la grafica.
        graph = new Painter(opf);
        graphPanel.add(graph);
    }
}

@SuppressWarnings("serial")
class Painter extends JComponent {
    private final int X = 512;
    private final int Y = 512;
    private int S = 30; // Fattore di zoom.
    // Area totale, per le regioni dei vincoli di maggioranza
    private final Area total = new Area(new Polygon(new int[] { 0, X, X, 0 }, new int[] { 0, 0, Y, Y }, 4));

    OriginalProblemFrame opf;
    JLabel problemLabel;

    final BasicStroke thin = new BasicStroke(1.0f);

    final BasicStroke thick = new BasicStroke(2.5f);

    final float dash[] = { 4.0f };
    final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 2.0f);

    public Painter(final OriginalProblemFrame opf) {
        this.opf = opf;
        this.setPreferredSize(new Dimension(X, Y));
        this.setBackground(null);
        this.setLayout(null);

        addProblemLabel();
    }

    @Override
    public void paint(final Graphics g0) {
        final Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(thin);

        final AffineTransform pre = g.getTransform();

        // Trasforma nelle giuste coordinate per il disegno
        final AffineTransform tra = AffineTransform.getTranslateInstance(80, -60);
        final AffineTransform originalTransform = g.getTransform();
        tra.scale(1.0, -1.0);
        tra.translate(0, -Y);
        tra.concatenate(pre);
        g.setTransform(tra);

        try {
            paintConstraints(g);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // Assi (dopo per potersi vedere)
        g.setColor(Color.BLACK);
        paintAxes(g);

        g.setTransform(originalTransform);
        super.paint(g); // Per visualizzare anche gli altri componenti.
    }

    @SuppressWarnings("unused")
    private Point getIntersection(final double[] f, final double[] g) {
        final Matrix dM = new Matrix(2, 2, 0);
        final Matrix dMx = new Matrix(2, 2, 0);
        final Matrix dMy = new Matrix(2, 2, 0);
        // Delta
        dM.setElement(0, 0, f[0]);
        dM.setElement(0, 1, f[1]);
        dM.setElement(1, 0, g[0]);
        dM.setElement(1, 1, g[1]);
        final double d = dM.delta();
        // Delta-x
        dMx.setElement(0, 0, f[2]);
        dMx.setElement(0, 1, f[1]);
        dMx.setElement(1, 0, g[2]);
        dMx.setElement(1, 1, g[1]);
        final double dx = dMx.delta();
        // Delta-y
        dMy.setElement(0, 0, f[0]);
        dMy.setElement(0, 1, f[2]);
        dMy.setElement(1, 0, g[0]);
        dMy.setElement(1, 1, g[2]);
        final double dy = dMy.delta();
        // Regola di Cramer
        if (d != 0)
            return new Point((int) (dx / d * S), (int) (dy / d * S));
        else
            return null;
    }

    private void paintAxes(final Graphics2D g) {
        int N = 120 - S * 2; // Numero di tacche degli assi
        int N_MOD = (S < 16 ? 2 : 1);
        // Assi
        g.drawLine(0, -Y / 2, 0, Y);
        g.drawLine(-X / 2, 0, X, 0);
        // Indici degli assi
        Font marks = new Font("monospaced", Font.PLAIN, 10);
        marks = marks.deriveFont(AffineTransform.getScaleInstance(1.0, -1.0));
        g.setFont(marks);
        for (int i = 1; i < N; i++) {
            for (int j = 1; j < N; j++)
                g.drawLine(i * S, j * S, i * S, j * S);
            if (i % N_MOD == 0) {
                g.drawLine(i * S, 0, i * S, -5);
                g.drawString(Integer.toString(i), (i * S) - 5, -15);
                if (i < 12) {
                    g.drawLine(-i * S, 0, -i * S, -5);
                    g.drawString(Integer.toString(-i), (-i * S) - 5, -15);
                }
            }
        }
        for (int i = 1; i < N; i++) {
            if (i % N_MOD == 0) {
                g.drawLine(0, i * S, -5, i * S);
                g.drawString(Integer.toString(i), -20, (i * S) - 5);
                if (i < 12) {
                    g.drawLine(0, -i * S, -5, -i * S);
                    g.drawString(Integer.toString(-i), -20, (-i * S) - 5);
                }
            }
        }
    }

    private void paintConstraints(final Graphics2D g) {
        final Constants[] cts = opf.getConstraintTypes();
        double a = 0, b = 0, c = 0;
        double x = 0.0, // Intersezione con 0 sull'asse x (radice)
        y = 0.0; // Intersezione con 0 sull'asse y (intercetta)
        // Riempi la regione ammissibile
        g.setPaint(new Color(185, 185, 225));
        g.fill(total);
        // Poi cancella le regioni non ammissibili
        for (int i = 0; i < cts.length; i++) {
            a = opf.getConstraintCoefficients()[i][0].getCoefficient();
            b = (opf.getConstraintCoefficients()[i][1].isVisible() ? opf.getConstraintCoefficients()[i][1].getCoefficient() : 0);
            c = opf.getKnownValuesVector()[i].getCoefficient();
            x = c / a; // y = 0
            y = c / b; // x = 0
            y *= S;
            x *= S; // Scalati sul grafico
            // Cancellazione
            g.setPaint(this.getBackground());
            paintRegion(g, -a / b, x, y, cts[i], b); // Coefficiente e intercetta
            // Anche al di fuori dei vincoli di non-negativita'
            final Area negative = new Area(new Polygon(new int[] { -X, X, X, -X }, new int[] { -Y, -Y, Y, Y }, 4));
            negative.subtract(total);
            g.fill(negative);
        }
        // Infine disegna i vincoli
        for (int i = 0; i < cts.length; i++) {
            a = opf.getConstraintCoefficients()[i][0].getCoefficient();
            b = (opf.getConstraintCoefficients()[i][1].isVisible() ? opf.getConstraintCoefficients()[i][1].getCoefficient() : 0);
            c = opf.getKnownValuesVector()[i].getCoefficient();
            x = c / a; // y = 0
            y = c / b; // x = 0
            y *= S;
            x *= S; // Scalati sul grafico
            double m = -a / b; // Coefficiente angolare
            // Vincoli per ultimi, perche' devono vedersi
            if (!cts[i].equals(Constants.Equality))
                g.setColor(Color.BLUE);
            else
                g.setColor(new Color(185, 185, 225));
            if (a != 0 & b != 0) {
                g.drawLine((int) x, 0, (int) (1 / m * Y + x), Y);
                g.drawLine((int) x, 0, (int) (1 / m * (-Y) + x), -Y);
            } else if (a == 0)
                g.drawLine(-X, (int) y, X, (int) y);
            else
                g.drawLine((int) x, -Y, (int) x, Y);
            if (b != 0 && y / S < 16)
                paintObjective(g, new Point(0, (int) y));
        }
    }

    private void paintObjective(final Graphics2D g, Point p) {
        g.setColor(Color.red);
        g.setStroke(dashed);

        final double a = opf.getObjectiveCoefficientVector()[0].getCoefficient();
        final double b = (opf.getObjectiveCoefficientVector()[1].isVisible() ? opf.getObjectiveCoefficientVector()[1]
                .getCoefficient() : 0);
        final double c = p.y * b + .01; // Gia' scalato sul grafico!

        double x = c / a; // y = 0
        double m = -a / b; // Coefficiente angolare
        g.drawLine((int) x, 0, (int) (1 / m * Y + x), Y);
        g.drawLine((int) x, 0, (int) (1 / m * (-Y) + x), -Y);
        g.setStroke(thin);
    }

    private void paintRegion(final Graphics2D g, final double m, final double x, double y, final Constants v, double b) {
int np = 0;
        int[] xs = null;
        int[] ys = null;
        Polygon region = null;
        // Area totale manipolabile senza alterare l'originale
        final Area unfill = (Area) total.clone();
        // Area disegnata
        Area feasible = null;

        /* In ogni caso calcolo quest'area, tra gli assi e i bordi
		 *
		 * 'b' è il valore del secondo coefficiente.
		 * Siccome calcolo OVUNQUE come se avessi un vincolo di minoranza devo tener conto del suo segno per poter disegnare
		 * la regione corretta in seguito. (ovvero se invertire o meno)
		 */
		boolean flag = ((int) b) < 0;
		
        if (m < 0 && !Double.isInfinite(m)) { // La retta scende
            xs = new int[] { 0, 0, (int) (-y / m) };
            ys = new int[] { 0, (int) y, 0 };
            np = 3;
        } else if (m > 0 && !Double.isInfinite(m)) { // La retta sale
            if (x > 0) {
                xs = new int[] { (int) (-y / m), X, (int) ((Y - y) / m) };
                ys = new int[] { 0, 0, Y };
                np = 3;
            } else {
            	xs = new int[] { (int) (-y / m), X, X };
            	ys = new int[] { 0, 0, (int) (m * X + y) };
                np = 3;
            }
        } else if (m == 0.0) { // La retta e' coricata
            xs = new int[] { 0, X, X, 0 };
            ys = new int[] { 0, 0, (int) y, (int) y };
            np = 4;
        } else if (Double.isInfinite(m)) { // La retta e' in piedi
            xs = new int[] { 0, 0, (int) x, (int) x };
            ys = new int[] { 0, Y, Y, 0 };
            np = 4;
        } else { // Qualcuno ha lasciato tutti 0 e il vincolo non esiste.
            xs = new int[] { -X, -X, X, X };
            ys = new int[] { -Y, Y, Y, -Y };
            np = 4;
			// Per farlo cadere sempre nel primo caso del prossimo if
            flag = v.equals(Constants.GreaterThan);
        }
        region = new Polygon(xs, ys, np);
        
        /* Qui si decide se invertire o meno l'area.
		 * Avendo presupposto si tratti di un vincolo di minoranza ho i seguenti casi:
		 * 	- v. di min. e b>0 oppure v. di magg. e b<0 (b<0 implica invertire la disuguaglianza)
		 *		- l'area è quella corretta
		 *	- v. di magg. e b>0 oppure v. di min. e b<0 ( ^ vedi sopra ^ )
		 *		- complemento l'area calcolata
		 *	- v. di uguaglianza
		 *		- nessuna area
		 */
        if (	(v.equals(Constants.LessThan) && !flag) ||
        		(v.equals(Constants.GreaterThan) && flag)	)
            feasible = new Area(region);
        else if (!v.equals(Constants.Equality)) {
            feasible = (Area) unfill.clone();
            feasible.subtract(new Area(region));
        }
        else if (v.equals(Constants.Equality))
            feasible = new Area();

        final Area unregion = new Area(feasible);
        unfill.subtract(unregion);
        g.fill(unfill);
    }

    protected void addProblemLabel() {
        problemLabel = new JLabel(opf.getProblemString());
        this.add(problemLabel);

        Insets ins = this.getInsets();
        Dimension size = problemLabel.getPreferredSize();
        problemLabel.setBounds(ins.left + X - 195, ins.top + 5, size.width, size.height);
    }

    protected double getObjective() {
        return 0.0;
    }

    protected int getZoom() {
        return S;
    }

    protected void setZoom(final int s) {
        S += s;
    }
}
