import java.util.*;

/**
 * Tas minimal de suggestions maximales
 * @author user
 *
 */
public class MinHeap {
	
	/**
	 * Liste des suggestions dans le tas
	 */
  List<Suggestion> h = new ArrayList<Suggestion>();
  
  /**
   * Taille du min tas
   */
  int taille = 5;

  /**
   * Constructeur
   */
  public MinHeap() {
  }

  /**
   * Constructeur 
   * @param keys
   */
  public MinHeap(Suggestion[] keys) {
    for (Suggestion key : keys) {
      h.add(key);
    }
    for (int k = h.size() / 2 - 1; k >= 0; k--) {
      faireDescendre(k, h.get(k));
    }
  }

  /**
   * Insertion d'un noeud en comparant avec le minimum. Le minimum est à la racine, en premier (position 0)
   * @param node
   */
  public void add(Suggestion node) {
	if (h.size() == taille) {
		if (min().compareTo(node) < 0)
			remove();
		else
			return;
	}
	
	Suggestion p = get(node.getMot());
	if (p != null) {
		if (p.compareTo(node) < 0)
			p.setProbabilite(node.getProbabilite());
		return;
	}
		
	
    h.add(null);
    int k = h.size() - 1;
    while (k > 0) {
      int parent = (k - 1) / 2;
      p = h.get(parent);
      if (node.getProbabilite() >= p.getProbabilite()) {
        break;
      }
      h.set(k, p);
      k = parent;
    }
    h.set(k, node);
  }
  
  /**
   * Renvoie la Suggestion associée au mot cherché
   * @param mot
   * @return Suggestion depuis le tas associée au mot
   */
  public Suggestion get(String mot) {
	  for (Suggestion entry : h) {
	      if (entry.getMot().equals(mot))
	    	  return entry;
	  }
	  return null;
  }

  /**
   * Remove le noeud min
   * @return noeud enlevé
   */
  public Suggestion remove() {
	  Suggestion removedNode = h.get(0);
	  Suggestion lastNode = h.remove(h.size() - 1);
    faireDescendre(0, lastNode);
    return removedNode;
  }

  /**
   * Renvoie la suggestion avec proba minimale
   * @return
   */
  public Suggestion min() {
    return h.get(0);
  }

  /**
   * Test de non vacuité du tas
   * @return booléen de vacuité
   */
  public boolean isEmpty() {
    return h.isEmpty();
  }

  /**
   * Compare un noeud avec ceux d'en-dessous pour le faire descendre s'il est plus grand
   * @param k
   * @param node
   */
  void faireDescendre(int k, Suggestion node) {
    if (h.isEmpty()) {
      return;
    }
    while (k < h.size() / 2) {
      int child = 2 * k + 1;
      if (child < h.size() - 1 && h.get(child).compareTo(h.get(child + 1))  > 0) {
        child++;
      }
      if (node.compareTo(h.get(child)) <= 0) {
        break;
      }
      h.set(k, h.get(child));
      k = child;
    }
    h.set(k, node);
  }

  /**
   * Test
   * @param args
   */
  public static void main(String[] args) {
    MinHeap heap = new MinHeap();
    Suggestion entry;
    for (int i=0; i<10; i++) {
    	entry = new Suggestion("entry"+(int)(10*Math.random()), Math.random());
    	System.out.println(entry.getMot() + " : " + entry.getProbabilite());
    	heap.add(entry);
    }
    
    System.out.println("-----------------");
    
    while (!heap.isEmpty()) {
    	entry = heap.remove();
    	System.out.println(entry.getMot() + " : " + entry.getProbabilite());
    }
  }
}