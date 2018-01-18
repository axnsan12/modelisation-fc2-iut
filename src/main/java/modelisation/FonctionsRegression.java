package modelisation;

import java.util.ArrayList;
import java.util.function.IntPredicate;

public class FonctionsRegression {

    /**
     * @param i indice teste
     * @param Y Variable cible
     * @param X Variable a etudier
     * @return
     */
    public static double d(int i, double[] Y, double[] X) {
        double c = C(i, X);
        return d(c, Y, X);
    }

    /**
     * @param c valeur C teste
     * @param Y Variable cible
     * @param X Variable a etudier
     * @return
     */
    public static double d(double c, double[] Y, double[] X) {
        double fils1 = moyenneFilsSup(c, Y, X);
        double fils2 = moyenneFilsInf(c, Y, X);
        return fils1 + fils2;
    }

    /**
     * Calculate the population variance of a given array.
     *
     * @param Y         population values
     * @param predicate predicate that decides whether to include a given element;
     *                  takes the index of the element as argument
     * @return population variance of {@code X}
     */
    public static double variance(double[] Y, IntPredicate predicate) {
        double moyenne = 0, cmpt = 0, d, sum1 = 0, sum2 = 0;
        for (int j = 0; j < Y.length; j++) {
            if (predicate.test(j)) {
                cmpt++;
                moyenne = moyenne + Y[j];
                sum1 = sum1 + Y[j];
                sum2 = sum2 + Y[j] * Y[j];
            }
        }
        moyenne = moyenne / cmpt;
        d = moyenne * moyenne + ((sum2 - 2 * moyenne * sum1) / cmpt);
        return d;
    }

    /**
     * Min
     *
     * @param c valeur C teste
     * @param Y Variable cible
     * @return
     */
    private static double moyenneFilsSup(double c, double[] Y, double[] X) {
        return variance(Y, idx -> X[idx] >= c);
    }

    /**
     * @param c valeur C teste
     * @param Y Variable cible
     * @return
     */
    private static double moyenneFilsInf(double c, double[] Y, double[] X) {
        return variance(Y, idx -> X[idx] < c);
    }

    public static double C(int i, double[] X) {
        return (X[i] + X[i - 1]) / 2;
    }


    public static double[] ensembleFils(double[] Y, double[] X, int mincut) {
        double[] fils = {0, 0};
        int indiceVariance = meilleureVarianceXi(Y, X, mincut);
        for (int i = mincut; i < Y.length - mincut; i++) {
            if (X[i] < X[indiceVariance]) {
                fils[0] = fils[0] + Y[i];
            } else {
                fils[1] = fils[1] + Y[i];
            }
        }
        return fils;
    }

    public static double[] meilleureVarianceGlob(double[] Y, ArrayList<double[]> X, int mincut) {
        double[] meilXi = null;
        int i = 1;
        double varCour, meilVar = 0;
        for (double[] Xi : X) {
            varCour = d(meilleureVarianceXi(Y, Xi, mincut), Xi, Y);
            System.out.println(i + ": Avant :" + varCour + " " + meilVar);
            if (varCour > meilVar) {
                meilVar = varCour;
                meilXi = Xi;
            }
            System.out.println(i + ": Après :" + varCour + " " + meilVar);
            i++;
        }
        return meilXi;
    }

    public static int meilleureVarianceXi(double[] Y, double[] X, int mincut) {
        int meil = Y.length + 1;
        double varCour, meilVar = 0;
        for (int i = mincut; i < Y.length - mincut; i++) {
            varCour = d(i, Y, X);
            if (varCour > meilVar) {
                meilVar = varCour;
                meil = i;
            }
        }
        return meil;
    }

}
