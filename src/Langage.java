import java.awt.Event;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Character.Subset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.*;


public class Langage {

	public Dictionnaire modele = new Dictionnaire(1);
	public String alphabet;
	public String nom;
	public double probabiliteLangage = 0.5;
	
	public Langage(String nom, String alphabet, String[] corpus) {
		this.nom = nom;
		this.alphabet = alphabet;
		for (String oeuvre:corpus)
			apprendre(mots(lireFichier(oeuvre)));
	}
	
	public static String[] mots(String texte) {
		return texte.split("[^\\p{L}]+");
	}
	
	public void apprendre(String [] features){		
	    for (String f : features)
	        modele.put(f.toLowerCase(), modele.get(f.toLowerCase())+1);
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
		
		mot = mot.toLowerCase();
		/*TODO changer hierarchie par ponderation*/
		
		/*
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
		*/		
		double probabiliteTypo = 15.;
		String[] candidats = new String[3];
		double[] probabilites = new double[3];
		
		candidats[0] = mot;
		candidats[1] = modele.getMaxKey(connus(modifications1(mot)));
		candidats[2] = modele.getMaxKey(modifications2Connues(mot));
		if (modele.containsKey(candidats[0]))
			probabilites[0] = modele.get(candidats[0])/1.;
		if (modele.containsKey(candidats[1]))
			probabilites[1] = modele.get(candidats[1])/probabiliteTypo;
		if (modele.containsKey(candidats[2]))
			probabilites[2] = modele.get(candidats[2])/(probabiliteTypo*probabiliteTypo);
		
		int max = 0;
		for (int i=1; i<3; i++)
			if (probabilites[i] > probabilites[max])
				max = i;
		
		return candidats[max];
		
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
	
	public double probabilitePhrase(String phrase) {
		double produit = 1;
		String[] mots = mots(phrase);
		for (String mot:mots)
			produit *= modele.get(mot);
		return produit;
	}
	
	public static Langage predireLangage(String phrase, Langage[] langages) {
		Langage langageMax = null;
		double probaMax = 0;
		for (Langage l:langages) {
			if (l.probabilitePhrase(phrase) > probaMax) {
				probaMax = l.probabilitePhrase(phrase);
				langageMax = l;
			}
		}
		return langageMax;
	}
	
	public static Langage francais = new Langage("Français", "abcdefghijklmnopqrstuvwxyzàáâèéêîïôçû", 
			  new String[]{"corpus/fr/miserables1.txt", 
						   "corpus/fr/miserables2.txt", 
						   "corpus/fr/miserables3.txt", 
						   "corpus/fr/miserables4.txt",
						   "corpus/fr/miserables5.txt",
						   "corpus/fr/assomoir.txt",
						   "corpus/fr/germinal.txt",
						   "corpus/fr/largent.txt",
						   "corpus/fr/swann.txt",
						   "corpus/fr/rougeetnoir.txt"});
	
	public static Langage anglais = new Langage("Anglais", "abcdefghijklmnopqrstuvwxyz", 
			  new String[]{"corpus/en/dictionary.txt",
						   "corpus/en/henriVI.txt",
						   "corpus/en/hamlet.txt",
						   "corpus/en/macbeth.txt",
						   "corpus/en/alice.txt",
						   "corpus/en/sherlock.txt",
						   "corpus/en/greatexpectations.txt",
						   "corpus/en/mobydick.txt",
						   "corpus/en/secretadversary.txt"});

	
	public static void main(String[] args) {
		//String phrase = anglais.corrigerPhrase("The quixk briwn hox jumps ovar the lazy dogt !";			
		//String phrase = francais.corrigerPhrase("Les saiglots logs des violo de l'autyomne bessent mon ceur d'une langeur monottone";
		//String phrase = "La mer est une lerme qui pele le longy dzs côtes";
		String phrase = "les sanglots lonts fes violons de l'automne blessent mon coeur d'une langueu monotone tout suffocant et bleme quand sonne l'heure je lme suouviens des hours anciens et je pleure";
		//String phrase = "oh what's the crime and what's the punushment the answer seems to vary from place to place and from time to tuime whats legal yesterday becomes suddendly illegal tomorow because some society says its so";
		Langage l = predireLangage(phrase, new Langage[]{anglais, francais});
		System.out.println("Cette phrase est en : " + l.nom );
		System.out.println("Correction orthographique : " + l.corrigerPhrase(phrase));
		System.out.println(anglais.modele.get("time"));
		System.out.println(anglais.modele.get("the"));
	}
}