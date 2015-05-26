/*
	WorldPanel....class defining the canvas in which the players live
*/

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;

public class WorldPanel extends JPanel implements java.awt.event.MouseListener
{
	
	
	
	WorldFrame theFrame;
	static BufferedImage theImage;
	static Graphics thePalate;
	static int width,height;
	static double scale;
	static double Zoomfactor=1;
	static int ZoomState=0;   // 0: No Zoom; +1: Zoom in; -1: Zoom out
	static boolean LockOverlap=false;
	static double [] offset = {0,0};
	
	public WorldPanel (int width, int height, WorldFrame theFrame) {
		this.width = width;
		this.height = height;
		scale = width/Sim2D.xDimension*Zoomfactor;   // Change scale here!
		this.theFrame = theFrame;
		setSize(width,height);
		addMouseListener(this);
		
	}
	public void initialize() {
		theImage = (BufferedImage)createImage(width,height);
		thePalate = theImage.getGraphics();
	}
	
	public void LockCenter()
	{
		double centerx, centery;
		
		if(LockOverlap)
		{
			
			// Thing.talk("LockCenter");
			
			centerx=Math.abs((Sim2D.bottom.position.X-Sim2D.top.position.X)/2);  // - because of +/- directed MTs
			centery=Math.abs((Sim2D.bottom.position.Y+Sim2D.top.position.Y)/2);
			
			offset[0]=centerx-(width/2)/scale;
			offset[1]=centery-(height/2)/scale;
		}
		
	}
	
	public void drawAllThings () { // Christian, this is the method that calls all Things to draw themselves. You don't new to replace this
	// code. You just need to write the drawYourself(Graphics thePalate,double scale, double [] offset); methods for each of your classes
		Motor aMotor;
		
		LockCenter();
		
		Sim2D.top.drawYourself(thePalate,scale,offset);
		Sim2D.bottom.drawYourself(thePalate,scale,offset);
		
		for (int i=0;i<Sim2D.arraysize;i++) {
			if((aMotor=Sim2D.motors[i])!=null)
			  aMotor.drawYourself(thePalate,scale,offset);
		}
	}
	
	public static void drawInfoString (String info) {
		
		//int Ypos;
		
		//Ypos=(int)((Sim2D.bottom.position.Y-WorldPanel.offset[1])*WorldPanel.scale);
		
		thePalate.setColor(Color.white);
		
		//thePalate.drawString(info,10,Ypos-20);
		
		thePalate.drawString(info,10,20);
	}
	
	public static void clearInfoArea () {
		thePalate.setColor(Color.black);
		thePalate.fillRect(10,10,width-20,30);
	}
	
	public void drawBoxAroundWorld () {
//		thePalate.setColor(Color.white);
//		int xCen = (int)(-offset[0]*scale);
//		int yCen = (int)(-offset[1]*scale);
//		int worldWidth = (int) (Sim2D.xDimension*scale);
//		int worldHeight = (int) (Sim2D.yDimension*scale);
//		thePalate.drawRect(xCen,yCen,worldWidth-1,worldHeight-1);
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
	public void paint (Graphics g) {
		g.drawImage(theImage,0,0,null);
	}
	
	
	public void clearImage() {
		thePalate.setColor(Color.black);
		thePalate.fillRect(0,0,width,height);
	}
	
	
	
	public void mouseReleased(MouseEvent p1)
	{
		int x,y;
		
		x=p1.getX();
		y=p1.getY();
		
		Thing.talk("Mouse: X="+x+" Y="+y);
		
		if(ZoomState>0)
		{
			Zoomfactor*=2;
			scale = WorldPanel.width/Sim2D.xDimension*WorldPanel.Zoomfactor;
		}
		
		if(ZoomState<0)
		{
			Zoomfactor/=2;
			scale = WorldPanel.width/Sim2D.xDimension*WorldPanel.Zoomfactor;
		}
			
		if(ZoomState==0 && Zoomfactor!=1)  // Reposition if Zoomstate=0 and image is actually zoomed
		{
			// Thing.talk("Reposition: Old offset ("+offset[0]+"|"+offset[1]+")");
			
			offset[0]+=(x-width/2)/scale;
			offset[1]+=(y-height/2)/scale;
			
			
			if(offset[0]<0)
				offset[0]=0;
			
			if(offset[1]<0)
				offset[1]=0;
			
			// Thing.talk("Reposition: New offset ("+offset[0]+"|"+offset[1]+")");
		}
		
		
		
			
	}
	
	
	public void mouseClicked(MouseEvent p1)
	{}
	public void mousePressed(MouseEvent p1)
	{}
	public void mouseEntered(MouseEvent p1)
	{}
	public void mouseExited(MouseEvent p1)
	{}

}

