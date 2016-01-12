import java.io.IOException;
import lejos.nxt.*;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.remote.RemoteMotor;
import lejos.nxt.remote.RemoteMotorPort;
import lejos.nxt.remote.RemoteNXT;
import lejos.util.Delay;

public class Main {

	private static int OFFSET = 4; 
	private static int ARM_LENGTH = 22;
	public static void main(String[] args)
	{	
		//initiate local motors and sensors
		NXTRegulatedMotor leftWheel = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor rightWheel = new NXTRegulatedMotor(MotorPort.B);
		NXTRegulatedMotor puller = new NXTRegulatedMotor(MotorPort.C);
		
		LightSensor searchLightSensor = new LightSensor(SensorPort.S2);
		LightSensor localizationLightSensor = new LightSensor(SensorPort.S1);
		UltrasonicSensor forwardUSSensor = new UltrasonicSensor(SensorPort.S3);
		
		TwoWheeledRobot robot = new TwoWheeledRobot(leftWheel, rightWheel);
		Odometer odometer = new Odometer(robot, true, leftWheel, rightWheel);
		Navigation nav = odometer.getNavigation();
		LCDInfo lcd = new LCDInfo(odometer);
		
		//BT.getDestination(nav);
		
		//Wait for signal from bluetooth
		//Create remoteNXT object
		
		try
		{	
			LCD.drawString("Connecting...", 0, 0);
			NXTCommConnector connector = Bluetooth.getConnector();
			RemoteNXT nxt = new RemoteNXT("SEagle", connector);
			LCD.clear();
			RemoteMotor leftArm = nxt.A;
			RemoteMotor rightArm = nxt.B;
			RemoteMotor claw = nxt.C;
			LightSensor secondSearchLightSensor = new LightSensor(nxt.S1);
			UltrasonicSensor sideUSSensor = new UltrasonicSensor(nxt.S4);
			
			ArmMovement arm = new ArmMovement(leftArm, rightArm, claw, puller, nav);
			
			//Do ultrasonic and light localization
			USLocalizer usl = new USLocalizer(odometer,forwardUSSensor);
			usl.doLocalization();
			nav.turnTo(nav.findAngle(45));
			
			LightLocalizer lsl = new LightLocalizer(odometer,localizationLightSensor);
			lsl.doLocalization();
			
			nav.travelTo(0, 0);
			nav.turnTo(nav.findAngle(0+OFFSET));
			odometer.setPosition(new double[] {0,0,0},new boolean[] {true,true,true});
			
			forwardUSSensor = new UltrasonicSensor(SensorPort.S3);
			searchLightSensor = new LightSensor(SensorPort.S2);
			
			//Search for light source
			LightSearch search = new LightSearch(odometer,searchLightSensor, forwardUSSensor, leftWheel, rightWheel, arm);
			search.getToSource();
			
			
			nav.turnToDestination();
			nav.travelToDestination();
			
			arm.openClaw();
			leftWheel.backward();
			rightWheel.backward();
			Delay.msDelay(1000);
			
			//nav.travelTo(0,0);		

		} catch (IOException e)	{
			LCD.clear();
			LCD.drawString("Conn failed", 0, 0);
		}
		

	}
}
