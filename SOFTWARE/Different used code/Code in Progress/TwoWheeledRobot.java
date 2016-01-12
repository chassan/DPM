import lejos.nxt.NXTRegulatedMotor;

public class TwoWheeledRobot {
	public static final double DEFAULT_LEFT_RADIUS = 2.80;
	public static final double DEFAULT_RIGHT_RADIUS = 2.80;
	public static final double DEFAULT_WIDTH = 12.45;
	
	private NXTRegulatedMotor leftWheel, rightWheel;
	private double leftRadius, rightRadius, width;
	private double forwardSpeed, rotationSpeed;
	
	public TwoWheeledRobot(NXTRegulatedMotor leftWheel,
						   NXTRegulatedMotor rightWheel) {
		this.leftWheel = leftWheel;
		this.rightWheel = rightWheel;
		this.leftRadius = DEFAULT_LEFT_RADIUS;
		this.rightRadius = DEFAULT_RIGHT_RADIUS;
		this.width = DEFAULT_WIDTH;
	}
	
	// accessors
	public double getLeftRadius()
	{
		return DEFAULT_LEFT_RADIUS;
	}
	
	public double getRightRadius()
	{
		return DEFAULT_RIGHT_RADIUS;
	}
	
	public double getWidth()
	{
		return DEFAULT_WIDTH;
	}
	
	public double getDisplacement() {
		return (leftWheel.getTachoCount() * leftRadius +
				rightWheel.getTachoCount() * rightRadius) *
				Math.PI / 360.0;
	}
	
	public double getHeading() {
		return (leftWheel.getTachoCount() * leftRadius -
				rightWheel.getTachoCount() * rightRadius) / width;
	}
	
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftWheel.getTachoCount();
		rightTacho = rightWheel.getTachoCount();
		
		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
	}
	
	// mutators
	public void setForwardSpeed(double speed) {
		forwardSpeed = speed;
		rotationSpeed = 0;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setRotationSpeed(double speed) {
		forwardSpeed = 0;
		rotationSpeed = speed;
		setSpeeds(forwardSpeed, rotationSpeed);
	}
	
	public void setSpeeds(double forwardSpeed, double rotationalSpeed) {
		double leftSpeed, rightSpeed;

		this.forwardSpeed = forwardSpeed;
		this.rotationSpeed = rotationalSpeed;

		leftSpeed = (forwardSpeed + rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (leftRadius * Math.PI);
		rightSpeed = (forwardSpeed - rotationalSpeed * width * Math.PI / 360.0) *
				180.0 / (rightRadius * Math.PI);

		// set motor directions
		if (leftSpeed > 0.0)
			leftWheel.forward();
		else {
			leftWheel.backward();
			leftSpeed = -leftSpeed;
		}
		
		if (rightSpeed > 0.0)
			rightWheel.forward();
		else {
			rightWheel.backward();
			rightSpeed = -rightSpeed;
		}
		
		// set motor speeds
		if (leftSpeed > 900.0)
			leftWheel.setSpeed(900);
		else
			leftWheel.setSpeed((int)leftSpeed);
		
		if (rightSpeed > 900.0)
			rightWheel.setSpeed(900);
		else
			rightWheel.setSpeed((int)rightSpeed);
	}
	
	public void stop()
	{
		leftWheel.stop(true);
		rightWheel.stop(false);
	}
}
