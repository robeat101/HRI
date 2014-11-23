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
	
	public Point toPoint(){
		return new Point(col, row);
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
}
