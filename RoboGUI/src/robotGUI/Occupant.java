package robotGUI;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;


public abstract class Occupant{
	
	Cell pos;
	
	public Occupant(Cell p){
		pos = p;
	}
	
	public Occupant(Point p){
		pos = new Cell(p);
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
