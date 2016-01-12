import lejos.nxt.*;
import lejos.util.Delay;

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
	private static double WIDTH;
	private static double[] position = new double[3];	//will hold x, y and theta values from the odometer
	private double xDest = 0.0;
	private double yDest = 0.0;
	private UltrasonicSensor forwardUSSensor = new UltrasonicSensor(SensorPort.S3);
	private UltrasonicSensor rightUSSensor;
	private UltrasonicSensor leftUSSensor;
	private ArmMovement arm;
	private boolean done = false;
	private double xStart = 0;
	private double yStart = 0;
	private double startHeading;
	public static double xCoordCorner = 0.0;
	public static double yCoordCorner = 0.0;
	public static double cornerHeading = 0.0;
	public int startingCorner = 0;
	private static final int USDISTANCE = 30;
	private static final double BACK_UP_DISTANCE = 30.0;
	private static final double PUSH_DISTANCE = 15.0;
	private static final double ARM_LENGTH = 22;
	
	public Navigation(Odometer odo, NXTRegulatedMotor leftWheel, NXTRegulatedMotor rightWheel) {
		this.odo = odo;
		this.bot = odo.getTwoWheeledRobot();
		LEFT_RADIUS = bot.getLeftRadius();
		RIGHT_RADIUS = bot.getRightRadius();
		WIDTH = bot.getWidth();
		this.leftWheel = leftWheel;
		this.rightWheel = rightWheel;
	}
	
	public void setNavSideSensors(UltrasonicSensor leftUSSensor, UltrasonicSensor rightUSSensor)
	{
		this.leftUSSensor = leftUSSensor;
		this.rightUSSensor = rightUSSensor;
	}
	
	public void setArm(ArmMovement arm)
	{
		this.arm = arm;
	}
	
	public void setStart(double xStart, double yStart){
		this.xStart = xStart;
		this.yStart = yStart;
		if(xStart==0.0)	{
			if(yStart==0.0)	{
				this.startHeading = 0.0;
				this.startingCorner = 1;
			} else if (yStart==10.0)	{
				this.startHeading = 90.0;
				this.startingCorner = 2;
			}
		} else if (xStart==10.0){
			if(yStart==0.0)	{
				this.startHeading = 180.0;
				this.startingCorner = 3;
			} else if (yStart==10.0)	{
				this.startHeading = 270.0;
				this.startingCorner = 4;
			}
		}
	}
	
	public double getXStart(){
		return this.xStart;
	}
	
	public double getyStart(){
		return this.yStart;
	}
	
	public int getCorner(){
		return this.startingCorner;
	}
	
	public void setDestination(double xDest, double yDest)
	{
		this.xDest = xDest;
		this.yDest = yDest;
	}
	
	public double getXDest(){
		return this.xDest;
	}
	
	public double getYDest(){
		return this.yDest;
	}
	
	public void travelToDestination()
	{
		travelTo(30.48*xDest-PUSH_DISTANCE, 30.48*yDest-PUSH_DISTANCE);
	}

	public void travelTo(double x, double y) {
		double[] myPos = new double[3];
		boolean doNothing = false;

		final double xD = x;
		final double yD = y;
		// USE THE FUNCTIONS setForwardSpeed and setRotationalSpeed from TwoWheeledRobot!
		//turn to the correct direction
		double angle = findAngle(x,y);
		turnTo(angle);
		Delay.msDelay(100);
		
		while(true){
			
			if (getFilteredData(forwardUSSensor)<20){
				odo.getPosition(myPos);
				
				if(getFilteredData(rightUSSensor)>getFilteredData(leftUSSensor)){
					if(getFilteredData(leftUSSensor)<USDISTANCE)
					{
						xCoordCorner=myPos[0];
						yCoordCorner=myPos[1];
						cornerHeading=myPos[2];
					}
					turnTo(90);
				}else{
					if(getFilteredData(rightUSSensor)<USDISTANCE)
					{
						xCoordCorner=myPos[0];
						yCoordCorner=myPos[1];
						cornerHeading=myPos[2];
					}
					turnTo(-90);
				}
				
				Delay.msDelay(100);
				
				travelTo(30.48*2, true);
				while(true){
					if(getFilteredData(forwardUSSensor)<15){
						bot.stop();
						break;
					}
					
					if(leftWheel.getRotationSpeed()==0) break;
				}
				
				//turns to the right angle and try to get to the coordinates again (recursion)
				done = false;
				travelTo(x,y);	
				doNothing = true;
				break;
				}
			
			
			
			if(!doNothing){
			//calculate deltax, deltay and distance
			//reading from odometer is in millimeters, but position stores it in centimeters, so no factor of 10 needs to be applied to correct it

			(new Thread() {
				public void run()
				{
					leftWheel.setSpeed(FORWARD_SPEED);
					rightWheel.setSpeed(FORWARD_SPEED);
					
					leftWheel.rotate(convertDistance(LEFT_RADIUS,findDistance(xD,yD)),true);
					rightWheel.rotate(convertDistance(RIGHT_RADIUS,findDistance(xD,yD)),false);
					done=true;
				}
			}).start();
			doNothing = true;
			
			}

			if(done)
			{
				done=false;
				break;
			}
			
		}
	}
	
	public void defenderFinish()
	{
		//if a corner is found..
		if(xCoordCorner!=0.0 || yCoordCorner!=0.0)
		{
			travelTo(xCoordCorner,yCoordCorner);
			turnTo(findAngle(cornerHeading));
			leftWheel.setSpeed(-FORWARD_SPEED);
			rightWheel.setSpeed(-FORWARD_SPEED);
			
			leftWheel.rotate(convertDistance(LEFT_RADIUS,BACK_UP_DISTANCE),true);
			rightWheel.rotate(convertDistance(RIGHT_RADIUS,BACK_UP_DISTANCE),false);
			
			arm.armDown(100,1);
			
			arm.openClaw();
			
			leftWheel.setSpeed(FORWARD_SPEED);
			rightWheel.setSpeed(FORWARD_SPEED);
			
			leftWheel.rotate(convertDistance(LEFT_RADIUS,PUSH_DISTANCE),true);
			rightWheel.rotate(convertDistance(RIGHT_RADIUS,PUSH_DISTANCE),false);
			
			leftWheel.setSpeed(-FORWARD_SPEED);
			rightWheel.setSpeed(-FORWARD_SPEED);
			
			leftWheel.rotate(convertDistance(LEFT_RADIUS,PUSH_DISTANCE),true);
			rightWheel.rotate(convertDistance(RIGHT_RADIUS,PUSH_DISTANCE),false);
			
			arm.armUp(95,0);
			
			travelTo(xStart,yStart);
			
			turnTo(findAngle(startHeading));
			
			leftWheel.setSpeed(-FORWARD_SPEED);
			rightWheel.setSpeed(-FORWARD_SPEED);
			
			leftWheel.rotate(convertDistance(LEFT_RADIUS,PUSH_DISTANCE),true);
			rightWheel.rotate(convertDistance(RIGHT_RADIUS,PUSH_DISTANCE),false);
		} else { 
			//if a corner is not found..
			travelTo(0,0);
			
			turnTo(findAngle(0));
			
			done = false;
			(new Thread(){
				public void run()
				{
					turnTo(90,true);
					done=true;
				}
			}).start();
			
			double[] pos = new double[3];
			double lowestHeading = 45.0;
			double lowestReading = 100.0;
			double currentReading = 0.0;
			while(!done)
			{
				currentReading = forwardUSSensor.getDistance();
				if(currentReading<lowestReading){
					odo.getPosition(pos);
					lowestReading = currentReading;
					lowestHeading = pos[2];
				}
			}
			done=false;
			
			turnTo(findAngle(lowestHeading));
			travelTo(currentReading-ARM_LENGTH,true);
			
			arm.armDown(95, 1);
			arm.openClaw();
			
			leftWheel.setSpeed(-FORWARD_SPEED);
			rightWheel.setSpeed(-FORWARD_SPEED);
			
			leftWheel.rotate(convertDistance(LEFT_RADIUS,PUSH_DISTANCE),true);
			rightWheel.rotate(convertDistance(RIGHT_RADIUS,PUSH_DISTANCE),false);
			
			travelTo(xStart,yStart);
			turnTo(findAngle(startHeading));
			
			leftWheel.setSpeed(-FORWARD_SPEED);
			rightWheel.setSpeed(-FORWARD_SPEED);
			
			leftWheel.rotate(convertDistance(LEFT_RADIUS,PUSH_DISTANCE),true);
			rightWheel.rotate(convertDistance(RIGHT_RADIUS,PUSH_DISTANCE),false);
		}
	}
	
	public void attackerFinish()
	{
		travelTo(xDest,yDest);
		
		leftWheel.setSpeed(-FORWARD_SPEED);
		rightWheel.setSpeed(-FORWARD_SPEED);
		
		leftWheel.rotate(convertDistance(LEFT_RADIUS,ARM_LENGTH),true);
		rightWheel.rotate(convertDistance(RIGHT_RADIUS,ARM_LENGTH),false);
		
		arm.armDown(95, 1);
		arm.openClaw();
		
		leftWheel.setSpeed(-FORWARD_SPEED);
		rightWheel.setSpeed(-FORWARD_SPEED);
		
		leftWheel.rotate(convertDistance(LEFT_RADIUS,PUSH_DISTANCE),true);
		rightWheel.rotate(convertDistance(RIGHT_RADIUS,PUSH_DISTANCE),false);
		
		arm.armUp(95,0);
		
		travelTo(xStart,yStart);
		turnTo(findAngle(startHeading+45));
		
		leftWheel.setSpeed(-FORWARD_SPEED);
		rightWheel.setSpeed(-FORWARD_SPEED);
		
		leftWheel.rotate(convertDistance(LEFT_RADIUS,PUSH_DISTANCE),true);
		rightWheel.rotate(convertDistance(RIGHT_RADIUS,PUSH_DISTANCE),false);
		
		//play WE ARE THE CHAMPIONS
	}
	
	public void travelTo(double distance) {
		travelTo(distance,false);
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
	
	private int getFilteredData(UltrasonicSensor sensor) {
		int distance;
		
		// do a ping
		sensor.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = sensor.getDistance();
		if (distance > 60)
			distance = 60;
				
		return distance;
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
