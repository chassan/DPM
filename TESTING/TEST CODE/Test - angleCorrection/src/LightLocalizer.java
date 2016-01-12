/*
 * EDITED BY:
 * Alex Carruthers
 * Toby Toubiya
 */

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Sound;

public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightSensor ls;
	
	public LightLocalizer(Odometer odo, LightSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		robot.setRotationSpeed(50);
		int counter = 0;
		int highLight = ls.getLightValue();
		double distanceFromCenterToLightSensor = 12;
		
		//create an array that stores the angles that it detects the lines
		double[] lineOrientations = new double[4];
		double[] pos = new double[3];
		//count the 4 lines
		while (counter < 4){
			if(ls.getLightValue() < highLight - 10){
				Sound.beep();
				odo.getPosition(pos);
				lineOrientations[counter] = pos[2];
				counter++;
				try { Thread.sleep(100); } catch (InterruptedException e) {}
			}
		}
		robot.stop();
		try { Thread.sleep(300); } catch (InterruptedException e) {}
		//because of the direction it is turning, it hits the X-axis, then Y, then X, then Y
		//convert to radians so that the Math.cos function works
		double thetaY = (lineOrientations[1] - lineOrientations[3]) / 180 * Math.PI;
		double thetaX = (lineOrientations[0] - lineOrientations[2]) / 180 * Math.PI;
		double newX = -distanceFromCenterToLightSensor * Math.cos(thetaY/2);
		double newY = -distanceFromCenterToLightSensor * Math.cos(thetaX/2);
		
		//update the angle
		odo.getPosition(pos);
		double newTheta = 90 + ((lineOrientations[1] - lineOrientations[3]) / 2) + lineOrientations[3] - pos[2];
		
		odo.setPosition(new double [] {newX, newY, newTheta}, new boolean [] {true, true, true});
	}

}
