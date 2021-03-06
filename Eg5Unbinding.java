/**
 * Eg5Unbinding.java
 *
 * @author Created by Omnicore CodeGuide
 */


public class Eg5Unbinding
{
	// Relate Unbinding rate with speed to get run length
	// 0 Load: 		8 steps, 12.5 steps/s : 1.56 1/s Unbinding rate
	// Full load:	1 step ,  2.5 steps/s : 2.5  1/s Unbinding rate
	// Thus: y=(0.94/Stallforce)*Force+1.56 1/s
	
	
	public static final double getProbabilityDensity(double inputForce)
	{
		double unbrate=0;
		
		unbrate=(0.94/Eg5Stepping.StallForce)*inputForce+1.56; // 100* added for debugging
		
		// Thing.talk("Unbindingrate: "+unbrate);
		
		return (Math.log(.5)*unbrate);
	}
}

