import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class USLocalizer {
  public enum LocalizationType { FALLING_EDGE, RISING_EDGE, CIRCULAR };
  public static double ROTATION_SPEED = 20;
  
  private Odometer odo;
  private Gr8Bot robot;
  private UltrasonicSensor us;
  private LocalizationType locType;
  
  // those variables are used for my filter (moving window)
  private int[] a;
  private int currentLocation;
  
  
  public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
    this.odo = odo;
    this.robot = odo.getGr8Bot();
    this.us = us;
   // this.locType = locType;
        
    // initializing the values for the moving window
    currentLocation = 2;
    a = new int[5];
    for(int i = 0;i <5;i++)
    {      
      // there will be a delay here
      a[i] = getFilteredData();
    }
    // switch off the ultrasonic sensor
    us.off();
  }
  
  public void doLocalization() {
    double [] pos = new double [3];
    double angleA, angleB;
    
    if(getFilteredData()<50){
    	locType = LocalizationType.FALLING_EDGE;
    } else {
    	locType = LocalizationType.RISING_EDGE;
    }
    
    if (locType == LocalizationType.FALLING_EDGE) {
      // rotate the robot until it sees no wall
      robot.setRotationSpeed(ROTATION_SPEED);
      while (getFilteredData2() < 30);
      
      // keep rotating until the robot sees a wall, then latch the angle
      while (getFilteredData2() > 25);
      odo.getPosition(pos);
      angleA = pos[2];
      
      // switch direction and wait until it sees no wall
      robot.setRotationSpeed(-ROTATION_SPEED);
      while (getFilteredData2() < 30);
      
      // keep rotating until the robot sees a wall, then latch the angle
      while (getFilteredData2() > 25);
      odo.getPosition(pos);
      angleB = pos[2];
      
      // angleA is clockwise from angleB, so assume the average of the
      // angles to the right of angleB is 45 degrees past 'north'
      if (angleA < angleB){
    	robot.setRotationSpeed(ROTATION_SPEED);
        Navigation.turnTo(odo, (angleA + angleB + 270) / 2);}
      else
    	robot.setRotationSpeed(ROTATION_SPEED);
        Navigation.turnTo(odo, (angleA + angleB - 90) / 2);
      
      // update the odometer position
      odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
      
      
    } else {
      // rotate the robot until it sees a wall
      robot.setRotationSpeed(ROTATION_SPEED);
      while (getFilteredData2() > 25);
      
      // rotate the robot until it sees no wall, then latch the angle
      while (getFilteredData2() < 30);
      odo.getPosition(pos);
      angleA = pos[2];
      
      // switch direction and wait until it sees a wall
      robot.setRotationSpeed(-ROTATION_SPEED);
      while (getFilteredData2() > 25);
      
      
      // rotate the robot until it sees no wall, then latch the angle
      while (getFilteredData2() < 30);
      odo.getPosition(pos);
      angleB = pos[2];
      
      // angleA is clockwise from angleB, so assume the average of the
      // angles to the right of angleB is 45 degrees past 'north'
      if (angleA < angleB)
        Navigation.turnTo(odo, (angleA + angleB - 90) / 2);
      else
        Navigation.turnTo(odo, (angleA + angleB + 270 ) / 2);
      
      odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
      
    } 
  }
  
  
  private int getFilteredData() {
    
    int distance;
    // do a ping
    us.ping();
    
    // wait for the ping to complete
    try { Thread.sleep(50); } catch (InterruptedException e) {}

    distance = us.getDistance();
    // filter out large values
    if (distance > 50)
      distance = 50;
    return distance;
  }
  
  // moving window filter discussed in class
  // it arranges the last 5 values then it returns the median
  private int getFilteredData2() {
    int distance;
    a[currentLocation] = getFilteredData();
    currentLocation = (currentLocation+1)%5;
    int[] b = a.clone();
    sort(b, 5);
    distance = b[2];
    return distance;
  }
  
  
  // bubble sort
  private static void sort(int v[], int n) {
    int i, j;  
    for(i = 0; i < n; i++){
      for(j = i -1; j >= 0 && v[j] > v[j+1]; j = j -1) {
        swap(v,j);
      }
    }   
  }
  
  // sort hellper method
  private static void swap(int v[], int k){
    int temp;  
    temp = v[k];
    v[k] = v[k+1];
    v[k+1] = temp;
  }
    
 /* private int getFilteredData() {
    int distance;
    
    // do a ping
    us.ping();
    
    // wait for the ping to complete
    try { Thread.sleep(50); } catch (InterruptedException e) {}
    
    // there will be a delay here
    distance = us.getDistance();
    
    return distance;
  }*/
  
    
  /*public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
    this.odo = odo;
    this.robot = odo.getGr8Bot();
    this.us = us;
    this.locType = locType;
  }*/
  
}

////import lejos.nxt.*;
////import lejos.util.*;
////
////public class USLocalizer {
////	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
////	public static double ROTATION_SPEED = 30;
////	public static int DISTANCE = 50;	//for use in filtering distance measurements of Ultrasonic Sensor
////	
////	private Navigation nav;
////	private Odometer odo;
////	private Gr8Bot robot;
////	private UltrasonicSensor us;
////	private LocalizationType locType;
////	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
////		this.odo = odo;
////		this.robot = odo.getGr8Bot();
////		this.us = us;
////		this.locType = locType;
////		// switch off the ultrasonic sensor
////		us.off();
////	}
////	
////	public void doLocalization() {
////		double [] pos = new double [3];	//to hold position values from odometer
////		double angleA, angleB;	//to store values of angles used to calculate approximate heading
////		double measure;	//for storing temporary values of the filtered ultrasonic sensor readings
////		if (locType == LocalizationType.FALLING_EDGE) {
////			// rotate the robot until it sees no wall
////			robot.setRotationSpeed(ROTATION_SPEED);	//clockwise rotation
////			measure=getFilteredData();//Get a value to start with
////			while(measure<=DISTANCE)	{
////				measure=getFilteredData();	//will acquire values until no wall is seen (i.e. when measure is 255, exit loop)
////			}
////			// keep rotating until the robot sees a wall, then latch the angle
////			while(measure>DISTANCE)	{
////				measure=getFilteredData(); //will acquire values until a wall is seen (i.e. when measure is less than or equal to DISTANCE, exit loop)
////			}
////			//getting current odometer values
////			odo.getPosition(pos);	//acquire current position values
////			angleA = pos[2];	//first angle is A
////			// switch direction and wait until it sees no wall
////			robot.setRotationSpeed(-ROTATION_SPEED);	//anti-clockwise rotation
////			while(measure<=DISTANCE){
////				measure=getFilteredData(); //will acquire values until no wall is seen
////			}
////			// keep rotating until the robot sees a wall, then latch the angle
////			while(measure>DISTANCE)	{
////				measure=getFilteredData(); //will acquire values until a wall is seen
////			}
////			//getting current odometer values
////			odo.getPosition(pos);	//acquire current position values
////			angleB = pos[2];	//second angle is B
////			//angleB needs to be negative for our calculation so...
////			angleB = angleB - 360;
////			// angleA is clockwise from angleB, so assume the average of the
////			// angles to the right of angleB is 45 degrees past 'north'
////			
////			// update the odometer position (example to follow:)
////			// odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
////			
////			//In this case we use the formula (45 - (angleA+angleB)/2) to calculate theta, we do not update x or y, because we have no way of gauging them at the moment
////			//the theta calculated is the starting angle, and needs to be added to the current theta (i.e. pos[2])
////
////			//getting current odometer values
////			odo.getPosition(pos);	//acquire current position values
////			odo.setPosition(new double [] {0.0, 0.0, pos[2] + (45-(angleA+angleB)/2.0)}, new boolean [] {false, false, true});
////		} else {
////			/*
////			 * The robot should turn until it sees the wall, then look for the
////			 * "rising edges:" the points where it no longer sees the wall.
////			 * This is very similar to the FALLING_EDGE routine, but the robot
////			 * will face toward the wall for most of it.
////			 */
////			
////			//rotate the robot until it sees a wall
////			robot.setRotationSpeed(ROTATION_SPEED);	//clockwise rotation
////			measure=getFilteredData();//Get a value to start with
////			while(measure>DISTANCE)	{
////				measure=getFilteredData();//Will do nothing until it sees a wall
////			}
////			//keep rotating until the robot sees no wall, then latch the angle
////			while(measure<=DISTANCE)	{
////				measure=getFilteredData();  //will acquire values until no wall is seen
////			}
////			odo.getPosition(pos);	//acquire current position values
////			angleA = pos[2];	//first angle is A
////			//switch direction and wait until the robot sees a wall
////			robot.setRotationSpeed(-ROTATION_SPEED);
////			while(measure>DISTANCE)	{
////				measure=getFilteredData(); //will acquire values until a wall is seen
////			}
////			//keep rotating until the robot sees no wall, then latch the angle
////			while(measure<=DISTANCE)	{
////				measure=getFilteredData(); //will acquire values until no wall is seen
////			}
////			odo.getPosition(pos);	//acquire current position values
////			angleB = pos[2];	//second angle is B
////			//angleB needs to be negative for our calculation so...
////			angleB = angleB - 360;
////			// angleA is anti-clockwise from angleB, so assume the average of the
////			// angles to the left of angleB is 225 degrees past 'north'
////			
////			//In this case we use the formula (225 - (angleA+angleB)/2) to calculate theta, we do not update x or y, because we have no way of gauging them at the moment
////			//the theta calculated is the starting angle, and needs to be added to the current theta (i.e. pos[2])
////			odo.getPosition(pos);
////			odo.setPosition(new double [] {0.0, 0.0, pos[2] + (225-(angleA+angleB)/2.0)}, new boolean [] {false, false, true});
////		}
////	}
////	
////	private int getFilteredData() {	//if a result of 255 is measured by the ultrasonic sensor, a second value is acquired and used, just in case
////		int distance = 0;	//to hold measure from ultrasonic sensor
////		// do a ping
////		us.ping();
////				
////		// wait for the ping to complete
////		try { Thread.sleep(50); } catch (InterruptedException e) {}
////					
////		// there will be a delay here
////		distance = us.getDistance();
////			
////		if(distance==255)	{
////			//if value is 255, try again to make sure it is not a mistaken value of 255
////			// do a ping
////			us.ping();
////					
////			// wait for the ping to complete
////			try { Thread.sleep(50); } catch (InterruptedException e) {}
////						
////			// there will be a delay here
////			distance = us.getDistance();
////		}
////		return distance;
////	}
////
////}

//
//import lejos.nxt.Sound;
//import lejos.nxt.UltrasonicSensor;
//
//public class USLocalizer {
//	public enum LocalizationType { FALLING_EDGE, RISING_EDGE, CIRCULAR };
//	public static double ROTATION_SPEED = 40;
//
//	private Odometer odo;
//	private Gr8Bot robot;
//	private UltrasonicSensor us;
//	private LocalizationType locType;
//	
//	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
//		this.odo = odo;
//		this.robot = odo.getGr8Bot();
//		this.us = us;
//		this.locType = locType;
//		
//		// switch off the ultrasonic sensor
//		us.off();
//	}
//	
//	public void doLocalization() {
//		double [] pos = new double [3];
//		double angleA = 0, angleB, buddy;
//		
//		if (locType == LocalizationType.FALLING_EDGE) {
//			// rotate the robot until it sees no wall
//			robot.setRotationSpeed(ROTATION_SPEED);
//			while (getFilteredData() < 30);
//			playSound(1);
//			
//			// keep rotating until the robot sees a wall, then latch the angle
//			while (getFilteredData() > 25);
//			odo.getPosition(pos);
//			angleA = pos[2];
//			playSound(2);
//		
//			// switch direction and wait until it sees no wall
//			robot.setRotationSpeed(-ROTATION_SPEED);
//			while (getFilteredData() < 30);
//			playSound(3);
//			
//			// keep rotating until the robot sees a wall, then latch the angle
//			while (getFilteredData() > 25);
//			odo.getPosition(pos);
//			angleB = pos[2];
//			playSound(4);
//			
//			// angleA is clockwise from angleB, so assume the average of the
//			// angles to the right of angleB is 45 degrees past 'north'
//			if (angleA < angleB)
//				Navigation.turnTo(odo, (angleA + angleB + 270) / 2);
//			else
//				Navigation.turnTo(odo, (angleA + angleB - 90) / 2);
//			
//			// update the odometer position
//			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
//			
//		} else if (locType == LocalizationType.RISING_EDGE) {
//			
//			//rotate until we see a wall
//			robot.setRotationSpeed(ROTATION_SPEED);
//			while (getFilteredData() > 25);
//			System.out.println("WE SAW A WALL!");
//			playSound(1);
//			
//			//keep rotating until we no see wall, then get angle
//			//but first, get past the misleading, oh misleading corner.  
//			//but if we already were past it, then by all means, come back!
//			odo.getPosition(pos);
//			buddy = pos[2];
//			Navigation.turnTo(odo, buddy + 90);
//			if(getFilteredData() > 25){
//				System.out.println("OMG THERE'S ONLY EMPTYNESS!");
//				Navigation.turnTo(odo, buddy);
//			}
//			//kk, now do your thing you selfish robot
//			robot.setRotationSpeed(ROTATION_SPEED);
//			while (getFilteredData() < 30);
//			odo.getPosition(pos);
//			angleA = pos[2];
//			System.out.println("OMG WE LOST THE WALL!");
//			playSound(2);
//			
//			//then switch direction!  and start over: rotate back until we see the wall again 
//				//(somehow we get the feeling this is not especially efficient)
//			robot.setRotationSpeed(-ROTATION_SPEED);
//			while (getFilteredData() > 25);
//			System.out.println("THERE IT IS!");
//			playSound(3);
//			
//			//and once again, wait for the wall to magically disappear and steal the angle from the poor, poor odometer
//			//but first, get past the misleading, oh misleading corner.  
//			//and this time, we're sure that we weren't already past it!  so just do your job mighty little robot!
//			odo.getPosition(pos);
//			buddy = pos[2];
//			Navigation.turnTo(odo, buddy - 90);
//			robot.setRotationSpeed(-ROTATION_SPEED);
//			while (getFilteredData() < 30);
//			odo.getPosition(pos);
//			angleB = pos[2];
//			System.out.println("WTF IT'S GONE AGAIN!");
//			playSound(4);
//			
//			//finally, determine where the north is and ROTATE TO THERE
//			if (angleA > angleB)
//				Navigation.turnTo(odo, (angleA + angleB + 270) / 2);
//			else
//				Navigation.turnTo(odo, (angleA + angleB - 90) / 2);
//			System.out.println("Ah, at last here we are...  maybe?");
//			
//			// update the odometer position
//			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
//			
//		} else {
//			
//			//first determine if we start facing the wall and if so, adjust
//			robot.setRotationSpeed(ROTATION_SPEED);
//			if(getFilteredData() < 30){
//				System.out.println("Start facing the wall");
//				//rotate by 200 degrees to be sure not to face the wall anymore
//				try {Thread.sleep((int)(200*1000/ROTATION_SPEED));} catch(Exception e){}
//			}
//			
//			//if not, rotate until we see a wall, then get first angle
//			while (getFilteredData() > 25);
//			odo.getPosition(pos);
//			angleA = pos[2];
//			System.out.println("Found first wall");
//			
//			//then rotate until we can't see the wall anymore and thus get second angle
//			//but before that, make sure we safely get past the corner!
//			//to do that, turn 120 degrees before checking for a "gap"
//			try {Thread.sleep((int)(90*1000/ROTATION_SPEED));} catch(Exception e){}
//			while (getFilteredData() < 25)System.out.println(getFilteredData());
//			odo.getPosition(pos);
//			angleB = pos[2];
//			System.out.println("Lost the wall");
//	
//			//so now we have the angles, and we can safely rotate to the north
//			if (angleA < angleB)
//				Navigation.turnTo(odo, (angleA + angleB + 265) / 2);
//			else
//				Navigation.turnTo(odo, (angleA + angleB - 95) / 2);
//			
//			 //update the odometer position
//			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
//		}
//	}
//	
//	private int getFilteredData() {
//		int distance;
//		
//		// do a ping
//		us.ping();
//		try {Thread.sleep(40);} catch(Exception e){}
//		
//		// there will be a delay here
//		distance = us.getDistance();
//		
//		if(distance > 50)
//			distance = 50;
//				
//		return distance;
//	}
//	
//	private void playSound(int number){
//		Sound.playTone(150*number, 20);
//		try{
//		Thread.sleep(20);
//		}catch (Exception e){}
//	}
//}
