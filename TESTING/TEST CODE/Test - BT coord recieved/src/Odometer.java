import lejos.nxt.*;

/*
 * Odometer.java
 */

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;
	
	private double leftRPM = 0, rightRPM = 0;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	// default constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		// Using the tachometers of the left and right motors, we are able to compute the distance using the formulas
		// given in class.
		
		while (true) {
			updateStart = System.currentTimeMillis();
			double dLeftRPM, dRightRPM, dTheta, dPos;
			dLeftRPM = (leftMotor.getTachoCount())*Math.PI/180 - leftRPM;		
			dRightRPM = (rightMotor.getTachoCount())*Math.PI/180 - rightRPM;
			dTheta = (dLeftRPM*Lab3p1.leftRadius - dRightRPM*Lab3p1.rightRadius)/Lab3p1.width;
			dPos = (dLeftRPM*Lab3p1.leftRadius + dRightRPM*Lab3p1.rightRadius)/2;
			leftRPM = leftRPM + dLeftRPM;
			rightRPM = rightRPM + dRightRPM;
			
			synchronized (lock) {
				x = x+ Math.sin(theta + dTheta/2.0)*dPos;
				y = y+ Math.cos(theta + dTheta/2.0)*dPos;
				theta = theta + dTheta;
				if (theta>2*Math.PI){							//to get angle between 0 and 360 degrees
					theta = theta - (2*Math.PI);
				}
				if (theta<0){
					theta = theta + (2*Math.PI);
				}
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = (theta/Math.PI)*180;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2] * Math.PI / 180.0;
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}