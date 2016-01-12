import lejos.nxt.*;
import lejos.nxt.remote.RemoteMotor;
import lejos.util.Delay;

public class ArmMovement {

	private RemoteMotor leftArm;
	private RemoteMotor rightArm;
	private RemoteMotor claw;
	private NXTRegulatedMotor puller;
	private Navigation nav;
	
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
		claw.stop();
		setPullerSpeed(200);
		setArmSpeed(50);
		armDown(93,0);
		nav.travelTo(40);
		closeClaw();
		armUp(95,1);
		nav.travelTo(-20);
	}
	
	public void setArmSpeed(int speed)
	{
		leftArm.setSpeed(speed);
		rightArm.setSpeed(speed);
	}
	
	public void setPullerSpeed(int speed)
	{
		puller.setSpeed(speed);
	}
	
	public void setClawSpeed(int speed)
	{
		claw.setSpeed(speed);
	}
	
	public void closeClaw()
	{
		setClawSpeed(500);
		claw.backward();
	}
	
	public void openClaw()
	{
		setClawSpeed(50);
		claw.forward();
		Delay.msDelay(1000);
		claw.stop();
		setClawSpeed(50);
	}
	
	public void armUp(int angle, int carry)
	{
		setArmSpeed(50+450*carry);
		setPullerSpeed(200+700*carry);
		leftArm.rotate(-angle,true);
		rightArm.rotate(-angle,true);
		puller.rotate((int)(-angle*10),false);
		setArmSpeed(50);
		setPullerSpeed(200);
	}
	
	public void armDown(int angle, int carry)
	{
		setArmSpeed(50-25*carry);
		leftArm.rotate(angle,true);
		rightArm.rotate(angle,true);
		puller.rotate((int)(angle*10),false);
		setArmSpeed(50);
	}
}

