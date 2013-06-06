import java.util.HashMap;
import java.util.Set;

/**
 * Table de hashage avec une valeur par defaut
 *
 */
public class Dictionnaire extends HashMap<String,Integer> {
  
	/**
	 * Valeur par defaut
	 */
	protected Integer valeurParDefaut;
	
	/**
	 * Constructeur
	 * @param defaultValue
	 */
  public Dictionnaire(Integer defaultValue) {
    this.valeurParDefaut = defaultValue;
  }
  
  /**
   * Obtenir la valeur d'une cle
   */
  @Override
  public Integer get(Object k) {
    Integer v = super.get(k);
    return ((v == null) && !this.containsKey(k)) ? this.valeurParDefaut : v;
  } 
}