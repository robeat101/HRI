/**
 * This class is the main JPanel in which robot world and all its contents are rendered.
 * it gives and overhead view of the domain space and the tasks and agents in it.
 */

package robotGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.AbstractAction;

public class overheadView extends JPanel {

	Graphics2D g2d;
	 
	RobotWorld world;
    
    public void update(){
    	world.update();        	
    	repaint();
    }
    
	public overheadView(){
		int width = 800;
		int height = 800;
		this.setPreferredSize(new Dimension(width,height));
		world = new RobotWorld(width, height);
	}
	
	private void doDrawing(Graphics g) {
        
        //draw RobotWorld
        world.draw(g);
	} 
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }    
}
