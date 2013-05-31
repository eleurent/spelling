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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.*;

/**
 * Modèle de langage pour la prédiction de correction
 * @author user
 *
 */
public class Langage {

	/**
	 * Le nom du langage
	 */
	public String nom;
	
	/**
	 * La liste des caractères qui composent l'alphabet du langage
	 */
	public String alphabet;
	
	/**
	 * Le dictionnaire de fréquence des mots
	 */
	public Dictionnaire modele = new Dictionnaire(1);
	
	/**
	 * Crée un nouveau langage par choix d'un alphabet et apprentissage d'un corpus
	 * @param nom
	 * @param alphabet
	 * @param corpus
	 */
	public Langage(String nom, String alphabet, String[] corpus) {
		this.nom = nom;
		this.alphabet = alphabet;
		for (String oeuvre:corpus)
			apprendre(mots(lireFichier(oeuvre)));
	}
	
	/**
	 * Découpe un texte en mots
	 * @param texte une chaine de caractère
	 * @return les mots du texte
	 */
	public static String[] mots(String texte) {
		return texte.split("[^\\p{L}]+");
	}
	
	/**
	 * Apprentissage des fréquences d'un ensemble mots dans le modèle de langage
	 * @param features les mots à apprendre
	 */
	public void apprendre(String [] features){
	    for (String f : features)
	        modele.put(f.toLowerCase(), modele.get(f.toLowerCase())+1);
	}
	
	/**
	 * Renvoie les mots connus d'un ensemble
	 * @param mots un ensemble de mots
	 * @return le sous-ensemble des mots connus
	 */
	public Set<String> connus(Set<String> mots) {
		Set<String> connus = new HashSet<String>();
		for (String mot : mots)
			if (modele.containsKey(mot))
				connus.add(mot);
		return connus;
	}
	
	/**
	 * Génère l'ensemble des mots à distance 1 d'un mot donné
	 * @param mot
	 * @return
	 */
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
	
	/**
	 * Génère l'ensemble des mots à distance 2 d'un mot donné 
	 * @param mot
	 * @return
	 */
	public Set<String> modifications2Connues(String mot) {
		Set<String> modifications = new HashSet<String>();
		for (String edit1:modifications1(mot))
			for (String edit2:modifications1(edit1))
				if (modele.containsKey(edit2))
					modifications.add(edit2);
		return modifications;
	}	
	/**
	 * Détermine tous les mots probables à partir d'un mot mal orthographié
	 * @param mot
	 * @return la liste des suggestions
	 */
	public List<Suggestion> suggestions(String mot) {
		double probabiliteTypo = 20.;
		mot = mot.toLowerCase();
		MinHeap heap = new MinHeap();
		if (modele.containsKey(mot))
			heap.add(new Suggestion(mot, modele.get(mot)/1.));		
		for (String edit1:connus(modifications1(mot))) {
			heap.add(new Suggestion(edit1, modele.get(edit1)/probabiliteTypo));
		}		
		for (String edit2:modifications2Connues(mot)) {
			heap.add(new Suggestion(edit2, modele.get(edit2)/Math.pow(probabiliteTypo,3)));
		}		
		Collections.sort(heap.h, Collections.reverseOrder());
		if (heap.isEmpty())
			heap.add(new Suggestion(mot, 0.));
		return heap.h;
	}
	/**
	 * Renvoie la correction la plus probable
	 * @param mot
	 * @return la meilleure correction
	 */
	public String corriger(String mot) {
		return suggestions(mot).get(0).getMot();
	}
	/**
	 * Corrige toute la phrase
	 * @param phrase
	 * @return la phrase corrigée mot par mot
	 */
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
	/**
	 * Méthode pour lire le corpus de texte
	 * @param chemin
	 * @return le texte en String
	 */
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
	/**
	 * Calcule la probabilité qu'une phrase soit dans la langue actuelle
	 * @param phrase
	 * @return la probabilité que la phrase soit dans la langue actuelle
	 */
	public double probabilitePhrase(String phrase) {
		double produit = 1;
		String[] mots = mots(phrase);
		for (String mot:mots)
			produit *= modele.get(mot);
		return produit;
	}
	/**
	 * Détermine le langage le plus probable
	 * @param phrase
	 * @param langages
	 * @return le langage le plus probable
	 */
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
	/**
	 * Intégration du corpus français
	 */
	public static Langage francais = new Langage("Français", "abcdefghijklmnopqrstuvwxyzàáâèéêîïôçû", 
			  new String[]{"corpus/fr/dictionnaire.txt",
						   "corpus/fr/miserables1.txt", 
						   "corpus/fr/miserables2.txt", 
						   "corpus/fr/miserables3.txt", 
						   "corpus/fr/miserables4.txt",
						   "corpus/fr/miserables5.txt",
						   "corpus/fr/assomoir.txt",
						   "corpus/fr/germinal.txt",
						   "corpus/fr/largent.txt",
						   "corpus/fr/swann.txt",
						   "corpus/fr/rougeetnoir.txt"});
	/**
	 * Intégration du corpus anglais
	 */
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

	/**
	 * Test
	 * @param args
	 */
	public static void main(String[] args) {
		//String phrase = anglais.corrigerPhrase("The quixk briwn hox jumps ovar the lazy dogt !";			
		//String phrase = francais.corrigerPhrase("Les saiglots logs des violo de l'autyomne bessent mon ceur d'une langeur monottone";
		String phrase = "La mer est une lerme qui perle le longy dzs côtes";
		//String phrase = "les sanglots lonts fes violons de l'automne blessent mon coeur d'une langueu monotone tout suffocant et bleme quand sonne l'heure je lme suouviens des hours anciens et je pleure";
		//String phrase = "oh what's the crime and what's the punushment the answer seems to vary from place to place and from time to tuime whats legal yesterday becomes suddendly illegal tomorow because some society says its so";
		/*Langage l = predireLangage(phrase, new Langage[]{anglais, francais});
		System.out.println("Cette phrase est en : " + l.nom );
		System.out.println("Correction orthographique : " + l.corrigerPhrase(phrase));*/
		for (Suggestion suggestion:francais.suggestions("mer")) {
			System.out.println(suggestion.getMot() + " : " + suggestion.getProbabilite());
		}
		System.out.println(francais.modele.get("tions"));
		System.out.println(francais.modele.get("étions"));
	}
}