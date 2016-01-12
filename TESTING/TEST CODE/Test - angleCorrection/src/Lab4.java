/*
 * EDITED BY:
 * Alex Carruthers
 * Toby Toubiya
 */

import lejos.nxt.*;

public class Lab4 {

	public static void main(String[] args) {
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		LCDInfo lcd = new LCDInfo(odo);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S3);
		//LightSensor ls = new LightSensor(SensorPort.S1);
		//ls.setFloodlight(true);
		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
		usl.doLocalization();
		//odo.getNavigation().travelTo(8, 8);
		 //perform the light sensor localization
		//LightLocalizer lsl = new LightLocalizer(odo, ls);
		//lsl.doLocalization();
		//8odo.getNavigation().travelTo(0,0);
		odo.getNavigation().turnTo(0);
		Button.waitForPress();
	}

}
