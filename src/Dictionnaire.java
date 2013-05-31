import java.util.HashMap;
import java.util.Set;

/**
 * Table de hashage avec une valeur par défaut
 * @author user
 *
 */
public class Dictionnaire extends HashMap<String,Integer> {
  
	/**
	 * Valeur par défaut
	 */
	protected Integer defaultValue;
	
	/**
	 * Constructeur
	 * @param defaultValue
	 */
  public Dictionnaire(Integer defaultValue) {
    this.defaultValue = defaultValue;
  }
  
  /**
   * Obtenir la valeur d'une clé
   */
  @Override
  public Integer get(Object k) {
    Integer v = super.get(k);
    return ((v == null) && !this.containsKey(k)) ? this.defaultValue : v;
  }
  
}