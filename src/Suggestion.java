/**
 * Mots suggérés pour correction avec leur probabilité
 *
 */
class Suggestion implements Comparable {
   
	/**
	 * La suggestion
	 */
	private final String mot;
	
	/**
	 * Probabilité de la suggestion
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
     * Renvoie la probabilité de la suggestion
     * @return probabilité suggestion
     */
    public double getProbabilite() {
        return probabilite;
    }

    /**
     * Fixe la probabilité donnée
     * @param probabilite
     */
    public void setProbabilite(double probabilite) {        
        this.probabilite = probabilite;        
    }

    /**
     * Compare la probabilité de notre suggestion à celle d'une autre suggestion. Renvoie 1 si proba supérieur à celle ente parenthèses, -1 si inférieur, zéro si égale.
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