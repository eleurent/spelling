import java.awt.Event;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Character.Subset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Spelling {

	public static Dictionnaire modele = new Dictionnaire(1);
	
	public static String[] mots(String texte) {
		return texte.split("[^a-zA-Z']+");
	}
	
	public static void apprendre(String [] features){		
	    for (String f : features)
	        modele.put(f.toLowerCase(), modele.get(f)+1);
	}
	
	public static Set<String> connus(Set<String> mots) {
		Set<String> connus = new HashSet<String>();
		for (String mot : mots)
			if (modele.containsKey(mot))
				connus.add(mot);
		return connus;
	}
	
	public static Set<String> modifications1(String mot){		
		Set<String> modifications = new HashSet<String>();	
	    String[][] splits =  new String[mot.length() + 1][2];
	    for (int i=0; i<mot.length()+1; i++){
		    splits[i][0] = mot.substring(0, i);
		    splits[i][1] = mot.substring(i, mot.length());
	    }
	    for (int i=0; i<splits.length; i++) {
	    	// Supressions
	    	if (!splits[i][1].isEmpty())
	    		modifications.add(splits[i][0] + splits[i][1].substring(1));
	    	// Transpositions
	    	if (splits[i][1].length() > 1)
	    		modifications.add(splits[i][0] + splits[i][1].charAt(1) + splits[i][1].charAt(0) + splits[i][1].substring(2));
	    	// Mutations
	    	if (!splits[i][1].isEmpty())
	    		for (int j=0; j<alphabet.length; j++)
	    			modifications.add(splits[i][0] + alphabet[j] + splits[i][1].substring(1));
	    	// Insertions
	    	for (int j=0; j<alphabet.length; j++)
    			modifications.add(splits[i][0] + alphabet[j] + splits[i][1]);	    	
	    }
	   return modifications;
	}
	
	public static Set<String> modifications2Connues(String mot) {
		Set<String> modifications = new HashSet<String>();
		for (String edit1:modifications1(mot))
			for (String edit2:modifications1(edit1))
				if (modele.containsKey(edit2))
					modifications.add(edit2);
		return modifications;
	}
	
	public static String corriger(String mot) {
		String correction;
		if (modele.containsKey(mot))
			return mot;
		else if ((correction = modele.getMaxKey(connus(modifications1(mot)))) != null) {
			return correction;
		}
		else if ((correction = modele.getMaxKey(modifications2Connues(mot))) != null) {
			return correction;
		}
		else return mot;		
	}
	
	public static String lireFichier(String chemin) {
		String everything = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(chemin));
			StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        everything = sb.toString();
	        br.close();
		} catch (FileNotFoundException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	    return everything;	    		
	}
	
	
	public static void main(String[] args) {
		apprendre(mots(lireFichier("corpus/miserables1.txt")));
		apprendre(mots(lireFichier("corpus/miserables2.txt")));
		apprendre(mots(lireFichier("corpus/miserables3.txt")));
		apprendre(mots(lireFichier("corpus/miserables4.txt")));
		apprendre(mots(lireFichier("corpus/miserables5.txt")));
		apprendre(mots("bonjour, comment allez vous ? Bon, vous etes bien bien aimables, vous"));
		
	
		System.out.println(corriger("coucoz"));
	}
	
	public static char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

}
