//import bluetooth.BluetoothConnection;
import bluetooth.PlayerRole;
import bluetooth.StartCorner;
//import bluetooth.Transmission;
//import lejos.nxt.LCD;
//import lejos.nxt.Sound;
//import lejos.util.Delay;

public class BT {
	//Initialisation of the flag coordinates 
	static int destX = 0;
	static int destY = 0;
	static int flagX = 0;
	static int flagY = 0;
	static final double TILE = 30.48;
	
	static boolean clawIsClosed = false;
	public static boolean isAttacker;
	
	public static void getDestination(Navigation nav) {
		//Sets up bluetooth connection and transmission
//		BluetoothConnection conn = new BluetoothConnection();
//		Transmission t = conn.getTransmission();
		
//		if (t == null) {
//			Sound.beep();
//			Delay.msDelay(1000);
//
//			LCD.drawString("Failed to read transmission", 0, 5);
//		} else {
//			
			//robot will start from here:
//			StartCorner corner = t.startingCorner;
			StartCorner corner = StartCorner.BOTTOM_LEFT; //TBR

			//The role of the player is: 
//			PlayerRole role = t.role; 
			PlayerRole role = PlayerRole.ATTACKER; //TBR

			
			//defender will go here to get the flag:
//			flagX = t.fx;	//flag pos x
//			flagY = t.fy;	//flag pos y
			flagX = 4; //TBR
			flagY = 4; //TBR

			// attacker will drop the flag off here
//			destX = t.dx;	//destination pos x
//			destY = t.dy;	//destination pos y
			destX = 0; //TBR
			destY = 6; //TBR

			// print out the transmission information to the LCD
//			conn.printTransmission();
			
			//Sets initial point coordinate
			if(corner.equals(StartCorner.BOTTOM_LEFT)){
				nav.setStart(0, 0);
			} else if(corner.equals(StartCorner.BOTTOM_RIGHT)){
				nav.setStart(10*TILE, 0);
			} else if(corner.equals(StartCorner.TOP_LEFT)){
				nav.setStart(0, 10*TILE);
			} else if(corner.equals(StartCorner.TOP_RIGHT)){
				nav.setStart(10*TILE, 10*TILE);
			}	
			
			//Sets destination coordinate
			if(role.equals(PlayerRole.ATTACKER)){
				nav.setDestination(destX, destY);
				isAttacker = true;
			}
			else if(role.equals(PlayerRole.DEFENDER)){
				nav.setDestination(flagX, flagY);
				isAttacker = false;
			}
			
//			conn.printTransmission();
//		}	
	}
}
