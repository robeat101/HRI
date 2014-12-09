package robotGUI;

import java.awt.Color;
import java.awt.Graphics;

public class Obstacle extends Occupant {

	
	private static final Color OBSTACLE_COLOR = Color.BLACK;
	public Obstacle(Cell p, RobotWorld world) {
		super(p, world);
	}

	public void draw(Graphics g, RobotWorld world){
		float rowSpace = world.getRowSpace();
		float colSpace = world.getColSpace();
		g.setColor(OBSTACLE_COLOR);
		g.fillRect(Math.round(pos.getCol()*colSpace), Math.round(pos.getRow()*rowSpace), Math.round(colSpace), Math.round(rowSpace));
	}
	
}
