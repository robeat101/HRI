/**
 * This is a representation of a robot.
 * It records the current position and orientation of the robot (state), as well as some behavior parameters.
 */

package robotGUI;

import robotExceptions.InvalidHeadingException;
import robotExceptions.noPathFoundException;
import robotGenericValues.StandardValues;
import robotGenericValues.status;
import robotPathPlanning.astarCell;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Stack;

import com.ibm.icu.impl.ReplaceableUCharacterIterator;

public class Robot extends Occupant {
	private int				ID;

	// position, orientation
	private float			theta;

	// behavior
	private float			intelligence;

	// goals
	private Cell			curGoal;
	private Stack<astarCell>			path;


	// status
	private status			robotStatus;
	
	TransparentRotatedImage	img;
	private int renderY;
	private int renderX;
	private int renderGoalX;
	private int renderGoalY;

	//robot colors
	private final static String[] possibleRobotColors = {"RED", "BLUE", "GREEN", "YELLOW", "ORANGE", "VIOLET"};
	private final static Color[] correspondingGoalColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, new Color(255,99,0), Color.MAGENTA};
	public final static int nPossibleRobotColors = possibleRobotColors.length;
	
	//goal
	private static final int GOAL_WIDTH = 20;
	
	public Robot(Cell c, int theta, Cell curGoal, float intelligence, int id,
			RobotWorld world)
	{
		super(c, world);
		this.theta = theta;
		this.curGoal = curGoal;
		this.intelligence = intelligence;
		this.robotStatus = status.FORWARD;
		this.ID = id;
		System.out.println("Making new robot "+ID+" with position (" + c.getCol()
				+ "," + c.getRow() + ") theta = " + theta + " intelligence="
				+ intelligence + ".");

		try
		{
			img = new TransparentRotatedImage(ImageIO.read(new File(
					"images/robot_"+possibleRobotColors[id]+".png")), new Dimension(Math.round(world
					.getColSpace()), Math.round(world.getRowSpace())), theta,
					c.toPoint(world));
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
			this.Astar(world);
		}
		catch(noPathFoundException e)
		{
			e.printStackTrace();
		}
	}

	public void turnRobot(status turnStatus)
	{
		setRotation(theta + ((turnStatus == status.LEFT)?1:-1)*StandardValues.VALID_HEADING_STEP);
	}

	private float angleNormalize(float angle){
		while (angle >= 360)
		{
			angle -= 360.0f;
		}
		while (angle < 0)
		{
			angle += 360.0f;
		}
		return angle;
	}
	private void setRotation(float angle)
	{
		this.theta = angleNormalize(angle);
//		if (Math.abs(theta % StandardValues.VALID_HEADING_STEP) > 0.1f){
//			System.out.println("ERROR: set angle to non 90-deg increment!");
//		}
		img.setRotation(theta);
	}

	public void moveRobot(RobotWorld world) throws InvalidHeadingException
	{
		// System.out.println("\t\t\tMoving Robot " + moveStatus.toString());

		if (Math.abs(theta % StandardValues.VALID_HEADING_STEP) > 5.0f)
		{
			throw new InvalidHeadingException(
					"Cannot move robot when theta is " + theta + ".");
		} else
		{
			//snap theta
			this.setRotation(90+ StandardValues.VALID_HEADING_STEP * ((int)(theta+StandardValues.VALID_HEADING_STEP/2)/StandardValues.VALID_HEADING_STEP));
			if (Math.abs(theta % StandardValues.VALID_HEADING_STEP) > 0.1f){
				System.out.println("ERROR: set angle to non 90-deg increment!");
			}
			
			int directionSign = (robotStatus == status.FORWARD) ? 1 : -1;
			int newRow = this.pos.getRow();
			int newCol = this.pos.getCol();

			newRow += directionSign
					* (int) Math.round(StandardValues.DELTA_POS
							* Math.sin(Math.toRadians(theta)));
			newCol += directionSign
					* (int) Math.round(StandardValues.DELTA_POS
							* Math.cos(Math.toRadians(theta)));

			// if the position is valid and there's no one in the new position,
			// move the robot to its new position in the grid
			if (newCol < world.getCols() && newRow < world.getRows()
					&& newCol >= 0 && newRow >= 0)
			{

				// if the cell we're moving too is unoccupied
				if (world.grid[newCol][newRow] == null)
				{
					world.grid[this.pos.getCol()][this.pos.getRow()] = null;
					world.grid[newCol][newRow] = this;

					// actually move ourselves
					this.pos.setCol(newCol);
					this.pos.setRow(newRow);

					// recalculate the place we will be drawn
					recalcRenderPosition(world);
				} else
				{
//					System.out.println("Robot collision detected!");
				}
			} else
			{
				this.setRotation(this.theta + 180); // turn around
			}
		}
	}

	private void recalcRenderPosition(RobotWorld world)
	{
		this.renderX = Math.round(this.pos.getCol() * world.getColSpace());
		this.renderY = Math.round(this.pos.getRow() * world.getRowSpace());
		this.img.setPosition(this.renderX, this.renderY);

		this.renderGoalX = Math.round((this.curGoal.getCol()+0.5f) * world.getColSpace()-GOAL_WIDTH/2.0f);
		this.renderGoalY = Math.round((this.curGoal.getRow()+0.5f) * world.getRowSpace()-GOAL_WIDTH/2.0f);
	}
	
	public void updateRobot(RobotWorld world)
	{
		if (robotStatus != status.WAITING){
			
			//random movements!
			Random rand = new Random();
			int randStatusInt = rand.nextInt(3);
			switch(randStatusInt){
				case 0:
					robotStatus= status.FORWARD;
					break;
				case 1:
					robotStatus = status.LEFT;
					break;
				case 2:
					robotStatus = status.RIGHT;
					break;
			}
			
			/*
			//decide direction based on popping next cell from path
			Cell nextPathCell = this.path.pop().getPos();
			
			//get the difference between our current position and the next cell in the path (should always be one space!)
			int colDiff = this.pos.getCol() - nextPathCell.getCol();
			int rowDiff = this.pos.getRow() - nextPathCell.getRow();
			
			//Error checking
			if (Math.abs(colDiff) > 1 || Math.abs(rowDiff) > 1){
				System.out.println("ERROR: next path cell is not neightbor!");
			}
			if (colDiff!=0 && rowDiff !=0){
				System.out.println("ERROR: next path cell is not cardinal.");
			}
			
			// Direction logic
			float thetaToGo = 0.0f;
			if (colDiff <= -1){	//going WEST (theta of 180)
				thetaToGo = 180.0f - theta;
				
			}else if (colDiff >= 1){ //Going EAST (theta at 0=360)
				thetaToGo = 0.0f - theta;
				
			}else if (rowDiff <= -1){ //going SOUTH (theta of 270)
				thetaToGo = 270.0f - theta; 
				
			}else if (rowDiff >= 1){ //going NORTH (theta of 90)
				thetaToGo = 90.0f - theta;
			}
			
			//normalize thetaToGo to be less than 180deg turns. Stupid angles
			while (thetaToGo > 180.0f){
				thetaToGo -= 10.0f;
			}
			while (thetaToGo < -180.0f){
				thetaToGo += 180.0f;
			}
			
			if (Math.abs(thetaToGo) <= 45.0f){ //close enough
				robotStatus = status.FORWARD;
				//snap theta to nearest standard value
				setRotation((float)(StandardValues.VALID_HEADING_STEP*((int)(theta+0.5*StandardValues.VALID_HEADING_STEP)/StandardValues.VALID_HEADING_STEP)));
			}else{
				robotStatus = (thetaToGo > 45.0f) ? status.RIGHT : status.LEFT;
			}
			*/
		}
		
			
		// Execute!
		if (robotStatus == status.FORWARD || robotStatus == status.BACKWARD)
		{
			try
			{
				moveRobot(world);
			} catch (InvalidHeadingException e)
			{
				e.printStackTrace();
			}
		} else if (robotStatus == status.LEFT || robotStatus == status.RIGHT)
		{
			turnRobot(robotStatus);
		} else if (robotStatus == status.WAITING)
		{
			// TODO: Call check next step function
		}
	}

	
	public void interpolateRobot(RobotWorld world, float amount)
	{
		if (robotStatus == status.FORWARD || robotStatus == status.BACKWARD)
		{
			int directionSign = (robotStatus == status.FORWARD) ? 1 : -1;
			int dx = directionSign
					* (int) Math.round(StandardValues.DELTA_POS
							* Math.cos(Math.toRadians(theta)));
			int dy = directionSign
					* (int) Math.round(StandardValues.DELTA_POS
							* Math.sin(Math.toRadians(theta)));
			int x = Math.round((this.pos.getCol() + amount * dx)
					* world.getColSpace());
			int y = Math.round((this.pos.getRow() + amount * dy)
					* world.getRowSpace());
			this.img.setPosition(x, y);
		} else if (robotStatus == status.LEFT || robotStatus == status.RIGHT)
		{
			int directionSign = (robotStatus == status.RIGHT) ? 1 : -1;
			//setRotation(theta+directionSign*StandardValues.VALID_HEADING_STEP*amount);
			img.setRotation(theta+directionSign*StandardValues.VALID_HEADING_STEP*amount);
		}
	}

	public String ListOfAStarCellsToString(List<astarCell> a){
		String s = "List<astarCell> length " + a.size()+"\n";
		ListIterator<astarCell> iter = a.listIterator();
		while (iter.hasNext()){
			astarCell cell = iter.next();
			s+="\t"+cell.getPos().toString()+" "+cell.getCost(curGoal)+ "\n";
		}
		return s;
	}
	
	public void Astar(RobotWorld world) throws noPathFoundException
	{
		this.path = null; 
		List<astarCell> openCells = new ArrayList<astarCell>();
		List<astarCell> closedCells = new ArrayList<astarCell>();
		openCells.add(new astarCell(pos));

		while (!openCells.isEmpty()) //while there are any cells left to explore on the empty list
		{
			astarCell q = findLeastCostNode(openCells); //get the one with the least F value
			if(q.equals(new astarCell(curGoal)))		//if that cell is the goal
			{
				setPath(q);	//set the path to the goal and exit
				break;
			}
			
			//not the goal
			
			List<astarCell> potentialSuccessors = getSuccessors(q);	//get its neighbors
			openCells = checkSuccessors(potentialSuccessors, openCells,	//add unexplored neighbors to the open set
					closedCells, world);
			openCells.remove(q);	//remove the cell from the open set
//			System.out.println("Open set is " + ListOfAStarCellsToString(openCells));
		}
		
		if(this.path == null)
		{
			throw new noPathFoundException(); 
		}

	}

	private void setPath(astarCell q)
	{
		Stack<astarCell> path = new Stack<astarCell>(); 
		while(q.getParent() != null)
		{
			path.push(q);
			q = q.getParent();
		}
		this.path = path;
	}

	private List<astarCell> checkSuccessors(
			List<astarCell> potentialSuccessors, List<astarCell> openCells,
			List<astarCell> closedCells, RobotWorld world)
	{
		astarCell q = null;
		int size = potentialSuccessors.size();
		boolean validCell = false;
		int replaceIndex = -1;
		for (int i = 0; i < size; i++)
		{
			q = potentialSuccessors.get(i);
			if (!world.occupied(q.getPos()))
			{
				validCell = true;
				replaceIndex = -1;

				for(int j = 0; j < openCells.size(); j++)
					if(openCells.get(j).equals(q))
					{
						replaceIndex = j;
						if(openCells.get(j).getCost(curGoal) < q.getCost(curGoal))
						{
							validCell = false;
						}
					}
				for(int j = 0; j < closedCells.size(); j++)
					if(closedCells.get(j).equals(q))
					{
						if(openCells.get(j).getCost(curGoal) < q.getCost(curGoal))
						{
							validCell = false;
						}
					}
				if(validCell)
				{
					if(replaceIndex > -1)
						openCells.remove(replaceIndex);
					openCells.add(q);
				}
			}
		}
		return openCells;
	}

	private List<astarCell> getSuccessors(astarCell q)
	{
		List<astarCell> potentialSuccessors = new ArrayList<astarCell>();
		Cell newPosition;
		// Top
		if (inWorldRange(q.getPos().getCol(), q.getPos().getRow() + 1))
		{
			newPosition = new Cell(q.getPos().getCol(), q.getPos().getRow() + 1);
			potentialSuccessors.add(new astarCell(newPosition, q));
		}
		// Right
		if (inWorldRange(q.getPos().getCol() + 1, q.getPos().getRow()))
		{
			newPosition = new Cell(q.getPos().getCol() + 1, q.getPos().getRow());
			potentialSuccessors.add(new astarCell(newPosition, q));
		}
		// Bottom
		if (inWorldRange(q.getPos().getCol(), q.getPos().getRow() - 1))
		{
			newPosition = new Cell(q.getPos().getCol(), q.getPos().getRow() - 1);
			potentialSuccessors.add(new astarCell(newPosition, q));
		}
		// Left
		if (inWorldRange(q.getPos().getCol() - 1, q.getPos().getRow()))
		{
			newPosition = new Cell(q.getPos().getCol() - 1, q.getPos().getRow());
			potentialSuccessors.add(new astarCell(newPosition, q));
		}
		return potentialSuccessors;
	}

	private boolean inWorldRange(int x, int y)
	{
		return x < RobotWorld.defaultCols && x > 0
				&& y < RobotWorld.defaultRows && y > 0;
	}

	private astarCell findLeastCostNode(List<astarCell> listOfAstarCells)
	{
		int size = listOfAstarCells.size();
		astarCell calc = listOfAstarCells.get(0);
		for (int i = 0; i < size; i++)
			if (calc.getCost(this.curGoal) > listOfAstarCells.get(i).getCost(
					curGoal))
				calc = listOfAstarCells.get(i);
		return calc;
	}

	public void draw(Graphics g)
	{

		//Draw goal
		Graphics2D g2d = (Graphics2D)g;

		// Assume x, y, and diameter are instance variables.
		Ellipse2D.Double circle = new Ellipse2D.Double(this.renderGoalX, this.renderGoalY, GOAL_WIDTH, GOAL_WIDTH);
		g2d.setColor(correspondingGoalColors[this.ID]);
		g2d.fill(circle);
		g2d.draw(circle);
		// System.out.println("\tDrawing Robot at "+this.pos.toString()+", "+theta+"deg)");
		img.paintComponent(g);

	}
	
	public void fix(){
		//TODO: the user clicked on this robot to fix it. Fix it
		System.out.println("Robot "+ this.ID + " fixed!");
		DataLogger.getDataLogger().log("Robot " + this.ID + " fixed", SimTimer.getCurTime());
	}

	
	public Cell getGoal() {
		return curGoal;
	}

}
