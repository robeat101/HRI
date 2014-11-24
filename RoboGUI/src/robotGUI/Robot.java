/**
 * This is a representation of a robot.
 * It records the current position and orientation of the robot (state), as well as some behavior parameters.
 */

package robotGUI;

import java.awt.AlphaComposite;
import java.awt.Dimension;
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

public class Robot extends Occupant{
	private int ID;
	
	// position, orientation
	private float		theta;

	// behavior
	private float	intelligence;

	// goals
	private Cell	curGoal;
	private Cell [] path;
	
	//status
	private status robotStatus;

	//rendering
	TransparentRotatedImage img;
	
	public Robot(Cell c, int theta, Cell curGoal, float intelligence, int id, RobotWorld world) {
		super(c, world);
		this.theta = theta;
		this.curGoal = curGoal;
		this.intelligence = intelligence;
		this.robotStatus = status.FORWARD;
		this.ID = id;
		System.out.println("Making new robot with position ("+c.getCol()+","+c.getRow()+") theta = "+theta + " intelligence="+intelligence+".");
			
		try {
			img = new TransparentRotatedImage(ImageIO.read(new File("images/turtle.png")), new Dimension(Math.round(world.getColSpace()), Math.round(world.getRowSpace())), theta, c.toPoint(world));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void turnRobot(status turnStatus)
	{
		float angle = this.theta;
		if (turnStatus == status.LEFT)
		{
			angle+= StandardValues.DELTA_THETA;
		} else if (turnStatus == status.RIGHT)
		{
			angle-= StandardValues.DELTA_THETA;
		}
		setRotation(angle);
	}
	
	private void setRotation(float angle){
		this.theta=angle;
		while (theta>=360){
			theta-=360.0f;
		}
		while (theta<0){
			theta+=360.0f;
		}
		img.setRotation(theta);
	}

	
	public void moveRobot(RobotWorld world) throws InvalidHeadingException
	{
		//System.out.println("\t\t\tMoving Robot " + moveStatus.toString());
		
		if (theta % StandardValues.VALID_HEADING_STEP != 0)
		{
			throw new InvalidHeadingException("Cannot move robot when theta is " + theta + ".");
		} else{
			int directionSign = (robotStatus == status.FORWARD) ? 1 : -1;
			int newRow = this.pos.getRow();
			int newCol = this.pos.getCol();
			
			newRow += directionSign * (int) Math.round(StandardValues.DELTA_POS * Math.sin(Math.toRadians(theta)));
			newCol += directionSign * (int) Math.round(StandardValues.DELTA_POS * Math.cos(Math.toRadians(theta)));
				
			//if the position is valid and there's no one in the new position, move the robot to its new position in the grid
			if (	newCol<world.getCols()
					&&newRow<world.getRows()
					&&newCol>=0
					&&newRow>=0
				){
					
				// if the cell we're moving too is unoccupied
				if (world.grid[newCol][newRow] == null){
					world.grid[this.pos.getCol()][this.pos.getRow()] = null;
					world.grid[newCol][newRow] = this;
						
					//actually move ourselves
					this.pos.setCol(newCol);
					this.pos.setRow(newRow);
						
					//recalculate the place we will be drawn
					recalcRenderPosition(world);
				}else{
					System.out.println("Robot collision detected!");
				}
			}else{
				this.setRotation(this.theta+180); //turn around
			}
		}
	}
	
	private void recalcRenderPosition(RobotWorld world){
		int x=Math.round(this.pos.getCol()*world.getColSpace());
		int y=Math.round(this.pos.getRow()*world.getRowSpace());
		this.img.setPosition(x, y);
	}
	
	public void updateRobot(RobotWorld world){
		
		if(robotStatus == status.FORWARD || robotStatus == status.BACKWARD) {
			try {
				moveRobot(world);
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
	
	public void interpolateRobot(RobotWorld world, float amount){
		if(robotStatus == status.FORWARD || robotStatus == status.BACKWARD) {
			int directionSign = (robotStatus == status.FORWARD) ? 1 : -1;
			int dx = directionSign * (int) Math.round(StandardValues.DELTA_POS * Math.cos(Math.toRadians(theta)));
			int dy = directionSign * (int) Math.round(StandardValues.DELTA_POS * Math.sin(Math.toRadians(theta)));
			int x=Math.round((this.pos.getCol()+amount*dx)*world.getColSpace());
			int y=Math.round((this.pos.getRow()+amount*dy)*world.getRowSpace());
			this.img.setPosition(x, y);
		}
		else if (robotStatus == status.LEFT || robotStatus == status.RIGHT)
		{
			//int directionSign = (robotStatus == status.RIGHT) ? 1 : -1;
			//setRotation(theta+directionSign*90*amount);
		}
	}

	public void draw(Graphics g){
		//System.out.println("\tDrawing Robot at "+this.pos.toString()+", "+theta+"deg)");
		img.paintComponent(g);
	}
	
}
