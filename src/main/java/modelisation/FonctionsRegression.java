package modelisation;

import java.util.ArrayList;

public class FonctionsRegression {

    /**
     * @param i indice teste
     * @param Y Variable cible
     * @param X Variable a etudier
     * @return
     */
    public static double d(int i, double[] Y, double[] X) {
        double[] fils1 = moyenneFilsSup(i, Y, X);
        double[] fils2 = moyenneFilsInf(i, Y, X);
        return fils1[1] + fils2[1];

    }

    /**
     * Min
     *
     * @param i indice teste
     * @param Y Variable cible
     * @param X Variable a etudier
     * @return
     */
    private static double[] moyenneFilsSup(int i, double[] Y, double[] X) {
        double c = C(i, X);
        double moyenne = 0, cmpt = 0, d, sum1 = 0, sum2 = 0;
        double combi[] = new double[2];
        for (int j = 0; j < Y.length; j++) {
            if (X[j] >= c) {
                cmpt++;
                moyenne = moyenne + Y[j];
                sum1 = sum1 + Y[j];
                sum2 = sum2 + Y[j] * Y[j];
            }
        }
        moyenne = moyenne / cmpt;
        d = moyenne * moyenne + ((sum2 - 2 * moyenne * sum1) / cmpt);
        combi[0] = moyenne;
        combi[1] = d;
        return combi;
    }

    /**
     * @param i indice teste
     * @param Y Variable cible
     * @param X Variable a etudier
     * @return
     */
    private static double[] moyenneFilsInf(int i, double[] Y, double[] X) {
        double c = C(i, X);
        double moyenne = 0, cmpt = 0, d, sum1 = 0, sum2 = 0;
        double combi[] = new double[2];
        for (int j = 0; j < Y.length; j++) {
            if (X[j] < c) {
                cmpt++;
                moyenne = moyenne + Y[j];
                sum1 = sum1 + Y[j];
                sum2 = sum2 + Y[j] * Y[j];
            }
        }
        moyenne = moyenne / cmpt;
        d = moyenne * moyenne + ((sum2 - 2 * moyenne * sum1) / cmpt);
        combi[0] = moyenne;
        combi[1] = d;
        return combi;
    }

    private static double C(int i, double[] X) {
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
