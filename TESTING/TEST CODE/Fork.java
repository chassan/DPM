
import lejos.nxt.*;

public class Fork {

	public static void  main (String[] args)
	{
		Motor.A.setSpeed(900);
		Motor.B.setSpeed(900);
		Motor.C.setSpeed(900);
		Button.waitForAnyPress();
		Motor.A.rotate(-45,true);
		Motor.B.rotate(-45,true);
		Motor.C.rotate(-90,false);
		Button.waitForAnyPress();
		Motor.A.setSpeed(200);
		Motor.B.setSpeed(200);
		Motor.C.setSpeed(200);
		Motor.C.rotate(90,true);
		Motor.A.rotate(45,true);
		Motor.B.rotate(45,false);
		Button.waitForAnyPress();
	}
	
}
