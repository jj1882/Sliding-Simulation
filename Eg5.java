/**
 * Eg5.java
 *
 * @author Created by Omnicore CodeGuide
 */
import java.awt.*;

public class Eg5 extends Motor
{
	
	public Microtubule MT_Top;
	public Microtubule MT_Bottom;
	
	public Motorheadpair Top=new Motorheadpair();
	public Motorheadpair Bottom=new Motorheadpair();
	
	public static double ZeroLength=60; // Ron's Guess, similar to Sharp, ... , Scholey. JCB 1999
	public static double MaxForce=7; // Maximum Force the Motor can produce
	public static double SpringConst=1; // For now, later relate to Stallforce
	public static double DelayTime=10;  // min. 10 ms before motor can move again
	
	private static int Eg5number=0;
	
	//		this.SpringConst=4000; 	// pN; Joe Howard Page 148, Young's Mod=2 GPa, A=2nm2
  	 								// This must be too much
		
	// Constructor: pass the point where the Motor originates, and the two MTs.
	// Let it bind if close enough
	
	
	public Eg5(Point p0, Microtubule M1, Microtubule M2)
	{
		MT_Top=M1; // Is this really passing the Pointer?
		MT_Bottom=M2;
		
		Top.Directionality=1; Bottom.Directionality=1; // Eg5 in plus directed
		Top.Speed=100;Bottom.Speed=100; // 100 nm per s
		Top.HeadStateA=0; Top.HeadStateB=0;		// The thing is unbound initially
		Bottom.HeadStateA=0; Bottom.HeadStateB=0;
		
		Top.HingePos=new Point(p0); Bottom.HingePos=new Point(p0); // Molecule is straight initially BS, include length
		Top.HingePos.Y+=ZeroLength/2;  		// Put the motor center in the middle Spot
		Bottom.HingePos.Y-=ZeroLength/2;	// THIS HAS TO BE RANDOMIZED FOR NCD!!!!!!!!
		
	}
	
	public static int getmotornumber()
	{
		// talk("yeah! Eg5number="+Eg5number);
		return (Eg5number);
	}
	
	public void motornumberplus1()
	{
		Eg5number++;
	}
	
	public void motornumberminus1()
	{
		Eg5number--;
	}
	
	public Point GetForce()
	// Make unit vector of force, Multiply by tension, then return
	
	{
		GetTension();
				
		SpringForce=Point.DifferenceVector(Top.HingePos,Bottom.HingePos).MakeUnitVector();
		
		SpringForce.X=SpringForce.X*Tension;
		
		SpringForce.Y=SpringForce.Y*Tension;
		
		return (SpringForce);
	
	}
	
	
	public static int FindMTsubunit(Microtubule mt, Motorheadpair mhp)
	// Return Subunit of mt closest to the motor if within ZeroLength, otherwise -1 if nothing is close
	{
		int subunit;
		
		if(Math.abs(mhp.HingePos.Y-mt.position.Y)>=Eg5.ZeroLength)  // if too far away in Y
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
			Top.HingePos.X=Bottom.HingePos.X;
			Top.HingePos.Y=Bottom.HingePos.Y+ZeroLength;
			
			Tension=0;
			return (Tension);
		}
		
		if(!Bottom.bound())
		{
			Bottom.HingePos.X=Top.HingePos.X;
			Bottom.HingePos.Y=Top.HingePos.Y-ZeroLength;
			Tension=0;
			return (Tension);
		}
		
		// Reposition bound heads in case the MTs have moved
		Top.reposition(MT_Top);
		Bottom.reposition(MT_Bottom);
				
		Tension=SpringConst*((Top.HingePos.DistanceTo(Bottom.HingePos)-ZeroLength)/ZeroLength); // Strain Force in pN
		return (Tension);
	}
	
	public boolean bound()
	// true if at last one motorhead is bound
	{
		if(Top.bound() || Bottom.bound())
			return true;
		
		return false;
	}
	
	public boolean BindOutofSolution(int toporbottom, int n) // Bind to TOP or BOTTOM mt at position n
	{
		boolean a; // Returnvalue, true if binding was successful
		
		if(toporbottom==TOP)
			a=Top.bindHeadA(MT_Top,n);
		else
			a=Bottom.bindHeadA(MT_Bottom,n);
		
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
		
		// talk("Motors in solution"+Sim2D.Eg5inSolution+" out of "+Sim2D.MaxEg5inSolution);
		
		// Unbinding
		// Don't want to rebind the same head that just unbound, though
	
		if(Top.bound())
		// check for unbinding
		{
			Top.AddedUnbindingProbablity+=Eg5Unbinding.getProbabilityDensity(GetTension())*dt/UNITTIME;
			
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
			Top.AddedRebindingProbability+=Eg5Rebinding.getProbabilityDensity(MT_Top, Top)*dt/UNITTIME;
			
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
					}
					

				}
		
			}
		}
		
		
		// Same for bottom
		
		if(Bottom.bound())
		// check for unbinding
		{
			Bottom.AddedUnbindingProbablity+=Eg5Unbinding.getProbabilityDensity(GetTension())*dt/UNITTIME;
			
			if(Bottom.AddedUnbindingProbablity <= Bottom.LnRhoUnbind) // Unbind!
			{
				talk("Unbind Bottom");
				
				bindingposition=Math.max(Bottom.HeadStateA,Bottom.HeadStateB);
				
				MT_Bottom.free(bindingposition, MT_Bottom.getBiggestOccupiedSite(bindingposition));  // free binding site
				
				Bottom.unbindBoth(); // unbind motorhead
			}
		}
		else
		// check for rebinding
		{
			Bottom.AddedRebindingProbability+=Eg5Rebinding.getProbabilityDensity(MT_Bottom, Bottom)*dt/UNITTIME;
			
			if(Bottom.AddedRebindingProbability<=Bottom.LnRhoRebind)   // was >= before
			{
				//bind if close to MT
				bindingposition=FindMTsubunit(MT_Bottom,Bottom);

				if(bindingposition != NOTCLOSE)
				{
					bindingsite=MT_Bottom.getFreeSite(bindingposition);
				
					if(bindingsite != NOTFREE && bindingsite != AT_END)
					{
						MT_Bottom.occupy(bindingposition,bindingsite);
						Bottom.HeadStateA=bindingposition;
				
						MT_Bottom.occupy(Bottom.HeadStateA, bindingsite);

						Bottom.reposition(MT_Bottom,Bottom.HeadStateA);
						
						Bottom.TimeSinceBoundA=0;
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
		
		stepTop=NOSTEP; stepBottom=NOSTEP;
			
		if(Top.bound())  // If Top bound
		{
			// talk("Trying to step Top");
			
			if(Top.HeadStateA!=UNBOUND) //Head A of Top Pair is bound
			{
				Top.TimeSinceBoundA+=dt;
								
				Top.AddedSteppingProbability+=Eg5Stepping.getProbabilityDensity(GetTension()) *dt/UNITTIME; //Sum Probabilities
				
				if(Top.AddedSteppingProbability<=Top.LnRhoStep)   // was >= before
					stepTop=STEPA;
			}
			else  // Head B of Top Pair is bound
			{
				Top.TimeSinceBoundB+=dt;
								
				Top.AddedSteppingProbability+=Eg5Stepping.getProbabilityDensity(GetTension())*dt/UNITTIME; //Sum Probabilities
				
				if(Top.AddedSteppingProbability<=Top.LnRhoStep)  // Was >= before
					stepTop=STEPB;
			}
		}
		
		if(Bottom.bound())  // If Bottom bound, same above
		{
			// talk("Trying to step Bottom");
			
			if(Bottom.HeadStateA!=UNBOUND) //Head A of Bottom Pair is bound
			{
				Bottom.TimeSinceBoundA+=dt;
								
				Bottom.AddedSteppingProbability+=Eg5Stepping.getProbabilityDensity(GetTension()) *dt/UNITTIME; //Sum Probabilities
				
				if(Bottom.AddedSteppingProbability<=Bottom.LnRhoStep) // Was >= before
					stepBottom=STEPA;
			}
			else  // Head B of Bottom Pair is bound
			{
				Bottom.TimeSinceBoundB+=dt;
								
				Bottom.AddedSteppingProbability+=Eg5Stepping.getProbabilityDensity(GetTension())*dt/UNITTIME; //Sum Probabilities
				
				if(Bottom.AddedSteppingProbability<=Bottom.LnRhoStep) // Was >= before
					stepBottom=STEPB;
			}
			
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
					
					talk("STEP!");
					
					MT_Top.free(Top.HeadStateA, MT_Top.getBiggestOccupiedSite(Top.HeadStateA));
	
					Top.HeadStateB=Top.HeadStateA+1;	//Plus-end directed motor, forward
					
					Top.unbindHeadA();				//Switch Heads
				
					MT_Top.occupy(Top.HeadStateB, bindingsite);

					Top.reposition(MT_Top,Top.HeadStateB);
				
					//Maybe take care of timesincebound; if one wants to keep minimum binding time
								
					Top.resetLnRhoStep();	// New Prob. for next Step
				
				}
				
				break;
				
			case STEPB: //inverse of above
				
				if(Top.TimeSinceBoundB>=DelayTime)	// Keeps minimum time & prevents stepping imediately after rebinding
				{
					//See if position is free on MT;
					bindingsite=MT_Top.getFreeSite(Top.HeadStateB+1); //Plus-end directed motor
				
					if(bindingsite==NOTFREE)
						break;			// Don't Step. just wait
					
					if(bindingsite==AT_END)
						break;			// Figure out later
				
					//really Step: Clear old position; occupy new position; reset random number
					
					talk("STEP!");
					
					MT_Top.free(Top.HeadStateB, MT_Top.getBiggestOccupiedSite(Top.HeadStateB));
	
					Top.HeadStateA=Top.HeadStateB+1;	//Plus-end directed motor, forward
						
					Top.unbindHeadB();				//Switch Heads
							
					MT_Top.occupy(Top.HeadStateA, bindingsite);

					Top.reposition(MT_Top,Top.HeadStateA);
				
					//Maybe take care of timesincebound; if one wants to keep minimum binding time
								
					Top.resetLnRhoStep();	// New Prob. for next Step
				
				}
				
				break;
		}
		
		switch(stepBottom)
		{
			case NOSTEP:
				
				break;
			
			case STEPA:
				
				if(Bottom.TimeSinceBoundA>=DelayTime)	// Keeps minimum time & prevents stepping imediately after rebinding
				{
					//See if position is free on MT;
					bindingsite=MT_Bottom.getFreeSite(Bottom.HeadStateA+1); //Plus-end directed motor
				
					if(bindingsite==NOTFREE)
						break;			// Don't Step. just wait
					
					if(bindingsite==AT_END)
						break;			// Figure out later
				
					//really Step: Clear old position; occupy new position; reset random number
					
					talk("STEP!");
					
					MT_Bottom.free(Bottom.HeadStateA, MT_Bottom.getBiggestOccupiedSite(Bottom.HeadStateA));
	
					Bottom.HeadStateB=Bottom.HeadStateA+1;	//Plus-end directed motor, forward
					
					Bottom.unbindHeadA();				//Switch Heads
				
					MT_Bottom.occupy(Bottom.HeadStateB, bindingsite);

					Bottom.reposition(MT_Bottom,Bottom.HeadStateB);
				
					//Maybe take care of timesincebound; if one wants to keep minimum binding time
								
					Bottom.resetLnRhoStep();	// New Prob. for next Step
				
				}
				
				break;
				
			case STEPB: //inverse of above
				
				if(Bottom.TimeSinceBoundB>=DelayTime)	// Keeps minimum time & prevents stepping imediately after rebinding
				{
					//See if position is free on MT;
					bindingsite=MT_Bottom.getFreeSite(Bottom.HeadStateB+1); //Plus-end directed motor
				
					if(bindingsite==NOTFREE)
						break;			// Don't Step. just wait
					
					if(bindingsite==AT_END)
						break;			// Figure out later
				
					//really Step: Clear old position; occupy new position; reset random number
					
					talk("STEP!");
						
					MT_Bottom.free(Bottom.HeadStateB, MT_Bottom.getBiggestOccupiedSite(Bottom.HeadStateB));
	
					Bottom.HeadStateA=Bottom.HeadStateB+1;	//Plus-end directed motor, forward
						
					Bottom.unbindHeadB();				//Switch Heads
							
					MT_Bottom.occupy(Bottom.HeadStateA, bindingsite);

					Bottom.reposition(MT_Bottom,Bottom.HeadStateA);
				
					//Maybe take care of timesincebound; if one wants to keep minimum binding time
								
					Bottom.resetLnRhoStep();	// New Prob. for next Step
				
				}
				
				break;
				
				
		}
		
		
		
		return (0);
		
		
	}
		
	public void drawYourself(Graphics G, double scalefactor, double [] offset)
	{

		int redness=(int)(Tension*255/Eg5Stepping.StallForce);
		
		if(redness>255)
			redness=255;
		
		int xleft=(int)((Top.HingePos.X-offset[0])*scalefactor);
		int xright=(int)((Bottom.HingePos.X-offset[0])*scalefactor);
		
		int yleft=(int)((Top.HingePos.Y-offset[1])*scalefactor);
		int yright=(int)((Bottom.HingePos.Y-offset[1])*scalefactor);
				
		G.setColor(new Color(redness,255,0));
		G.drawLine(xleft,yleft,xright,yright);
		
		Top.drawyourself(G,scalefactor,offset,TOP,Bottom.HingePos);
		Bottom.drawyourself(G,scalefactor,offset,BOTTOM,Top.HingePos);
	
	}
		
	
}

