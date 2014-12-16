package robotGUI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TransparentRotatedImage extends JPanel {
	private final Image image;
    private double rotation;
    private AffineTransform curTransform;
    private Point renderPosition;
    
    public TransparentRotatedImage(Image image, Dimension d, int rot, Point position) {
        this.image = image.getScaledInstance(d.width,d.height, Image.SCALE_SMOOTH);
        setOpaque(false);
        rotation = rot;
        renderPosition = position;
        recalcTransformation();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        //g2.setColor(new Color(0, 0, 200, 90));
        //g2.fillRect(0, 0, getWidth(), getHeight());
        AffineTransform prevTransform = g2.getTransform();
        g2.setTransform(curTransform);
        g2.drawImage(image, -image.getWidth(this) / 2, -image.getWidth(this) / 2, this); //renderPosition.x, renderPosition.y
        g2.setTransform(prevTransform);
    }
    
    protected void recalcTransformation() {
    	try {
            AffineTransform translateInstance = AffineTransform.getTranslateInstance(renderPosition.x+image.getWidth(this) / 2, renderPosition.y+image.getWidth(this) / 2);//image.getWidth(this) / 2, image.getWidth(this) / 2);
            @SuppressWarnings("unused")
			AffineTransform inverse = translateInstance.createInverse();
            AffineTransform rotateInstance = AffineTransform.getRotateInstance(Math.toRadians(rotation));
            //AffineTransform at = translateInstance;
            
            AffineTransform at = translateInstance;
            at.concatenate(rotateInstance);
            //at.concatenate(rotateInstance);
            //at.concatenate(inverse);
            curTransform = at;
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            curTransform = null;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(this), image.getHeight(this));
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
        recalcTransformation();
        repaint();
    }
    
    public void setPosition(int X, int Y){
    	renderPosition.x = X;//+image.getWidth(this)/2;//this.getWidth()/2;
    	renderPosition.y = Y;//+image.getHeight(this)/2;//this.getHeight()/2;
    	recalcTransformation();
    	repaint();
    }
}
