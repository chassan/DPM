import lejos.nxt.*;
import lejos.util.*;

public class Test {

	private static NXTMotor claw = new NXTMotor(MotorPort.C);
	private static NXTMotor armA = new NXTMotor(MotorPort.A);
	private static NXTMotor armB = new NXTMotor(MotorPort.B);
	
	public static void main(String[] args)
	{/*
		LCD.drawString("Ready?",0,1);
		pause();
		Motor.A.setSpeed(900);
		Motor.B.setSpeed(900);
		Motor.C.setSpeed(50);
		claw.setPower(100);
		armA.setPower(100);
		armB.setPower(100);
		Motor.C.rotate(-60);
		pause();
		Motor.A.rotate(-90);
		Motor.B.rotate(-90);
		pause();
		/*/
		setArmSpeed(200);
		setClawSpeed(50);
		LCD.drawString("Ready?",0,1);
		LCD.drawString("C:" + claw.getPower(),0,2);
		pause();
		openClaw(60);
		armDown(90,0);
		pause();
		//Motor.C.rotate(-65,false);
		closeClaw();
		pause();
		armUp(90,1);
		pause();
		armDown(90,1);
		pause();
		//Motor.C.rotate(65,false);
		openClaw();
		pause();
		Motor.C.rotateTo(0,false);
		pause();
		armUp(90,0);
		pause();
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
		openClaw(70);
	}
	
	public static void closeClaw(int angle)
	{
		setClawSpeed(100);
		Motor.C.rotate(-angle,true);
		claw.setPower(100);
	}
	
	public static void openClaw(int angle)
	{
		claw.setPower(0);
		Motor.C.flt();
		setClawSpeed(50);
		Motor.C.rotate(angle,true);
	}
	
	public static void armUp(int angle, int carry)
	{
		int power = armA.getPower();
		setArmSpeed(50+450*carry);
		armA.setPower(100);
		armB.setPower(100);
		Motor.A.rotate(-angle,true);
		Motor.B.rotate(-angle,false);
		setArmSpeed(200);
		armA.setPower(power);
		armB.setPower(power);
	}
	
	public static void armDown(int angle, int carry)
	{
		setArmSpeed(50-25*carry);
		Motor.A.rotate(angle,true);
		Motor.B.rotate(angle,false);
		setArmSpeed(200);
	}
	
	public static void pause()
	{
		Button.waitForAnyPress();
	}
}
