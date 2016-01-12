import lejos.nxt.Sound;




public class Navigation {
 // forward and rotational speeds in cm / s and degrees / s, respectively
 private static double FORWARD_SPEED = 5.0;
 private static double ROTATION_SPEED = 20.0;
 private static double ROTATION_TOLERANCE = 0.1;
 
 public static void travelTo(Odometer odo, double x, double y){
	 Gr8Bot robot = odo.getGr8Bot();
	 double targetX, targetY, targetAngle, distance; 
	 double[] initPos = new double [3];
	 robot.setRotationSpeed(0.0);
	 odo.getPosition(initPos);
	 targetX = x-initPos[0];
	 targetY = y-initPos[1];
	 targetAngle = Math.toDegrees(Math.atan(targetY/targetX));
	 distance = Math.sqrt(Math.pow(targetX, 2) + Math.pow(targetY, 2));
	 robot.setRotationSpeed(ROTATION_SPEED);
	 Sound.buzz();
	// turnTo(odo, Odometer.minimumAngleFromTo(initPos[2], targetAngle));
	 turnTo(odo, initPos[2]+ targetAngle);
	 robot.setForwardSpeed(FORWARD_SPEED);
	 goForward(odo, distance);
 }
 
 public static void goForward(Odometer odo, double distance) {
  Gr8Bot robot = odo.getGr8Bot();
  double [] initPos = new double [3], currPos = new double [3];
  
  // stop any rotational motion
  robot.setRotationSpeed(0.0);
  
  // latch the initial position
  odo.getPosition(initPos);
  odo.getPosition(currPos);
  
  // start the motors and wait to reach the appropriate distance from
  // the initial position
  if (distance < 0.0)
   robot.setForwardSpeed(-FORWARD_SPEED);
  else
   robot.setForwardSpeed(FORWARD_SPEED);
  
  while ((currPos[0] - initPos[0]) * (currPos[0] - initPos[0]) +
    (currPos[1] - initPos[1]) * (currPos[1] - initPos[1]) <
    distance * distance) {
   odo.getPosition(currPos);
  }
  
  // stop the motors
  robot.setForwardSpeed(0.0);
 }
 
 
 
 public static void turnTo(Odometer odo, double angle) {
  Gr8Bot robot = odo.getGr8Bot();
  double [] currPos = new double [3];
  double currSpeed = ROTATION_SPEED;
  double angDiff;
  
  // stop any forward motion
  robot.setForwardSpeed(0.0);
  
  // latch the initial position
  odo.getPosition(currPos);
  angDiff = Odometer.minimumAngleFromTo(currPos[2], angle);
  if (angDiff > 0.0)
   robot.setRotationSpeed(currSpeed);
  else
   robot.setRotationSpeed(currSpeed *= -1);
  
  // turn to the appropriate angle
  while (Math.abs(angDiff) > ROTATION_TOLERANCE) {
   if (currSpeed > 0.0 && angDiff < 0.0)
    robot.setRotationSpeed(currSpeed *= -0.5);
   else if (currSpeed < 0.0 && angDiff > 0.0)
    robot.setRotationSpeed(currSpeed *= -0.5);
   
   odo.getPosition(currPos);
   angDiff = Odometer.minimumAngleFromTo(currPos[2], angle);
  }
  
  // stop the motors
  robot.setRotationSpeed(0.0);
 }
 

}
