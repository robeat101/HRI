/**
 * This class is the main JPanel in which robot world and all its contents are rendered.
 * it gives and overhead view of the domain space and the tasks and agents in it.
 */

package robotGUI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JProgressBar;


@SuppressWarnings("serial")
public class overheadView extends JPanel implements MouseListener{

	Graphics2D g2d;
	 
	RobotWorld world;
    
    public void update(){
    	world.update();        	
    	repaint();
    }
    public void interpolate(float amount){
    	world.interpolate(amount);
    	repaint();
    }
    
	public overheadView(){
		int width = 800;
		int height = 800;
		this.setPreferredSize(new Dimension(width,height));
		world = new RobotWorld(width, height);
		addMouseListener(this);
	}
	
	public void setUserIndicators(JProgressBar FixIndicator, ScoreLabel ScoreIndicator){
		this.world.user.setIndicators(FixIndicator, ScoreIndicator);
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
	
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
//		System.out.println("Mouse clicked in overhead view at ("+e.getX()+","+e.getY()+")");
		int x= e.getX();
		int y= e.getY();
		world.userClick(x,y);
	}
	
	
	@Override
	public void mouseEntered(MouseEvent e){}
	@Override
	public void mouseExited(MouseEvent e){}
	@Override
	public void mousePressed(MouseEvent e){}
	@Override
	public void mouseReleased(MouseEvent e){}
	 
}
