package it.silma.simply.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "it.silma.simply.utils.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    // Costanti letterali
    public static final String HTML = Messages.getString("Constants.0"); //$NON-NLS-1$
    public static final String HTML_END = Messages.getString("Constants.1"); //$NON-NLS-1$
    public static final String INFO_WELCOME = Messages.getString("Constants.2") //$NON-NLS-1$
            + Messages.getString("Constants.3") //$NON-NLS-1$
            + Messages.getString("Constants.4") //$NON-NLS-1$
            + Messages.getString("Constants.5") //$NON-NLS-1$
            + Messages.getString("Constants.6") //$NON-NLS-1$
            + Messages.getString("Constants.7"); //$NON-NLS-1$
    public static final String INFO_CORRECT_ANS = Messages.getString("Constants.8"); //$NON-NLS-1$
    public static final String INFO_WRONG_ANS = Messages.getString("Constants.9"); //$NON-NLS-1$
    public static final String INFO_NOT_ARTIFICIAL_MISS = Messages.getString("Constants.10") //$NON-NLS-1$
            + Messages.getString("Constants.11") //$NON-NLS-1$
            + Messages.getString("Constants.12"); //$NON-NLS-1$
    public static final String INFO_ARTIFICIAL_OK = Messages.getString("Constants.13"); //$NON-NLS-1$
    public static final String INFO_NOT_ARTIFICIAL_HIT = Messages.getString("Constants.14") //$NON-NLS-1$
            + Messages.getString("Constants.15"); //$NON-NLS-1$
    public static final String INFO_OPTIMUM_HIT = Messages.getString("Constants.16") //$NON-NLS-1$
            + Messages.getString("Constants.17"); //$NON-NLS-1$
    public static final String INFO_OPTIMUM_PHASE_ONE = Messages.getString("Constants.18") //$NON-NLS-1$
            + Messages.getString("Constants.19"); //$NON-NLS-1$
    public static final String INFO_OPTIMUM_MISS = Messages.getString("Constants.20") //$NON-NLS-1$
            + Messages.getString("Constants.21"); //$NON-NLS-1$
    public static final String INFO_NOT_OPTIMUM = Messages.getString("Constants.22") //$NON-NLS-1$
            + Messages.getString("Constants.23"); //$NON-NLS-1$
    public static final String INFO_NEGATIVE_KNOWN = Messages.getString("Constants.24") //$NON-NLS-1$
            + Messages.getString("Constants.25"); //$NON-NLS-1$
    public static final String INFO_UNFEASIBLE = Messages.getString("Constants.26"); //$NON-NLS-1$
    public static final String INFO_FEASIBLE = Messages.getString("Constants.27"); //$NON-NLS-1$
    public static final String INFO_UNBOUNDED = HTML + Messages.getString("Constants.28") //$NON-NLS-1$
            + HTML_END;
    public static final String INFO_BOUNDED = HTML + Messages.getString("Constants.29") //$NON-NLS-1$
            + HTML_END;
    public static final String INFO_GRADE = Messages.getString("Constants.30"); //$NON-NLS-1$
    public static final String INFO_EXPORT_AMPL = HTML + Messages.getString("Constants.31") //$NON-NLS-1$
            + Messages.getString("Constants.32") //$NON-NLS-1$
            + Messages.getString("Constants.33"); //$NON-NLS-1$
    public static final String QUESTION_ARTIFICIAL = HTML + Messages.getString("Constants.34") //$NON-NLS-1$
            + HTML_END;
    public static final String QUESTION_OPTIMUM = HTML + Messages.getString("Constants.35") //$NON-NLS-1$
            + HTML_END;
    public static final String QUESTION_FEASIBLE = Messages.getString("Constants.36"); //$NON-NLS-1$
    public static final String QUESTION_UNBOUNDED = HTML + Messages.getString("Constants.37") + HTML_END; //$NON-NLS-1$
    public static final String TABLEAU_LEGEND = Messages.getString("Constants.38"); //$NON-NLS-1$
    public static final String TABLEAU_PREPROCESS = Messages.getString("Constants.39") //$NON-NLS-1$
            + TABLEAU_LEGEND;
    public static final String TABLEAU_PHASE_ONE_BEGIN = Messages.getString("Constants.40") //$NON-NLS-1$
            + TABLEAU_LEGEND;
    public static final String TABLEAU_PHASE_ONE_NORMALIZE = Messages.getString("Constants.41"); //$NON-NLS-1$
    public static final String TABLEAU_PHASE_ONE_OVER = Messages.getString("Constants.42"); //$NON-NLS-1$
    public static final String TABLEAU_PHASE_TWO = Messages.getString("Constants.43"); //$NON-NLS-1$
    public static final String TABLEAU_BAD = Messages.getString("Constants.44"); //$NON-NLS-1$
    public static final String TABLEAU_GOOD = Messages.getString("Constants.45"); //$NON-NLS-1$
    public static final String TABLEAU_GIVEUP = Messages.getString("Constants.46"); //$NON-NLS-1$
    public static final String TABLEAU_SELECT_ENTERING = Messages.getString("Constants.47"); //$NON-NLS-1$
    public static final String TABLEAU_ENTERING_GOOD = Messages.getString("Constants.48"); //$NON-NLS-1$
    public static final String TABLEAU_ENTERING_NO_GOOD = Messages.getString("Constants.49"); //$NON-NLS-1$
    public static final String TABLEAU_ENTERING_BAD = Messages.getString("Constants.50"); //$NON-NLS-1$
    public static final String TABLEAU_SELECT_EXITING = Messages.getString("Constants.51"); //$NON-NLS-1$
    public static final String TABLEAU_EXITING_BAD = Messages.getString("Constants.52"); //$NON-NLS-1$
    public static final String TABLEAU_EXITING_GOOD = Messages.getString("Constants.53"); //$NON-NLS-1$
    public static final String TABLEAU_PIVOT_FIRST = Messages.getString("Constants.54"); //$NON-NLS-1$
    public static final String TABLEAU_PIVOT_SECOND = Messages.getString("Constants.55"); //$NON-NLS-1$
    public static final String TABLEAU_SOLVED = Messages.getString("Constants.56") //$NON-NLS-1$
            + INFO_GRADE;
    public static final String TABLEAU_UNFEASIBLE = Messages.getString("Constants.57") //$NON-NLS-1$
            + INFO_GRADE;
    public static final String TABLEAU_UNBOUNDED = Messages.getString("Constants.58") //$NON-NLS-1$
            + INFO_GRADE;
    public static final String STATUS_READY = Messages.getString("Constants.59"); //$NON-NLS-1$
    public static final String STATUS_WAITING = Messages.getString("Constants.60"); //$NON-NLS-1$
    public static final String STATUS_MAXIMIZE = Messages.getString("Constants.61"); //$NON-NLS-1$
    public static final String STATUS_MINIMIZE = Messages.getString("Constants.62"); //$NON-NLS-1$
    public static final String STATUS_PHASE_ONE = Messages.getString("Constants.63"); //$NON-NLS-1$
    public static final String STATUS_PHASE_TWO = Messages.getString("Constants.64"); //$NON-NLS-1$
    public static final String STATUS_FINISHED = Messages.getString("Constants.65"); //$NON-NLS-1$
    public static final String ABOUT_ME = HTML + Messages.getString("Constants.66") //$NON-NLS-1$
            + Messages.getString("Constants.67") //$NON-NLS-1$
            + HTML_END;
    public static final String CONTACT_ME = HTML + Messages.getString("Constants.68") //$NON-NLS-1$
            + Messages.getString("Constants.69") //$NON-NLS-1$
            + HTML_END;
    public static final String INFORM_ME = HTML + Messages.getString("Constants.70") //$NON-NLS-1$
            + HTML_END;
}
