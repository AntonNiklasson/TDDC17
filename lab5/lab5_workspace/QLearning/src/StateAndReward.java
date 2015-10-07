public class StateAndReward {

	static int number_anglestates = 19;
	static int goal_anglestate = 10;
	
	/* State discretization function for the angle controller */
	public static String getStateAngle(double angle, double vx, double vy) {
		String state = "A:" + Integer.toString(discretize(angle, number_anglestates, -Math.PI / 2, Math.PI / 2));
		
		return state;
	}

	/* Reward function for the angle controller */
	public static double getRewardAngle(double angle, double vx, double vy) {
		return (20 / Math.PI) * (Math.PI - Math.abs(angle));
	}

	/* State discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {
		int discreetAngle = discretize(angle, number_anglestates, -Math.PI / 2, Math.PI / 2);
		int discreetVelocityX = discretize(vx, 5, 0, 20);
		int discreetVelocityY = discretize(vy, 10, 0, 20);
		
		String state = "A:" + Integer.toString(discreetAngle) + "VX:" + Integer.toString(discreetVelocityX) + "VY:" + Integer.toString(discreetVelocityY);
		
		return state;
	}

	/* Reward function for the full hover controller */
	public static double getRewardHover(double angle, double vx, double vy) {
		double angleReward = getRewardAngle(angle, vx, vy);
		double velocityReward = 20 - Math.abs(vx) - Math.abs(vy);
		
		if(velocityReward < 0) velocityReward = 0;
		
		return angleReward + velocityReward;
	}

	// ///////////////////////////////////////////////////////////
	// discretize() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is retu
	/* TODO: IMPLEMENT THIS FUNCTION */
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 1 and nrValues-2 is returned.
	//
	// Use discretize2() if you want a discretization method that does
	// not handle values lower than min and higher than max.
	// ///////////////////////////////////////////////////////////
	public static int discretize(double value, int nrValues, double min,
			double max) {
		if (nrValues < 2) {
			return 0;
		}

		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * (nrValues - 2)) + 1;
	}

	// ///////////////////////////////////////////////////////////
	// discretize2() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned
	// If the value is higher than min, nrValues-1 is returned
	// otherwise a value between 0 and nrValues-1 is returned.
	// ///////////////////////////////////////////////////////////
	public static int discretize2(double value, int nrValues, double min,
			double max) {
		double diff = max - min;

		if (value < min) {
			return 0;
		}
		if (value > max) {
			return nrValues - 1;
		}

		double tempValue = value - min;
		double ratio = tempValue / diff;

		return (int) (ratio * nrValues);
	}

}
