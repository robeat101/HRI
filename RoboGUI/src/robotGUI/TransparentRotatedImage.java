package robotGUI;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JPanel;

public class TransparentRotatedImage extends JPanel {
	private final Image image;
    private double rotation;

    public TransparentRotatedImage(Image image) {
        this.image = image;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g2.setColor(new Color(0, 0, 200, 90));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setTransform(getTransformation());
        g2.drawImage(image, 0, 0, this);
    }

    protected AffineTransform getTransformation() {
        try {
            AffineTransform translateInstance = AffineTransform.getTranslateInstance(+image.getWidth(this) / 2,
                    +image.getWidth(this) / 2);
            AffineTransform inverse = translateInstance.createInverse();
            AffineTransform rotateInstance = AffineTransform.getRotateInstance(Math.toRadians(rotation));
            AffineTransform at = translateInstance;
            at.concatenate(rotateInstance);
            at.concatenate(inverse);
            return at;
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(this), image.getHeight(this));
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
        repaint();
    }
}
