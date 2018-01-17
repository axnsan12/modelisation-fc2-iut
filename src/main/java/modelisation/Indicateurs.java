package modelisation;

public class Indicateurs {
	
	//Arbre de classification
	
	//Retourne le chi de deux vecteurs passes en parametres
	/**
	 * 
	 * @param X vecteur d'entiers
	 * @param Y vecteur d'entiers
	 * @return Retourne le coefficient chi2 des deux vecteurs.
	 */
	public static double chi2(int []X, int[] Y)
	{
		int [][] tab = tabCont(X,Y);
		double chi=(double)tab[tab.length-1][tab[0].length-1];
		double cases=0;
		for (int i=0;i<tab.length-1;i++)
		{
			for (int j=0;j<tab[i].length-1;j++)
			{
				cases = cases + ((double)tab[i][j]*(double)tab[i][j])/((double)tab[i][tab[i].length-1]*(double)tab[tab.length-1][j]);
			}
		}
		chi = chi*(cases-1);
		return chi;
	}
	
	//Retourne le Gini de deux vecteurs passes en parametres
	/**
	 * 
	 * @param X vecteur d'entiers
	 * @param Y vecteur d'entiers
	 * @return Retourne le coefficient Gini des deux vecteurs.
	 */
	public static double gini(int []X, int[] Y)
	{
	int [][] tab = tabCont(X,Y);
	double Gini=1;
	double Gimoyen=0;
	for (int i=0;i<tab.length-1;i++)
	{
		Gini=1;
		for (int j=0;j<tab[i].length-1;j++)
		{
			Gini = Gini - (double)(tab[i][j]/(double)tab[i][tab[i].length-1])*(double)(tab[i][j]/(double)tab[i][tab[i].length-1]);
		}
		Gimoyen = Gimoyen + Gini*(double)tab[i][tab[i].length-1]/(double)tab[tab.length-1][tab[i].length-1];
	}
	return Gimoyen;
	}
	
	//retourne la valeur d'erreur de classement de deux vecteurs passes en parametres
	/**
	 * 
	 * @param X vecteur d'entiers
	 * @param Y vecteur d'entiers
	 * @return Retourne le coefficient Erreur de classement des deux vecteurs.
	 */
	public static double erreurClass(int []X, int[] Y)
	{
		double maximum=0,erreurmoy=0;
		int [][] tab = tabCont(X,Y);
		
		for (int i=0;i<tab.length-1;i++)
		{
			maximum=0;
			for (int j=0;j<tab[0].length-1;j++)
			{
				if ((double)tab[i][j]/(double)tab[i][tab[0].length -1] > maximum)
				{
					maximum = (double)tab[i][j]/(double)tab[i][tab[0].length -1];
				}
			}
			erreurmoy=erreurmoy +(1-maximum)*((double)tab[i][tab[i].length-1]/(double)tab[tab.length-1][tab[i].length-1]);
		}
		return erreurmoy;
	}
	
	//retourne l'entropie de deux vecteurs passes en parametres
	/**
	 * 
	 * @param X vecteur d'entiers
	 * @param Y vecteur d'entiers
	 * @return Retourne le coefficient Entropie des deux vecteurs.
	 */
	public static double entropie(int []X, int[] Y)
	{
		int [][] tab = tabCont(X,Y);
		double entropie=0;
		double pkt=0;
		for (int i=0;i<tab.length-1;i++)
		{
			for (int j=0;j<tab[i].length-1;j++)
			{
				pkt=((double)(tab[i][j]/(double)tab[i][tab[i].length-1])*(double)(tab[i][j]/(double)tab[i][tab[i].length-1]));
				entropie = entropie + pkt*Math.log(pkt)/Math.log(2);
			}
		}
		entropie = - entropie;
		return entropie;
	}

	/**
	 * 
	 * @param X : vecteur d'entiers
	 * @return Retourne la moyenne entre la plous grande valeur et la plus petite valeur
	 */
	public static double getSplitValue(int[]X)
	{
		return (maximum(X)+minimum(X))/2;
	}
	
	/**
	 * 
	 * @param X
	 * @param nblig
	 * @param pourcentage
	 * @return
	 */
	public static boolean shouldDiscretize(int []X, int nblig,int pourcentage)
	{
		int []totopt=calculOption(X);
		return X.length > nblig && totopt.length > pourcentage*X.length/100;
	}
	
	//Transforme une variable continue en variable discrète.
	public static int[] faireEcarts(int []X, double splitValue, int nblig, int pourcentage)
	{
		int []Xbis;
		Xbis = new int[X.length];
		
		if (shouldDiscretize(X, nblig, pourcentage))
		{
			for (int i=0;i<Xbis.length;i++)
			{
				if (Xbis[i]<splitValue) {Xbis[i]=1;}
				else {Xbis[i]=2;}
			}
		}
		else
		{
			Xbis=X;
		}
		return Xbis;
	}
	
	//Afficher le tableau de contingence de deux vecteurs passes en parametres 
			public static void afficherTabCont(int []X, int[] Y)
			{
				int [][] tabCont = tabCont(X,Y);
				int opt1 = 0;
				
				if (tabCont == null) {System.out.println("null");}
				else {
				System.out.print("|___ /");
				for (int j=1;j<tabCont[0].length;j++)
				{
					System.out.print("|" + j + " ");
				}
				System.out.println("|Total|");
				for (int i=1;i<tabCont.length+1;i++)
				{
					if (i == tabCont.length)
					{
						System.out.print("|Total");
					}
					else
					{
						System.out.print("|0000" + i);
					}
					
					for (int j=1;j<tabCont[0].length+1;j++)
					{
						System.out.print("|" +tabCont[i-1][j-1] + " ");
					}
					if (i == tabCont.length)
					{
						System.out.print("| ");
					}
					else
					{
						System.out.println("| ");
					}
				}}
				
			}
			
			//Renvoie la table de contingence de deux vecteurs passes en parametres
			private static int[][] tabCont(int[] X, int[] Y) {
				int [][] tab;
				int []totopt1=calculOption(X);
				int []totopt2=calculOption(Y);
				int som=0,indoption1=0,indoption2=0;
				
				tab = new int[totopt2.length+1][totopt1.length+1];
				
				for (int i=0;i<totopt2.length+1;i++)
				{
					for(int j=0;j<totopt1.length+1;j++)
					{
						tab[i][j] = 0;
						
					}
				}
				for (int k=0;k<X.length;k++)
				{
					indoption1=index(X[k],totopt1);					
					indoption2=index(Y[k],totopt2);
					
					if (indoption1 == totopt1.length || indoption2 == totopt2.length)
					{
						return null;
					}
					
					tab[indoption2][indoption1] = tab[indoption2][indoption1]+1;
					tab[indoption2][totopt1.length] = tab[indoption2][totopt1.length]+1;
					tab[totopt2.length][indoption1] = tab[totopt2.length][indoption1]+1;
					tab[totopt2.length][totopt1.length]=tab[totopt2.length][totopt1.length]+1;
				}
				return tab;
			}

			private static int index(int option, int[] totopt) {
				// TODO Auto-generated method stub
				for (int i=0;i<totopt.length;i++)
				{
					if (totopt[i] == option)
					{
						return i;
					}
				}
				return totopt.length;
			}

	
	//Arbre de regression
			
			/**
			 * 
			 * @param i indice teste
			 * @param Y Variable cible
			 * @param X Variable a etudier
			 * @return
			 */
	public static double d(int i, double []Y, double [] X)
	{
		double [] fils1 = moyenneFilsSup(i,Y,X);
		double [] fils2 = moyenneFilsInf(i,Y,X);
		return fils1[1] + fils2[1];
		
	}
			
			/**
			 * Min
			 * @param i indice teste
			 * @param Y Variable cible
			 * @param X Variable a etudier
			 * @return
			 */
	private static double [] moyenneFilsSup(int i, double []Y, double [] X)
	{
		double c = C(i,X);
		double moyenne=0,cmpt=0,d=0,sum1=0,sum2=0;
		double combi[] = new double[2];
		for (int j=0;j<Y.length;j++)
		{
			if (X[j] >= c)
			{
				cmpt ++;
				moyenne = moyenne + Y[j];
				sum1 = sum1 + Y[j];
				sum2 = sum2 + Y[j]*Y[j];
			}
		}
		moyenne = moyenne/cmpt;
		d = moyenne*moyenne +((sum2-2*moyenne*sum1)/cmpt);
		combi[0]=moyenne;
		combi[1]=d;
		return combi;
	}
	
			/**
			 * 
			 * @param i indice teste
			 * @param Y Variable cible
			 * @param X Variable a etudier
			 * @return
			 */
	private static double[] moyenneFilsInf(int i, double []Y, double [] X)
	{
		double c = C(i,X);
		double moyenne=0,cmpt=0,d=0,sum1=0,sum2=0;
		double combi[] = new double[2];
		for (int j=0;j<Y.length;j++)
		{
			if (X[j] < c)
			{
				cmpt ++;
				moyenne = moyenne + Y[j];
				sum1 = sum1 + Y[j];
				sum2 = sum2 + Y[j]*Y[j];
			}
		}
		moyenne = moyenne/cmpt;
		d = moyenne*moyenne +((sum2-2*moyenne*sum1)/cmpt);
		combi[0]=moyenne;
		combi[1]=d;
		return combi;
	}
	
	private static double C(int i,double[] X)
	{
		return (X[i]+X[i-1])/2;
	}
	
	//Outils internes
	
	private static int minimum(int[] x) {
		int min=x[0];
		for (int i=1;i<x.length;i++)
		{
			if (min > x[i]) {min = x[i];}
		}
		return min;
	}
	

	private static int maximum(int[] x) {
		// TODO Auto-generated method stub
		int max=x[0];
		for (int i=1;i<x.length;i++)
		{
			if (max < x[i]) {max = x[i];}
		}
		return max;
	}

	

		//Renvoie un vecteur contenant la liste des options possibles.
		private static int[] calculOption(int[] x) {
			String s1="", s2="";
			boolean opt=false;
			String []s;
			int[] options;
			
			for (int i=0;i<x.length;i++)
			{
				s2=s2+x[i];
				if (!s1.contains(s2)){
					if (!opt)
					{
						opt=true;
					}
					else
					{
						s1=s1+";";
					}
					s1=s1+s2;
				}
				s2="";
			}
			
			s=s1.split(";");
			options = new int[s.length];
			
			for (int j=0;j<s.length;j++)
			{
				options[j] = Integer.parseInt(s[j]);
			}
			
			triBulleCroissant(options);
			return options;
		}
		
		private static void triBulleCroissant(int tableau[]) {
			int longueur = tableau.length;
			int tampon = 0;
			boolean permut;
	 
			do {
				// hypothèse : le tableau est trié
				permut = false;
				for (int i = 0; i < longueur - 1; i++) {
					// Teste si 2 éléments successifs sont dans le bon ordre ou non
					if (tableau[i] > tableau[i + 1]) {
						// s'ils ne le sont pas, on échange leurs positions
						tampon = tableau[i];
						tableau[i] = tableau[i + 1];
						tableau[i + 1] = tampon;
						permut = true;
					}
				}
			} while (permut);
		}

	}
