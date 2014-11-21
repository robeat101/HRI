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

public class Robot extends Occupant{
	// position, orientation
	private int		theta;

	// behavior
	private float	intelligence;

	// goals
	private Point	curGoal;
	private Point [] path;
	
	//status
	private status robotStatus;

	public Robot(Point pos, int theta, Point curGoal, float intelligence) {
		super(pos);
		this.theta = theta;
		this.curGoal = curGoal;
		this.intelligence = intelligence;
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
			this.pos.setRow(this.pos.getRow()
					+ (int) Math.round(StandardValues.DELTA_POS
							* Math.sin(Math.toRadians(theta))));
			this.pos.setCol(this.pos.getCol()
					+ (int) Math.round(StandardValues.DELTA_POS
							* Math.cos(Math.toRadians(theta))));
		} else if (moveStatus == status.BACKWARD)
		{
			this.pos.setRow(this.pos.getRow()
					- (int) Math.round(StandardValues.DELTA_POS
							* Math.sin(Math.toRadians(theta))));
			this.pos.setCol(this.pos.getCol()
					- (int) Math.round(StandardValues.DELTA_POS
							* Math.cos(Math.toRadians(theta))));
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
