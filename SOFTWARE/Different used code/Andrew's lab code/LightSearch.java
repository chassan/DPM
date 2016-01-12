import lejos.nxt.*;
import lejos.util.*;

public class LightSearch {
	public static final double ROTATION_SPEED = 30.0; //this translates to a motor speed of about 83
	private static final long CYCLE_PERIOD = 40; //makes sure that particular check runs only once per unit time
	public static final double OFFSET = 10.0; //used with turnWindow so the while loop can terminate (while loop checks for 90 degree turn, this makes sure it doesn't come short of it, preventing it from ending)
	private static final double LIGHT_DIFFERENCE = 7.0; //the difference between high and low readings necessary to justifiably assume that the light sensor has spotted a light
	private double turnWindow;	//how much the robot has to turn (used with OFFSET in while loop for above reason)
	
	public static double tilePos = 30.0; //position to travel to (tilePos, tilePos), will be incremented by 30 as necessary
	
	private Navigation nav;
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightSensor ls;
	public LightSearch(Odometer odo, LightSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		this.nav = odo.getNavigation();
	}
	
	public double search(double window)	{
		double[] pos = new double [3];	// to hold position values from odometer
		double highReading;		// holds the value of the currently highest light sensor reading
		double measure;		// holds current light sensor readings
		double heading = 45.0;		// holds the value of the heading at which the highest light sensor reading was attained
		double lowReading;	//holds lowest value from light sensor, used to check whether robot actually saw light or not, for the case where light points away from robot
		turnWindow = window;	//sets global variable turnWindow to value passed when method is called
		double change;	//holds angle through which robot has turned presently
		double lastHeading;	//holds the heading the robot was last time
		double difference;	//holds difference between current and last headings
		
		long correctionStart, correctionEnd;	//initializing timer variables
		
		// turn until facing +ve X axis (90 degree heading)
		while(true)
		{	//will repeat until light is seen (substantial difference between low and high readings)
			// rotate the robot
			robot.setRotationSpeed(ROTATION_SPEED);
			robot.setForwardSpeed(0);
			
			nav.turnTo(turnWindow+OFFSET,true);	//have robot turn past a certain angle
			
			//the following resets are useful for when robot doesn't see light initially and needs to move and search again
			change = 0.0;	//set/reset change
			highReading = 0.0; //set/reset highReading
			lowReading = 100.0; //set/reset lowReading
			odo.getPosition(pos); //acquire current position values
			lastHeading = pos[2]; //set/reset lastHeading to current heading
			difference = 0.0; //set/reset difference
			
			while(change<=turnWindow){
				correctionStart = System.currentTimeMillis();	//measure cycle start time
				measure = ls.readValue();	//taking current light sensor value
				odo.getPosition(pos);	//acquire current position values
				
				if(measure > highReading){	//if measure is greater than the highReading...
					highReading = measure;	//set highReading to measure...
					heading = pos[2];		//and set the heading to current heading
				}
				
				if(measure<lowReading)	{
					lowReading = measure;	//set lowReading to measure
				}
				
				difference = pos[2] - lastHeading;	//store the value of the difference between current heading and previous heading
				if(difference<0)	{
					// there may be a case where pos[2] is 3 degrees and lastHeading is 358 degrees, which would give us a change of -355 degrees, which is not correct obviously
					// so what we have it do is add 360 to the difference, which in this example would give us the corrected difference of 5 degrees
					difference += 360;
				}
				change += difference;	//add the difference
				lastHeading = pos[2];	//set last heading to current heading
				
				correctionEnd = System.currentTimeMillis();	//measure cycle end time
				
				//make sure light sensor checks once per cycle
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
				
				if(highReading-lowReading>7 && measure-lowReading<=7){
					//prevent robot from turning 360 degrees unnecessarily, which could mess up the odometer
					break; //if light sensor sees light before robot is done rotating and is sure it no longer sees it, stop searching (terminate loop)
				}
			}
			
			
			//if the robot is not able to find the light at the start (while searching), the robot will travel diagonally, stop at an intersection and search for a light until it finds one
			if(LCDInfo.currentState == LCDInfo.StateType.searching && highReading-lowReading<=LIGHT_DIFFERENCE)	{
				//light is not seen
				nav.travelTo(tilePos,tilePos);	//travel to next tile intersection
				tilePos+=30.0;	//set tilePos to next tile intersection
				turnWindow=360.0;//if the robot doesn't see a light initially, it will spin 360 degrees next time 
			}	else if (highReading-lowReading>LIGHT_DIFFERENCE)	{
				break; //terminate loop if light is seen
			}
		}
		

		//now we should have an approximate direction in which the robot must travel to reach the light source
		nav.turnTo(nav.findAngle(heading),false);		//turn to face expected direction of travel
		
		return lowReading; //for use in navigation where robot makes a decision based on current light reading, this is a value to compare to
	}
}