/**
 * This class is the main JPanel in which robot world and all its contents are rendered.
 * it gives and overhead view of the domain space and the tasks and agents in it.
 */

package robotGUI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class overheadView extends JPanel {
	private int rows;
	private int cols;
	private int rowSpace;
	private int colSpace;
	Graphics2D g2d;
	 
	RobotWorld world;
	
	public overheadView(){
		rows=20;
		cols=20;
        
		world = new RobotWorld(rows, cols);
	}
	
	private void doDrawing(Graphics g) {
		g2d = (Graphics2D) g;
		rowSpace = this.getHeight()/rows;
		colSpace = this.getWidth()/cols;
		
		//draw robots
        drawGrid(g2d);	//draw grid
   } 
	
	private void drawGrid(Graphics2D g2d){
        int y;
		for (int r=0;r<=rows;r++){
        	y=r*rowSpace;
        	g2d.drawLine(0, y, this.getWidth(),y);
        }
		int x;
        for (int c=0;c<=cols;c++){
        	x=c*colSpace;
        	g2d.drawLine(x, 0, x, this.getHeight());
        }
	}
	
	@Override
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        doDrawing(g);
    }    
}
