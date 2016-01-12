/*
 * SquareDriver.java
 */
import lejos.nxt.*;

public class PathDriver extends Thread {
	private static final int FORWARD_SPEED = 125;
	private static final int ROTATE_SPEED = 75;
	
	//The different points that we want to reach are presented in an array with their horizontal and vertical components.
	//static  double[] X = {0, 60, 30, 30, 64};
	//static  double[] Y = {0, 30, 30, 60, 5};


	public static void drive(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, double leftRadius, double rightRadius, double width) {
		// reset the motors
		for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(3000);
		}

		

		// wait 5 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// there is nothing to be done here because it is not expected that
			// the odometer will be interrupted by another thread
		}
	}
	
	//Initialise thetaR with 90 since we start perpendicularly with the x axis.
	//ThetaR is the angle at current position.
	//ThetaD is the angle of the path we want to travel. 
	static double thetaR = 90;
	public static void travelTo (double xCoord, double yCoord){
		
		//Calculate the distance to follow between 2 consecutive points (given in the argument)
		double distance = Math.sqrt(Math.pow(xCoord, 2) + Math.pow(yCoord, 2));
		double thetaD;
		
		//Before treating any angles, let's handle the situation where y/x is impossible to calculate since x = 0.
		if(xCoord == 0 && yCoord > 0){
			thetaD = 90;
		}
		else{
			thetaD = Math.toDegrees(Math.atan2(yCoord, xCoord));
		}

		Lab3p1.leftMotor.setSpeed(ROTATE_SPEED);
		Lab3p1.rightMotor.setSpeed(ROTATE_SPEED);

		//The method turnTo converts the rotation angle into motor movement. 
		turnTo(thetaD - thetaR);
		//After each iteration, the angle of the path we wanted to travel becomes the current angle. 
		thetaR = thetaD;
		
		Lab3p1.leftMotor.setSpeed(FORWARD_SPEED);
		Lab3p1.rightMotor.setSpeed(FORWARD_SPEED);
		
		//After the robot has turned with the rigt angle, the only thing remaining is to move forward by the desired distance. 
		Lab3p1.leftMotor.rotate(convertDistance(Lab3p1.leftRadius, distance), true);
		Lab3p1.rightMotor.rotate(convertDistance(Lab3p1.rightRadius, distance), false);

	}

	private static void turnTo(double theta){
		Lab3p1.leftMotor.rotate(convertAngle(Lab3p1.leftRadius, Lab3p1.width, (theta)), true);
		Lab3p1.rightMotor.rotate(-convertAngle(Lab3p1.rightRadius, Lab3p1.width, (theta)), false);
	}
	

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	//This method returns true if another thread has called travelTo() or turnTo() (hence all the PathDriver method), else returns false. 
	//We are not using it since a little confusing. 
	public static boolean isNavigating(){
		if ( PathDriver.interrupted() == false) return true;
		return false;
	}
}