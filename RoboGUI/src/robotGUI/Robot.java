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

public class Robot extends Occupant {
	private int				ID;
	private int 			colorId;
	
	// position, orientation
	private float			theta;

	// behavior
	private float			intelligence;
	public static final float[] intelligences = {.99f, 0.95f, 0.9f, 0.8f, 0.7f, 0.6f};
	private static final float addedIntelligenceFromFix = 0.5f;
	private static final float intelligenceDecayPerStep = 0.25f;
	private float intelligenceAdder = 0.0f;
	private long brokeTime =0;
	// goals
	private Cell			curGoal;
	private Stack<astarCell>			path;
	
	//Score
	private int score = 0; 

	// status
	private status			robotStatus;
	
	TransparentRotatedImage	img;
	private int renderY;
	private int renderX;
	private int renderGoalX;
	private int renderGoalY;
	private int renderAlertX;
	private int renderAlertY;
	private char[] strAlert;
	
	//robot colors
	private final static String[] possibleRobotColors = {"RED", "BLUE", "GREEN", "YELLOW", "ORANGE", "VIOLET"};
	private final static Color[] correspondingGoalColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, new Color(255,99,0), Color.MAGENTA};
	private static boolean[] colorIDsUsed = {false, false, false, false, false, false};
	public final static int nPossibleRobotColors = possibleRobotColors.length;
	
	//debug
	private final static boolean VERBOSE = false;
	
	//goal
	private static final int GOAL_WIDTH = 20;
	private static final int ALERT_WIDTH = 15;
	public void setNewGoal(RobotWorld world){
		this.curGoal = world.getRandomUnoccupiedCell();
		if (VERBOSE){System.out.println("Setting robot " + this.ID + " goal to " + curGoal.toString());}
		this.recalcGoalRenderPosition(world);
		try{
			this.Astar(world);
			setStatusToGetToNextPathCell(); 
		}
		catch(noPathFoundException e)
		{
			this.robotStatus = status.NOPATH;
		}

	}
	
	public Robot(Cell c, int theta, int id,
			RobotWorld world)
	{
		super(c, world);
		this.theta = theta;
		this.intelligence = intelligences[id];
		this.strAlert = (StandardValues.DRAW_INTELLIGENCE)?(Integer.toString((int)Math.round(this.intelligence*100)).toCharArray()):("!".toCharArray());
		this.robotStatus = status.WAITING;
		this.ID = id;
		if(VERBOSE){
			System.out.println("Making new robot "+ID+" with position (" + c.getCol()
				+ "," + c.getRow() + ") theta = " + theta + " intelligence="
				+ intelligence + ".");
		}

		this.colorId = getRandomUnusedColorID();
		try
		{
			img = new TransparentRotatedImage(ImageIO.read(new File(
					"images/robot_"+possibleRobotColors[this.colorId]+".png")), new Dimension(Math.round(world
					.getColSpace()), Math.round(world.getRowSpace())), theta,
					c.toPoint(world));
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		DataLogger.getDataLogger().logInit("Robot " + this.ID + " initialized with intelligence " + this.intelligence);
	}

	private int getRandomUnusedColorID() {
		Random rand = new Random();
		int id = rand.nextInt(nPossibleRobotColors);
		while (colorIDsUsed[id]){
			id = rand.nextInt(nPossibleRobotColors);
		}
		colorIDsUsed[id]=true;
		return id;
	}

	//Turns the robot 90 degrees relative to its current rotation depending on the turn status
	public void turnRobot(status turnStatus)
	{
		setRotation(theta + ((turnStatus == status.LEFT)?1:-1)*StandardValues.VALID_HEADING_STEP);
		if (turnStatus != status.LEFT && turnStatus!=status.RIGHT){
			System.out.println("ERROR: turnRobot() called when robot wasn't turning!");
		}
		setStatusToGetToNextPathCell();
	}

	private float normalizeAngle(float angle){
		while (angle > 360)
		{
			angle -= 360.0f;
		}
		while (angle < -0)
		{
			angle += 360.0f;
		}
		return angle;
	}
	
	// used to determine smallest absolute equivalent turning angles
	private float normalizeAngleNeg(float angle){
		while (angle > 180)
		{
			angle -= 360.0f;
		}
		while (angle < -180)
		{
			angle += 360f;
		}
		return angle;
	}
	
	//sets the absolute robot rotation angle and also rotates the image accordingly.
	private void setRotation(float angle)
	{
		this.theta = normalizeAngle(angle);
		if (Math.abs(theta % StandardValues.VALID_HEADING_STEP) > 0.1f){
			System.out.println("ERROR: set rotation angle to non 90-deg increment!");
		}
		img.setRotation(theta);
	}

	private void snapTheta(){
		//snap theta to nearest cardinal direction
		this.setRotation(StandardValues.VALID_HEADING_STEP * ((int)(theta+StandardValues.VALID_HEADING_STEP/2)/StandardValues.VALID_HEADING_STEP));
	}
	
	public void moveRobot(RobotWorld world) throws InvalidHeadingException
	{
		if (VERBOSE){System.out.println("\tMoving Robot " + robotStatus.toString());}
		if (Math.abs(theta % StandardValues.VALID_HEADING_STEP) > 5.0f)	//if the robot is not facing a cardinal direction within some small threshold
		{
			throw new InvalidHeadingException("Cannot move robot when theta is " + theta + ".");
		} else
		{
			//snapTheta();
			
			//calculate what row and column we'll end up on when we move
			int directionSign = (robotStatus == status.FORWARD) ? 1 : -1;
			if (robotStatus != status.FORWARD && robotStatus != status.BACKWARD){
				System.out.println("ERROR: Robot moves but was not moving foreward or backward!");
			}
			int newRow = this.pos.getRow(); 
			int newCol = this.pos.getCol();
			newRow += directionSign
					* (int) 0.5 + Math.round(StandardValues.DELTA_POS
							* Math.sin(Math.toRadians(theta)));
			newCol += directionSign
					* (int) 0.5 + Math.round(StandardValues.DELTA_POS
							* Math.cos(Math.toRadians(theta)));

			// if the position is valid (on the grid)
			if (newCol < world.getCols() && newRow < world.getRows()
					&& newCol >= 0 && newRow >= 0)
			{

				// if the cell we're moving too is unoccupied
				Occupant curOccupant = world.grid[newCol][newRow];
				if (curOccupant == null)
				{
					world.grid[this.pos.getCol()][this.pos.getRow()] = null; 	//leave our previous position
					world.grid[newCol][newRow] = this;							//set us as the new occupant of our new position

					// actually move ourselves
					this.pos.setCol(newCol);
					this.pos.setRow(newRow);

					// recalculate the place we will be drawn
					recalcRenderPosition(world);
					if (!this.path.isEmpty()){
						Cell nextPathCell = this.path.peek().getPos();
						if (pos.getCol() == nextPathCell.getCol() && pos.getRow() == nextPathCell.getRow()){
							this.path.pop(); //NOTE we can pop this path cell from the path since we just got to it
						
							
						}else{ //robot must have gotten confused and moved off it's path. make a new path.
							System.out.println(" ERROR: Robot moved to a cell "+this.pos.toString()+" that was not it's next path cell"+nextPathCell.toString()+".");
							this.followNewPath(world);
						}
					}else{
						System.out.println("ERROR: Robot moved but the path was empty.");
						this.setNewGoal(world);
					}
					
					//apparently we must detect goal reaching at te step inwhich we move to the goal.
					if (this.pos.equals(this.getGoal())){
						//System.out.println("ERROR: moving robot when it reached it's goal.");
						this.reachedGoal();
					}else{
						setStatusToGetToNextPathCell();
					}
					
				} else { // another occupant is currently in the cell
					if (VERBOSE){System.out.println("Robot collision detected!");}
					if (curOccupant instanceof Obstacle){
						obstacleEncountered(world);
					}else{ //hit another robot
						this.robotStatus = status.COLLISION;
					}
				}
			} else { // robot reached a wall
				//next cell is occupied - turn around.
				obstacleEncountered(world);
			}
		}
	}
	
	private void reachedGoal(){
		if (VERBOSE){System.out.println("Robot " + this.ID + " at "+this.pos.toString()+" reached its goal "+this.curGoal.toString()+"!");}
		this.score += StandardValues.SCORE_GOAL_VALUE;
		RobotWorld.getRobotWolrd().Score += StandardValues.SCORE_GOAL_VALUE;
		this.robotStatus = status.REACHEDGOAL;
		DataLogger.getDataLogger().log("Robot "+this.ID+" reached goal. RobotScore: "+this.score+", WorldScore:" + RobotWorld.getRobotWolrd().Score);
		this.setNewGoal(RobotWorld.getRobotWolrd());
	}

	//Robot tried to move into an obstacle (this should actually never happen unless the robot is being unintelligent?)
	private void obstacleEncountered(RobotWorld world){
		if (VERBOSE){System.out.println("Robot " + this.ID + " encountered an obstacle!");}
		followNewPath(world);
	}
	
	private void followNewPath(RobotWorld world){
		try{
			this.Astar(world); //re-plan around obstacle
			setStatusToGetToNextPathCell();
		}catch(noPathFoundException e){
			this.robotStatus = status.COLLISION;
		}
	}
	
	private void recalcRenderPosition(RobotWorld world)
	{
		this.renderX = Math.round(this.pos.getCol() * world.getColSpace());
		this.renderY = Math.round(this.pos.getRow() * world.getRowSpace());
		this.img.setPosition(this.renderX, this.renderY);
	}
	
	private void recalcGoalRenderPosition(RobotWorld world){
		this.renderGoalX = Math.round((this.curGoal.getCol()+0.5f) * world.getColSpace()-GOAL_WIDTH/2.0f);
		this.renderGoalY = Math.round((this.curGoal.getRow()+0.5f) * world.getRowSpace()-GOAL_WIDTH/2.0f);
	}
	
	private void doSomethingRandom(){
		//random movements!
		Random rand = new Random();
		int randStatusInt;
		status originalStatus = this.robotStatus;
		while (this.robotStatus != originalStatus){
			randStatusInt = rand.nextInt(5);
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
				case 3:
					robotStatus = status.BACKWARD;
					break;
				case 4:
					robotStatus = status.WAITING;
					break;
			}
		}
	}
	
	private void setStatusToGetToNextPathCell(){
		
		//decide direction based on popping next cell from path
		if (!this.path.isEmpty()){
			Cell nextPathCell = this.path.peek().getPos();

			if (VERBOSE){System.out.println("\tDeciding next action to get from current position " + pos.toString() +" to next path cell " + nextPathCell.toString() + "...");}
			
			//get the difference between our current position and the next cell in the path (should always be one space!)
			int colDiff = nextPathCell.getCol() - this.pos.getCol();
			int rowDiff = nextPathCell.getRow() - this.pos.getRow();
			
			if (VERBOSE){System.out.println("\t\tRobot must go Left " + colDiff + " and down " + rowDiff + ".");}
			
			//Error checking
			if ((Math.abs(colDiff) > 1 || Math.abs(rowDiff) > 1) || (colDiff!=0 && rowDiff !=0)){
				if (VERBOSE){
					System.out.println("ERROR: next path cell is not neightbor" +  this.pos.toString() + " to " + nextPathCell.toString());
					System.out.println("ERROR: next path cell is not cardinal:" + this.pos.toString() + " to " + nextPathCell.toString());
				}
				robotStatus = status.OFFPATH;
			}
			
			// Direction logic			
			float thetaToGo = 0.0f;
			if (colDiff == -1){	//going WEST (theta of 180)
				thetaToGo = StandardValues.WEST_THETA - theta;
			}else if (colDiff == 1){ //Going EAST (theta at 0=360)
					thetaToGo = StandardValues.EAST_THETA - theta;
				
			}else if (rowDiff == 1){ //going SOUTH (theta of 90)
				thetaToGo = StandardValues.SOUTH_THETA -  theta; 
				
			}else if (rowDiff == -1){ //going NORTH (theta of 270)
				thetaToGo = StandardValues.NORTH_THETA - theta;
			}

			
			//thetaToGo = normalizeAngle(thetaToGo);
			thetaToGo = normalizeAngleNeg(thetaToGo);
						
			if (VERBOSE){System.out.println("Robot turn " + thetaToGo + " degrees to face next path cell");}
			
			if (Math.abs(thetaToGo) <= 45.0f && Math.abs(thetaToGo) >= -45.0f){ //close enough
			
				robotStatus = status.FORWARD;
				if (VERBOSE){System.out.println("Setting "+robotStatus.toString() + " to get to from "+this.pos.toString() +" to next path cell " + nextPathCell.toString());}

				//snap theta to nearest standard value
				setRotation((float)(StandardValues.VALID_HEADING_STEP*((int)(theta+0.5*StandardValues.VALID_HEADING_STEP)/StandardValues.VALID_HEADING_STEP)));
			}else{
				robotStatus = (thetaToGo < -45.0f) ? status.RIGHT : status.LEFT;
				if (VERBOSE){System.out.println("Turning robot "+robotStatus.toString() + " to get to from "+this.pos.toString() +" facing " +getAngleAsString()+ " to next path cell " + nextPathCell.toString());}
			}
		}else if(curGoal.getCol() == pos.getCol() && curGoal.getRow() == pos.getRow()){
			if (this.robotStatus != status.REACHEDGOAL){
				if (VERBOSE){System.out.println("REACHED GOAL!!");}
				System.out.println("ERROR: Reached goal while setting status (not already detected?)");
				this.reachedGoal();
			}else{
				System.out.println("ERROR: Reached goal while setting status?");
				this.setNewGoal(RobotWorld.getRobotWolrd());
			}
			//this.robotStatus = status.REACHEDGOAL;
		}
		else
		{
			System.out.println("ERROR: robot tried to find the next action to follow an empty path");
		}
	}
	
	private String getAngleAsString(){
		return Integer.toString((int)(theta+0.5));
	}
	
	public void updateRobot(RobotWorld world)
	{		
	
		if(curGoal.equals(pos) || this.pos.equals(this.curGoal))
		{
			System.out.println("ERROR: Detected goal after it was reached!");
			this.reachedGoal();
		}

		//update on this timestep to complete the current move and determine the next one.		
		if (VERBOSE){System.out.println("Updating robot now at "+this.pos.toString()+" facing "+getAngleAsString()+"...");}
		// Execute robot status!
		if (robotStatus == status.FORWARD || robotStatus == status.BACKWARD){
			try
			{
				moveRobot(world);
			} catch (InvalidHeadingException e)
			{
				e.printStackTrace();
			}
		}else if (robotStatus == status.LEFT || robotStatus == status.RIGHT){
			turnRobot(robotStatus);
		}else if (robotStatus == status.WAITING){
			System.out.println("...robot waiting...");
		}else if (robotStatus == status.COLLISION){
			followNewPath(world);
		}else if (robotStatus == status.NOPATH){
			setNewGoal(world); //keep trying to get new goals.
		}
		else if(robotStatus == status.REACHEDGOAL)
		{
			this.setNewGoal(world); //get a new goal!
		}
		
		// confuse the unintelligent (for the next timestep)!
		Random confusion = new Random();
		if ((this.robotStatus!=status.CONFUSED) && (confusion.nextFloat() >= intelligence + intelligenceAdder)){ //the greater the intelligence, the less the probability of confusion
			this.robotStatus = status.CONFUSED;
			if (VERBOSE){System.out.println("Robot " + this.ID + " got confused!");}
			//DataLogger.getDataLogger().log("Robot " + this.ID + " broke.");
			this.brokeTime=SimTimer.getCurTime();
		}
		
		//decline added intelligence over time
		if (intelligenceAdder>0.0f){
			intelligenceAdder-=intelligenceDecayPerStep;
			if (intelligenceAdder<0.0f){
				intelligenceAdder=0.0f;
			}
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
			
			//only interpolate into unoccupied cells.
			if (world.grid[this.pos.getCol()+dx][this.pos.getRow()+dy] == null){
				int x = Math.round((this.pos.getCol() + amount * dx)
						* world.getColSpace());
				int y = Math.round((this.pos.getRow() + amount * dy)
						* world.getRowSpace());
				this.img.setPosition(x, y);
			}
		} else if (robotStatus == status.LEFT || robotStatus == status.RIGHT)
		{
			int directionSign = (robotStatus == status.RIGHT) ? 1 : -1;
			//setRotation(theta+directionSign*StandardValues.VALID_HEADING_STEP*amount);
			img.setRotation(theta-directionSign*StandardValues.VALID_HEADING_STEP*amount);
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
			openCells.remove(q);//Pop q from open set. 

			List<astarCell> potentialSuccessors = getSuccessors(q);	//get its neighbors
			openCells = checkSuccessors(potentialSuccessors, openCells,	closedCells, world); //add unexplored neighbors to the open set

			closedCells.add(q); //Add q to closed set.
		}
		
		if(this.path == null)
		{
			throw new noPathFoundException(); 
		}else{
			if (VERBOSE){System.out.println("\tA* found path from current posititon " + this.pos + " to goal " + this.curGoal + "\n" + ListOfAStarCellsToString(this.path));}
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
						if(closedCells.get(j).getCost(curGoal) < q.getCost(curGoal))
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

	private static final int charOffsetX = -2;
	private static final int charOffsetY = 5;
	public void draw(Graphics g)
	{
		//Draw goal
		Graphics2D g2d = (Graphics2D)g;
		
		// Assume x, y, and diameter are instance variables.
		Ellipse2D.Double circle = new Ellipse2D.Double(this.renderGoalX, this.renderGoalY, GOAL_WIDTH, GOAL_WIDTH);
		g2d.setColor(correspondingGoalColors[this.colorId]);
		g2d.fill(circle);
		g2d.draw(circle);
		// System.out.println("\tDrawing Robot at "+this.pos.toString()+", "+theta+"deg)");
		img.paintComponent(g);
		
		//draw other stuff on top of the robots
		Point thisRobotsRenderPosition = this.img.getRenderPosition();
		double alertX= thisRobotsRenderPosition.getX()+RobotWorld.getCellWidth()/2.0f;
		double alertY= thisRobotsRenderPosition.getY()+RobotWorld.getCellHeight()/2.0f;
		
		//draw alert if we're confused
		if (robotStatus == status.CONFUSED){
			circle = new Ellipse2D.Double(alertX-ALERT_WIDTH/2.0f, alertY-ALERT_WIDTH/2.0f, ALERT_WIDTH, ALERT_WIDTH);
			g2d.setColor(Color.BLACK);
			g2d.fill(circle);
			g2d.draw(circle);
			
			if (SimTimer.getCurTimeMillis()%2==0){
				g2d.setColor(Color.YELLOW);
				g2d.drawChars(strAlert, 0, 1, (int)Math.round(alertX)+charOffsetX, (int)Math.round(alertY)+charOffsetY);
			}
		}else{
			if (StandardValues.DRAW_INTELLIGENCE){
				g2d.setColor(Color.BLACK);
				g2d.drawChars(strAlert, 0, 1, (int)Math.round(alertX)+charOffsetX, (int)Math.round(alertY)+charOffsetY);
			}
		}
	}
	
	//TODO: the user clicked on this robot to fix it. Fix it!
	
	public void fix(RobotWorld world){
		intelligenceAdder = addedIntelligenceFromFix;
		if (VERBOSE){System.out.println("Robot "+ this.ID + " fixed!");}
		this.followNewPath(world); //find a new way to get there!
		DataLogger.getDataLogger().log("RobotID:" + this.ID + " after neglectTime:"+(SimTimer.getCurTime()-this.brokeTime)+", PathCellsToGo:"+((this.path==null)?"NoPath":(this.path.size()))+", UserMana: " + world.user.fixingAbility+", RobotScore: "+this.score);
		this.brokeTime=0;
		//System.out.println("user clicked on robot " + this.ID);
	}
	
	public Cell getGoal() {
		return curGoal;
	}

}
