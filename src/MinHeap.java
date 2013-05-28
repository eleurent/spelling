import java.util.*;

public class MinHeap {
  List<Suggestion> h = new ArrayList<Suggestion>();
  int taille = 5;

  public MinHeap() {
  }

  public MinHeap(Suggestion[] keys) {
    for (Suggestion key : keys) {
      h.add(key);
    }
    for (int k = h.size() / 2 - 1; k >= 0; k--) {
      percolateDown(k, h.get(k));
    }
  }

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
  
  public Suggestion get(String mot) {
	  for (Suggestion entry : h) {
	      if (entry.getMot().equals(mot))
	    	  return entry;
	  }
	  return null;
  }

  public Suggestion remove() {
	  Suggestion removedNode = h.get(0);
	  Suggestion lastNode = h.remove(h.size() - 1);
    percolateDown(0, lastNode);
    return removedNode;
  }

  public Suggestion min() {
    return h.get(0);
  }

  public boolean isEmpty() {
    return h.isEmpty();
  }

  void percolateDown(int k, Suggestion node) {
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

  // Usage example
  public static void main(String[] args) {
    MinHeap heap = new MinHeap();
    Suggestion entry;
    for (int i=0; i<10; i++) {
    	entry = new Suggestion("entry"+(int)(10*Math.random()), Math.random());
    	System.out.println(entry.getMot() + " : " + entry.getProbabilite());
    	heap.add(entry);
    }
    
    System.out.println("-----------------");
    
    // print keys in sorted order
    while (!heap.isEmpty()) {
    	entry = heap.remove();
    	System.out.println(entry.getMot() + " : " + entry.getProbabilite());
    }
  }
}