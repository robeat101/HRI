package robotGUI;

import java.awt.Color;

import javax.swing.JProgressBar;

import robotGenericValues.StandardValues;

public class User {
	float fixingAbility = StandardValues.MAX_USER_FIXING_ABILITY;
	float fixingIncreasePerUpdate = 0.1f;
	JProgressBar indicator;
	
	public void update() {
		if (fixingAbility<StandardValues.MAX_USER_FIXING_ABILITY-fixingIncreasePerUpdate){
			fixingAbility+=fixingIncreasePerUpdate;
		}else{
			fixingAbility=StandardValues.MAX_USER_FIXING_ABILITY;
		}
		indicator.setValue((int)(fixingAbility*indicator.getMaximum()/StandardValues.MAX_USER_FIXING_ABILITY));
		indicator.setForeground(fixingAbility>=StandardValues.COST_TO_FIX_ROBOT?Color.GREEN:Color.RED);
		indicator.repaint();
	}

	public void fixRobot(Robot curOccupant) {
		if (fixingAbility>StandardValues.COST_TO_FIX_ROBOT){
			curOccupant.fix();
			fixingAbility-=StandardValues.COST_TO_FIX_ROBOT;
		}
	}


	public void setindicator(JProgressBar ind) {
		this.indicator=ind;
	}

}
