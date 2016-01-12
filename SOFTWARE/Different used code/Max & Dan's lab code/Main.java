//By Dan Crisan and Maxime Grégoire

//Code for the lab 5

import lejos.nxt.*;

public class Main {

	public static void main(String[] args) {
		
		// setup the odometer, display, and ultrasonic and light sensors
		Gr8Bot robot = new Gr8Bot(Motor.A, Motor.B);
		Odometer odo = new Odometer(robot, true);
		LCDInfo lcd = new LCDInfo(odo);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S3);
		LightSensor ls = new LightSensor(SensorPort.S2);
		
		
		
		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.RISING_EDGE);
		usl.doLocalization();
		Sound.beep();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ZeroFinder zf = new ZeroFinder(odo, ls);
		//zf.doLocalization();
		
		
		// perform the light sensor localization
		//LightLocalizer lsl = new LightLocalizer(odo, ls, us);
		//lsl.getToSource();			
		//Button.waitForPress();
	}

}

