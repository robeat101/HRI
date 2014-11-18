/**
 * This is a representation of a robot.
 * It records the current position and orientation of the robot (state), as well as some behavior parameters.
 */

package robotGUI;

import java.awt.Point;

import robotExceptions.InvalidHeadingException;
import robotGenericValues.StandardValues;
import robotGenericValues.direction;


public class Robot {
	// position, orientation
	private Point	pos;
	private float	theta;

	// behavior
	private float	intelligence;

	// goals
	private Point	curGoal;

	public void turnRobot(direction turnDirection)
	{
		if(turnDirection == direction.LEFT){
			this.theta = this.theta + StandardValues.DELTA_THETA;
		}
		else if (turnDirection == direction.RIGHT) {
			this.theta = this.theta - StandardValues.DELTA_THETA;
		}
	}
	
	public void moveRobot(direction moveDirection) throws InvalidHeadingException
	{
		if(theta % 0.25 != 0)
		{
			throw new InvalidHeadingException("Cannot move robot when theta is " + theta + "."); 
		}
		else if(moveDirection == direction.FORWARD){
			//TODO: Add movement
		}
		
	}
	

	
}
