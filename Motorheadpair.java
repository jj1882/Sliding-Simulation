/**
 * Motorheadpair.java
 *
 * @author Created by Omnicore CodeGuide
 */

import java.awt.*;

public class Motorheadpair extends Thing
{
	public Point HingePos; 	// Position of the connection of the pair to the stalk
							// Equal to the position of a bound motorhead, otherwise set to
							// position of the domain on the other side by the motor class
	
	public int HeadStateA=0;	// Position on MT (kth site), 0=unbound,
	public int HeadStateB=0;	// dto.
	
	public double TimeSinceBoundA=0;  // Time since head is bound if HeadState>0
	public double TimeSinceBoundB=0;
	
	public double AddedSteppingProbability;	// Added Probability that something happens
	public double LnRhoStep;				// Log of Random Number for Stepping
	
	public double AddedUnbindingProbablity;
	public double LnRhoUnbind;
	
	
	public double AddedRebindingProbability;
	public double LnRhoRebind;
	
	public double Speed;	// Some measure for Stepping probability after TimeSinceBound
	public double Directionality; // 1 or -1, 0 for Ncd Binding site
	
	public Motorheadpair()
	{
		resetLnRhoStep();
		resetLnRhoUnbind();
		resetLnRhoRebind();
	}
	
	public void resetLnRhoStep()
	{
		LnRhoStep=Math.log(Math.random());
		AddedSteppingProbability=0;
	}
	
	public void resetLnRhoUnbind()
	{
		LnRhoUnbind=Math.log(Math.random());
		AddedUnbindingProbablity=0;
	}
	
	public void resetLnRhoRebind()
	{
		LnRhoRebind=Math.log(Math.random());
		AddedRebindingProbability=0;
	}
	
	public void reposition(Microtubule mt, int n)
	// Put HingePos at the position of the nth MT subunit
	{
		HingePos=Microtubule.MTpos2realPos(mt, n);
	}
	
	
	public void reposition(Microtubule mt)
	// Reset Hingepos if Motorhead is bound to MT
	{
		if(bound())
			reposition(mt,Math.max(HeadStateA,HeadStateB)); // Reposition to position of the bound head (HeadState -1 for Unbound)
	}
	
	
	
	public boolean bound()
	{
		if(HeadStateA==0 && HeadStateB==0)
			return false;
		return true;
	}
	
	public void unbindHeadA()
	{
		HeadStateA=0;
		TimeSinceBoundA=0;
	}
	
	public void unbindHeadB()
	{
		HeadStateB=0;
		TimeSinceBoundB=0;
	}
	
	public void unbindBoth()
	{
		unbindHeadA();
		unbindHeadB();
		resetLnRhoStep();
		resetLnRhoUnbind();
	}
	
	public double TimeSinceBound()
	{
		return(Math.max(TimeSinceBoundA,TimeSinceBoundB));
	}
	
	public boolean bindHeadA(Microtubule mt, int bindingposition)
	// Copied from Eg5 Step Method
	{
		
		int bindingsite;
				
		bindingsite=mt.getFreeSite(bindingposition);
		
		
		
		if(bindingsite != NOTFREE && bindingsite != AT_END)  //is free
		{
			mt.occupy(bindingposition,bindingsite);
			
			HeadStateA=bindingposition;
				
			mt.occupy(HeadStateA, bindingsite);

			reposition(mt,HeadStateA);
						
			TimeSinceBoundA=0;
			
			return true;
		}
		
		return false;
	}
	
	public void drawyourself(Graphics G, double sf, double [] offset, int toporbottom, Point OtherHinge)
	// sf: scalefactor
	{
		int xDiaA=(HeadStateA==UNBOUND)?6:8; // nm
		int xDiaB=(HeadStateB==UNBOUND)?6:8;
		int yDia=6;
		double jitter;
		double yshift;
		
		Point connector;
	
		
		
		
		
		if(Directionality==0)
		{
			G.setColor(Color.GRAY);
			G.fillRect((int) (sf* (HingePos.X-xDiaA/2.-offset[0]) ), (int) (sf* (HingePos.Y-yDia/2.-offset[1]) ),
						  (int)(sf*xDiaA), (int)(sf*yDia)) ;
		
			return;
		}
		
		G.setColor(Color.pink);
				
		if(HeadStateA!=UNBOUND)  //bound
		{
		   G.fillOval((int) (sf* (HingePos.X-xDiaA/2.-offset[0]) ), (int) (sf* (HingePos.Y-yDia/2.-offset[1]) ),
						  (int)(sf*xDiaA), (int)(sf*yDia)) ;
		}
		else  //unbound
		{
			//jitter=(Math.random()-.5)*4*Math.sqrt(Sim2D.deltaT)*(2*Microtubule.SubunitLength);
			
			jitter=Directionality*Microtubule.SubunitLength;
			yshift=-toporbottom*(.5*Microtubule.SubunitLength);
			
			G.fillOval((int) (sf* (jitter+HingePos.X-xDiaA/2.-offset[0]) ), (int) (sf* (HingePos.Y+yshift-yDia/2.-offset[1]) ),
						  (int)(sf*xDiaA), (int)(sf*yDia)) ;
			
//			connector=Point.DifferenceVector(HingePos,OtherHinge);
//			connector=connector.MakeUnitVector();
//			connector.X*=10;	// Conncector length 10 nm
//			connector.Y*=10;
//
//			connector.X+=HingePos.X;
//			connector.Y=HingePos.Y-connector.Y;
//
//			G.drawLine((int) (sf* (jitter+HingePos.X-offset[0]) ), (int) (sf* (HingePos.Y+yshift-offset[1]) ),
//					   (int) (sf*(connector.X-offset[0])), (int) (sf*(connector.Y-offset[1])) );
//
			
			
		}
		
		G.setColor(Color.cyan);
				
		if(HeadStateB!=UNBOUND)  //bound
		{
		   G.fillOval((int) (sf* (HingePos.X-xDiaB/2.-offset[0]) ), (int) (sf* (HingePos.Y-yDia/2.-offset[1]) ),
						  (int)(sf*xDiaB), (int)(sf*yDia)) ;
		}
		else  //unbound
		{
			//jitter=(Math.random()-.5)*(2*Microtubule.SubunitLength)*Math.sqrt(Sim2D.deltaT);
			if(HeadStateA!=UNBOUND)
				jitter=Directionality*Microtubule.SubunitLength;
			else
				jitter=-Directionality*Microtubule.SubunitLength;
			
			yshift=-toporbottom*(.5*Microtubule.SubunitLength);
			
			G.fillOval((int) (sf* (jitter+HingePos.X-xDiaB/2.-offset[0]) ), (int) (sf* (HingePos.Y+yshift-yDia/2.-offset[1]) ),
						  (int)(sf*xDiaB), (int)(sf*yDia)) ;
		}
	
	
	
	}
		
}

