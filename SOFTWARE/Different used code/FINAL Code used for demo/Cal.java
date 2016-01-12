import lejos.nxt.*;
import lejos.util.*;
import lejos.nxt.LightSensor;

public class Cal {
	
	private static final long CORRECTION_PERIOD = 50;
	
	public static void main(String[] args)	{
		int buttonChoice;
		LightSensor ls = new LightSensor(SensorPort.S1);
		
		buttonChoice = Button.waitForAnyPress();
		if(buttonChoice==Button.ID_LEFT)	{
			LCD.drawString("High",0,1);
			Button.waitForAnyPress();
			ls.calibrateHigh();
			LCD.drawString("Low.",0,1);
			Button.waitForAnyPress();
			ls.calibrateLow();
		}
		double prevV = 0.0;
		double thisV = 0.0;
		
		Motor.A.setSpeed(30);
		Motor.B.setSpeed(30);
		
		Motor.A.backward();
		Motor.B.forward();
		
		int i = 0;
		long correctionStart, correctionEnd;
		
		while(true)	{
			correctionStart = System.currentTimeMillis();
			
			thisV = ls.readValue();
			LCD.drawString("" + thisV,0,1);
			LCD.drawString("" + prevV,0,2);
			LCD.drawString("" + (thisV-prevV),0,3);
			
			if(thisV-prevV>4)	{
				Motor.A.stop();
				Motor.B.stop();
				Delay.msDelay(1000);
				Motor.A.backward();
				Motor.B.forward();
			}
			
			prevV = thisV;
			
			buttonChoice = Button.readButtons();
			
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
			
			if(buttonChoice == Button.ID_ESCAPE)	{
				break;
			}
		}
	}
}