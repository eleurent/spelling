import java.util.*;

/**
 * Tas minimal de suggestions maximales
 *
 */
public class MinHeap {
	
	/**
	 * Liste des suggestions dans le tas
	 */
  List<Suggestion> h = new ArrayList<Suggestion>();
  
  /**
   * Taille du tas minimal
   */
  int taille = 5;

  /**
   * Constructeur
   */
  public MinHeap() {
  }

  /**
   * Constructeur avec une liste de suggestions
   */
  public MinHeap(Suggestion[] suggestions) {
    for (Suggestion suggestion : suggestions) {
      h.add(suggestion);
    }
    for (int k = h.size() / 2 - 1; k >= 0; k--) {
      faireDescendre(k, h.get(k));
    }
  }

  /**
   * Insertion d'une nouvelle suggestion
   * @param noeud
   */
  public void add(Suggestion noeud) {
	if (h.size() == taille) {
		if (min().compareTo(noeud) < 0)
			remove();
		else
			return;
	}
	
	//Mise a jour de la probabilite d'une suggestion si le mot est deja present
	Suggestion p = get(noeud.getMot());
	if (p != null) {
		if (p.compareTo(noeud) < 0)
			p.setProbabilite(noeud.getProbabilite());
		return;
	}

    h.add(null);
    int k = h.size() - 1;
    while (k > 0) {
      int parent = (k - 1) / 2;
      p = h.get(parent);
      if (noeud.getProbabilite() >= p.getProbabilite()) {
        break;
      }
      h.set(k, p);
      k = parent;
    }
    h.set(k, noeud);
  }
  
  /**
   * Renvoie la Suggestion associee au mot cherche
   * @param mot
   * @return Suggestion depuis le tas associee au mot
   */
  public Suggestion get(String mot) {
	  for (Suggestion noeud : h) {
	      if (noeud.getMot().equals(mot))
	    	  return noeud;
	  }
	  return null;
  }

  /**
   * Supprime le noeud min
   * @return noeud enleve
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
   * Test de vacuite du tas
   * @return
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
      int fils = 2 * k + 1;
      if (fils < h.size() - 1 && h.get(fils).compareTo(h.get(fils + 1))  > 0) {
        fils++;
      }
      if (node.compareTo(h.get(fils)) <= 0) {
        break;
      }
      h.set(k, h.get(fils));
      k = fils;
    }
    h.set(k, node);
  }

  /**
   * Test unitaire
   * @param args
   */
  public static void main(String[] args) {
    MinHeap heap = new MinHeap();
    Suggestion noeud;
    for (int i=0; i<10; i++) {
    	noeud = new Suggestion("noeud"+(int)(10*Math.random()), Math.random());
    	System.out.println(noeud.getMot() + " : " + noeud.getProbabilite());
    	heap.add(noeud);
    }
    
    System.out.println("-----------------");
    
    while (!heap.isEmpty()) {
    	noeud = heap.remove();
    	System.out.println(noeud.getMot() + " : " + noeud.getProbabilite());
    }
  }
}