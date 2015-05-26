/**
 * Thing.java
 *
 * @author Created by Omnicore CodeGuide
 */

import java.awt.*;


public class Thing extends StaticVars
{
	static double deltaT=Sim2D.deltaT;
	static int thingCt=0;
	
	Thing() {}
	
	public void drawYourself(Graphics G, double scalefactor, double [] offset)
	{}
	
	public static void talk(String msg)
	{
		System.out.println(msg);
	}
	
}

