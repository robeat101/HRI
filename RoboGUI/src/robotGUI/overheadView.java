/**
 * This class is the main JPanel in which robot world and all its contents are rendered.
 * it gives and overhead view of the domain space and the tasks and agents in it.
 */

package robotGUI;

import java.awt.Color;
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
	private int rows;
	private int cols;
	private int rowSpace;
	private int colSpace;
	Graphics2D g2d;
	 
	RobotWorld world;
	
	BufferedImage transformedImage;
	float angle = 0;
    private Timer timer = new Timer(20, new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
        	
        	angle+=0.1;

        	repaint();
        }
    });
    
	public overheadView(){
		rows=20;
		cols=20;
        
		world = new RobotWorld(rows, cols, this);
		
		try{
			transformedImage =  ImageIO.read(new File("images/turtle.png"));//getImage();
		}catch(IOException e){
			System.out.println("Exception reading image file");
		}
		timer.start();
	}
	
	private void doDrawing(Graphics g) {
		g2d = (Graphics2D) g;
		rowSpace = this.getHeight()/rows;
		colSpace = this.getWidth()/cols;
		
		//draw grid
        drawGrid(g2d);	//draw grid
        
        //draw RobotWorld
        world.draw(g);
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
	
	
	private void rerotate(){
		System.out.println("Rotation angle: " + angle+".");
		double sin=Math.abs(Math.sin(angle)), cos=Math.abs(Math.cos(angle));
		int w = transformedImage.getWidth(), h = transformedImage.getHeight();
		int neww = (int)Math.floor(w*cos+h*sin), newh=(int)Math.floor(h*cos+w*sin);
		BufferedImage rotatedimg = new BufferedImage(neww, newh, Transparency.TRANSLUCENT);
		Graphics2D newImgG2d = rotatedimg.createGraphics();
		newImgG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		newImgG2d.translate((neww-w)/2,(newh-h)/2);
		newImgG2d.rotate(angle, w/2, h/2);
		//newImgG2d.drawRenderedImage(transformedImage,  null);
		transformedImage = rotatedimg;
		newImgG2d.dispose();
	}
	
	private BufferedImage getImage(){
		BufferedImage img = new BufferedImage(100,100, BufferedImage.TRANSLUCENT);
		Graphics2D imgg2d = img.createGraphics();
		imgg2d.setColor(Color.GREEN);
		imgg2d.fillRect(0, 0, img.getWidth(), img.getHeight());
		imgg2d.dispose();
		return img;
	}
	
	private BufferedImage rotate(BufferedImage bi, float angle){
		AffineTransform at = new AffineTransform();
		//at.translate(-bi.getWidth()/2, -bi.getHeight()/2);
		at.rotate(angle, bi.getWidth()/2.0, bi.getHeight()/2.0);
		BufferedImageOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return op.filter(bi, null);
	}
	
	@Override
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);

        
        doDrawing(g);
        
        g.drawImage(rotate(transformedImage, angle), 10, 400, null);

    }    
}
