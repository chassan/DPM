 import lejos.nxt.*;
import lejos.util.*;

public class Localizer {
	public static double ROTATION_SPEED = 30;
	public static int DISTANCE = 50;	//for use in filtering distance measurements of Ultrasonic Sensor
	
	private Navigation nav;
	private Odometer odo;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	public Localizer(Odometer odo, UltrasonicSensor us) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.nav = odo.getNavigation();
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double [] pos = new double [3];	//to hold position values from odometer
		double angleA, angleB;	//to store values of angles used to calculate approximate heading
		double measure;	//for storing temporary values of the filtered ultrasonic sensor readings
		// rotate the robot until it sees no wall
		robot.setRotationSpeed(ROTATION_SPEED);	//clockwise rotation
		measure=getFilteredData();//Get a value to start with
		while(measure<=DISTANCE)	{
			measure=getFilteredData();	//will acquire values until no wall is seen (i.e. when measure is 255, exit loop)
		}
		// keep rotating until the robot sees a wall, then latch the angle
		while(measure>DISTANCE)	{
			measure=getFilteredData(); //will acquire values until a wall is seen (i.e. when measure is less than or equal to DISTANCE, exit loop)
		}
		//getting current odometer values
		odo.getPosition(pos);	//acquire current position values
		angleA = pos[2];	//first angle is A
		// switch direction and wait until it sees no wall
		robot.setRotationSpeed(-ROTATION_SPEED);	//anti-clockwise rotation
		while(measure<=DISTANCE){
			measure=getFilteredData(); //will acquire values until no wall is seen
		}
		// keep rotating until the robot sees a wall, then latch the angle
		while(measure>DISTANCE)	{
			measure=getFilteredData(); //will acquire values until a wall is seen
		}
		//getting current odometer values
		odo.getPosition(pos);	//acquire current position values
		angleB = pos[2];	//second angle is B
		//angleB needs to be negative for our calculation so...
		angleB = angleB - 360;
		// angleA is clockwise from angleB, so assume the average of the
		// angles to the right of angleB is 45 degrees past 'north'
		
		// update the odometer position (example to follow:)
		// odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		
		//In this case we use the formula (45 - (angleA+angleB)/2) to calculate theta, we do not update x or y, because we have no way of gauging them at the moment
		//the theta calculated is the starting angle, and needs to be added to the current theta (i.e. pos[2])
			//getting current odometer values
		odo.getPosition(pos);	//acquire current position values
		double newTheta = pos[2] + (45-(angleA+angleB)/2.0);	//calculate new theta heading
		odo.setPosition(new double [] {0.0, 0.0, newTheta}, new boolean [] {false, false, true});
		nav.travelTo(15, 15);	//assuming robot starts at (-15, -15), so this should take the robot to origin
		nav.turnTo(nav.findAngle(0), false);	//turn to face "north" (i.e. +ve Y axis)
		odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});	//robot should now be at origin facing "north". As a result, odometer is updated to reflect this
		Button.waitForAnyPress();	//wait for TA to check robot
	}
	
	private int getFilteredData() {	//if a result of 255 is measured by the ultrasonic sensor, a second value is acquired and used, just in case
		int distance = 0;	//to hold measure from ultrasonic sensor
		// do a ping
		us.ping();
				
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
					
		// there will be a delay here
		distance = us.getDistance();
			
		if(distance==255)	{
			//if value is 255, try again to make sure it is not a mistaken value of 255
			// do a ping
			us.ping();
					
			// wait for the ping to complete
			try { Thread.sleep(50); } catch (InterruptedException e) {}
						
			// there will be a delay here
			distance = us.getDistance();
		}
		return distance;
	}

}
