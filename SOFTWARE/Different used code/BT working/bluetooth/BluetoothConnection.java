/*
* @author Sean Lawlor
* @date November 3, 2011
* @class ECSE 211 - Design Principle and Methods
*/
package bluetooth;

import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.*;
/**
 * This class inits a bluetooth connection, waits for the data
 * and then allows access to the data after closing the BT channel.
 * 
 * It should be used by calling the constructor which will automatically wait for
 * data without any further user command
 * 
 * Then, once completed, it will allow access to an instance of the Transmission
 * class which has access to all of the data needed
 * 
 * @author Sean Lawlor, Stepan Salenikovich
 * @version Fall 2012
 */
public class BluetoothConnection {
	private Transmission trans;
	
	/**
	 * Waits for connection from BT server indefinitely. Once connected, gets the transmission and closes the BT connection.
	 */
	public BluetoothConnection() {
		LCD.clear();
		LCD.drawString("Starting BT connection", 0, 0);
		
		NXTConnection conn = Bluetooth.waitForConnection();
		DataInputStream dis = conn.openDataInputStream();
		LCD.drawString("Opened DIS", 0, 1);
		this.trans = ParseTransmission.parse(dis);
		LCD.drawString("Finished Parsing", 0, 2);
		try {
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		conn.close();
	}
	
	public Transmission getTransmission() {
		return this.trans;
	}
	
	/**
	 * Prints the transmission: starting corner, role, fx, fy, dx, dy
	 */
	public void printTransmission() {
		try {
			LCD.clear();
			LCD.drawString(("Transmitted Values"), 0, 0);
			LCD.drawString("Start: " + trans.startingCorner.toString(), 0, 1);
			LCD.drawString("Role: " + trans.role.toString(), 0, 2);
			LCD.drawString("fx: " + trans.fx, 0, 3);
			LCD.drawString("fy: " + trans.fy, 0, 4);
			LCD.drawString("dx: " + trans.dx, 0, 5);
			LCD.drawString("dy: " + trans.dy, 0, 6);
		} catch (NullPointerException e) {
			LCD.drawString("Bad Trans", 0, 7);
		}
	}
	
}
