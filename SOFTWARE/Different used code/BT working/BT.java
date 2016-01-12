import bluetooth.BluetoothConnection;
import bluetooth.PlayerRole;
import bluetooth.StartCorner;
import bluetooth.Transmission;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.util.Delay;

public class BT {
	
//	//Initialisation of some constants
//	static final double leftRadius = 2.8; 
//	static final double rightRadius = 2.8; 
//	static final double width = 12.48; 
//	static final NXTRegulatedMotor leftMotor = Motor.A;
//	static final NXTRegulatedMotor rightMotor = Motor.B;
	
	//Initialisation of the flag coordinates 
	static int destX = 0;
	static int destY = 0;
	static int flagX = 0;
	static int flagY = 0;
	
	static boolean clawIsClosed = false; 
	
	public static void getDestination(Navigation nav) {
		//Sets up bluetooth connection and transmission
		BluetoothConnection conn = new BluetoothConnection();
		Transmission t = conn.getTransmission();
		
		if (t == null) {
			Sound.beep();
			Delay.msDelay(1000);

			LCD.drawString("Failed to read transmission", 0, 5);
		} else {
			
			//robot will start from here:
			StartCorner corner = t.startingCorner;
			Sound.beep();
			Delay.msDelay(1000);
			//The role of the player is: 
			PlayerRole role = t.role; 
			Sound.beep();
			Delay.msDelay(1000);
			
			//defender will go here to get the flag:
			flagX = t.fx;	//flag pos x
			flagY = t.fy;	//flag pos y
			Sound.beep();
			Delay.msDelay(1000);
			// attacker will drop the flag off here
			destX = t.dx;	//destination pos x
			destY = t.dy;	//destination pos y
			Sound.beep();
			Delay.msDelay(1000);
			// print out the transmission information to the LCD
			conn.printTransmission();
			
			//Sets initial point coordinate
			if(t.startingCorner.equals(StartCorner.BOTTOM_LEFT)){
				nav.setStart(0, 0);
				
			}
			Sound.beep();
			Delay.msDelay(1000);
			
			//Sets destination coordinate
			if(t.role.equals(PlayerRole.ATTACKER)){
				nav.setDestination(destX, destY);
			}
			else if(t.role.equals(PlayerRole.DEFENDER)){
				nav.setDestination(flagX, flagY);
			}
			Sound.beep();
			Delay.msDelay(1000);

			conn.printTransmission();
		}
		
//		else if(t.startingCorner.equals(StartCorner.BOTTOM_RIGHT)){
//			nav.setStart(10*30.48, 0);
//		}
//		else if(t.startingCorner.equals(StartCorner.TOP_LEFT)){
//			nav.setStart(0, 10*30.48);
//		}
//		else if(t.startingCorner.equals(StartCorner.TOP_RIGHT)){
//			nav.setStart(10*30.48, 10*30.48);
//		}		
//		// some objects that need to be instantiated
//		int buttonChoice;
//		int goToX = dx;
//		int goToY = dy;
//		
//		Odometer odometer = new Odometer();
//		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer);
//		
//		if(clawIsClosed){
//			flagX = odometer.getX();
//			flagY = odometer.getY();
//			
//		}
//		//Small arrays containing the initial coordinate and the final coordinate 
//		//THE ODO IS NOT UPDATED! IT TAKES AS INITIAL COORD 0,0 INSTEAD OF THE COORDINATES OF THE FLAG! 
//		final double[] X = {flagX, - goToX * 30}; // WORKS WITH A MINUS .... WE NEED TO FIX THIS... 
//		final double[] Y = {flagY, goToY * 30}; 
//
//		
//		
//
//
//			// start the odometer, the odometry display and (possibly) the
//			// odometry correction
//			odometer.start();
//			odometryDisplay.start();
//			//odometryCorrection.start();
//
//
//			// spawn a new Thread to avoid PathDriver.drive() from blocking
//			(new Thread() {
//				//Finds the shortest path between 2 consecutive points. 
//				public void run() {
//					for (int i = 1; i < 2; i++) {
//						PathDriver.travelTo(X[i] - X[i-1], Y[i] - Y[i-1]);
//					}
//				}
//			}).start();
//		
//		
//		while (Button.waitForPress() != Button.ID_ESCAPE);
//		System.exit(0);
	}
}
