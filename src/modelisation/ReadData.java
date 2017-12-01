package modelisation;

import java.io.*;
import java.util.*;

public class ReadData 
{

	public static ArrayList<Column<String>> readData(String fichier) throws IOException
	{
		BufferedReader bf;
		String ligne;
		String [] infos;
		Column<String> colonne;
		//int numero;
		ArrayList<String> base = new ArrayList<>();
		ArrayList<Column<String>> retour = new ArrayList<>();
		
		bf = new BufferedReader(new FileReader(new File(fichier)));
		ligne = bf.readLine();
		infos=ligne.split(";");
		for (int i=0;i<infos.length;i++)
		{
			colonne = new Column<String>(infos[i]);
			retour.add(colonne);
		}
		ligne = bf.readLine();
		//numero = 1;
		while (ligne !=null)
		{
			base.add(ligne);
			ligne = bf.readLine();	
		}
		
		for (String l:base)
		{
			infos=l.split(";");
			for (int i=0;i<infos.length;i++)
			{
				retour.get(i).addValue(infos[i]);
			}
		}
		
		for (Column<String> c:retour)
		{
			System.out.println(c);
		}
		
		bf.close();
		return retour;
		
	}
}
