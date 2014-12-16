package robotGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;


public class ScoreLabel extends JLabel {

	public ScoreLabel(){
		super();
		Font f = new Font("OCR Extended", Font.BOLD, 500);
		this.setOpaque(true);
		this.setSize(this.getWidth(), 300);
		this.setBackground(Color.DARK_GRAY);
		this.setForeground(Color.GREEN);
		
		this.setHorizontalAlignment(SwingConstants.CENTER);

		this.setMinimumSize(new Dimension(getMinimumSize().width, 300));
		//this.setVerticalAlignment(SwingConstants.CENTER);
	}
	
	public void setScore(int n){
		this.setText(Integer.toString(n));
		//String.format("%08d", 
	}
	
}
