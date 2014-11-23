package robotGUI;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Random;

import javax.swing.JPanel;

public class RobotWorld {

	private int nRobots;
	private Robot[] robots;
	
	private int rows;
	private int cols;
	
	Occupant grid[][];
	
	private JPanel renderPanel;
	
	public RobotWorld(int r, int c, JPanel panel){
		rows=r;
		cols=c;
		
		System.out.println("Making RobotWorld " + panel.toString());
		
		grid = new Occupant[cols][rows];
		
		//make a bunch of robots
		nRobots = 10;
		robots = new Robot[nRobots];
		for (int i=0;i<nRobots;i++){
			robots[i] = makeRandomRobot();
		}
		
		renderPanel = panel;
	}
	
	private Robot makeRandomRobot(){
		Random rn = new Random();
		
		Cell randPos = getRadomUnoccupiedCell();
		
		//make a random intelligence
		float intelligence = rn.nextFloat();

		//make a random theta
		int theta = rn.nextInt(360);
		
		Cell goal = getRadomUnoccupiedCell();
		
		return new Robot(randPos.toPoint(),theta, goal.toPoint(), intelligence, renderPanel);
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
		drawRobots(g);
	}
	
	private void drawRobots(Graphics g){
		System.out.println("\tDrawing robots...");
		for (int i=0;i<nRobots;i++){
			robots[i].draw(g);
		}
	}

}
