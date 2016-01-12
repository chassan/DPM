import lejos.nxt.*;

public class LightSearchCopy {
	public static final double ROTATION_SPEED = 30.0; //this translates to a motor speed of about 83
	private static final long CYCLE_PERIOD = 40; //makes sure that particular check runs only once per unit time
	public static final double OFFSET = 10.0; //used with turnWindow so the while loop can terminate (while loop checks for 90 degree turn, this makes sure it doesn't come short of it, preventing it from ending)
	private static final double LIGHT_DIFFERENCE = 10.0; //the difference between high and low readings necessary to justifiably assume that the light sensor has spotted a light
	private double turnWindow;	//how much the robot has to turn (used with OFFSET in while loop for above reason)
	private static boolean stop = false; //indicates to the scanning thread to stop once the robot has finished turning
	
	public static double tilePos = 30.0; //position to travel to (tilePos, tilePos), will be incremented by 30 as necessary
	
	private Navigation nav;
	private Odometer odo;
	private LightSensor ls;
	
	public LightSearchCopy(Odometer odo, LightSensor ls) {
		this.odo = odo;
		this.ls = ls;
		this.nav = odo.getNavigation();
	}
	
	public double search(double window)	{
		double[] pos = new double [3];	// to hold position values from odometer
		double highReading = 0.0;;		// holds the value of the currently highest light sensor reading
		double measure = 100.0;		// holds current light sensor readings
		double heading = 45.0;		// holds the value of the heading at which the highest light sensor reading was attained
		double lowReading = 100.0;	//holds lowest value from light sensor, used to check whether robot actually saw light or not, for the case where light points away from robot
		turnWindow = window;	//sets global variable turnWindow to value passed when method is called
		long correctionStart, correctionEnd;	//initializing timer variables
		
		// turn until facing +ve X axis (90 degree heading)
		(new Thread(){
			public void run()	{
				while(!stop)
				{
					nav.turnTo(turnWindow);
					stop=true;
				}
			}
		}).start();
		
		while(!stop)
		{	
			correctionStart = System.currentTimeMillis();	// measure cycle start time
			measure = ls.readValue();	// taking current light sensor value
			odo.getPosition(pos);	// acquire current position values
			
			if(measure > highReading){	// if measure is greater than the highReading...
				highReading = measure;	// set highReading to measure...
				heading = pos[2];		// and set the heading to current heading
			}
			
			if(measure<lowReading)	{
				lowReading = measure;	// set lowReading to measure
			}
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
			
			if(highReading-lowReading>7 && measure-lowReading<=LIGHT_DIFFERENCE){
			//prevent robot from turning 360 degrees unnecessarily, which could mess up the odometer
				break; //if light sensor sees light before robot is done rotating and is sure it no longer sees it, stop searching (terminate loop)
			}
		}
		Motor.A.stop();
		Motor.B.stop();
		//now we should have an approximate direction in which the robot must travel to reach the light source
		nav.turnTo(nav.findAngle(heading));		//turn to face expected direction of travel
		
		return lowReading; //for use in navigation where robot makes a decision based on current light reading, this is a value to compare to
	}
}