import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;


public class LightSearch {
	  private Odometer odo;
	  private TwoWheeledRobot robot;
	  private LightSensor ls;
	  private UltrasonicSensor us;
	  private Navigation nav;

	  private NXTRegulatedMotor leftWheel;
	  private NXTRegulatedMotor rightWheel;
	  private ArmMovement arm;
	  
	  public static double FORWARD_SPEED = 100; //was 20
	  public static double ROTATION_SPEED = 5;
	  private final double TILE = 30.48;
	  public boolean closeEnough = false;
	  public boolean getCloser = false;
	  private final int DISTANCE_FROM_BLOCK = 30;
	  
	  private double highReading;
  
	  public LightSearch(Odometer odo, LightSensor ls, UltrasonicSensor us, 

		  NXTRegulatedMotor leftWheel, NXTRegulatedMotor rightWheel, ArmMovement arm){
	  	  	this.odo = odo;
	  	    this.robot = odo.getTwoWheeledRobot();
	  	    this.ls = ls;
	  	    this.us = us;
	  	    this.nav = odo.getNavigation();
	  	    this.leftWheel = leftWheel;
	  	    this.rightWheel = rightWheel;
	  	    this.arm = arm;
	    }
			    
			    
	    //find the source orientation, and go 50 cm in that direction,
	    //then check again for the orientation, until it is close enough
			    
	  public void getToSource() {
		  ls.setFloodlight(false);
		  while(!closeEnough){
	  	  getCloser(40);
  	  }
			  	  
	  Sound.beep();
	try {
		Thread.sleep(2000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}

  
  public void getCloser_AttackMode()
  {
	  //determine starting corner
	  int corner = nav.getCorner();
	  double brightValue = 0;
	  int brightestPlace = 0;
	  int Middle = 1;
	  int OppositeCorner = 2;
	  
	  nav.travelTo(5*TILE, 5*TILE);
	  getToSource();
  }
//	  brightValue = highReading;
//	  nav.travelTo(TILE*5,TILE*5);
//	  getBright();
//	  
//	  if(highReading>brightValue){
//		  brightestPlace = 1;
//	  }
//	  
//	  
//	  
//	  switch(brightestPlace){
//	  case 0: nav.travelTo(getNextX(corner), getNextY(corner));
//	  		  getToSource();
//	  		  break;
//	  case 1:
//	  		  getToSource();
//	  		  break;
//	  }
//	 
//	  }
  
//  public void getCloser_AttackMode()
//  {
//	  //determine starting corner
//	  int corner = nav.getCorner();
//	  nav.travelTo(getNextX(0),getNextY(0)); //TBC make it work for all corners
//	  double brightValue = 0;
//	  int brightCorner = 0;
//	  for(int i=0;i<4;i++)
//	  {
//		  getBright();
//		  if(brightValue<this.highReading)
//		  {
//			  brightValue = this.highReading;
//			  brightCorner = corner;
//		  }
//		  //travel to next corner
//		  nav.travelTo(getNextX(corner),getNextY(corner));
//		  corner++;
//	  }
//	  
//	  nav.travelTo(getNextX(brightCorner-1),getNextY(corner-1));
//	  
//	  getToSource();
//  }
  
public void getCloser(int distance){
	  
	  //Get the brightest orientation and turn in that direction
	  double orientation = getBright();
	  robot.setRotationSpeed(ROTATION_SPEED);
	  nav.turnTo(nav.findAngle(orientation));
	  robot.setForwardSpeed(FORWARD_SPEED);
	  nav.travelTo(distance, true); 
	  boolean isVirgin = true;
	  
	  while(true){
		  if(isVirgin){
			  Delay.msDelay(1000);
			  isVirgin = false;
		  }
		  if(leftWheel.getRotationSpeed() == 0 && 

rightWheel.getRotationSpeed() == 0){
			  Sound.twoBeeps();
			  break;
		  }
		  if(us.getDistance()<distance){
			  closeEnough = true;
			  robot.stop();
			  nav.travelTo(-15);
			  arm.grab();
			  break;
		  }
	  }
	  
	  if(us.getDistance()<distance && !closeEnough){
		  closeEnough = true;
		  robot.stop();
		  nav.travelTo(-15);
		  arm.grab();
	  }
	  
	  //Use the ultrasonic sensor to check if the source is close enough (20cm).
	  //If it is, it adjusts the distance.
	 /* if(usLecture<(3/2)*distance && !getCloser){
		  distance = distance/2;
	  }
	  
	  if(usLecture<DISTANCE_FROM_BLOCK){
		 closeEnough = true;
	  }else{
		  if(usLecture<distance){
			  distance = usLecture-DISTANCE_FROM_BLOCK;
			  closeEnough = true;
		  }
	  
	  //Go the distance
	  robot.setForwardSpeed(FORWARD_SPEED);
	  nav.travelTo(distance);
	  }*/
  }
  
  

public double getBright(){
	  int filterFirstValues = 0;
	  double [] initPos = new double [3];
	  double [] pos = new double [3];
	  odo.getPosition(pos);
	  odo.getPosition(initPos);
	  double brightestValue = ls.readValue();
	  double brightestAngle = pos[2];
	  double actualValue;
	  robot.setRotationSpeed(30);
	  while(true){
		  odo.getPosition(pos);
		  if (Math.abs(pos[2]-initPos[2])<1 && filterFirstValues>1000){
			  Sound.buzz();
			  break;
		  }else{
			  filterFirstValues++;
			  //if(filterFirstValues%5 == 2){
				  actualValue = ls.readValue();
				  if(actualValue>brightestValue){
					  brightestValue = actualValue;
					  brightestAngle = pos[2];
				  }
			  //}
		  }
			  
	  }
	  robot.stop();
	  
	  return brightestAngle;
}


  
  
//  public void getCloser(int distance){
//	  
//	  //Get the brightest orientation and turn in that direction
//	  double orientation = getBright();
//	  nav.turnTo(nav.findAngle(orientation));
//	  nav.travelTo(distance, true); 
//	  
//	  Delay.msDelay(1000);
//	  
//	  while(true){
//		  if(leftWheel.getRotationSpeed() == 0 && rightWheel.getRotationSpeed() == 0){
//			  Sound.twoBeeps();
//			  break;
//		  }
//		  if(us.getDistance()<DISTANCE_FROM_BLOCK){
//			  closeEnough = true;
//			  robot.stop();
//			  nav.travelTo(-10);
//			  arm.grab();
//			  break;
//		  }
//	  }
//  }
//  
//  public double getBright(){
//	  int filterFirstValues = 0;
//	  double [] initPos = new double [3];
//	  double [] pos = new double [3];
//	  odo.getPosition(pos);
//	  odo.getPosition(initPos);
//	  double brightestValue = 0;
//	  double lowestValue = 100;
//	  double brightestAngle = pos[2];
//	  double actualValue;
//	  nav.turnTo(360,true);
//	  while(true){
//		  odo.getPosition(pos);
//		  if (leftWheel.getRotationSpeed()==0 && rightWheel.getRotationSpeed()==0 && filterFirstValues>1000){
//			  Sound.buzz();
//			  break;
//		  }else{
//			  filterFirstValues++;
////			  if(filterFirstValues%5 == 2){
//				  actualValue = ls.readValue();
//				  if(actualValue>brightestValue){
//					  brightestValue = actualValue;
//					  brightestAngle = pos[2];
//				  }
//				  if(actualValue<lowestValue){
//					  lowestValue = actualValue;
//				  }
////			  }
//		  }
//			  
//	  }
//	  robot.stop();
//	  
//	  highReading = brightestValue;
//	  
//	  return brightestAngle;
//  }
  
  public double getNextX(int actualCorner){
	  double[] XArray = {9*TILE,TILE,TILE,9*TILE};
	  return XArray[(actualCorner)%4];
  }
  
  public double getNextY(int actualCorner){
	  double[] YArray = {0+TILE,0+TILE,9*TILE,9*TILE};
	  return YArray[(actualCorner)%4];
  }
}

