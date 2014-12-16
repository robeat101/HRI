package robotGUI;

import java.awt.Color;

import javax.swing.JProgressBar;

import robotGenericValues.StandardValues;

public class User {
	float fixingAbility = StandardValues.MAX_USER_FIXING_ABILITY;
	float fixingIncreasePerUpdate = 0.1f;
	JProgressBar fixIndicator;
	ScoreLabel scoreIndicator;
	
	public void update() {
		if (fixingAbility<StandardValues.MAX_USER_FIXING_ABILITY-fixingIncreasePerUpdate){
			fixingAbility+=fixingIncreasePerUpdate;
		}else{
			fixingAbility=StandardValues.MAX_USER_FIXING_ABILITY;
		}
		fixIndicator.setValue((int)(fixingAbility*fixIndicator.getMaximum()/StandardValues.MAX_USER_FIXING_ABILITY));
		fixIndicator.setForeground(fixingAbility>=StandardValues.COST_TO_FIX_ROBOT?Color.GREEN:Color.RED);
		fixIndicator.repaint();
	}

	public void fixRobot(Robot curOccupant, RobotWorld world) {
		if (fixingAbility>StandardValues.COST_TO_FIX_ROBOT){
			curOccupant.fix(world);
			fixingAbility-=StandardValues.COST_TO_FIX_ROBOT;
		}
	}


	public void setIndicators(JProgressBar fixInd, ScoreLabel scoreInd) {
		this.fixIndicator=fixInd;
		this.scoreIndicator = scoreInd;
		this.scoreIndicator.setScore(0);
	}

}
