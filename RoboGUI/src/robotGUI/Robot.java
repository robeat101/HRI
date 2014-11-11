/**
 * This is a representation of a robot.
 * It records the current position and orientation of the robot (state), as well as some behavior parameters.
 */

package robotGUI;

import java.awt.Point;

public class Robot {
	//position, orientation
	private Point pos;
	private float theta;
	
	//behavior
	private float intelligence;
	
	//goals
	private Point curGoal;
	
}
