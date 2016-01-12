import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.nxt.remote.*;

public class BT {

	public static void main (String[] args)
	{
		
		try
		{
			LCD.drawString("Connecting...", 0, 0);
			NXTCommConnector connector = Bluetooth.getConnector();
			RemoteNXT nxt = new RemoteNXT("NXT", connector);
			LCD.clear();
			move(nxt);
		}	catch (IOException ioe)	{
			LCD.clear();
			LCD.drawString("Conn Failed", 0, 0);
			Button.waitForAnyPress();
			System.exit(1);
		}
	}
	
	public static void move (RemoteNXT nxt)
	{
		Button.waitForAnyPress();
		nxt.A.setSpeed(100);
		nxt.B.setSpeed(100);
		nxt.C.setSpeed(100);
		Button.waitForAnyPress();
		nxt.A.rotate(90,true);
		nxt.B.rotate(90,true);
		nxt.C.rotate(45,false);
		Button.waitForAnyPress();
		nxt.C.rotate(-90,true);
		nxt.A.rotate(-90,true);
		nxt.B.rotate(-45,false);
		Button.waitForAnyPress();
	}
	
}