/**
 * microtubule.java
 *
 * @author Created by Omnicore CodeGuide
 */
import java.awt.*;

public class Microtubule extends Thing
{
	public boolean fixed=false; // true if fixed directly against wall
	public Point position; 		// position of minus end;
	public int directionality;	// 1: Minus end left; -1: Minus end right
	
	public static int length=1000;  	// in subunits
	public static int BindingSites=3;  //parallel binding sites (Rails) on MT
	public static double SubunitLength=8; // 8 nm subunits
	
	private double SummedForces=0;
	
	public double viscosity;  // Unit: pN*ms/nm
	
	private int [][] occupied = new int [length+1] [BindingSites];
	
	
	public double SitesOccupied=0;
	public double SitesFree;
	public double SitesTotal;
	
	public Microtubule(Point p, boolean f, int direc)
	{
		int i,j;
		
		directionality=direc;
		
		position=new Point(p);
		fixed=f;
		
		SitesTotal=length*BindingSites;
		SitesFree=SitesTotal;
		
		viscosity=1;
		
		for(i=0;i<length;i++)	// Does a Java array start with 0 or 1? 0!
			for(j=0;j<BindingSites;j++)
				occupied[i][j]=FREE;
		
		printpos();
				
	}
	
	public void printpos()
	{
		talk("MT Coordiantes: ("+position.X+"|"+position.Y+"), Length: "+length+" Directionality: "+directionality+ "Fixed: "+fixed);
	}
	
	public void AcceptForce(double aForce)
	{
		SummedForces+=aForce;
	}
	
	public void ZeroForce()
	{
		SummedForces=0;
	}
	
	public void free(int k, int site) // free site at kth protofilament
	{
		SitesFree++;
		SitesOccupied--;
		occupied [k] [site]=FREE;
	}
	
	public void occupy(int k, int site) // occupy site at kth protofilament
	{
		SitesFree--;
		SitesOccupied++;
		occupied [k] [site]=OCCUPIED;
	}
	
	public int getFreeSite(int pos)
	//	Look which site on MT at position "pos" is free
	//  Also checks if motor is at the end of MT
	{
		int i;
		
		if(pos>(length-1) || pos<0)
			return(AT_END);
		
		for(i=0;i<BindingSites;i++)
		{
			if(occupied[pos][i]==FREE)
				return(i);
		}
		
		return(NOTFREE);
	}
	
	public int getBiggestOccupiedSite(int site)
	//Motors don't keep track of their track on MT
	//Thus you need this to remove Motors from a position
	{
		int i;
		
		for(i=BindingSites-1;i>=0;i--)
		{
			if(occupied[site][i]==OCCUPIED)
			{
				// talk("Biggest occupied site="+i);
				return(i);
			}
			
		}
		
		return(0);	//Must be in first Track if it's nowhere else
	}
	
	public void move(double dt)
	// Move microtubule according to SummedForces
	{
		double dx;
		
		if(fixed)	// Don't move when fixed
			return;
	
		// talk("SummedForces="+SummedForces);
		
		dx=(SummedForces/viscosity)*dt;
		
		position.X+=dx;
	}
	
	// Converts position on MT to real point
	public static Point MTpos2realPos(Microtubule mt, int subunit)
	{
		Point p=new Point(0,0); // Syntax error otherwise???
				
		p.Y=mt.position.Y;
		p.X=mt.position.X+mt.directionality*SubunitLength*subunit;
		
		return(p);
	}
	
	public void drawYourself(Graphics G, double scalefactor, double [] offset)
	{
		int xleft=(int)((position.X-offset[0])*scalefactor);
		int xright=(int)((position.X+directionality*length*SubunitLength-offset[0])*scalefactor);
		
		int yleft=(int)((position.Y-offset[1])*scalefactor);
		G.setColor(Color.orange);
		G.drawLine(xleft,yleft,xright,yleft);
	}
}

