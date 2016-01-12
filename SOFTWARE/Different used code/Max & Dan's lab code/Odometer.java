import lejos.util.Timer;
import lejos.util.TimerListener;

public class Odometer implements TimerListener {
 public static final int DEFAULT_PERIOD = 25;
 private Gr8Bot robot;
 private Timer odometerTimer;
 
 // position data
 private Object lock;
 private double x, y, theta;
 private double [] oldDH, dDH;
 
 public Odometer(Gr8Bot robot, int period, boolean start) {
  // initialise variables
  this.robot = robot;
  odometerTimer = new Timer(period, this);
  x = 0.0;
  y = 0.0;
  theta = 0.0;
  oldDH = new double [2];
  dDH = new double [2];
  lock = new Object();
  
  // start the odometer immediately, if necessary
  if (start)
   odometerTimer.start();
 }
 
 public Odometer(Gr8Bot robot) {
  this(robot, DEFAULT_PERIOD, false);
 }
 
 public Odometer(Gr8Bot robot, boolean start) {
  this(robot, DEFAULT_PERIOD, start);
 }
 
 public Odometer(Gr8Bot robot, int period) {
  this(robot, period, false);
 }
 
 public void timedOut() {
  robot.getDisplacementAndHeading(dDH);
  dDH[0] -= oldDH[0];
  dDH[1] -= oldDH[1];
  
  // update the position in a critical region
  synchronized (lock) {
   x += dDH[0] * Math.sin((oldDH[1] + dDH[1] / 2.0) * Math.PI / 180.0);
   y += dDH[0] * Math.cos((oldDH[1] + dDH[1] / 2.0) * Math.PI / 180.0);
   theta += dDH[1];
   
   // keep theta between 0 and 360
   if (theta < 0.0)
    theta += 360.0;
   else if (theta >= 360.0)
    theta -= 360.0;
  }
  
  oldDH[0] += dDH[0];
  oldDH[1] += dDH[1];
 }
 
 // accessors
 public void getPosition(double [] pos) {
  synchronized (lock) {
   pos[0] = x;
   pos[1] = y;
   pos[2] = theta;
  }
 }
 
 public Gr8Bot getGr8Bot() {
  return robot;
 }
 
 // mutators
 public void setPosition(double [] pos, boolean [] update) {
  synchronized (lock) {
   if (update[0]) x = pos[0];
   if (update[1]) y = pos[1];
   if (update[2]) theta = fixAngle(pos[2]);// * Math.PI / 180.0;
  }
 }
 
 // static 'helper' methods
 public static double fixAngle(double angle) {  
  if (angle < 0.0)
   angle = 360.0 + (angle % 360.0);
  
  return angle % 360.0;
 }
 
 public static double minimumAngleFromTo(double a, double b) {
  double d = fixAngle(b - a);
  
  if (d < 180.0)
   return d;
  else
   return d - 360.0;
 }
}
