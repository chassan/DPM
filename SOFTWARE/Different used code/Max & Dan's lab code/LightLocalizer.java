import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;


public class LightLocalizer {
  private Odometer odo;
  private Gr8Bot robot;
  private LightSensor ls;
  private UltrasonicSensor us;

  //Boolean to check if the light is close enough (used in doLo
  public boolean closeEnough = false;
  
  
  public static double FORWARD_SPEED = 7;
  public static double ROTATION_SPEED = 15;
  
  public LightLocalizer(Odometer odo, LightSensor ls, UltrasonicSensor us){
	  	this.odo = odo;
	    this.robot = odo.getGr8Bot();
	    this.ls = ls;
	    this.us = us;
  }
  
  
  //find the source orientation, and go 50 cm in that direction,
  //then check again for the orientation, until it is close enough
  
  public void getToSource() {
	  
	  while(!closeEnough){
	  getCloser(50);
	  }
	  
  }
  
  
  
  public void getCloser(int distance){
	  
	  //Get the brightest orientation and turn in that direction
	  double orientation = getBrightestOrientation();
	  robot.setRotationSpeed(ROTATION_SPEED);
	  Navigation.turnTo(odo, orientation);
	  
	  //Wait 5 seconds (as required in specs) 
	  try {Thread.sleep(5000);} catch (InterruptedException e) {}
	  
	  //Use the ultrasonic sensor to check if the source is close enough (20cm).
	  //If it is, it adjusts the distance.
	  if(us.getDistance()<distance+20){
		  distance = us.getDistance()-20;
		  closeEnough = true;
	  }
	  
	  //Go the distance
	  robot.setForwardSpeed(FORWARD_SPEED);
	  Navigation.goForward(odo, distance);
	  
  }
  
  
  
  public double getBrightestOrientation(){
	  
	  //get the actual orientation, store it in myPos and set it to be the brightest orientation
	  double [] myPos = new double [3];
	  odo.getPosition(myPos);
	  double brightestOrientation = myPos[2];
	  int brightestLight = ls.readValue();	
	  
	  //sets the counter for the while loop to 0 (number of degrees turned)
	  int degrees = 0;
	  
	  //turn 360 degrees (5 by 5) and scan to find the brightest light and orientation
	  //Note that we could have done it 1 by 1 or even .5 by .5, but we did it to maximize
	  //the speed
	  while(degrees<360){
		  degrees = degrees + 5;
		  int lightAcquired = turnAndScan();
		  
		  //if the light acquired is brighter than the past brightest, change it
		  if (lightAcquired>brightestLight){
			  brightestLight = lightAcquired;
			  odo.getPosition(myPos);
			  brightestOrientation = myPos[2];
			 
			  
		  }
		  
	  }
	  
	  return brightestOrientation;
	  
	  
  }
  

  //Turn and scan for 360 degrees. Every time it turns, it returns the value read 
  //from the light sensor.
  public int turnAndScan(){
	  
	  int currentLight = ls.readValue();
	  double [] myPos = new double [3];
	  odo.getPosition(myPos);
	  
	  robot.setRotationSpeed(ROTATION_SPEED);
	  Navigation.turnTo(odo, myPos[2]+5);
	  return currentLight;
	  
  }
  
}

