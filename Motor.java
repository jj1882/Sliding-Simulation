/**
 * Motor.java
 *
 * @author Created by Omnicore CodeGuide
 */

// Superclass for motors

public class Motor extends Thing
{
	public double Tension=0;
	public Point SpringForce;
	
	public double Step(double dt) {return (0);}
	public double GetTension() { return (Tension);}
	public Point GetForce() { return (SpringForce); } //Positive x Force towards the right
	// Force Spring exerts on Head A (Eg5) or the only Motorhead (Ncd);

	public boolean bound()	// true if bound
	{
		return false;
	}
	
	public static int getmotornumber()
	{
		talk("This should never be called, Motor Class is abstract");
		return 0;
	}
	
	public void motornumberplus1()
	{
		talk("This should never be called, Motor Class is abstract +1");
	}
	
	public void motornumberminus1()
	{
		talk("This should never be called, Motor Class is abstract -1");
	}
	
	public boolean BindOutofSolution(int toporbottom, int n)
	{
		return false;
	}
	
}

