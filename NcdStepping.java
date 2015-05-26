/**
 * NcdForceVelocityFxn.java
 *
 * @author Created by Omnicore CodeGuide
 */


public class NcdStepping
{
	public static double StallForce=7;       // pN
	public static double RipOffForce=15;     // pN
	
	
	public static final double getProbabilityDensity(double inputForce)
	//Probability for unit time, for 1 second
	{
		double speed;
		// In 1 second the motor makes AverageSpeed/8nm Steps. Thus the probability should be
		// the same, taking into account the delaytime. Maybe multiplied bt ln(1/2);
		
		// High load ca. 1/8 of maxspeed, thus y=(-(7/8)/Stallforce)*inputforce+1
		
		if(inputForce<StallForce)
		{
			speed=((-(7/8)/StallForce)*inputForce+1)*Ncd.AverageSpeed;
			
			return(Math.log(.5)*speed/Microtubule.SubunitLength);
		}
		else
		{
			if(inputForce<RipOffForce)
				return(0);  // Motor sticks
			else
				return(-1); // Motor lets go
		}
	}
	
	public static final double getMaxForceMotorCanProduce() // Final cannot be overwritten
	{
		return(StallForce);
	}
	
//	public static final double getAvgSpeed()
//	{
//		return(AverageSpeed);
//	}
	

}

