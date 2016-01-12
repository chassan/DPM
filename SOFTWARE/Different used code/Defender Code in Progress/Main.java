import java.io.IOException;
import lejos.nxt.*;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.remote.RemoteMotor;
import lejos.nxt.remote.RemoteNXT;
import lejos.util.Delay;

public class Main {
	
	private static boolean attackerStart = false;
	
	public static void main(String[] args)
	{	
		//initiate local motors and sensors
		NXTRegulatedMotor leftWheel = new NXTRegulatedMotor(MotorPort.A);
		NXTRegulatedMotor rightWheel = new NXTRegulatedMotor(MotorPort.B);
		NXTRegulatedMotor puller = new NXTRegulatedMotor(MotorPort.C);
		
		LightSensor searchLightSensor = new LightSensor(SensorPort.S2);
		LightSensor localizationLightSensor = new LightSensor(SensorPort.S1);
		UltrasonicSensor forwardUSSensor = new UltrasonicSensor(SensorPort.S3);
		
		final TwoWheeledRobot robot = new TwoWheeledRobot(leftWheel, rightWheel);
		final Odometer odometer = new Odometer(robot, true, leftWheel, rightWheel);
		final Navigation nav = odometer.getNavigation();
		final LCDInfo lcd = new LCDInfo(odometer);
		
		BT.getDestination(nav);
		//Wait for signal from bluetooth
		
		lcd.start();
		
		try
		{	
			//Create RemoteNXT motors and sensors
			LCD.drawString("Connecting...", 0, 0);
			NXTCommConnector connector = Bluetooth.getConnector();
			RemoteNXT nxt = new RemoteNXT("SEagle", connector);
			LCD.clear();
			RemoteMotor leftArm = nxt.A;
			RemoteMotor rightArm = nxt.B;
			RemoteMotor claw = nxt.C;
//			final LightSensor secondSearchLightSensor = new LightSensor(nxt.S2);
			final UltrasonicSensor rightUSSensor = new UltrasonicSensor(nxt.S1);
			final UltrasonicSensor leftUSSensor = new UltrasonicSensor(nxt.S4);
			
			leftWheel.setAcceleration(3000);
			rightWheel.setAcceleration(3000);
			
			ArmMovement arm = new ArmMovement(leftArm, rightArm, claw, puller, nav);
			nav.setArm(arm);
			nav.setNavSideSensors(leftUSSensor, rightUSSensor);
			
			searchLightSensor.setFloodlight(false);
			
			if(BT.isAttacker)
			{
				//ATTACKER
				
				long startTime, endTime;
				
				startTime = System.currentTimeMillis();
				endTime = System.currentTimeMillis();
				(new Thread(){
					public void run()
					{
						while (Button.waitForAnyPress() != Button.ID_ENTER){
							
						}
						attackerStart=true;
						Sound.twoBeeps();
					}
				}).start();
				while(!attackerStart)
				{
					endTime=System.currentTimeMillis();
					if(endTime-startTime>300000)
					{
						Sound.beep();
						break;
					}
				}
				
				//Do ultrasonic localization
				USLocalizer usl = new USLocalizer(odometer,forwardUSSensor);
				usl.doLocalization();
				
				forwardUSSensor = new UltrasonicSensor(SensorPort.S3);
				
				//Do light localization
				LightLocalizer lsl = new LightLocalizer(odometer,localizationLightSensor);
				lsl.doLocalization();
				
				forwardUSSensor = new UltrasonicSensor(SensorPort.S3);
				
				//Search for light source
				LightSearch search = new LightSearch(odometer,searchLightSensor, forwardUSSensor, leftWheel, rightWheel, arm);
				searchLightSensor.setFloodlight(true);
				search.getCloser_AttackMode();
				
				forwardUSSensor = new UltrasonicSensor(SensorPort.S3);
				
				//Finish up
				nav.attackerFinish();
			} else {
				//DEFENDER
				
				
				//Do ultrasonic localization
				USLocalizer usl = new USLocalizer(odometer,forwardUSSensor);
				usl.doLocalization();
				
				forwardUSSensor = new UltrasonicSensor(SensorPort.S3);
				
				//Do light localization
				LightLocalizer lsl = new LightLocalizer(odometer,localizationLightSensor);
				lsl.doLocalization();

				forwardUSSensor = new UltrasonicSensor(SensorPort.S3);
				
				//Travel to beacon coordinates
				nav.travelToDestination();
				
				forwardUSSensor = new UltrasonicSensor(SensorPort.S3);
				
				//Odometer is assumed to be off, so search for light source
				LightSearch search = new LightSearch(odometer,searchLightSensor, forwardUSSensor, leftWheel, rightWheel, arm);
				search.getToSource();
				
				//Finish up
				nav.defenderFinish();
			}
		} catch (IOException e)	{
			LCD.clear();
			LCD.drawString("Conn failed", 0, 0);
		}
	}
}
