package robotGUI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

public class RobotWorld {

	//robots
	private int nRobots;
	private Robot[] robots;
	
	//occupancy grid
	private static int defaultRows = 20;
	private static int defaultCols = 20;
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
		System.out.println("Making RobotWorld...");
		
		//make robots and add them to the grid
		grid = new Occupant[cols][rows];
		for (r=0;r<rows;r++){
			for (c=0;c<cols;c++){
				grid[c][r]=null;
			}
		}
		makeRobots();
	}
	
	private void makeRobots(){
		//make a bunch of robots
		nRobots = 10;
		robots = new Robot[nRobots];
		for (int i=0;i<nRobots;i++){
			robots[i] = makeRandomRobot();
		}
	}
	
	public void update(){
		System.out.println("\tUPDATE ROBOT WORLD...");
		//update robots
		updateRobots();
	}
	
	private void updateRobots(){
		System.out.println("\t\tUpdate robots...");
		for (int i=0;i<nRobots;i++){
			robots[i].updateRobot(this);
		}
	}
	
	private Robot makeRandomRobot(){
		Random rn = new Random();
		
		Cell randPos = getRadomUnoccupiedCell();	//make a random unoccupied position
		float intelligence = rn.nextFloat();		//make a random intelligence
		int theta = rn.nextInt(360);				//make a random rotation theta
		Cell goal = getRadomUnoccupiedCell();		//make a random goal
		
		//create the robot
		Robot robot =  new Robot(randPos,theta, goal, intelligence, this);
		
		// add the robot as the occupant of its own grid cell
		grid[randPos.getCol()][randPos.getRow()] = robot;
		
		return robot;
	}
	
	Occupant getOccupant(Cell c){
		return grid[c.getCol()][c.getRow()];
	}
	
	boolean occupied(Cell c){
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
		System.out.println("Drawing Robot world...");
		drawGrid((Graphics2D)g);
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
		System.out.println("\tDrawing robots...");
		for (int i=0;i<nRobots;i++){
			robots[i].draw(g);
		}
	}

}
