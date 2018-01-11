package modelisation;

public class Indicateurs {
	
	//Retourne le chi de deux vecteurs passes en parametres
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
	
	//Transforme une variable continue en variable discrète.
	public static int[] faireEcarts(int []X)
	{
		int []totopt=calculOption(X);
		int []Xbis=null;
		if(X.length > 9)
		{
			if (totopt.length > 20*X.length/100)
			{
				Xbis = new int[X.length];
				for (int i=0;i<Xbis.length;i++)
				{
					if (Xbis[i]<(maximum(X)+minimum(X))/2) {Xbis[i]=1;}
					else {Xbis[i]=2;}
				}
			}
			else
			{
				Xbis=X;
			}
		}
		else
		{
			Xbis=X;
		}
		return Xbis;
	}
	
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

	//Afficher le tableau de contingence de deux vecteurs passes en parametres 
		public static void afficherTabCont(int []X, int[] Y)
		{
			int [][] tabCont = tabCont(X,Y);
			int opt1 = 0;
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
			}
			
		}
		
		//Renvoie la table de contingence de deux vecteurs passes en parametres
		private static int[][] tabCont(int[] X, int[] Y) {
			int [][] tab;
			int []totopt1=calculOption(X);
			int []totopt2=calculOption(Y);
			int som=0;
			
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
				tab[Y[k]-1][X[k]-1] = tab[Y[k]-1][X[k]-1]+1;
				tab[Y[k]-1][totopt1.length] = tab[Y[k]-1][totopt1.length]+1;
				tab[totopt2.length][X[k]-1] = tab[totopt2.length][X[k]-1]+1;
				tab[totopt2.length][totopt1.length]=tab[totopt2.length][totopt1.length]+1;
			}
			return tab;
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