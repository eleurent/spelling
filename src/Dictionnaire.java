import java.util.HashMap;
import java.util.Set;

public class Dictionnaire extends HashMap<String,Integer> {
  protected Integer defaultValue;
  public Dictionnaire(Integer defaultValue) {
    this.defaultValue = defaultValue;
  }
  @Override
  public Integer get(Object k) {
    Integer v = super.get(k);
    return ((v == null) && !this.containsKey(k)) ? this.defaultValue : v;
  }

}