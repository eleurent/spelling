import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Fenetre extends JFrame implements KeyListener {
	JPanel panneauPhrases;
	JTextField input;
	JLabel nomLangagePredit;
	JTextField output;
	Langage langagePredit;
	Updater updater;
	JComboBox<String> choixCorrection;
	
	public Fenetre() {
		panneauPhrases = new JPanel();
		panneauPhrases.setLayout(new BorderLayout());
		input = new JTextField("Life is a tale told by an idiot, full of sound and fury, signifying nothing");
		input.addKeyListener(this);
		output = new JTextField();
		output.setEditable(false);
		nomLangagePredit = new JLabel("???");
		panneauPhrases.add(input, BorderLayout.NORTH);
		panneauPhrases.add(nomLangagePredit, BorderLayout.EAST);
		panneauPhrases.add(output, BorderLayout.CENTER);
		add(panneauPhrases);
		corrigerPhrase();
		updater = new Updater(this);
		updater.start();
		setSize(new Dimension(600,100));
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);		
	}
	
	public void corrigerPhrase() {
		langagePredit = Langage.predireLangage(input.getText(), new Langage[]{Langage.anglais, Langage.francais});
		nomLangagePredit.setText(langagePredit.nom);
		output.setText(langagePredit.corrigerPhrase(input.getText()));
	}
	
	public static void main(String[] args) {
		Fenetre fenetre = new Fenetre();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		updater.MAJRequise = true;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
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
}
