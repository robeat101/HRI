package robotGUI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

import robotGenericValues.StandardValues;

public class RobotWorld {

	//users
	User user;
	
	//robots
	private int nRobots = 1; //Robot.nPossibleRobotColors;
	private Robot[] robots;
	
	//obstacles
	private int nObstacles = 30;
	private Obstacle[] obstacles;
	
	//occupancy grid
	public static int defaultRows = 20;
	public static int defaultCols = 20;
	private int rows;
	private int cols;
	private float rowSpace;
	private float colSpace;
	Occupant grid[][];
	
	//private JPanel renderPanel;
	public RobotWorld(int colSpace, int rowSpace){
		this(defaultRows, defaultCols, colSpace, rowSpace);
	}
	public RobotWorld(int r, int c, int WIDTH, int HEIGHT){
		rows=r;
		cols=c;
		colSpace = (float)WIDTH/(float)rows;
		rowSpace = (float)HEIGHT/(float)cols;
		System.out.println("Making RobotWorld of "+cols+"X"+rows+" cells each size "+colSpace+"x"+rowSpace+".");
		
		//make user
		user = new User();
		
		//make robots and add them to the grid
		grid = new Occupant[cols][rows];
		for (r=0;r<rows;r++){
			for (c=0;c<cols;c++){
				grid[c][r]=null;
			}
		}
		makeRobots();
		makeObstacles();
		
		DataLogger.initDataLogger();
	}
	
	private void makeRobots(){
		//make a bunch of robots
		//nRobots = 10;
		robots = new Robot[nRobots];
		for (int i=0;i<nRobots;i++){
			robots[i] = makeRandomRobot(i);
		}
	}
	
	private void makeObstacles(){
		//nObstacles = 15;
		obstacles = new Obstacle[nObstacles];
		for (int i=0;i<nObstacles;i++){
			obstacles[i] = makeRandomObstacle();
		}
	}
	
	public void update(){
//		System.out.println("\tUPDATE ROBOT WORLD...");
		//update robots
		updateRobots();
	}
	
	public void interpolate(float amount){
		interpolateRobots(amount);
		user.update();
	}
	
	private void updateRobots(){
//		System.out.println("\t\tUpdate robots...");
		for (int i=0;i<nRobots;i++){
			robots[i].updateRobot(this);
		}
	}
	
	public void interpolateRobots(float amount){
		for (int i=0;i<nRobots;i++){
			robots[i].interpolateRobot(this, amount);
		}
		user.update();
	}
	
	private Robot makeRandomRobot(int id){
		Random rn = new Random();
		
		Cell randPos = getRadomUnoccupiedCell();	//make a random unoccupied position
		float intelligence = rn.nextFloat();		//make a random intelligence
		int theta = rn.nextInt(3)*StandardValues.VALID_HEADING_STEP;				//make a random rotation theta
		Cell goal = getRadomUnoccupiedCell();		//make a random goal
		
		//create the robot
		Robot robot =  new Robot(randPos,theta, goal, intelligence, id, this);
		
		return robot;
	}
	
	private Obstacle makeRandomObstacle(){
		Cell randPos = getRadomUnoccupiedCell();	//make a random unoccupied position
		return new Obstacle(randPos, this);
	}
	
	Occupant getOccupant(Cell c){
		return grid[c.getCol()][c.getRow()];
	}
	
	public boolean occupied(Cell c){
		return (getOccupant(c) != null);
	}
	
	public int getCols(){
		return cols;
	}
	public int getRows(){
		return rows;
	}
	
	public float getColSpace(){
		return colSpace;
	}
	public float getRowSpace(){
		return rowSpace;
	}
	
	/**
	 * Returns a random unoccupied point
	 */
	public Cell getRadomUnoccupiedCell(){
		Random rn = new Random();
		
		//make a random unoccupied position
		int robotCol = rn.nextInt(cols);
		int robotRow = rn.nextInt(rows);
		while (occupied(new Cell(robotCol, robotRow))){
			robotCol = rn.nextInt(cols);
			robotRow = rn.nextInt(rows);
		}
		
		return new Cell(robotCol, robotRow);
	}
	
	public void draw(Graphics g){
//		System.out.println("Drawing Robot world...");
		drawGrid((Graphics2D)g);
		drawObstacles(g);
		drawRobots(g);
	}
	
	private void drawGrid(Graphics2D g2d){
        int y;
		for (int r=0;r<=rows;r++){
        	y=Math.round(r*rowSpace);
        	g2d.drawLine(0, y, Math.round(colSpace*cols),y);
        }
		int x;
        for (int c=0;c<=cols;c++){
        	x=Math.round(c*colSpace);
        	g2d.drawLine(x, 0, x, Math.round(rowSpace*rows));
        }
	}
	
	private void drawRobots(Graphics g){
//		System.out.println("\tDrawing robots...");
		for (int i=0;i<nRobots;i++){
			robots[i].draw(g);
		}
	}

	
	private void drawObstacles(Graphics g){
		for (int i=0;i<nObstacles;i++){
			obstacles[i].draw(g, this);
		}
	}
	
	
	public void userClick(int x, int y){
		int cellX = (int)(x/colSpace);
		int cellY = (int)(y/rowSpace);
//		System.out.println("User clicked on world cell ("+cellX+","+cellY+")");
		
		Occupant currOccupant = getOccupant(new Cell(cellX, cellY));
		if (currOccupant != null){
			if (currOccupant instanceof Robot){
				user.fixRobot((Robot)currOccupant);
			}
		}
	}
}
