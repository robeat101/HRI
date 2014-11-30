/**
 * This is a representation of a robot.
 * It records the current position and orientation of the robot (state), as well as some behavior parameters.
 */

package robotGUI;

import java.awt.Point;

import robotExceptions.InvalidHeadingException;
import robotGenericValues.StandardValues;
import robotGenericValues.status;

import java.lang.Math;

public class Robot {
	// position, orientation
	private Point	pos;
	private int		theta;

	// behavior
	private float	intelligence;

	// goals
	private Point	curGoal;
	private Point [] path;
	
	//status
	private status robotStatus;

	public Robot(Point pos, float intelligence, Point curGoal, Point[] path, status robotStatus) {
		this.pos = pos;
		this.intelligence = intelligence;
		this.curGoal = curGoal;
		this.path = path;
		this.robotStatus = robotStatus;
	}

	public void turnRobot(status turnStatus)
	{
		if (turnStatus == status.LEFT)
		{
			this.theta = this.theta + StandardValues.DELTA_THETA;
		} else if (turnStatus == status.RIGHT)
		{
			this.theta = this.theta - StandardValues.DELTA_THETA;
		}
	}

	public void moveRobot(status moveStatus)
			throws InvalidHeadingException
	{
		if (theta % StandardValues.VALID_HEADING_STEP != 0)
		{
			throw new InvalidHeadingException(
					"Cannot move robot when theta is " + theta + ".");
		} else if (moveStatus == status.FORWARD)
		{
			this.pos.y = this.pos.y
					+ (int) Math.round(StandardValues.DELTA_POS
							* Math.sin(Math.toRadians(theta)));
			this.pos.x = this.pos.x
					+ (int) Math.round(StandardValues.DELTA_POS
							* Math.cos(Math.toRadians(theta)));
		} else if (moveStatus == status.BACKWARD)
		{
			this.pos.y = this.pos.y
					- (int) Math.round(StandardValues.DELTA_POS
							* Math.sin(Math.toRadians(theta)));
			this.pos.x = this.pos.x
					- (int) Math.round(StandardValues.DELTA_POS
							* Math.cos(Math.toRadians(theta)));
		}
	}
	
	
	public void updateRobot(){

		if(robotStatus == status.FORWARD || robotStatus == status.BACKWARD) {
			try {
				moveRobot(robotStatus);
			} catch (InvalidHeadingException e) {
				e.printStackTrace();
			}
		}
		else if (robotStatus == status.LEFT || robotStatus == status.RIGHT)
		{
			turnRobot(robotStatus);
		}
		else if(robotStatus == status.WAITING)
		{
			//TODO: Call check next step function
		}


	}
}
