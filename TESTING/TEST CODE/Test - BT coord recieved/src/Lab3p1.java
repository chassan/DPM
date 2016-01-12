import bluetooth.BluetoothConnection;
import bluetooth.PlayerRole;
import bluetooth.StartCorner;
import bluetooth.Transmission;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

import lejos.nxt.*;

public class Lab3p1 {
	
	//Initialisation of some constants
	static final double leftRadius = 2.8; //CORRECT TO 2.85
	static final double rightRadius = 2.8; //CORRECT TO 2.85
	static final double width = 15.8; //CORRECT TO 13.62
	static final NXTRegulatedMotor leftMotor = Motor.A;
	static final NXTRegulatedMotor rightMotor = Motor.B;
	//Initialisation of the flag coordinates 
	static int dx = 0;
	static int dy = 0;
	static double flagX = 0;
	static double flagY = 0;
	
	static boolean clawIsClosed = false; 
	
	
	
	public static void main(String[] args) {
		//Sets up bluetooth connection and transmission
		BluetoothConnection conn = new BluetoothConnection();
		Transmission t = conn.getTransmission();
		if (t == null) {
			LCD.drawString("Failed to read transmission", 0, 5);
		} else {
			
			StartCorner corner = t.startingCorner; //NOT USED FOR DEMO: starting code is not really useful for the demo part. 
			PlayerRole role = t.role; //NOT USED FOR DEMO: we will have the same coord in attacker and defender field. 
			
			//defender will go here to get the flag:
			int fx = t.fx;	//flag pos x
			int fy = t.fy;	//flag pos y
			
			// attacker will drop the flag off here
			dx = t.dx;	//destination pos x
			dy = t.dy;	//destination pos y
			
			// print out the transmission information to the LCD
			conn.printTransmission();
		}
		
		// some objects that need to be instantiated
		int buttonChoice;
		int goToX = dx;
		int goToY = dy;
		
		Odometer odometer = new Odometer();
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer);
		
		if(clawIsClosed){
			flagX = odometer.getX();
			flagY = odometer.getY();
			
		}
		//Small arrays containing the initial coordinate and the final coordinate 
		//THE ODO IS NOT UPDATED! IT TAKES AS INITIAL COORD 0,0 INSTEAD OF THE COORDINATES OF THE FLAG! 
		final double[] X = {flagX, - goToX * 30}; // WORKS WITH A MINUS .... WE NEED TO FIX THIS... 
		final double[] Y = {flagY, goToY * 30}; 

		
		


			// start the odometer, the odometry display and (possibly) the
			// odometry correction
			odometer.start();
			odometryDisplay.start();
			//odometryCorrection.start();


			// spawn a new Thread to avoid PathDriver.drive() from blocking
			(new Thread() {
				//Finds the shortest path between 2 consecutive points. 
				public void run() {
					for (int i = 1; i < 2; i++) {
						PathDriver.travelTo(X[i] - X[i-1], Y[i] - Y[i-1]);
					}
				}
			}).start();
		
		
		while (Button.waitForPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
