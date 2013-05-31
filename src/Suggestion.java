class Suggestion implements Comparable {
    private final String mot;
    private double probabilite;

    public Suggestion(String mot, double probabilite) {
        this.mot = mot;
        this.probabilite = probabilite;
    }

    public String getMot() {
        return mot;
    }

    public double getProbabilite() {
        return probabilite;
    }

    public void setProbabilite(double probabilite) {        
        this.probabilite = probabilite;        
    }

	@Override
	public int compareTo(Object o) {
		double oProba = ((Suggestion)o).probabilite;
		if (probabilite > oProba)
			return 1;
		else if (probabilite < oProba)
			return -1;
		else 
			return 0;
	}
    
}