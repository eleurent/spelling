/**
 * Mots suggeres pour correction avec leur probabilite
 *
 */
class Suggestion implements Comparable {
   
	/**
	 * La suggestion
	 */
	private final String mot;
	
	/**
	 * Probabilite de la suggestion
	 */
    private double probabilite;

    /**
     * Constructeur
     * @param mot
     * @param probabilite
     */
    public Suggestion(String mot, double probabilite) {
        this.mot = mot;
        this.probabilite = probabilite;
    }

    /**
     * Renvoie la suggestion
     * @return mot
     */
    public String getMot() {
        return mot;
    }

    /**
     * Renvoie la probabilite de la suggestion
     * @return probabilite
     */
    public double getProbabilite() {
        return probabilite;
    }

    /**
     * Change la probabilite donnee
     * @param probabilite
     */
    public void setProbabilite(double probabilite) {        
        this.probabilite = probabilite;        
    }

    /**
     * Compare la probabilite de notre suggestion a celle d'une autre suggestion
     */
	@Override
	public int compareTo(Object o) {
		double autre = ((Suggestion)o).probabilite;
		if (probabilite > autre)
			return 1;
		else if (probabilite < autre)
			return -1;
		else 
			return 0;
	}
    
}