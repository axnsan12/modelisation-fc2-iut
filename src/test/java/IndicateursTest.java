import modelisation.Indicateurs;

public class IndicateursTest {

    public static void main(String[] args) {

        int[] X = {3, 3, 3, 1, 3, 3, 1, 3, 2, 2, 2, 2, 3, 2, 2, 3, 2, 1, 2, 1};
        int[] Y = {1, 1, 2, 1, 2, 1, 1, 2, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 2};
        double[] Z = {1, 2, 2, 2, 3, 2};
        double[] T = {0, 0.5, -1, 2, 1, -0.5};
        System.out.println(Indicateurs.chi2(X, Y));
        System.out.println(Indicateurs.gini(X, Y));
        System.out.println(Indicateurs.erreurClass(X, Y));
        System.out.println(Indicateurs.entropie(X, Y));
    }
}
