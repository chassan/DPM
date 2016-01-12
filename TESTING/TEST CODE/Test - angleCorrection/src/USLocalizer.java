/*
 * DONE @ AUTHORIZED BY:
 * Alex Carruthers
 * Toby Toubiya
 * EDITED BY: Crisan Dan 
 */

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;
	
	private Odometer odo;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		
		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot until it sees no wall
			robot.setRotationSpeed(50);
			while(true){
				if(getFilteredData()==60){
					break;
				}
			}
			// keep rotating until the robot sees a wall, then latch the angle
			while(true){
				if(getFilteredData()<60){
					Sound.beep();
					odo.getPosition(pos);
					angleA = pos[2];
					break;
				}
			}
			//now we get the angle and set A to be that angle
			
			// switch direction and wait until it sees no wall
			robot.setRotationSpeed(-50);
			while(true){
				if(getFilteredData()==60){
					break;
				}
			}
			try { Thread.sleep(1500); } catch (InterruptedException e) {}

			// keep rotating until the robot sees a wall, then latch the angle
			while(true){
				if(getFilteredData()<60){
					Sound.beep();

					robot.stop();
					odo.getPosition(pos);
					angleB = pos[2];
					break;
				}
			}
			double deltaAngle;
			if(angleA < angleB){
				deltaAngle = 45-((angleA+angleB)/2) + 90;
			}
			else
				deltaAngle = 225-((angleA+angleB)/2) + 90;
			odo.getPosition(pos);
			// update the odometer position (example to follow:)
			odo.setPosition(new double [] {0.0, 0.0, pos[2]+deltaAngle}, new boolean [] {false, false, true});
		} 
		else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			// rotate the robot until it sees a wall
			robot.setRotationSpeed(50);
			while(true){
				if(getFilteredData()<60){
					break;
				}
			}
			// keep rotating until the robot sees no wall, then latch the angle
			while(true){
				if(getFilteredData()==60){
					Sound.beep();
					odo.getPosition(pos);
					angleA = pos[2];
					break;
				}
			}
			//now we get the angle and set A to be that angle
			
			// switch direction and wait until it sees a wall
			robot.setRotationSpeed(-50);
			while(true){
				if(getFilteredData()<60){
					break;
				}
			}
			try { Thread.sleep(1500); } catch (InterruptedException e) {}

			// keep rotating until the robot sees no wall, then latch the angle
			while(true){
				if(getFilteredData()==60){
					Sound.beep();
					robot.stop();
					odo.getPosition(pos);
					angleB = pos[2];
					break;
				}
			}
			double deltaAngle;
			if(angleA > angleB){
				deltaAngle = 225-((angleA+angleB)/2);
			}
			else
				deltaAngle = 45-((angleA+angleB)/2);
			odo.getPosition(pos);
			// update the odometer position (example to follow:)
			odo.setPosition(new double [] {0.0, 0.0, pos[2]+deltaAngle}, new boolean [] {false, false, true});
		}
				
		
	}
	
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();
		if (distance > 60)
			distance = 60;
				
		return distance;
	}

}
