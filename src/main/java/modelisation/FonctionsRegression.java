package modelisation;

import java.util.ArrayList;
import java.util.function.IntPredicate;

public class FonctionsRegression {

    /**
     * @param i indice teste
     * @param Y Variable cible
     * @param X Variable a etudier
     * @return Retourne la variance relative a un vecteur et a un indice précis par rapport à la variable mesuree
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
    
    /**
     * 
     * @param i référence d'indice a tester
     * @param X vecteur à tester
     * @return Retourne le facteur c a pour un vecteur et un indice precis
     */
    public static double C(int i, double[] X) {
        return (X[i] + X[i - 1]) / 2;
    }

    /**
     * 
     * @param Y vecteur de double, variable a etudier
     * @param X vecteur de double, vecteur a evaluer
     * @param mincut nombre minimum de personnes par ensemble
     * @return Retourne un ensemble de deux double contenant les quantité presente dans chacun des deux sous fils
     */
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
    
    /**
     * 
     * @param Y vecteur de double, variable cible
     * @param X liste de vecteurs de doubles, ensemble des variables sauf la cible
     * @param mincut nombre minimum de personnes dans un espace
     * @return Retourne le meilleur vecteur de découpage, selon la variance
     */
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
            System.out.println(i + ": AprÃ¨s :" + varCour + " " + meilVar);
            i++;
        }
        return meilXi;
    }
    
    /**
     * 
     * @param Y vecteur de double, variable cible
     * @param X vecteur de double, variable étudiée
     * @param mincut nombre minimum de personnes par sous ensemble
     * @return Retourne le meilleur indice de découpage, selon la variance
     */
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
