/*
	WorldFrame.... the main GUI frame. Christian, you can leave this code as is
*/

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;
import java.text.*;

public class WorldFrame extends JFrame implements ActionListener, ItemListener {
	static Sim2D MC;
	static WorldPanel thePanel;
	static MenuBar theMenuBar;
	JLabel simTime,muttonCounter,gluttonCounter;
	CheckboxMenuItem runMenuItem,paintMenuItem,writeJPEGMenuItem,writeInfoMenuItem;
	JPanel infoPanel = new JPanel();
	static int width,height;
	

	public WorldFrame (Sim2D MC,int width, int height) {
		WorldFrame.MC = MC;
		WorldFrame.width = width;
		WorldFrame.height = height;
		this.setTitle ("Christian's Sim");
		this.setSize (width+10,height+30);
		this.setBackground(Color.black);
		makeMenuBar();
		this.show();
		
		
		thePanel = new WorldPanel (width,height, this); 	// panel where the players will be drawn
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add (thePanel, BorderLayout.CENTER);
		thePanel.initialize();
		
		this.show();
		
	}
	
	public void showAll() {
		thePanel.clearImage();
		thePanel.drawBoxAroundWorld();
		thePanel.drawAllThings();
		WorldPanel.drawInfoString(Sim2D.getInfoString());
		thePanel.repaint();
	}
	
	public void showInfoOnly() {
		WorldPanel.clearInfoArea();
		WorldPanel.drawInfoString(Sim2D.getInfoString());
		thePanel.repaint();
	}
	
	public void actionPerformed (ActionEvent event) {
		String arg = event.getActionCommand();

		if(arg.indexOf("deltaT =") >=0) {
			if(arg.indexOf("deltaT = .0001") >=0) {
				Sim2D.setTimeStep(.0001);;
				return;
				}
			if(arg.indexOf("deltaT = .0005") >=0) {
				Sim2D.setTimeStep(.0005);
				return;
				}
			if(arg.indexOf("deltaT = .001") >=0) {
				Sim2D.setTimeStep(.001);
				return;
				}
			if(arg.indexOf("deltaT = .0025") >=0) {
				Sim2D.setTimeStep(.0025);
				return;
				}
			if(arg.indexOf("deltaT = .005") >=0) {
				Sim2D.setTimeStep(.005);
				return;
				}
			if(arg.indexOf("deltaT = .01") >=0) {
				Sim2D.setTimeStep(.01);
				return;
				}
			if(arg.indexOf("deltaT = .025") >=0) {
				Sim2D.setTimeStep(.025);
				return;
				}
			if(arg.indexOf("deltaT = .05") >=0) {
				Sim2D.setTimeStep(.05);
				return;
				}
			if(arg.indexOf("deltaT = .1") >=0) {
				Sim2D.setTimeStep(.1);
				return;
				}
			if(arg.indexOf("deltaT = .5") >=0) {
				Sim2D.setTimeStep(.5);
				return;
				}
			if(arg.indexOf("deltaT = 1") >=0) {
				Sim2D.setTimeStep(1);
				return;
				}
				
			if(arg.indexOf("deltaT = 10") >=0) {
				Sim2D.setTimeStep(10);
				return;
				}
			return;
			}
			
	if(arg.indexOf("paintInterval =") >=0) {
		if(arg.indexOf("paintInterval = 100") >=0) {
			Sim2D.paintInterval = 100;
			return;
			}
			
		if(arg.indexOf("paintInterval = 50") >=0) {
			Sim2D.paintInterval = 50;
			return;
			}

		if(arg.indexOf("paintInterval = 20") >=0) {
			Sim2D.paintInterval = 20;
			return;
			}
			
		if(arg.indexOf("paintInterval = 10") >=0) {
			Sim2D.paintInterval = 10;
			return;
			}
			
		if(arg.indexOf("paintInterval = 1") >=0) {
			Sim2D.paintInterval = 1;
			return;
			}
			
		if(arg.indexOf("paintInterval = 2") >=0) {
			Sim2D.paintInterval = 2;
			return;
			}
			
		if(arg.indexOf("paintInterval = 3") >=0) {
			Sim2D.paintInterval = 3;
			return;
			}
			
		if(arg.indexOf("paintInterval = 4") >=0) {
			Sim2D.paintInterval = 4;
			return;
			}
			
		if(arg.indexOf("paintInterval = 5") >=0) {
			Sim2D.paintInterval = 5;
			return;
			}
		return;
		}
		
		if(arg.indexOf("Zoom") >=0)
		{
					
			if(arg.indexOf("Zoom Off") >=0)
			{
				WorldPanel.ZoomState= 0;
				WorldPanel.Zoomfactor=1;
				WorldPanel.scale = WorldPanel.width/Sim2D.xDimension*WorldPanel.Zoomfactor;
				return;
			}
			
			if(arg.indexOf("Zoom In") >=0)
			{
				WorldPanel.ZoomState= 1;
				return;
			}
			
			if(arg.indexOf("Zoom Out") >=0)
			{
				WorldPanel.ZoomState= -1;
				return;
			}
			
			if(arg.indexOf("Move Zoom") >=0)
			{
				WorldPanel.ZoomState= 0;
				return;
			}
			
			if(arg.indexOf("Zoom Lock overlap") >=0)
			{
				// Thing.talk("Lock overlap");
				if(WorldPanel.LockOverlap)
				{
					WorldPanel.LockOverlap=false;
					Thing.talk("Lock overlap Off");
				}
				else
				{
					WorldPanel.LockOverlap=true;
					Thing.talk("Lock overlap On");
				}
				
				return;
			}
			
		
			return;
		}
		
	}

	public static final void talk(String stg) {
		System.out.println(stg); //  this is how you print text to the java Console.
		}
	
	public void itemStateChanged( ItemEvent event ) {
		Object src = event.getSource( );
		
			
		if ( src == runMenuItem ) {
			Sim2D.running = runMenuItem.getState( );
		}
		
		if ( src == paintMenuItem ) {
			Sim2D.paintOn = paintMenuItem.getState( );
		}
		
		if ( src == writeJPEGMenuItem ) {
			if (writeJPEGMenuItem.getState()) {
				Sim2D.moviePath = FileOps.getDirectoryToSave(" Create folder for JPEG files...",this);
				
				
				
				
				if (Sim2D.moviePath != null) {
					int lastbitIndex = Sim2D.moviePath.lastIndexOf(File.separator);
					Sim2D.movieFileName = Sim2D.moviePath.substring(lastbitIndex+1);
					Sim2D.movieCounter = Sim2D.movieStep;		// take first JPEG now
					Sim2D.makeMovie = true;
					return;
				}
			}
			writeJPEGMenuItem.setState(false);
			Sim2D.makeMovie = false;
			Sim2D.moviePath = null;
			Sim2D.movieFileName = null;
		}
		
		if ( src == writeInfoMenuItem ) {
			if (writeInfoMenuItem.getState()) {
				String infoPath = FileOps.getDirectoryToSave(" Create folder for Info file...",this);
				if (infoPath != null) {
					int lastbitIndex = infoPath.lastIndexOf(File.separator);
					String infoName = infoPath.substring(lastbitIndex+1);
					FileOps.mkInfoFile(infoPath + File.separator + infoName);
					Sim2D.infoWriteCounter = Sim2D.infoWriteStep;		// write first Info line now
					Sim2D.infoWrite = true;
					return;
				}
			}
			writeInfoMenuItem.setState(false);
			Sim2D.infoWrite = false;
		}
	}
	
	public void makeMenuBar () {
		theMenuBar = new MenuBar();
		this.setMenuBar( theMenuBar );

		// Add menu for program control
		Menu m = new Menu ("Program");
		m.addActionListener(this);
		
		runMenuItem = new CheckboxMenuItem ("Run",Sim2D.running);
		runMenuItem.addItemListener(this);
		m.add(runMenuItem);

		paintMenuItem = new CheckboxMenuItem ("Paint",Sim2D.paintOn);
		paintMenuItem.addItemListener(this);
		m.add(paintMenuItem);
		
		m.add("--------");
		
		writeJPEGMenuItem = new CheckboxMenuItem("Write JPEGs",Sim2D.makeMovie);
		writeJPEGMenuItem.addItemListener(this);
		m.add(writeJPEGMenuItem);
		
		writeInfoMenuItem = new CheckboxMenuItem("Write Info To File",Sim2D.infoWrite);
		writeInfoMenuItem.addItemListener(this);
		m.add(writeInfoMenuItem);
		
		theMenuBar.add(m);
		
		Menu mpaint = new Menu("paintInterval");
		mpaint.addActionListener(this);
		MenuItem in1= new MenuItem("paintInterval = 1"); in1.addActionListener(this); mpaint.add(in1);
		MenuItem in2= new MenuItem("paintInterval = 2"); in2.addActionListener(this); mpaint.add(in2);
		MenuItem in3= new MenuItem("paintInterval = 3"); in3.addActionListener(this); mpaint.add(in3);
		MenuItem in4= new MenuItem("paintInterval = 4"); in4.addActionListener(this); mpaint.add(in4);
		MenuItem in5= new MenuItem("paintInterval = 5"); in5.addActionListener(this); mpaint.add(in5);
		MenuItem in10= new MenuItem("paintInterval = 10"); in10.addActionListener(this); mpaint.add(in10);
		MenuItem in20= new MenuItem("paintInterval = 20"); in20.addActionListener(this); mpaint.add(in20);
		MenuItem in50= new MenuItem("paintInterval = 50"); in50.addActionListener(this); mpaint.add(in50);
		MenuItem in100= new MenuItem("paintInterval = 100"); in100.addActionListener(this); mpaint.add(in100);
		theMenuBar.add(mpaint);

		Menu mTStep = new Menu("deltaT");
		mTStep.addActionListener(this);
		MenuItem ts1= new MenuItem("deltaT = .0001"); ts1.addActionListener(this); mTStep.add(ts1);
		MenuItem ts2= new MenuItem("deltaT = .0005"); ts2.addActionListener(this); mTStep.add(ts2);
		MenuItem ts3= new MenuItem("deltaT = .001"); ts3.addActionListener(this); mTStep.add(ts3);
		MenuItem ts4= new MenuItem("deltaT = .0025"); ts4.addActionListener(this); mTStep.add(ts4);
		MenuItem ts5= new MenuItem("deltaT = .005"); ts5.addActionListener(this); mTStep.add(ts5);
		MenuItem ts10= new MenuItem("deltaT = .01"); ts10.addActionListener(this); mTStep.add(ts10);
		MenuItem ts20= new MenuItem("deltaT = .025"); ts20.addActionListener(this); mTStep.add(ts20);
		MenuItem ts50= new MenuItem("deltaT = .05"); ts50.addActionListener(this); mTStep.add(ts50);
		MenuItem ts100= new MenuItem("deltaT = .1"); ts100.addActionListener(this); mTStep.add(ts100);
		MenuItem ts1000= new MenuItem("deltaT = 1"); ts1000.addActionListener(this); mTStep.add(ts1000);
		MenuItem ts10000= new MenuItem("deltaT = 10"); ts10000.addActionListener(this); mTStep.add(ts10000);
		theMenuBar.add(mTStep);
		
		Menu mTZoom = new Menu("Zoom");
		mTZoom.addActionListener(this);
		MenuItem zoom0= new MenuItem("Zoom Off"); zoom0.addActionListener(this); mTZoom.add(zoom0);
		MenuItem zoomin= new MenuItem("Zoom In"); zoomin.addActionListener(this); mTZoom.add(zoomin);
		MenuItem zoomout= new MenuItem("Zoom Out"); zoomout.addActionListener(this); mTZoom.add(zoomout);
		MenuItem zoommove= new MenuItem("Move Zoom"); zoommove.addActionListener(this); mTZoom.add(zoommove);
		MenuItem zoomlockcenter= new MenuItem("Zoom Lock overlap"); zoomlockcenter.addActionListener(this); mTZoom.add(zoomlockcenter);
		
		theMenuBar.add(mTZoom);
	}
	

}
