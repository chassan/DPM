import lejos.nxt.*;
import lejos.nxt.remote.RemoteMotor;
import lejos.util.Delay;

public class ArmMovement {

	private static RemoteMotor leftArm;
	private static RemoteMotor rightArm;
	private static RemoteMotor claw;
	private static NXTRegulatedMotor puller;
	private static Navigation nav;
	
	public ArmMovement(RemoteMotor leftArm, RemoteMotor rightArm, RemoteMotor claw, NXTRegulatedMotor puller, Navigation nav)
	{
		this.leftArm = leftArm;
		this.rightArm = rightArm;
		this.claw = claw;
		this.puller = puller;
		this.nav = nav;
	}
	
	public void grab()
	{
		claw.setSpeed(50);
		setPullerSpeed(200);
		setArmSpeed(25);
		puller.rotate(720,true);
		leftArm.rotate(80,true);
		rightArm.rotate(80,false);
		nav.travelTo(40);
		claw.rotate(-100,true);
		Delay.msDelay(3000);
	}
	
	public static void setArmSpeed(int speed)
	{
		leftArm.setSpeed(speed);
		rightArm.setSpeed(speed);
	}
	
	public static void setPullerSpeed(int speed)
	{
		puller.setSpeed(speed);
	}
	
	public static void setClawSpeed(int speed)
	{
		claw.setSpeed(speed);
	}
	
	public static void closeClaw()
	{
		closeClaw(135);
	}
	
	public static void openClaw()
	{
		openClaw(135);
	}
	
	public static void closeClaw(int angle)
	{
		setClawSpeed(100);
		claw.backward();
		//claw.rotate(-angle,true);
	}
	
	public static void openClaw(int angle)
	{
		claw.flt();
		setClawSpeed(50);
		claw.forward();
		//claw.rotate(angle,true);
	}
	
	public static void armUp(int angle, int carry)
	{
		setArmSpeed(50+450*carry);
//		leftArm.rotate(-angle,true);
//		rightArm.rotate(-angle,true);
		puller.rotate((int)(-angle),false);
		setArmSpeed(50);
	}
	
	public static void armDown(int angle, int carry)
	{
		setArmSpeed(50-25*carry);
//		leftArm.rotate(angle,true);
//		rightArm.rotate(angle,true);
		puller.rotate((int)(angle),false);
		setArmSpeed(50);
	}
}
