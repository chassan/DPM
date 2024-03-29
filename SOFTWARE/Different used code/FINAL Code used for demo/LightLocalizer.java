import lejos.nxt.*;
import lejos.util.Delay;

public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private LightSensor ls;
	private Navigation nav;
	private static final int ROTATION_SPEED = 20;
	private static final long CORRECTION_PERIOD = 40;
	private static final double d = 19.5;	//the distance from the center of the robot to the light sensor in millimeters
	
	public LightLocalizer(Odometer odo, LightSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		this.nav = odo.getNavigation();
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// that is a point where the light sensor will pass over 4 line portions when the robot turns on a point
		//if robot is placed exactly in the middle of the square, this should take it to a point halfway between its starting position and
		//the true origin (the intersection of the lines) which will allow its light sensor to reach past the sections of lines it needs to pass over
		
		// start rotating and clock all 4 gridlines 
		robot.setRotationSpeed(-ROTATION_SPEED);	//will rotate anti-clockwise
		
		int i = 0;	//to be used as a counter in the while loop
		
		double[] angle = new double[5];	//to store the clocked theta values
		double[] pos = new double[3];	//to store values from odometer 
		double thisV = 0.0;	//this..
		double prevV = 0.0;	//and this are used to measure spikes in light sensor readings
		
		//create longs to store timing info to make sure light sensor only one measure is taken per cycle of time CORRECTION_PERIOD
		long correctionStart, correctionEnd;
		//portions were taken from Lab 2 OdometryCorrection
		Delay.msDelay(500);
		prevV = ls.readValue(); //so thisV-prevV doesn't read a huge spike at the first value
		
		while(i<5)	{	//while loop will scan 5 values, the first 4 are only for x and y calculations while the last, in conjunction with the first and third,
			//is used in theta calculations
			correctionStart = System.currentTimeMillis();	//measure cycle start
			
			thisV = ls.readValue();	//take light sensor value
			odo.getPosition(pos);	//acquire odometer position values
			if((thisV-prevV)>5 && (i==0 || Math.abs(pos[2]-180.0-angle[i-1])>20))	{
				//if it is the first spike (thisV-prevV > 5 and i==0) clock angle
				//if it is not the first spike (thisV-prevV > 5 and i!=0) and current angle is more than 10 degrees from last clocked angle ((pos[2]-180.0)-angle[i-1]>10), clock angle
				angle[i]=pos[2]-180.0;	//light sensor points in opposite direction (e.g. when robot's heading is 0 degrees, light sensor is at 180 degrees)
				i++;	
				Sound.beep();
			}
			prevV = thisV;
			correctionEnd = System.currentTimeMillis();	//measure current time
			//this makes sure the while loop will run through once per cycle of time CORRECTION_PERIOD
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
		robot.setRotationSpeed(0);	//stop robot
		// do trig to compute (0,0) and 0 degrees
		//angle[0], the first angle clocked should be the farthest down/south so angles 0 and 2 determine x, 1 and 3 determine y
		odo.getPosition(pos);	//acquire odometer position values
		//x = -d * cos((angle[0]-angle[2])/2)		pi/180 is used because Math.cos(x) calculates a result as if x was in radians
		//y = -d * cos((angle[1]-angle[3])/2)		pi/180 is used again for the same reason
		//theta correction = 90 - (final heading-180) + ((angle[0]-angle[2]/2)
		//to correct heading we subtract theta correction from current heading
		odo.setPosition(new double [] {-d * Math.cos(((angle[0]-angle[2])/2.0)*Math.PI/180.0), -d * Math.cos(((angle[1]+angle[3])/2.0-angle[3])*Math.PI/180.0), (pos[2])-(90-angle[4]+((angle[0]-angle[2])/2.0))}, new boolean [] {true, true, true});
		
		// when done travel to (0,0) and turn to 0 degrees
		//nav.travelTo(0.0,0.0);	//travel to origin/intersection
		//nav.turnTo(nav.findAngle(0));	//rotate until facing forward
	}

}
