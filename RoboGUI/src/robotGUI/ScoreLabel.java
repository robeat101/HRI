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
		this.setSize(this.getWidth(), 500);
		this.setBackground(Color.DARK_GRAY);
		this.setForeground(Color.GREEN);
		
		this.setHorizontalAlignment(SwingConstants.CENTER);
		int height = 50;
		Dimension size = new Dimension(getSize().width, height);
		this.setMinimumSize(size);
		this.setPreferredSize(size);
		this.setSize(size);
		//this.setVerticalAlignment(SwingConstants.CENTER);
	}
	
	public void setScore(int n){
		this.setText(String.format("%08d",n));
	}
	
}
