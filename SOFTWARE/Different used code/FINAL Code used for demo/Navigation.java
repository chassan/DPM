import lejos.nxt.*;

public class Navigation {
	// put your navigation code here 
	private Odometer odo;
	private TwoWheeledRobot bot;
	private NXTRegulatedMotor leftWheel;
	private NXTRegulatedMotor rightWheel;
	
	private static double LEFT_RADIUS;	
	private static double RIGHT_RADIUS;
	private static final int FORWARD_SPEED = 100;
	private static final int ROTATION_SPEED = 80;
	private static final double DISTANCE = 25.0;
	private static double WIDTH;
	private static final double LIGHT_DIFFERENCE = 10.0; //the difference between high and low readings necessary to justifiably assume that the light sensor has spotted a light
	private static double[] position = new double[3];	//will hold x, y and theta values from the odometer
	private double xDest = 5.0;
	private double yDest = 2.0;
	
	public Navigation(Odometer odo, NXTRegulatedMotor leftWheel, NXTRegulatedMotor rightWheel) {
		this.odo = odo;
		this.bot = odo.getTwoWheeledRobot();
		LEFT_RADIUS = bot.getLeftRadius();
		RIGHT_RADIUS = bot.getRightRadius();
		WIDTH = bot.getWidth();
		this.leftWheel = leftWheel;
		this.rightWheel = rightWheel;
	}
	
	public void setDestination(double xDest, double yDest)
	{
		this.xDest = xDest;
		this.yDest = yDest;
	}
	
	public void turnToDestination()
	{
		turnTo(findAngle(30*xDest, 30*yDest));
	}
	
	public void travelToDestination()
	{
		travelTo(findAngle(30*xDest, 30*yDest));
	}
	
	public void travelTo(double x, double y) {
		// USE THE FUNCTIONS setForwardSpeed and setRotationalSpeed from TwoWheeledRobot!
		//turn to the correct direction
		double angle = findAngle(x,y);
		turnTo(angle);
		
		//calculate deltax, deltay and distance
		//reading from odometer is in millimeters, but position stores it in centimeters, so no factor of 10 needs to be applied to correct it
		double distance = findDistance(x, y);
		
		leftWheel.setSpeed(FORWARD_SPEED);
		rightWheel.setSpeed(FORWARD_SPEED);

		leftWheel.rotate(convertDistance(LEFT_RADIUS,distance),true);
		rightWheel.rotate(convertDistance(RIGHT_RADIUS,distance),false);
	}
	
	public void travelTo(double distance) {
		leftWheel.setSpeed(FORWARD_SPEED);
		rightWheel.setSpeed(FORWARD_SPEED);

		leftWheel.rotate(convertDistance(LEFT_RADIUS,distance),true);
		rightWheel.rotate(convertDistance(RIGHT_RADIUS,distance),false);
	}
	
	public void travelTo(double distance, boolean goBack) {
		leftWheel.setSpeed(FORWARD_SPEED);
		rightWheel.setSpeed(FORWARD_SPEED);

		leftWheel.rotate(convertDistance(LEFT_RADIUS,distance),true);
		rightWheel.rotate(convertDistance(RIGHT_RADIUS,distance),goBack);
	}
	
	public void turnTo(double angle) {
		turnTo(angle,false);
	}
	
	public void turnTo(double angle, boolean goBack) {
		leftWheel.setSpeed(ROTATION_SPEED);
		rightWheel.setSpeed(ROTATION_SPEED);
		
		leftWheel.rotate(convertAngle(LEFT_RADIUS,WIDTH,angle),true);
		rightWheel.rotate(-convertAngle(RIGHT_RADIUS,WIDTH,angle),goBack);
	}
	
	public double findDistance(double x, double y)
	{
		odo.getPosition(position);	//get current position values
		double deltaX = (x-position[0]);
		double deltaY = (y-position[1]);
		return Math.sqrt(deltaX*deltaX + deltaY*deltaY);
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
