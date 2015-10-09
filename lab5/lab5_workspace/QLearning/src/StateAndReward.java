public class StateAndReward {
	
	static int ANGLE_RESOLUTION = 7;
	static int VX_RESOLUTION = 2;
	static int VY_RESOLUTION = 4;
	
	/* State discretization function for the angle controller */
	public static String getStateAngle(double angle, double vx, double vy) {
		String state = "A:" + Integer.toString(angleDiscreetState(angle));
		
		return state;
	}

	/* Reward function for the angle controller */
	public static double getRewardAngle(double angle, double vx, double vy) {
		double reward = 40 - 2 * Math.abs(angle);
		
		if(reward < 0) reward = 0;
		
		return reward;
	}

	/* State discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {
		int discreetAngle = angleDiscreetState(angle);
		int discreetVelocityX = vxDiscreetState(vx);
		int discreetVelocityY = vyDiscreetState(vy);
		
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
	
	public static int angleDiscreetState(double angle) {
		return discretize(angle, ANGLE_RESOLUTION, -Math.PI, Math.PI);
	}
	
	public static int vxDiscreetState(double vx) {
		return discretize(vx, VX_RESOLUTION, 0, 8);
	}
	
	public static int vyDiscreetState(double vy) {
		return discretize(vy, VY_RESOLUTION, 0, 8);
	}

	// ///////////////////////////////////////////////////////////
	// discretize() performs a uniform discretization of the
	// value parameter.
	// It returns an integer between 0 and nrValues-1.
	// The min and max parameters are used to specify the interval
	// for the discretization.
	// If the value is lower than min, 0 is returned.
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
