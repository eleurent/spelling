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
		String[][] separations =  new String[mot.length() + 1][2];
		for (int i=0; i<mot.length()+1; i++){
			separations[i][0] = mot.substring(0, i);
			separations[i][1] = mot.substring(i, mot.length());
		}
		for (int i=0; i<separations.length; i++) {
			// Supressions
			if (!separations[i][1].isEmpty())
				modifications.add(separations[i][0] + separations[i][1].substring(1));
			// Transpositions
			if (separations[i][1].length() > 1)
				modifications.add(separations[i][0] + separations[i][1].charAt(1) + separations[i][1].charAt(0) + separations[i][1].substring(2));
			// Mutations
			if (!separations[i][1].isEmpty())
				for (int j=0; j<alphabet.length(); j++)
					modifications.add(separations[i][0] + alphabet.charAt(j) + separations[i][1].substring(1));
			// Insertions
			for (int j=0; j<alphabet.length(); j++)
				modifications.add(separations[i][0] + alphabet.charAt(j) + separations[i][1]);            
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
     * Corrige toute la phrase, en conservant la ponctuation
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
     * Lis un fichier texte
     * @param chemin
     * @return
     */
    public static String lireFichier(String chemin) {
        String texte = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(chemin));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            texte = sb.toString();
            br.close();
        } catch (FileNotFoundException e) { 
            e.printStackTrace();
        } catch (IOException e) { 
            e.printStackTrace();
        }
        return texte;                
    }
    
    /**
     * Effectue une modification sur un mot, pour générer une faute
     * @param mot
     * @return
     */
    public String modifier(String mot) {
            
        int i = (int)(Math.random()*mot.length());
        String debut = mot.substring(0, i);
        String fin = mot.substring(i, mot.length());
        int lettre = (int)(Math.random()*alphabet.length());
        
        switch((int)(Math.random()*4)) {
        case 0: // Supression
            return debut + fin.substring(1);
        case 1: // Transposition
            if (fin.length() > 1)
                return debut + fin.charAt(1) + fin.charAt(0) + fin.substring(2);
            else return modifier(mot);
        case 2: // Mutation
            return debut + alphabet.charAt(lettre) + fin.substring(1);
        case 3: // Insertion
            return debut + alphabet.charAt(lettre) + fin;
        default:
            return mot;
        }        
    }
    
    /**
     * Evalue la performance de l'algorithme de correction
     * @param texte Un texte 
     * @param nombreMotsMax Le nombre de mots à traiter au maximum
     * @param biais Le nombre de fois qu'on considère avoir déjà rencontré un mot nouveau
     */
    public void evaluer(String texte, int nombreMotsMax, int biais) {
        int n = 0, fautes = 0, inconnus=0;
        for (String mot:mots(texte)) {
            if (mot.length() > 4) {
                mot = mot.toLowerCase();
                n++;
                if (biais >0)
                    modele.put(mot, modele.get(mot)+biais);
                String faute = modifier(mot);
                String correction = corriger(faute);
                if (!correction.equals(mot)) {
                    fautes++;
                    if (!modele.containsKey(mot)){
                        inconnus++;
                        //System.out.println(mot + " inconnu !");
                    }
                }
                if (n>=nombreMotsMax)
                    break;
                //System.out.println(faute + " ->" + correction + "(" + modele.get(correction) + "), " + mot + " attendu (" + modele.get(mot) + ")");
            }
        }
        System.out.println(fautes + " fautes sur " + n + " mots, avec " + inconnus + " inconnus (" + (100*fautes)/n + "%, " + (100*(fautes-inconnus))/(n-inconnus) + "%)");
    }
    
    /**
     * Calcule la probabilité d'une phrase
     * @param phrase
     * @return 
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
                           "corpus/fr/rougeetnoir.txt",
						   "corpus/fr/lestroismousquetaires.txt",
						   "corpus/fr/madamebovary.txt"});
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
                           "corpus/en/secretadversary.txt",
                           "corpus/en/mobydick.txt",
                           "corpus/en/frankenstein.txt"});
    
    /**
     * Test
     * @param args
     */
    public static void main(String[] args) {
        //francais.evaluer("Les sanglots longs des violons de l'automne blessent mon coeur d'une langueur monotone. Tout suffocant et blême quand sonne l'heure je me souviens des jours anciens et je pleure. Et je m'en vais au vent mauvais qui m'emporte deça delà, pareil à la feuille morte");
        /*francais.evaluer(lireFichier("corpus/fr/largent.txt"), 1000,0);
        francais.evaluer(lireFichier("corpus/fr/notredamedeparis.txt"), 1000,0);
        francais.evaluer(lireFichier("corpus/fr/largent.txt"), 1000,100);
        francais.evaluer(lireFichier("corpus/fr/notredamedeparis.txt"), 1000,100);*/
        
        anglais.evaluer(lireFichier("corpus/en/frankenstein.txt"), 1000,0);
        anglais.evaluer(lireFichier("corpus/en/thepictureofdoriangray.txt"), 1000,0);
        anglais.evaluer(lireFichier("corpus/en/frankenstein.txt"), 1000,100);
        anglais.evaluer(lireFichier("corpus/en/thepictureofdoriangray.txt"), 1000,100);
    }
}