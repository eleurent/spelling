import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;

/**
 * Fenetre d'affichage
 * @author user
 *
 */
public class Fenetre extends JFrame implements KeyListener, MouseListener{
	/**
	 * Panel d'affichage des phrases tapees et corrigees
	 */
	JPanel panneauPhrases;
	
	/**
	 * Panel des suggestions et de la langue
	 */
	JPanel panneauOptions;
	
	/**
	 * TextField de la phrase a corriger
	 */
	JTextField input;
	
	/**
	 * Affiche la langue la plus probable
	 */
	JLabel nomLangagePredit;
	
	/**
	 * Affiche la phrase corrigee
	 */
	JTextField output;
	
	/**
	 * Le langage le plus probable
	 */
	Langage langagePredit;
	
	/**
	 * Update l'affichage des phrases et langues
	 */
	Updater updater;
	
	/**
	 * Strings contenus dans les suggestions
	 */
	Vector<String> choixModele;
	
	/**
	 * Affichage des strings
	 */
	JList<String> choixCorrection;
	
	/**
	 * Cree la fenetre d'affichage
	 */
	public Fenetre() {
		panneauPhrases = new JPanel();
		panneauOptions = new JPanel();
		panneauOptions.setLayout(new BorderLayout());
		panneauPhrases.setLayout(new GridLayout(2,1));
		input = new JTextField("Lifde isq a talez tomld by an idiot, ful of sund an furyt, signifin noting");
		input.addKeyListener(this);
		input.addMouseListener(this);
		output = new JTextField();
		output.setEditable(false);
		nomLangagePredit = new JLabel("???");
		choixModele = new Vector<String>();
		choixCorrection = new JList<String>(choixModele);
		setLayout(new BorderLayout());
		panneauPhrases.add(input);
		panneauPhrases.add(output);
		panneauOptions.add(choixCorrection, BorderLayout.CENTER);
		panneauOptions.add(nomLangagePredit, BorderLayout.SOUTH);
		add(panneauPhrases, BorderLayout.CENTER);
		add(panneauOptions, BorderLayout.EAST);		
		corrigerPhrase();
		updater = new Updater(this);
		updater.start();
		setSize(new Dimension(700,170));
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);		
	}
	/**
	 * Corrige la phrase tapee et l'affiche
	 */
	public void corrigerPhrase() {
		langagePredit = Langage.predireLangage(input.getText(), new Langage[]{Langage.anglais, Langage.francais});
		nomLangagePredit.setText(langagePredit.nom);
		output.setText(langagePredit.corrigerPhrase(input.getText()));
		afficherSuggestions();
	}
	/**
	 * Affiche les suggestions
	 */
	public void afficherSuggestions() {
		int indice = input.getCaretPosition();
		String[] avant = input.getText().substring(0,indice).split("[^\\p{L}]+");		
		String mot =  avant[avant.length-1] + input.getText().substring(indice).split("[^\\p{L}]+")[0];
		List<Suggestion> suggestions = langagePredit.suggestions(mot);		
		choixModele.clear();
		for(Suggestion suggestion:suggestions) {
			choixModele.add(suggestion.getMot() + " (" + suggestion.getProbabilite() + ")");
		}
		choixCorrection.setListData(choixModele);
	}
	
	/**
	 * Main principal
	 * @param args
	 */
	public static void main(String[] args) {
		Fenetre fenetre = new Fenetre();
	}
	/**
	 * Met a jour la phrase corrigee et le langage detecte a intervalle regulier
	 * @author user
	 *
	 */
	class Updater extends Thread {
		Fenetre fenetre;
		boolean MAJRequise;
		public Updater(Fenetre fenetre) {
			this.fenetre = fenetre;
			MAJRequise = true;
		}
		@Override
		public void run() {
			while(true) {				
				if (MAJRequise) {
					fenetre.corrigerPhrase();
					MAJRequise = false;
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		updater.MAJRequise = true;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		updater.MAJRequise = true;
	}
}
