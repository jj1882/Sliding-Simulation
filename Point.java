/**
 * Point.java
 * Point Class that can also do some very basic vector operations
 * @author Created by Omnicore CodeGuide
 */


public class Point
{
	public double X;
	public double Y;
	
	public Point()
	{
		X=0;
		Y=0;
	}
	
	public Point(double a, double b)
	{
		X=a;
		Y=b;
	}
	
	public Point(Point p)
	{
		X=p.X;
		Y=p.Y;
	}
	
	public double DistanceTo (Point p)
	{
		double dist;
	
		dist=Math.sqrt((p.X-X)*(p.X-X)+(p.Y-Y)*(p.Y-Y));
	
		return (dist);
	}
	
	public double DistanceTo0 ()
	{
		double dist;
	
		dist=Math.sqrt(X*X+Y*Y);
	
		return (dist);
	}
	
	public static double Distance (Point p1, Point p2)
	{
		
		double dist;
	
		dist=Math.sqrt((p1.X-p2.X)*(p1.X-p2.X)+(p1.Y-p2.Y)*(p1.Y-p2.Y));
	
		return (dist);
	}
	
	public static Point DifferenceVector(Point p1, Point p2)
	{
		Point p=new Point();
		
		p.X=p1.X-p2.X;
		p.Y=p1.Y-p2.Y;
		
		return(p);
	}
	
	public Point MakeUnitVector()
	{
		double d;
		Point p=new Point();
		
		d=DistanceTo0();
		
		p.X=X/d;
		p.Y=Y/d;
		
		return(p);
	}
}


