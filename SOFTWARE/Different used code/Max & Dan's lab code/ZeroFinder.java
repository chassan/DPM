import lejos.nxt.*;
import lejos.util.*;

public class ZeroFinder {
	private Odometer odo;
	private Gr8Bot robot;
	private LightSensor ls;
	private static final int ROTATION_SPEED = 20;
	private static final long CORRECTION_PERIOD = 40;
	private static final double d = 19.2;	//the variable d from the localization tutorial, the distance from the center of the robot to the light sensor in centimeters, like the odometer
	
	public ZeroFinder(Odometer odo, LightSensor ls) {
		this.odo = odo;
		this.robot = odo.getGr8Bot();
		this.ls = ls;
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// that is a point where the light sensor will pass over 4 line portions when the robot turns on a point
		//if robot is placed exactly in the middle of the square, this should take it to a point halfway between its starting position and
		//the true origin (the intersection of the lines) which will allow its light sensor to reach past the sections of lines it needs to pass over
		robot.setRotationSpeed(ROTATION_SPEED);
		Navigation.turnTo(odo, 45);
		Sound.beep();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// start rotating and clock all 4 gridlines 
		robot.setRotationSpeed(-ROTATION_SPEED);	//will rotate anti-clockwise
		Sound.buzz();
		int i = 0;	//to be used as a counter in the while loop
		
		double[] angle = new double[5];	//to store the four clocked theta values
		double[] pos = new double[3];	//to store values from odometer 
		double thisV = 0.0;	//this..
		double prevV = 0.0;	//and this are used to measure spikes in light sensor readings
		
		//create longs to store timing info to make sure light sensor only one measure is taken per cycle of time CORRECTION_PERIOD
		long correctionStart, correctionEnd;
		//portions were taken from Lab 2 OdometryCorrection
		prevV = ls.readValue(); //so thisV-prevV doesn't read a huge spike at the first value
		while(i<5)	{	//while loop will scan 5 values, the first 4 are only for x and y calculations while the last, in conjunction with the first and third,
			//is used in theta calculations
			correctionStart = System.currentTimeMillis();	//measure cycle start
			
			thisV = ls.readValue();	//take light sensor value
			odo.getPosition(pos);	//acquire odometer position values
			if((thisV-prevV)>5 && (i==0 || Math.abs(pos[2]-180.0-angle[i-1])>10))	{
				//if it is the first spike (thisV-prevV > 5 and i==0) clock angle
				//if it is not the first spike (thisV-prevV > 5 and i!=0) and current angle is more than 10 degrees from last clocked angle ((pos[2]-180.0)-angle[i-1]>10), clock angle
				angle[i]=pos[2]-180.0;	//light sensor points in opposite direction (e.g. when robot's heading is 0 degrees, light sensor is at 180 degrees)
				i++;	
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
		odo.getPosition(pos);
		// when done travel to (0,0) and turn to 0 degrees
		Navigation.travelTo(odo, 0.0,0.0);	//travel to origin/intersection
		Navigation.turnTo(odo, Odometer.minimumAngleFromTo(pos[2], 0));	//rotate until facing forward
	}

}


/*import lejos.nxt.LightSensor;

public class ZeroFinder {
  private Odometer odo;
  private Gr8Bot robot;
  private LightSensor ls;
  public static double FORWARD_SPEED = 7;
  public static double ROTATION_SPEED = 15;
  
  public ZeroFinder(Odometer odo, LightSensor ls) {
    this.odo = odo;
    this.robot = odo.getGr8Bot();
    this.ls = ls;
    
    // turn on the light
    ls.setFloodlight(true);
  }
  
  // take the robot to 0,0,0 and update the odometer
  public void doLocalization() {
    
    // go forward untill the LS detects a black line
    // back up a bit
    // alligen the robot perpendicular to the black line
    // update the odometer
    robot.setForwardSpeed(FORWARD_SPEED);
    while(!getLSReading());
    Navigation.goForward(odo, -1);
    doLocalizationHellper();
    odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {false, false, true});
    
    // go back and turn 90 degrees
    Navigation.goForward(odo, -18);
    Navigation.turnTo(odo,90);
    
    // go forward untill the LS detects a black line
    // back up a bit
    // alligen the robot perpendicular to the black line
    // update the odometer
    robot.setForwardSpeed(FORWARD_SPEED);
    while(!getLSReading());
    Navigation.goForward(odo, -1); 
    doLocalizationHellper();
    odo.setPosition(new double [] {0.0, 0.0, 90.0}, new boolean [] {false, false, true});
    
    // go the to intersection point (0,0)
    Navigation.goForward(odo, -10);
    Navigation.turnTo(odo,0);
    Navigation.goForward(odo, 8);
    odo.setPosition(new double [] {0.0, 0.0, 0}, new boolean [] {true, true, true});
    
    
  }
  
  // this method alligen the robot with a black line
  public void doLocalizationHellper(){
    double [] pos = new double [3];
    double angleA;
    double angleB;
    
    // rotate untill LS detects a black line
    // record the angle
    robot.setRotationSpeed(ROTATION_SPEED);
    while (!getLSReading());
    odo.getPosition(pos);
    angleA = pos[2];
    
    // rotate to the other direction
    robot.setRotationSpeed(-ROTATION_SPEED);
    try { Thread.sleep(250); } catch (InterruptedException e) {}
    
    // rotate untill LS detects a black line
    // record the angle
    while (!getLSReading());
    odo.getPosition(pos);
    angleB = pos[2];
    
    // go perpendicular to the black line
    if(angleA < angleB)
      Navigation.turnTo(odo, ((angleA + angleB) / 2) + 180);
    else
      Navigation.turnTo(odo, (angleA + angleB) / 2 );
  }
  
  // black < 430
  // wood  > 460
  // this method will get the LS reading and return true if it sees black
  private boolean getLSReading() {
    int r = ls.getNormalizedLightValue();
    // if the LS sees wood
    if (r >= 450)
      return false;
    // if it sees black
    else// if( r <= 430)
      return true;
    // not sure. Get the reading again
   // else
    //  return getLSReading();
  }
  
}
*/