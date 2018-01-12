package modelisation;

import java.util.ArrayList;

public class FonctionsRegression {

	public static double [] meilleureVarianceGlob(double[] Y, ArrayList<double[]> X, int mincut)
	{
		double [] meilXi=null;
		int i=1;
		double varCour=0,meilVar=0;
		for (double[] Xi : X) 
		{
			varCour = Indicateurs.d(meilleureVarianceXi(Y, Xi, mincut), Xi,Y);
			System.out.println(i + ": Avant :" + varCour + " " + meilVar);
			if (varCour > meilVar)
			{
				meilVar=varCour;
				meilXi=Xi;
			}
			System.out.println(i + ": Après :" + varCour + " " + meilVar);
			i ++;
		}
		return meilXi;
	}
	public static int meilleureVarianceXi(double[] Y, double[] X, int mincut)
	{
		int meil=Y.length + 1;
		double varCour=0,meilVar=0;
		for (int i=mincut;i<Y.length-mincut;i++)
		{
			varCour = Indicateurs.d(i, Y, X);
			if (varCour > meilVar)
			{
				meilVar=varCour;
				meil=i;
			}
		}
		return meil;
	}

}
