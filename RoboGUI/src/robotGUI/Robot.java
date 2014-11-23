/**
 * This is a representation of a robot.
 * It records the current position and orientation of the robot (state), as well as some behavior parameters.
 */

package robotGUI;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import robotExceptions.InvalidHeadingException;
import robotGenericValues.StandardValues;
import robotGenericValues.status;

import java.io.File;
import java.io.IOException;
import java.lang.Math;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Robot extends Occupant{
	// position, orientation
	private int		theta;

	// behavior
	private float	intelligence;

	// goals
	private Point	curGoal;
	private Point [] path;
	
	//status
	private status robotStatus;

	//rendering
	TransparentRotatedImage img;
	
	public Robot(Point pos, int theta, Point curGoal, float intelligence, JPanel panel) {
		super(pos);
		this.theta = theta;
		this.curGoal = curGoal;
		this.intelligence = intelligence;
		
		System.out.println("Making new robot with position ("+pos.getX()+","+pos.getY()+") theta = "+theta + " intelligence="+intelligence+".");
		
		ImageIcon icon = null;
		try{
			icon = new ImageIcon("C:/Users/Dan/Documents/CLASSWORK/Nick MQP/EclipseWorkspace/HRI/RoboGUI/resources/sea-turtle.png");
			img = new TransparentRotatedImage(icon.getImage());
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("icon: "+icon.toString());
		System.out.println("image: "+img.toString());
		System.out.println("panel: " + panel.toString());
		panel.add(img);
		
		//TODO: image seems fine - why is the JPanel null??????!!!
	}

	public void turnRobot(status turnStatus)
	{
		if (turnStatus == status.LEFT)
		{
			this.theta = this.theta + StandardValues.DELTA_THETA;
		} else if (turnStatus == status.RIGHT)
		{
			this.theta = this.theta - StandardValues.DELTA_THETA;
		}
	}

	public void moveRobot(status moveStatus)
			throws InvalidHeadingException
	{
		if (theta % StandardValues.VALID_HEADING_STEP != 0)
		{
			throw new InvalidHeadingException(
					"Cannot move robot when theta is " + theta + ".");
		} else if (moveStatus == status.FORWARD)
		{
			this.pos.setRow(this.pos.getRow()
					+ (int) Math.round(StandardValues.DELTA_POS
							* Math.sin(Math.toRadians(theta))));
			this.pos.setCol(this.pos.getCol()
					+ (int) Math.round(StandardValues.DELTA_POS
							* Math.cos(Math.toRadians(theta))));
		} else if (moveStatus == status.BACKWARD)
		{
			this.pos.setRow(this.pos.getRow()
					- (int) Math.round(StandardValues.DELTA_POS
							* Math.sin(Math.toRadians(theta))));
			this.pos.setCol(this.pos.getCol()
					- (int) Math.round(StandardValues.DELTA_POS
							* Math.cos(Math.toRadians(theta))));
		}
	}
	
	public void updateRobot(){

		if(robotStatus == status.FORWARD || robotStatus == status.BACKWARD) {
			try {
				moveRobot(robotStatus);
			} catch (InvalidHeadingException e) {
				e.printStackTrace();
			}
		}
		else if (robotStatus == status.LEFT || robotStatus == status.RIGHT)
		{
			turnRobot(robotStatus);
		}
		else if(robotStatus == status.WAITING)
		{
			//TODO: Call check next step function
		}
	}

	public void draw(Graphics g){
		int xPos = 10;
		int yPos = 10;
		System.out.println("\tDrawing Robot at ("+xPos+","+yPos+","+theta+")");
		//super.paint(g);
		img.paintComponent(g);
		
		/*
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.rotate(theta*Math.PI/180, getWidth()/2, getHeight()/2);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		
		int x = this.pos.getCol();
		int y = this.pos.getRow();
		g.drawImage(img.get, xPos, yPos, null);
		*/
	}
	
}
