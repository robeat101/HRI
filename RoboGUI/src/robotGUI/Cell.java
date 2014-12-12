package robotGUI;

import java.awt.Point;

public class Cell {
	private int row;
	private int col;
	
	public Cell(int c, int r){
		row=r;
		col=c;
	}
	
	public Cell(Point p){
		row=p.y;
		col=p.x;
	}
	
	public Point toPoint(RobotWorld world){
		return new Point(Math.round(col*world.getColSpace()), Math.round(row*world.getRowSpace()));
	}
	
	public int getCol(){
		return col;
	}
	public int getRow(){
		return row;
	}
	
	public void setCol(int c){
		this.col=c;
	}
	public void setRow(int r){
		this.row=r;
	}
	
	public String toString(){
		return "["+Integer.toString(this.col)+", "+Integer.toString(this.row)+"]";
	}
	
	public boolean equals(Cell cell)
	{
		return this.getCol() == cell.getCol() && this.getRow() == cell.getRow();
	}
}
