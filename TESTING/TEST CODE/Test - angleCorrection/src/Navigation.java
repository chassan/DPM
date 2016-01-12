/*
 * EDITED BY:
 * Alex Carruthers
 * Toby Toubiya
 */

import lejos.nxt.*;
public class Navigation {
	
	private Odometer odo;
	NXTRegulatedMotor leftMotor = Motor.A;
	NXTRegulatedMotor rightMotor = Motor.B;
	
	//Constants
	private int FORWARD_SPEED = 150;
	private int ROTATE_SPEED = 100;
	private double leftRadius = 2.718;
	private double rightRadius = 2.7;
	private double width = 16;
	
	// constructor
	public Navigation(Odometer odo) {
		this.odo = odo;
	}
	
	//convert distance and angle, taken straight from lab 2
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	private double convertAngleDegreesToRadians(double angle){
		return angle * Math.PI / 180;
	}
	public void turnTo(double angle){
		double[] pos = new double[3];
		odo.getPosition(pos);
		angle = convertAngleDegreesToRadians(angle);
		pos[2] = convertAngleDegreesToRadians(pos[2]);
		//calculate the angle the robot has to turn to go in the new heading
		double newTheta = pos[2] - angle;
		if (newTheta > 180){
			newTheta -= 360;
		}
		//set the speed of the motors
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		//rotate the robot to the correct heading
		leftMotor.rotate(-convertAngle(leftRadius, width, newTheta*180/Math.PI), true);
		rightMotor.rotate(convertAngle(rightRadius, width, newTheta*180/Math.PI), false);
	}
	
	public void travelTo(double x, double y){
		try { Thread.sleep(150); } catch (InterruptedException e) {}
		double[] pos = new double[3];
		odo.getPosition(pos);
		//calculate the x and y distances the robot has to go
		double deltaX = (x-pos[0]);
		double deltaY = (y-pos[1]);
				
		//calculate the angle that the robot has to turn to
		double theta0= Math.atan(deltaX/deltaY);
		
		//since atan only gives numbers in the range -pi/2 to pi/2,
		//edit the value if deltaX is 0
		if (deltaY < 0)
			theta0 = Math.PI-theta0;
		
		//turn the robot
		turnTo(theta0 * 180 / Math.PI);
		
		//calculate the distance the robot has to go
		double realDist = Math.sqrt(Math.pow(deltaX,2)+Math.pow(deltaY,2));
		
		//set the speed of the motors
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		//travel to the correct position
		leftMotor.rotate(convertDistance(leftRadius, realDist), true);
		rightMotor.rotate(convertDistance(rightRadius, realDist), false);
	}
}
