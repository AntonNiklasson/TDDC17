public class StateAndReward {
	
	static int ANGLE_RESOLUTION = 7;
	static double ANGLE_MIN = -1;
	static double ANGLE_MAX = 1;
	
	static int VX_RESOLUTION = 3;
	static int VX_GOAL = 1;
	static double VX_MIN = -3;
	static double VX_MAX = 3;
	
	static int VY_RESOLUTION = 11;
	static int VY_GOAL = 6;
	static double VY_MIN = -6;
	static double VY_MAX = 6;
	
	/* State discretization function for the angle controller */
	public static String getStateAngle(double angle, double vx, double vy) {
		String state = "A:" + Integer.toString(angleDiscreetState(angle));
		
		return state;
	}

	/* Reward function for the angle controller */
	public static double getRewardAngle(double angle, double vx, double vy) {
		int states_from_goal_state = Math.abs(angleDiscreetState(angle) - ((ANGLE_RESOLUTION - 1) / 2));
		double reward = 0;
		
//		System.out.println("Angle states from goal: " + states_from_goal_state);
		
		switch(states_from_goal_state) {
			case 0:
				reward = 20;
				break;
			case 1:
				reward = 5;
				break;
			case 2:
				reward = 2;
				break;
			default: break;
		}
		
		return reward;
	}

	/* State discretization function for the full hover controller */
	public static String getStateHover(double angle, double vx, double vy) {
		String state = "A:" + angleDiscreetState(angle) + "VX:" + vxDiscreetState(vx) + "VY:" + vyDiscreetState(vy);
		
		return state;
	}

	/* Reward function for the full hover controller */
	public static double getRewardHover(double angle, double vx, double vy) {
		double angleReward = getRewardAngle(angle, vx, vy);
		double vxReward = getVXReward(vx);
		double vyReward = getVYReward(vy);
		
		return angleReward + vxReward + vyReward;
	}
	
	public static double getVXReward(double vx) {
		double reward = 0;
		int states_from_goal = Math.abs(vxDiscreetState(vx) - VX_GOAL);
		
//		System.out.println("VX states from goal: " + states_from_goal);
//		System.out.println("VX: " + vx);
		
		switch(states_from_goal) {
			case 0:
				reward = 10;
				break;
			case 1:
				reward = 0;
				break;
			default: break;
		}
		
		return reward;
	}
	
	public static double getVYReward(double vy) {
		double reward = 0;
		int states_from_goal = Math.abs(vyDiscreetState(vy) - VY_GOAL); 
		
//		System.out.println("VY states from goal: " + states_from_goal);
//		System.out.println("VY = " + vy);
		
		switch(states_from_goal) {
			case 0:
				reward = 10;
				break;
			case 1:
				reward = 5;
				break;
			case 2:
				reward = 3;
				break;
			case 3:
				reward = 1;
			default: break;
		}
		
		return reward;
	}
	
	public static int angleDiscreetState(double angle) {
		return discretize(angle, ANGLE_RESOLUTION, ANGLE_MIN, ANGLE_MAX);
	}
	
	public static int vxDiscreetState(double vx) {
		return discretize(vx, VX_RESOLUTION, VX_MIN, VX_MAX);
	}
	
	public static int vyDiscreetState(double vy) {
		return discretize(vy, VY_RESOLUTION, VY_MIN, VY_MAX);
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
