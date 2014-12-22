/**
 * 
 */
package robotGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Timer;

import robotGenericValues.StandardValues;


@SuppressWarnings("serial")
public class SimTimer extends Timer {

	private static SimTimer theSimTimer;
	public static void makeTheTimer(final overheadView overheadViewPanel){
		theSimTimer = new SimTimer(StandardValues.DELTA_TIME_INTERPOLATE, overheadViewPanel);
	}
	
	private long time = 0;
	@SuppressWarnings("unused")
	private final static int interpolationsPerTimeStep = StandardValues.DELTA_TIME/StandardValues.DELTA_TIME_INTERPOLATE;
	
	ActionListener augmentedActionListener;
	public SimTimer(int delay, final overheadView overheadViewPanel) {
		super(delay,
			new AbstractAction() {
		        @Override
		        public void actionPerformed(ActionEvent ae) {
		        	theSimTimer.time++;
		        	int interpolationsPerTimeStep = StandardValues.DELTA_TIME/StandardValues.DELTA_TIME_INTERPOLATE;
		        	int remainder = (int)(theSimTimer.time%(long)interpolationsPerTimeStep);
		        	if (remainder!=0){
		        		overheadViewPanel.interpolate((float)remainder/interpolationsPerTimeStep);
		        	}else{
	//	        	System.out.println("UPDATE SIMULATION:");
		        		overheadViewPanel.update();
		        	}
		        }
			});
		this.start();
	}
	public static long getCurTimeMillis(){
		return theSimTimer.time;
	}
	
	public static long getCurTime(){
		return theSimTimer.time/StandardValues.DELTA_TIME_INTERPOLATE;
	}
}
