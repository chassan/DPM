import java.io.IOException;

import bluetooth.BluetoothConnection;
import bluetooth.PlayerRole;
import bluetooth.StartCorner;
import bluetooth.Transmission;
import lejos.nxt.*;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.remote.RemoteMotor;
import lejos.nxt.remote.RemoteMotorPort;
import lejos.nxt.remote.RemoteNXT;
import lejos.util.Delay;

public class Main {

	private static int ARM_LENGTH = 22;
	static int dx = 0;
	static int dy = 0;
	static double flagX = 0;
	static double flagY = 0;
	
	public static void main(String[] args)
	{	

		//initiate local motors and sensors
		NXTRegulatedMotor leftWheel = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor rightWheel = new NXTRegulatedMotor(MotorPort.B);
		NXTRegulatedMotor puller = new NXTRegulatedMotor(MotorPort.C);
		
//		LightSensor searchLightSensor = new LightSensor(SensorPort.S2);
//		LightSensor localizationLightSensor = new LightSensor(SensorPort.S1);
//		UltrasonicSensor forwardUSSensor = new UltrasonicSensor(SensorPort.S3);
		
		TwoWheeledRobot robot = new TwoWheeledRobot(leftWheel, rightWheel);
		Odometer odometer = new Odometer(robot, true, leftWheel, rightWheel);
		Navigation nav = odometer.getNavigation();
		LCDInfo lcd = new LCDInfo(odometer);
		

		BT.getDestination(nav);
		
		//Wait for signal from bluetooth
		//Create remoteNXT object
		//Sets up bluetooth connection and transmission
//		BluetoothConnection conn = new BluetoothConnection();
//		Transmission t = conn.getTransmission();
//		if (t == null) {
//			LCD.drawString("Failed to read transmission", 0, 5);
//		} else {
//			
//			StartCorner corner = t.startingCorner; //NOT USED FOR DEMO: starting code is not really useful for the demo part. 
//			PlayerRole role = t.role; //NOT USED FOR DEMO: we will have the same coord in attacker and defender field. 
//			
//			//defender will go here to get the flag:
//			int fx = t.fx;	//flag pos x
//			int fy = t.fy;	//flag pos y
//			
//			// attacker will drop the flag off here
//			dx = t.dx;	//destination pos x
//			dy = t.dy;	//destination pos y
//			
//			// print out the transmission information to the LCD
//			conn.printTransmission();
//		}
		
		try
		{	
			LCD.drawString("Connecting...", 0, 0);
			NXTCommConnector connector = Bluetooth.getConnector();
			RemoteNXT nxt = new RemoteNXT("SEagle", connector);
			LCD.clear();
			RemoteMotor leftArm = nxt.A;
			RemoteMotor rightArm = nxt.B;
			RemoteMotor claw = nxt.C;
			LightSensor secondSearchLightSensor = new LightSensor(nxt.S2);
			UltrasonicSensor rightUSSensor = new UltrasonicSensor(nxt.S1);
			UltrasonicSensor leftUSSensor = new UltrasonicSensor(nxt.S4);
			
			ArmMovement arm = new ArmMovement(leftArm, rightArm, claw, puller, nav);
			
			
			
			//Do ultrasonic and light localization
		//	Button.waitForAnyPress();
//			USLocalizer usl = new USLocalizer(odometer,forwardUSSensor);
//			usl.doLocalization();
//			
//			LightLocalizer lsl = new LightLocalizer(odometer,localizationLightSensor);
//			lsl.doLocalization();
//			nav.travelTo(0,30.48*2, leftUSSensor, rightUSSensor);
			
//			forwardUSSensor = new UltrasonicSensor(SensorPort.S3);
//			searchLightSensor = new LightSensor(SensorPort.S2);
			
			//Search for light source
//			LightSearch search = new LightSearch(odometer,searchLightSensor, forwardUSSensor, leftWheel, rightWheel, arm);
//			search.getToSource();
//			
//			

			//nav.travelToDestination();
			//Button.waitForAnyPress();
//			arm.grab();
//			arm.openClaw();
//			leftWheel.backward();
//			rightWheel.backward();
//			Delay.msDelay(1000);
////			
//			//nav.travelTo(0,0);		
//
		} catch (IOException e)	{
			LCD.clear();
			LCD.drawString("Conn failed", 0, 0);
		}
			
		//nav.travelTo(dx*30.48, dy*30.48);
			
			
//		nav.turnTo(360);
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
}
