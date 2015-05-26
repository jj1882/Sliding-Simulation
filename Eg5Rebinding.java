/**
 * Eg5Rebinding.java
 *
 * @author Created by Omnicore CodeGuide
 */


public class Eg5Rebinding extends Thing
{
	public static final double getProbabilityDensity(Microtubule mt, Motorheadpair m)
	{
		
		double p=0;
		Point dist;
				
		dist=Point.DifferenceVector(mt.position, m.HingePos);
		
		if(Eg5.FindMTsubunit(mt,m)==NOTCLOSE)
			return p;
		   
		if(Math.abs(dist.Y)<Eg5.ZeroLength)
			p=Eg5.ZeroLength/(10-100)*dist.Y+100;	// 100 1/s when distance=0, 10 1/s when distance = Motorlength
		
		// Thing.talk("Rebindingrate: "+p);
		
		return(Math.log(.5)*p);
	}
}


// Test probability here
		
		// If no head is bound try binding. If one head is bound I'll just try stepping (simplification)
		// If no head is bound the first one to bind will always be A
//		if(Top.HeadStateA==0 && Top.HeadStateB==0)
//		{
//			//bind if close to MT
//			bindingsite=FindMTsubunit(MT_Top,Top);
//
//			if(bindingsite >= 0)
//			{
//				for(i=0;i<MT_Top.BindingSites;i++) // Motors will crosswalk and not keep track of their track
//				{
//					if(MT_Top.occupied[bindingsite] [i] == 0) // Bind!
//					{
//						MT_Top.occupied[bindingsite] [i] = 1;
//						Top.HeadStateA=bindingsite;
//						Top.TimeSinceBoundA=0;
//						// Readjust position after binding
//						Top.HingePos=Microtubule.MTpos2realPos(MT_Top, bindingsite); 	// This puts in artificial strain when one head is unbound.
//																						// Thus there has to be relaxation in the force fxn
//
//						break; // will this get me out of the for loop? Otherwise all parallel sites will be blocked
//					}
//				}
//			}
//
//		}
