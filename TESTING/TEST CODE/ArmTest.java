import lejos.nxt.*;
import lejos.util.*;

public class ArmTest {

	public static void main(String[] args)
	{
		setPower(100);
		setArmSpeed(200);
		setClawSpeed(50);
		LCD.drawString("Ready?",0,1);
		pause();
		openClaw(60);
		armDown(90);
		pause();
		//Motor.C.rotate(-65,false);
		closeClaw();
		pause();
		armUp(90);
		pause();
		armDown(90);
		pause();
		//Motor.C.rotate(65,false);
		openClaw();
		pause();
		Motor.C.rotateTo(0,false);
		pause();
		armUp(90);
		pause();
	}
	
	public static void setPower(int power)
	{
		int i=2;
		int a;
		for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { Motor.A, Motor.B, Motor.C }) {
			a = motor.getPower(power);
			LCD.drawString("" + a,0,i);
			i++;
		}
	}
	
	public static void setArmSpeed(int speed)
	{
		Motor.A.setSpeed(speed);
		Motor.B.setSpeed(speed);
	}
	
	public static void setClawSpeed(int speed)
	{
		Motor.C.setSpeed(speed);
	}
	
	public static void closeClaw()
	{
		closeClaw(70);
	}
	
	public static void openClaw()
	{
		closeClaw(-70);
	}
	
	public static void closeClaw(int angle)
	{
		setClawSpeed(100);
		Motor.C.rotate(-angle,true);
	}
	
	public static void openClaw(int angle)
	{
		setClawSpeed(50);
		Motor.C.rotate(angle,true);
	}
	
	public static void armUp(int angle)
	{
		Motor.A.rotate(-angle,true);
		Motor.B.rotate(-angle,false);
	}
	
	public static void armDown(int angle)
	{
		setArmSpeed(50);
		Motor.A.rotate(angle,true);
		Motor.B.rotate(angle,false);
		setArmSpeed(200);
	}
	
	public static void pause()
	{
		Button.waitForAnyPress();
	}
}
