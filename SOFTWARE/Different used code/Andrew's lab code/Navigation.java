import lejos.nxt.*;
import lejos.util.*;

public class Navigation {
	// put your navigation code here 
	private Odometer odo;
	private TwoWheeledRobot robot;
	private static final int FORWARD_SPEED = 5;		//this translates to a motor speed of about 101
	private static final int ROTATION_SPEED = 30;	//this translates to a motor speed of about 83
	private static final double LEFT_RADIUS = 2.75;	
	private static final double RIGHT_RADIUS = 2.75;
	private static final double DISTANCE = 25.0;
	private static final double WIDTH = 15.85;
	private static final long CYCLE_PERIOD = 20; //makes sure that particular check runs only once per unit time
	private static final double LIGHT_DIFFERENCE = 7.0; //the difference between high and low readings necessary to justifiably assume that the light sensor has spotted a light
	private static double[] position = new double[3];	//will hold x, y and theta values from the odometer
	private static LightSensor ls;
	private boolean stop= false;	// used in travel method
	
	public Navigation(Odometer odo, LightSensor ls) {
		this.odo = odo;
		Navigation.ls = ls;
		this.robot = odo.getTwoWheeledRobot();
	}
	
	public Navigation(Odometer odo) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
	}
	
	public void travelTo(double x, double y) {
		// USE THE FUNCTIONS setForwardSpeed and setRotationalSpeed from TwoWheeledRobot!
		//turn to the correct direction
		double angle = findAngle(x,y);
		turnTo(angle, false); //have robot turn to face point
		odo.getPosition(position);
		
		//calculate deltax, deltay and distance
		//reading from odometer is in millimeters, but position stores it in centimeters, so no factor of 10 needs to be applied to correct it
		double deltaX = x - position[0];
		double deltaY = y - position[1];
		double distance = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
		
		//have robot move forward and not rotate
		robot.setForwardSpeed(FORWARD_SPEED);
		robot.setRotationSpeed(0);

		//travel over distance
		Motor.A.rotate(convertDistance(LEFT_RADIUS,distance),true);
		Motor.B.rotate(convertDistance(RIGHT_RADIUS,distance),false);
	}
	
	public void travel()	{
		//travel in a straight line until ultrasonic sensor detects something in front of it
		
		LightSearch search = new LightSearch(odo, ls);
		
		LCDInfo.currentState = (LCDInfo.StateType.searching);	//robot is searching
		double reading = search.search(90.0); //as robot is at origin and there is wall behind it and to its left, it does not need to search all around for a light source, only between 0 and 90 degree headings
		Button.waitForAnyPress(); //pause for 5 seconds so TA can check robot
		
		//have separate thread check ultrasonic sensor
		(new Thread(){
			// I didn't feel it was absolutely necessary for the ultrasaonic sensor readings to be filtered; it's not wall following or performing tasks that
			// require calculations to be made based on the readings, it just has to stop when it sees something and false positives happen so infrequently
			// that I deemed it unnecessary to have code that takes care of it
			private UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);

			public void run()	{
				us.continuous(); //this changes the ultrasonic sensor's ping mode to continuous, it should have been changed in localizer, now we change it back 
				long correctionStart, correctionEnd; //initialize timer variables
				while(true)	{
					correctionStart = System.currentTimeMillis(); //cycle start time
					
					if(LCDInfo.currentState==LCDInfo.StateType.travelling && us.getDistance()<DISTANCE)	{ //if ultrasonic sensor detects an object is close and is travelling...
						stop = true; //set stop to true, this will indicate to the other loop to stop
						break;	//terminate this loop
					}
					
					correctionEnd = System.currentTimeMillis(); //cycle end time
					
					//make sure ultrasonic sensor check occurs once per cycle
					if (correctionEnd - correctionStart < CYCLE_PERIOD) {
						try {
							Thread.sleep(CYCLE_PERIOD
									- (correctionEnd - correctionStart));
						} catch (InterruptedException e) {
							// there is nothing to be done here because it is not
							// expected that the odometry correction will be
							// interrupted by another thread
						}
					}
				}
			}
		}).start();
		
		LCDInfo.currentState = (LCDInfo.StateType.travelling);	//robot is now travelling
		robot.setForwardSpeed(FORWARD_SPEED);	//have robot move forward
		robot.setRotationSpeed(0);	//and not turn at all
		
		while(!stop)	{
			//will travel towards light unless it loses 'sight' of it (difference beteween current reading and lowReading from is less than or equal to 10)
			
			if(ls.readValue()-reading<=LIGHT_DIFFERENCE)	{
				// if signal is lost, search for light source
				search = new LightSearch(odo,ls);
				search.search(360.0); //have robot turn clockwise until it finds a light source
				
				//have robot travel straight towards it again
				robot.setForwardSpeed(FORWARD_SPEED);
				robot.setRotationSpeed(0);
			}
		}
		
		//stop robot
		Motor.A.stop();
		Motor.B.stop();
	}
	
	public void turnTo(double angle, boolean goBack) {	//goBack, is used when we want the robot to turn while doing something else, like when the light sensor takes values while the robot moves
		//Have robot turn and not move forward
		robot.setRotationSpeed(ROTATION_SPEED);
		robot.setForwardSpeed(0);
		
		//turn through angle
		Motor.A.rotate(convertAngle(LEFT_RADIUS,WIDTH,angle),true);
		Motor.B.rotate(-convertAngle(RIGHT_RADIUS,WIDTH,angle),goBack);
	}
	
	//find angle based on desired position as well as current position
	public double findAngle(double x, double y){
		odo.getPosition(position);	//get current position values
		double deltaX = (x-position[0]);
		double deltaY = (y-position[1]);
			
		double angle = 0.0;	//to store value of angle that the robot must be facing to travel straight to position x,y
		if(y-position[1]==0)	{	//when delta y = 0, an error occurs in calculating the atan
			angle = deltaX / Math.abs(deltaX) * 90.0; //delta x / |delta x| gives 1 when delta x is positive and -1 when delta x is negative 
		}	else	if(x-position[0]==0 && y-position[1]<0)	{ //will return 0 regardless of delta y, only needs correcting when delta y is negative
			angle = 180.0;
		}	else	{
			angle = 180.0 / Math.PI * Math.atan(deltaX/deltaY); //arctangent of (delta x/delta y)
		}
			
		//We imagine clockwise rotation to be positive, and anti-clockwise to be negative
		//When delta x is positive and delta y is negative, we have a larger positive rotation
		//but the arctan indicates a negative rotation, so wee add 180 to angle to correct it
		if(x-position[0]>0 && y-position[1]<0)	{
			angle = angle + 180;
		}
		//if delta x and delta y are both negative, delta x/ delta y will be positive and a positive angle
		//will be given by the atan. 180 is subtracted to fix this
		if(x-position[0]<0 && y-position[1]<0)	{
			angle = angle - 180;
		}
		//the other combinations of positive and negative delta x and delta y do not need corrections
		return findAngle(angle);	//now that we have an angle, we can send it to findAngle(double theta) to calculate the rest
	}
	
	//find desired angle based on current angle and desired angle
	//the method can be called separately, like the end where it must turn to have a heading of 0 degrees, so
	//the creation of a separate method was warranted
	public double findAngle(double theta){
		//Find the proper direction
		odo.getPosition(position);	//get current position values

		double angle;	//create angle to store value of angle through which robot must turn
		
		angle = theta - position[2];			//taking into account current angle in total turning angle
		
		//Following while loops make sure angle is minimal (i.e. making sure robot does
		//not turn -270 degrees when it can turn +90 degrees or +225 degrees when it can turn -135 degrees, for example)
		while (angle>180)	{
			angle = angle - 360;
		}
		while (angle<-180)	{
			angle = angle + 360;
		}
		
		return angle;	
	}
	
	private static int convertDistance(double radius, double distance) {
		//taken from the SquareDriver in Lab2
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		// also taken from the SquareDriver in Lab2
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
