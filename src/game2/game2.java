package game2;
import javax.swing.*;

public class game2 {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("PACMAN IN HELL");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		game2Panel panel = new game2Panel();
		frame.getContentPane().add(panel);		//add panel
		
		frame.pack();   
		frame.setVisible(true);
		
	}

}
