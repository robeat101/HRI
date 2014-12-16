package robotGUI;

import java.awt.Point;


public abstract class Occupant{
	
	Cell pos;
	
	public Occupant(Cell p, RobotWorld world){
		pos = p;
		world.grid[p.getCol()][p.getRow()]=this;
	}
	
	public Occupant(Point p, RobotWorld world){
		this(new Cell(p), world);
	}
	
	public void setCol(int c){
		this.pos.setCol(c);
	}
	public void setRow(int r){
		this.pos.setRow(r);
	}
	/*
	public void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		System.out.println("Occupant Paint Called!");
	}
	 */
}
