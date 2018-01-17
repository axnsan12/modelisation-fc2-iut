import modelisation.FonctionsRegression;

import java.util.ArrayList;

public class FonctionRegressionTest {
    public static void main(String[] args) {

        // TODO Auto-generated method stub

        double[] Y = {1, 2, 2, 2, 3, 2, 1, 5, 4, 2, 5, 2, 3, 2, 4, 2, 5, 2};
        double[] X1 = {0, 0.5, -1, 0.75, -3, 0.2, 3, 0.7, 2, 0, 0.75, -1, 2, 1, -1, 0.5, -0.5, 2.5};
        double[] X2 = {10, 12, 42, 52, 31, 92, 1, 54, 24, 12, 52, 21, 31, 24, 43, 62, 75, 21};
        double[] X3 = {1.99, 20, 23.95, 27.5, 3.99, 2.95, 10, 52.25, 14, 2, 25, 24.99, 13, 12.75, 32.45, 31.25, 25, 22};
        double[] X4 = {10, 25, 23, 21, 32, 24, 12, 15, 14, 22, 25, 22, 13, 12, 14, 29, 25, 12};
        double[] meil, fils;

        ArrayList<double[]> X = new ArrayList<>();
        X.add(X1);
        X.add(X2);
        X.add(X3);
        X.add(X4);
        System.out.println(FonctionsRegression.meilleureVarianceXi(Y, X1, 2));
        System.out.println(X2[FonctionsRegression.meilleureVarianceXi(Y, X2, 2)]);
        System.out.println(FonctionsRegression.meilleureVarianceXi(Y, X3, 2));
        System.out.println(FonctionsRegression.meilleureVarianceXi(Y, X4, 2));
        meil = FonctionsRegression.meilleureVarianceGlob(Y, X, 2);
        System.out.print("{");
        for (int i = 0; i < meil.length; i++) {
            System.out.print(meil[i] + ",");
        }
        System.out.println("}");


        fils = FonctionsRegression.ensembleFils(Y, X4, 2);
        System.out.print("{");
        for (int j = 0; j < fils.length; j++) {
            System.out.print(fils[j] + ",");
        }
        System.out.println("}");
    }
}
