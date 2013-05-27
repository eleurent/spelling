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
import java.util.regex.*;


public class Langage {

	public Dictionnaire modele = new Dictionnaire(1);
	public String alphabet;
	public double probabiliteLangage = 0.5;
	
	public Langage(String alphabet, String[] corpus) {
		this.alphabet = alphabet;
		for (String oeuvre:corpus)
			apprendre(mots(lireFichier(oeuvre)));
	}
	
	public static String[] mots(String texte) {
		return texte.split("[^\\p{L}]+");
	}
	
	public void apprendre(String [] features){		
	    for (String f : features)
	        modele.put(f.toLowerCase(), modele.get(f)+1);
	}
	
	public Set<String> connus(Set<String> mots) {
		Set<String> connus = new HashSet<String>();
		for (String mot : mots)
			if (modele.containsKey(mot))
				connus.add(mot);
		return connus;
	}
	
	public Set<String> modifications1(String mot){		
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
	    		for (int j=0; j<alphabet.length(); j++)
	    			modifications.add(splits[i][0] + alphabet.charAt(j) + splits[i][1].substring(1));
	    	// Insertions
	    	for (int j=0; j<alphabet.length(); j++)
    			modifications.add(splits[i][0] + alphabet.charAt(j) + splits[i][1]);	    	
	    }
	   return modifications;
	}
	
	public Set<String> modifications2Connues(String mot) {
		Set<String> modifications = new HashSet<String>();
		for (String edit1:modifications1(mot))
			for (String edit2:modifications1(edit1))
				if (modele.containsKey(edit2))
					modifications.add(edit2);
		return modifications;
	}
	
	public String corriger(String mot) {
		String correction;
		mot = mot.toLowerCase();
		/*TODO changer hierarchie par ponderation*/
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
	
	public String corrigerPhrase(String phrase) {
		Pattern pattern = Pattern.compile("[\\p{L}]+");
		Matcher m = pattern.matcher(phrase);
	    StringBuffer sb = new StringBuffer();  
		while (m.find())  
	    {  
	      m.appendReplacement(sb, "");  
	      sb.append(corriger(m.group()));  	      
	    }  
	    m.appendTail(sb);  
	    return sb.toString(); 
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
	
	public int probabilitePhrase(String phrase) {
		int produit = 1;
		String[] mots = mots(phrase);
		for (String mot:mots)
			produit *= modele.get(mot);
		return produit;
	}
	
	public static Langage francais = new Langage("abcdefghijklmnopqrstuvwxyzàáâèéêîïôçû", 
			  new String[]{"corpus/fr/miserables1.txt", 
						   "corpus/fr/miserables2.txt", 
						   "corpus/fr/miserables3.txt", 
						   "corpus/fr/miserables4.txt",
						   "corpus/fr/miserables5.txt"});
	
	public static Langage anglais = new Langage("abcdefghijklmnopqrstuvwxyz", 
			  new String[]{"corpus/en/dictionary.txt",
						   "corpus/en/henriVI.txt",
						   "corpus/en/hamlet.txt",
						   "corpus/en/macbeth.txt",
						   "corpus/en/alice.txt",
						   "corpus/en/sherlock.txt",
						   "corpus/en/greatexpectations.txt",
						   "corpus/en/mobydick.txt",
						   "corpus/en/secretadversary.txt",
						   "corpus/en/montecristo.txt",
						   "corpus/en/big.txt"});

	
	public static void main(String[] args) {
			//System.out.println(anglais.corrigerPhrase("The quixk briwn hox jumps ovar the lazy dogt !"));			
	}
}
