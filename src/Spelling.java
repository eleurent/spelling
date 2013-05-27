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
	
	
	public static char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

}
