/**
 * Ncd.java
 *
 * @author Created by Omnicore CodeGuide
 */


/**
 * Ncd.java
 *
 * @author Created by Omnicore CodeGuide
 */
import java.awt.*;

public class Ncd extends Motor
{
	
	public Microtubule MT_Top;
	public Microtubule MT_Bottom;
	
	public Motorheadpair Top=new Motorheadpair();
	public Motorheadpair NcdTail=new Motorheadpair();
	
	public static double ZeroLength=60; // Ron's Guess, similar to Sharp, ... , Scholey. JCB 1999
	//public static double MaxForce=7; // Maximum Force the Motor can produce
	public static double SpringConst=1; // For now, later relate to Stallforce
	public static double DelayTime=10;  // min. 10 ms before motor can move again
	public static double AverageSpeed=1000;   // nm/s, w/o load, 10x faster than Eg5 since it does unbind immediately
	
	private static int Ncdnumber=0;
	
	//		this.SpringConst=4000; 	// pN; Joe Howard Page 148, Young's Mod=2 GPa, A=2nm2
  	 								// This must be too much
		
	// Constructor: pass the point where the Motor originates, and the two MTs.
	// Let it bind if close enough
	
	
	public Ncd(Point p0, Microtubule M1, Microtubule M2)
	{
		
		
		double prop=Math.random();
		
		if(prop>0.5)
		{
			MT_Top=M1;
			MT_Bottom=M2;
		}
		else
		{
			MT_Top=M2;
			MT_Bottom=M1;
		}
		
		Top.Directionality=-1; NcdTail.Directionality=0; // Ncd is minus directed, Tail does not move
		Top.Speed=AverageSpeed; NcdTail.Speed=0; // 100 nm per s
		Top.HeadStateA=0; Top.HeadStateB=0;		// The thing is unbound initially
		NcdTail.HeadStateA=0; NcdTail.HeadStateB=0;
		
		Top.HingePos=new Point(p0); NcdTail.HingePos=new Point(p0); // Molecule is straight initially BS, include length
		
		if(Math.random()>0.5) // Unnecessary
		{
			Top.HingePos.Y+=ZeroLength/2;  		// Put the motor center in the middle Spot
			NcdTail.HingePos.Y-=ZeroLength/2;	// THIS HAS TO BE RANDOMIZED FOR NCD!!!!!!!! Randomized in bindoutofsolution!
		}
		else
		{
			Top.HingePos.Y-=ZeroLength/2;  		// Put the motor center in the middle Spot
			NcdTail.HingePos.Y+=ZeroLength/2;	// THIS HAS TO BE RANDOMIZED FOR NCD!!!!!!!! Randomized in bindoutofsolution!
		}
		
	}
	
	public static int getmotornumber()
	{
		// talk("yeah! Ncdnumber="+Ncdnumber);
		return (Ncdnumber);
	}
	
	public void motornumberplus1()
	{
		Ncdnumber++;
	}
	
	public void motornumberminus1()
	{
		Ncdnumber--;
	}
	
	public Point GetForce()
	// Make unit vector of force, Multiply by tension, then return
	
	{
		GetTension();
				
		if(MT_Top.position.Y>MT_Bottom.position.Y) // upside up
			SpringForce=Point.DifferenceVector(Top.HingePos,NcdTail.HingePos).MakeUnitVector();
		else
			SpringForce=Point.DifferenceVector(NcdTail.HingePos,Top.HingePos).MakeUnitVector();
		
		SpringForce.X=SpringForce.X*Tension;
		
		SpringForce.Y=SpringForce.Y*Tension;
		
		return (SpringForce);
	
	}
	
	
	public static int FindMTsubunit(Microtubule mt, Motorheadpair mhp)
	// Return Subunit of mt closest to the motor if within ZeroLength, otherwise -1 if nothing is close
	{
		int subunit;
		
		if(Math.abs(mhp.HingePos.Y-mt.position.Y)>=Ncd.ZeroLength)  // if too far away in Y
			return(NOTCLOSE);
		
		// To find the subunit with the minimum distance:
		// Motor.X=8nm*subunit*directionality+Mt.X
		
		subunit=(int) Math.round( (mhp.HingePos.X-mt.position.X)/ (mt.SubunitLength*mt.directionality) );
		
		if(subunit<=mt.length)
			return (subunit);
		else
			return (NOTCLOSE);
	}
	
	public double GetTension()
	{
		//release artificial strain
		//if both heads are unbound the thing will be removed anyway
		//might want to compute tension in force method
	
		if(!Top.bound())
		{
			Top.HingePos.X=NcdTail.HingePos.X;
			if(MT_Top.position.Y>MT_Bottom.position.Y) // upside up
				Top.HingePos.Y=NcdTail.HingePos.Y+ZeroLength;
			else
				Top.HingePos.Y=NcdTail.HingePos.Y-ZeroLength;
			Tension=0;
			return (Tension);
		}
		
		if(!NcdTail.bound())
		{
			NcdTail.HingePos.X=Top.HingePos.X;
			if(MT_Top.position.Y>MT_Bottom.position.Y) // upside up
				NcdTail.HingePos.Y=Top.HingePos.Y-ZeroLength;
			else
				NcdTail.HingePos.Y=Top.HingePos.Y+ZeroLength;
			Tension=0;
			return (Tension);
		}
		
		// Both heads are bound!
		
		// talk("Ncd Tension!");
		
		
		Top.reposition(MT_Top);
		NcdTail.reposition(MT_Bottom);
		
		
		Tension=SpringConst*((Top.HingePos.DistanceTo(NcdTail.HingePos)-ZeroLength)/ZeroLength); // Strain Force in pN
		return (Tension);
	}
	
	public boolean bound()
	// true if at last one motorhead is bound
	{
		if(Top.bound() || NcdTail.bound())
			return true;
		
		return false;
	}
	
	public boolean BindOutofSolution(int toporbottom, int n) // Bind to TOP or BOTTOM mt at position n
	{
		boolean a; // Returnvalue, true if binding was successful
		
		// double prob;
		
		//prob=Math.random();
		if(toporbottom==TOP)
			a=Top.bindHeadA(MT_Top,n);
		else
			a=NcdTail.bindHeadA(MT_Bottom,n);
		
		// a=Top.bindHeadA(MT_Top,n);
			
		// a=NcdTail.bindHeadA(MT_Bottom,n);
		
		
		return(a);
	}
		
	
	
	public double Step(double dt)
	{
		// If unbound: bind
		// If bound: try to step
		int bindingposition;	// kth site along MT, 0...1000
		int bindingsite;		// nth parallel protofilament, 0...2
		int stepTop = NOSTEP;
		int stepBottom = NOSTEP;
		
		// talk("Motors in solution"+Sim2D.NcdinSolution+" out of "+Sim2D.MaxNcdinSolution);
		
		// Unbinding
		// Don't want to rebind the same head that just unbound, though
	
		if(Top.bound())
		// check for unbinding
		{
			Top.AddedUnbindingProbablity+=NcdUnbinding.getProbabilityDensityHead(GetTension())*dt/UNITTIME;
			
			//if(Top.TimeSinceBoundA>0)
			if(Top.AddedUnbindingProbablity <= Top.LnRhoUnbind) // Unbind!
			{
				talk("Unbind Top");
				
				bindingposition=Math.max(Top.HeadStateA,Top.HeadStateB);
				
				MT_Top.free(bindingposition, MT_Top.getBiggestOccupiedSite(bindingposition));  // free binding site
				
				Top.unbindBoth(); // unbind motorhead
			}
		}
		else
		// check for rebinding
		{
			Top.AddedRebindingProbability+=NcdRebinding.getProbabilityDensity(MT_Top, Top)*dt/UNITTIME;   //Make this less?
			
			if(Top.AddedRebindingProbability<=Top.LnRhoRebind) // was >= before
			{
				//bind if close to MT
				bindingposition=FindMTsubunit(MT_Top,Top);

				if(bindingposition != NOTCLOSE)
				{
					bindingsite=MT_Top.getFreeSite(bindingposition);
				
					if(bindingsite != NOTFREE && bindingsite != AT_END)
					{
						MT_Top.occupy(bindingposition,bindingsite);
						Top.HeadStateA=bindingposition;
				
						MT_Top.occupy(Top.HeadStateA, bindingsite);

						Top.reposition(MT_Top,Top.HeadStateA);
						
						Top.TimeSinceBoundA=0;
						
						Top.resetLnRhoStep(); // Messed that up below
					}
					

				}
		
			}
		}
		
		
		// Same for bottom
		
		if(NcdTail.bound())
		// check for unbinding
		{
			NcdTail.AddedUnbindingProbablity+=NcdUnbinding.getProbabilityDensity(GetTension())*dt/UNITTIME;
			
			if(NcdTail.AddedUnbindingProbablity <= NcdTail.LnRhoUnbind) // Unbind!
			{
				talk("Unbind NcdTail");
				
				bindingposition=NcdTail.HeadStateA;
				
				MT_Bottom.free(bindingposition, MT_Bottom.getBiggestOccupiedSite(bindingposition));  // free binding site
				
				NcdTail.unbindBoth(); // unbind motorhead
			}
		}
		else
		// check for rebinding
		{
			NcdTail.AddedRebindingProbability+=NcdRebinding.getProbabilityDensity(MT_Bottom, NcdTail)*dt/UNITTIME;
			
			if(NcdTail.AddedRebindingProbability<=NcdTail.LnRhoRebind)   // was >= before
			{
				//bind if close to MT
				bindingposition=FindMTsubunit(MT_Bottom,NcdTail);

				if(bindingposition != NOTCLOSE)
				{
					bindingsite=MT_Bottom.getFreeSite(bindingposition);
				
					if(bindingsite != NOTFREE && bindingsite != AT_END)
					{
						MT_Bottom.occupy(bindingposition,bindingsite);
						NcdTail.HeadStateA=bindingposition;
				
						MT_Bottom.occupy(NcdTail.HeadStateA, bindingsite);

						NcdTail.reposition(MT_Bottom,NcdTail.HeadStateA);
						
						NcdTail.TimeSinceBoundA=0;
						
						
					}
					

				}
		
			}
		}
		
		
		if(!bound())
		{
			//remove motors that are unbound
			return(0);
		}
			
		
		// Now say if you step
		// First ask both heads, then step simultaneously, to avoid artefacts
		
		stepTop=NOSTEP;
			
		if(Top.bound())  // If Top bound
		{
			// talk("Trying to step Top");
			
			if(Top.HeadStateA!=UNBOUND) //Head A of Top Pair is bound
			{
				Top.TimeSinceBoundA+=dt;
								
				Top.AddedSteppingProbability+=NcdStepping.getProbabilityDensity(GetTension()) *dt/UNITTIME; //Sum Probabilities
				
				// if(Top.TimeSinceBound()>=DelayTime)
				if(Top.AddedSteppingProbability<=Top.LnRhoStep)   // was >= before
					stepTop=STEPA;
			}
//			else  // Head B of Top Pair is bound
//			{
//				Top.TimeSinceBoundB+=dt;
//
//				Top.AddedSteppingProbability+=NcdStepping.getProbabilityDensity(GetTension())*dt/UNITTIME; //Sum Probabilities
//
//				if(Top.AddedSteppingProbability<=Top.LnRhoStep)  // Was >= before
//					stepTop=STEPB;
//			}
		}
		
		
		
		// Step
		switch(stepTop)
		{
			case NOSTEP:
				
				break;
			
			case STEPA:
				
				if(Top.TimeSinceBoundA>=DelayTime)	// Keeps minimum time & prevents stepping imediately after rebinding
				{
					//See if position is free on MT;
					bindingsite=MT_Top.getFreeSite(Top.HeadStateA+1); //Plus-end directed motor
				
					if(bindingsite==NOTFREE)
						break;			// Don't Step. just wait
					
					if(bindingsite==AT_END)
						break;			// Figure out later
				
					//really Step: Clear old position; occupy new position; reset random number
					
					talk("STEP Ncd!");
					
					MT_Top.free(Top.HeadStateA, MT_Top.getBiggestOccupiedSite(Top.HeadStateA));
	
					Top.HeadStateA=Top.HeadStateA-1;	//Minus-end directed motor, forward
					
					// Top.unbindHeadA();				//Switch Heads
				
					MT_Top.occupy(Top.HeadStateA, bindingsite);

					// if(MT_Top.position.Y>MT_Bottom.position.Y) // upside up
					Top.reposition(MT_Top,Top.HeadStateA);
					
						
				
					//Maybe take care of timesincebound; if one wants to keep minimum binding time
								
					//Top.resetLnRhoStep();	// New Prob. for next Step
					Top.LnRhoStep=-1000; //Will never step again
					
					//if(Top.TimeSinceBoundA>5*DelayTime)
					//	Top.AddedUnbindingProbablity=-1000; // That should let it take only one step

				}
				
				break;
//
//			case STEPB: //inverse of above
//
//				if(Top.TimeSinceBoundB>=DelayTime)	// Keeps minimum time & prevents stepping imediately after rebinding
//				{
//					//See if position is free on MT;
//					bindingsite=MT_Top.getFreeSite(Top.HeadStateB+1); //Plus-end directed motor
//
//					if(bindingsite==NOTFREE)
//						break;			// Don't Step. just wait
//
//					if(bindingsite==AT_END)
//						break;			// Figure out later
//
//					//really Step: Clear old position; occupy new position; reset random number
//
//					talk("STEP!");
//
//					MT_Top.free(Top.HeadStateB, MT_Top.getBiggestOccupiedSite(Top.HeadStateB));
//
//					Top.HeadStateA=Top.HeadStateB+1;	//Plus-end directed motor, forward
//
//					Top.unbindHeadB();				//Switch Heads
//
//					MT_Top.occupy(Top.HeadStateA, bindingsite);
//
//					Top.reposition(MT_Top,Top.HeadStateA);
//
//					//Maybe take care of timesincebound; if one wants to keep minimum binding time
//
//					Top.resetLnRhoStep();	// New Prob. for next Step
//
//				}
//
//				break;
		}
		
		
		
		
		
		return (0);
		
		
	}
		
	public void drawYourself(Graphics G, double scalefactor, double [] offset)
	{

		int greeness=(int)(Tension*255*100/NcdStepping.StallForce);
		
		if(greeness>255)
			greeness=255;
		
		int xleft=(int)((Top.HingePos.X-offset[0])*scalefactor);
		int xright=(int)((NcdTail.HingePos.X-offset[0])*scalefactor);
		
		int yleft=(int)((Top.HingePos.Y-offset[1])*scalefactor);
		int yright=(int)((NcdTail.HingePos.Y-offset[1])*scalefactor);
				
		G.setColor(new Color(200,greeness,255));
		G.drawLine(xleft,yleft,xright,yright);
		
		// G.drawLine(xright-2,yright,xright+2,yright); //Quick and dirty way to draw tail for debugging
		
		// talk("Ncd Top.Directionality="+Top.Directionality);
		// talk("NcdTail.Directionality="+NcdTail.Directionality);
		
		Top.drawyourself(G,scalefactor,offset,TOP,NcdTail.HingePos);
		NcdTail.drawyourself(G,scalefactor,offset,BOTTOM,Top.HingePos);
	
	}
		
	
}

