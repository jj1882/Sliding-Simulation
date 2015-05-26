/**
 * NcdRebinding.java
 *
 * @author Created by Omnicore CodeGuide
 */


public class NcdRebinding extends Thing
{
	public static final double getProbabilityDensity(Microtubule mt, Motorheadpair m)
	{
		
		double p=0;
		Point dist;
				
		dist=Point.DifferenceVector(mt.position, m.HingePos);
		
		if(Ncd.FindMTsubunit(mt,m)==NOTCLOSE)
			return p;
		   
		if(Math.abs(dist.Y)<Ncd.ZeroLength)
			p=Ncd.ZeroLength/(10-100)*dist.Y+100;	// 100 1/s when distance=0, 10 1/s when distance = Motorlength
		
		// Thing.talk("Rebindingrate: "+p);
		
		return(Math.log(.5)*p);
	}
}

