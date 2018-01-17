import modelisation.Indicateurs;

import java.util.Random;

public class IndicateursTest {
    static Random r = new Random();

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int[] X = {0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0};
        int[] Y = {3, 1, 3, 1, 3, 3, 1, 3, 3, 2, 3, 1, 3, 3, 3, 2, 3, 2, 3, 3, 2, 2, 3, 1, 3, 3, 3, 1, 3, 3, 1, 1, 3, 2, 1, 1, 3, 3, 3, 3, 3, 2, 3, 2, 3, 3, 3, 3, 3, 3, 3, 3, 1, 2, 1, 1, 2, 3, 2, 3, 3, 1, 1, 3, 1, 3, 2, 3, 3, 3, 2, 3, 2, 3, 3, 3, 3, 3, 2, 3, 3, 3, 3, 1, 2, 3, 3, 3, 1, 3, 3, 3, 1, 3, 3, 3, 1, 1, 2, 2, 3, 3, 1, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3, 3, 3, 3, 3, 2, 1, 3, 2, 3, 2, 2, 1, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 1, 1, 3, 1, 3, 3, 3, 3, 2, 2, 3, 3, 2, 2, 2, 1, 3, 3, 3, 1, 3, 3, 3, 3, 3, 2, 3, 3, 3, 3, 1, 3, 1, 3, 1, 3, 3, 3, 1, 3, 3, 1, 2, 3, 3, 2, 3, 2, 3, 1, 3, 1, 3, 3, 2, 2, 3, 2, 1, 1, 3, 3, 3, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 2, 3, 2, 3, 1, 3, 2, 1, 2, 3, 2, 3, 3, 1, 3, 2, 3, 2, 3, 1, 3, 2, 3, 2, 3, 2, 2, 2, 2, 3, 3, 2, 3, 3, 1, 3, 2, 1, 2, 3, 3, 1, 3, 3, 3, 1, 1, 1, 2, 3, 3, 1, 1, 3, 2, 3, 3, 1, 1, 1, 3, 2, 1, 3, 1, 3, 2, 3, 3, 3, 3, 3, 3, 1, 3, 3, 3, 2, 3, 1, 1, 2, 3, 3, 1, 3, 1, 1, 1, 3, 3, 3, 2, 3, 1, 1, 1, 2, 1, 1, 1, 2, 3, 2, 3, 2, 2, 1, 1, 3, 3, 2, 2, 3, 1, 3, 2, 3, 1, 3, 1, 1, 3, 1, 3, 1, 1, 3, 1, 2, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 1, 3, 3, 3, 3, 1, 2, 3, 3, 3, 2, 3, 3, 3, 3, 1, 3, 3, 1, 1, 3, 3, 1, 3, 1, 3, 1, 3, 3, 1, 3, 3, 1, 3, 2, 3, 2, 3, 2, 1, 3, 3, 1, 3, 3, 3, 2, 2, 2, 3, 3, 3, 3, 3, 2, 3, 2, 3, 3, 3, 3, 1, 2, 3, 3, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 2, 2, 3, 3, 1, 3, 2, 3, 1, 1, 3, 2, 1, 2, 2, 3, 3, 2, 3, 1, 2, 1, 3, 1, 2, 3, 1, 1, 3, 3, 1, 1, 2, 3, 1, 3, 1, 2, 3, 3, 2, 1, 3, 3, 3, 3, 2, 2, 3, 1, 2, 3, 3, 3, 3, 2, 3, 3, 1, 3, 1, 1, 3, 3, 3, 3, 1, 1, 3, 3, 1, 3, 1, 3, 3, 3, 3, 3, 1, 1, 2, 1, 3, 3, 3, 3, 1, 1, 3, 1, 2, 3, 2, 3, 1, 3, 3, 1, 3, 3, 2, 1, 3, 2, 2, 3, 3, 3, 3, 2, 1, 1, 3, 1, 1, 3, 3, 2, 1, 1, 2, 2, 3, 2, 1, 2, 3, 3, 3, 1, 1, 1, 1, 3, 3, 3, 2, 3, 3, 3, 3, 3, 3, 3, 2, 1, 1, 3, 3, 3, 2, 1, 3, 3, 2, 1, 2, 1, 3, 1, 2, 1, 3, 3, 3, 1, 3, 3, 2, 3, 2, 3, 3, 1, 2, 3, 1, 3, 1, 3, 3, 1, 2, 1, 3, 3, 3, 3, 3, 2, 3, 3, 2, 2, 3, 1, 3, 3, 3, 1, 2, 1, 3, 3, 1, 3, 1, 1, 3, 2, 3, 2, 3, 3, 3, 1, 3, 3, 3, 1, 3, 1, 3, 3, 3, 2, 3, 3, 3, 2, 3, 3, 2, 1, 1, 3, 1, 3, 3, 2, 2, 3, 3, 1, 2, 1, 2, 2, 2, 3, 3, 3, 3, 1, 3, 1, 3, 3, 2, 2, 3, 3, 3, 1, 1, 3, 3, 3, 1, 2, 3, 3, 1, 3, 1, 1, 3, 3, 3, 2, 2, 1, 1, 3, 1, 1, 1, 3, 2, 3, 1, 2, 3, 3, 2, 3, 2, 2, 1, 3, 2, 3, 2, 3, 1, 3, 2, 2, 2, 3, 3, 1, 3, 3, 1, 1, 1, 3, 3, 1, 3, 2, 1, 3, 2, 3, 3, 3, 2, 2, 3, 2, 3, 1, 3, 3, 3, 1, 3, 1, 1, 3, 3, 3, 3, 3, 2, 3, 2, 3, 3, 3, 3, 1, 3, 1, 1, 3, 3, 3, 3, 3, 3, 1, 3, 2, 3, 1, 3, 2, 1, 3, 3, 3, 2, 2, 1, 3, 3, 3, 1, 3, 2, 1, 3, 3, 2, 3, 3, 1, 3, 2, 3, 3, 1, 3, 1, 3, 3, 3, 3, 2, 3, 1, 3, 2, 3, 3, 3, 1, 3, 3, 3, 1, 3, 2, 1, 3, 3, 3, 3, 3, 2, 1, 3, 3, 3, 1, 2, 3, 1, 1, 3, 3, 3, 2, 1, 3, 2, 2, 2, 1, 3, 3, 3, 1, 1, 3, 2, 3, 3, 3, 3, 1, 2, 3, 3, 2, 3, 3, 2, 1, 3, 1, 3};

        double[] Z = {1, 2, 2, 2, 3, 2};
        double[] T = {0, 0.5, -1, 2, 1, -0.5};
        
        Indicateurs.afficherTabCont(X, Y);
        System.out.println(Indicateurs.chi2(X, Y));
        System.out.println(Indicateurs.gini(X, Y));
        System.out.println(Indicateurs.erreurClass(X, Y));
        System.out.println(Indicateurs.entropie(X, Y));
    }
}
