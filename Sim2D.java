/*
	Sim2D... the file containing "main" and the simulation doLoop for a 2D psuedo-biological simulation
*/

import java.text.*;
import java.io.*;

public class Sim2D extends StaticVars{
	
	static WorldFrame theFrame;
	// the pixel world dimensions
	static int width = 1024;
	static int height = 500;
	
	// the virtual world dimensions
	static double xDimension = 100;		// x dimension in unspecified units
	static double yDimension = 100;		// y dimension
	
	static double deltaT = 0.001;			// time-step
	static double simulationTime = 0;		// keeps track of current time
	static double runTime = 100000.0;			// final time to run to
		// for file writing
	static boolean remote = false;			// no graphics 'cause running on server
	static int remoteCounter = 0;				// for counting integration steps between remote reports
	static int remoteReportStep = 100;		// number of integration steps between printed info reports
	static boolean infoWrite = false;			// write info to file flag
	static int infoWriteCounter = 0;
	static int infoWriteStep = 100;			// number of integration steps between info reports to file
	static boolean makeMovie = false;
	static int movieStep = 5;				// number of integration steps between movies
	static int movieCounter = 0;				// for counting integration steps between frames
	static String movieFileName = null;
	static String moviePath = null;
	static DecimalFormat fileIdFormat = new DecimalFormat ("#000000000.#;#000000000.#");
	static DecimalFormat timeOutFormat = new DecimalFormat ("#0.00#; #0.00#");	// a clean format for simulation time printing
	static int counter=0;

	// for painting control
	static boolean paintOn = true;
	static int paintInterval=3, paintCountdown=0;

	// for run control
	static boolean running = false;
	
	
	// Christian's Stuff
	
	static Motor [] motors;
	static Microtubule top, bottom;
	static Point p1, p2;
	
		
	static int numberofmotors=0;
	static int arraysize=0;  // There is no purpose of rearranging the motor array. Or is there
	double [] overlaps;
	double startoverlap;
	
	double Eg5concentration;
	double Ncdconcentration;
	// static int Eg5inSolution=0;
	static int MaxEg5inSolution=0;
	static int MaxNcdinSolution=0;
	
	double BindingProbabilityEg5=0.1;  // One Molecule binds on average every 0.1 seconds
	double BindingProbabilityNcd=0.1;  // One Molecule binds on average every 0.1 seconds
	
	double AddedBindingProbabilityEg5;
	double AddedBindingProbabilityNcd;
	double LnRhoEg5;
	double LnRhoNcd;
	
	public Sim2D (String [] inputParams) {
		if (inputParams.length != 0) { parseParams(inputParams); }	// figure out what options have been specified
		/* make things to start Christian, put code here to create your two microtubules and all the objects in your simulation
		arena at the biginning of the simulation.
		for (int i=0;i<5;i++) { Glutton.makeRandomGlutton(); } */
	
		
			
		Point f;
		
		// int prebinding=500;
		
		startoverlap=200; // 100 Subunits
		
		p1=new Point(1,500);	// Make sure it never goes negative. When there is only Eg5, it can start at 1 (before 10000)
		p2=new Point(1+Microtubule.SubunitLength*(2*Microtubule.length-startoverlap), 500+Eg5.ZeroLength);
		
		xDimension=(int)(2*Microtubule.length*Microtubule.SubunitLength);
		yDimension=500;
		
		bottom=new Microtubule(p1,FIXED,1);
		top=new Microtubule(p2,LOOSE,-1);
		
		// overlaps=new double [steps];
		
		Eg5concentration=1000;  // in nM, originally 1000=1uM
		Ncdconcentration=1000;
		
		MaxEg5inSolution=concentration2particlenumber(Eg5concentration,1000*1000*10000); //  uM, Box 1um*1um*10um
		MaxNcdinSolution=concentration2particlenumber(Ncdconcentration,1000*1000*10000); //  uM, Box 1um*1um*10um
		
		// Eg5inSolution=MaxEg5inSolution;
	
		resetLnRhoEg5();
		resetLnRhoNcd();
		
		motors=new Motor [20000]; // I thought the arrays are automatically dynamic, I guess not...
		
		if (!remote) {
			// make the frame to which we draw
			theFrame = new WorldFrame (this, width, height);
			theFrame.showAll();		// draw once at beginning
		}
		
		// start the time loop
		doLoop();
	}
	
	public static int getMToverlap()
	{
		double overlap;
		int ol;
		
		overlap=(bottom.position.X+bottom.directionality*Microtubule.SubunitLength*Microtubule.length)-
				(top.position.X+top.directionality*Microtubule.SubunitLength*Microtubule.length);
		
		ol=(int)(overlap/Microtubule.SubunitLength);
		
		return ol;
	}
	
	public int concentration2particlenumber(double concentration, double Volume)
	// Concentration: nM
	// Volume: nm^3
	// avg. distance=(V/N)^1/3
	// Box 500x500x8000:	10 nM = 12 molecules; 100 nM=120 molecules; 1 uM=1200 molecules
	{
		double N;
		
		N=concentration*Volume*6.022e-10;  //Unit conversion factor: nM, (nm)^3
		
		return((int) Math.round(N));
	}
	
	
	public void resetLnRhoEg5()
	{
		LnRhoEg5=Math.log(Math.random());
		AddedBindingProbabilityEg5=0;
	}
	
	public void resetLnRhoNcd()
	{
		LnRhoNcd=Math.log(Math.random());
		AddedBindingProbabilityNcd=0;
	}
	
	public void bindEg5(double dt)
	{
		double prob;
		Point temp=new Point();
		int n;
		
		// Binding Probability
		
		prob=Math.log(.5)*(MaxEg5inSolution-Eg5.getmotornumber())*BindingProbabilityEg5*dt/UNITTIME;
		
		AddedBindingProbabilityEg5+=prob;
		
			
		if(AddedBindingProbabilityEg5<=LnRhoEg5)    //before: >=
		{

			talk("Bind Eg5!");
			
			//Eg5inSolution--;  // why is this blue in the editor?
			//numberofmotors++;
			
			arraysize++;
			motors[arraysize]=new Eg5(temp,top,bottom);   // randomize this for Ncd!!!
			motors[arraysize].motornumberplus1();
			
			// figure out top or bottom
			
			n=(int)Math.round(Math.random()*(Microtubule.length-1));
			
			// talk("top.SitesFree="+top.SitesFree+" bottom.SitesFree="+bottom.SitesFree);
			
			
			//if(top.SitesFree>bottom.SitesFree) // bind to top
			if(Math.random()>0.5)
			{
				while(!motors[arraysize].BindOutofSolution(TOP,n)) // While not successful binding, try other sites
					n=(int)Math.round(Math.random()*Microtubule.length);
			}
			else // bind to bottom
			{
				while(!motors[arraysize].BindOutofSolution(BOTTOM,n)) // While not successful binding, try other sites
					n=(int)Math.round(Math.random()*Microtubule.length);
			}
			resetLnRhoEg5();
			
		}
	
	}
	
	public void bindNcd(double dt)
	{
		double prob;
		double prob2;
		Point temp=new Point();
		int n;
		
		// Binding Probability
		
		prob=Math.log(.5)*(MaxNcdinSolution-Ncd.getmotornumber())*BindingProbabilityNcd*dt/UNITTIME;

		
		AddedBindingProbabilityNcd+=prob;
		
			
		if(AddedBindingProbabilityNcd<=LnRhoNcd)    //before: >=
		{

			talk("Bind Ncd!");
			
			arraysize++;
			

			motors[arraysize]=new Ncd(temp,top,bottom);   // randomize this for Ncd!!! It is randomized in the constructor!
						
			motors[arraysize].motornumberplus1();
			
			// figure out top or bottom
			
			n=(int)Math.round(Math.random()*(Microtubule.length-1));
			
			// talk("top.SitesFree="+top.SitesFree+" bottom.SitesFree="+bottom.SitesFree);
			
			
			//if(top.SitesFree>bottom.SitesFree) // bind to top
			if(Math.random()>.5)
			{
				while(!motors[arraysize].BindOutofSolution(TOP,n)) // While not successful binding, try other sites
					n=(int)Math.round(Math.random()*Microtubule.length);
			}
			else // bind to bottom
			{
				while(!motors[arraysize].BindOutofSolution(BOTTOM,n)) // While not successful binding, try other sites
					n=(int)Math.round(Math.random()*Microtubule.length);
			}
			resetLnRhoNcd();
			
		}
	
	}
	
	// Back to normal
	
	public static void main (String args[]) {
		new Sim2D (args);
	}
	
	
	
	public static void setTimeStep(double delt) {
		deltaT=delt;
		Thing.deltaT = deltaT;
		}
		
		
	public void doLoop() {
		
		int j;
			
		double dt=1;
		
		Thing.deltaT=dt;
					
		talk("dt="+dt);
			
		Point f=new Point();
		
		while (simulationTime <= runTime) {
			if (!running) {
				try { Thread.sleep(200); }
				catch (InterruptedException e) {
					System.out.println ("error sleeping while paused");
					}
			    }
			else {
			/* Christian, this is where you put the main loop that makes one time steps */
				//let new motors bind
			
			if(dt!=Thing.deltaT)
			{
					dt=Thing.deltaT;
					talk("dt="+dt);
			}
						
			bindEg5(dt);
			bindNcd(dt);
			
			// talk("Arraysize: "+arraysize);
					 
			for(j=0;j<arraysize;j++)
			{
				
				if(motors[j]!=null)
				{
					//motor[unbound]=null
					if(!motors[j].bound())
					{
						talk("REMOVE!");
						motors[j].motornumberminus1();
						motors[j]=null;
						// numberofmotors--;
						// Eg5inSolution++;
						
					}
					else
					{
						//let everyone step and collect forces
						motors[j].Step(dt);
						f=motors[j].GetForce();
						top.AcceptForce(-f.X);      // Switched after debugging to -
						bottom.AcceptForce(+f.X);  // to +
					}
				}
						
			}
			
			
			//let force move MT
			
			top.move(dt);
			bottom.move(dt);
			
			// Zero forces
			top.ZeroForce();
			bottom.ZeroForce();
					
			
					
					/*Christian, below here is housekeeping code that paints the screen occasionally*/
				if (!remote) {
					if (paintOn) { // only update Thing painting if paintOn
						if(--paintCountdown <= 0) {
							paintCountdown = paintInterval;
							theFrame.showAll();
							}
						}
					else {
						if(--paintCountdown <= 0) {
							paintCountdown = paintInterval;
							theFrame.showInfoOnly();
							}
						}
					}
						
				else {
					checkRemoteReport();
					remoteCounter++;
					}
				
				if (infoWrite) {
					checkInfoWrite();
					infoWriteCounter++;
					}
					
				if(makeMovie)		// Added in Debugging
				{
					checkJPEGWrite();
					movieCounter++;
				}
				
				counter++;
				simulationTime += deltaT;

/* Christian, put additional code here, to be run each time step to get ready for the next one. You could move this code
to the section above that also runs once each time step.
				Thing.removeDeadThings();		// get rid of dead things
				Mutton.spawnMuttons();		// make more muttons...maybe
				Glutton.explodeObeseGluttons(); */
			} // end of else
		} // end of perpetual while() loop
		
		top.printpos();
		bottom.printpos();
	} // end of doLoop() method
	
	public void checkJPEGWrite () {
		
		if (movieCounter >= movieStep) {
			theFrame.showAll(); // paint before save
			String fileName = moviePath + File.separator + movieFileName + String.valueOf(fileIdFormat.format(counter)+".jpg");
			Jpegger.jpegFromBufferedImage(WorldPanel.theImage,fileName);
			movieCounter = 0;
		}
	}
	
	public static void checkRemoteReport () {
		if (remoteCounter >= remoteReportStep) {
			System.out.println(getInfoString());
			remoteCounter = 0;
		}
	}
	
	public static void checkInfoWrite () {
		if (infoWriteCounter >= infoWriteStep) {
			FileOps.writeInfo();
			infoWriteCounter = 0;
		}
	}
	
	public static String getInfoString() {
		
		String timeStr = " Sim Time" + /*"(dt="+deltaT+")"+*/ " = " + String.valueOf(timeOutFormat.format(Sim2D.simulationTime) );
		String infoString="Eg5 bound: "+Eg5.getmotornumber()+" Ncd bound: "+Ncd.getmotornumber()+" Overlap: "+getMToverlap()+" "+timeStr;

		/* Christian, generate here an informative string to be printed on the screen each paint step
		String timeStr = " Sim Time = " + String.valueOf(timeOutFormat.format(Sim2D.simulationTime)+
					"(dt="+deltaT+")");
		String mutCtStr = " Muttons: " + String.valueOf(Mutton.muttonCt)+
			" (totalMass="+Mutton.getTotalMass()+"/"+MuttonCarryingCapacity.getRemainingCapacity()+")";
		String glutCtStr = " Gluttons: " + String.valueOf(Glutton.gluttonCt)+
			" (totalMass="+Glutton.getTotalMass()+")";
		return glutCtStr + " , " + mutCtStr + " , " + timeStr; */
		return infoString;
	}
	
	private void parseParams(String [] inputParams) {
		for (int i = 0; i < inputParams.length; i++) {
			if ((inputParams[i].equals("-help")) | (inputParams[i].equals("?"))) {
				System.out.println ("The following command line arguments are accepted.....");
				System.out.println ("	-help	 prints this help message");
				System.out.println ("	-r remote start (no local GUI) with default initial conditions");
				System.out.println ("-rs [int] remote reporting step interval");
				System.out.println ("	-dt [double] use this value for the fixed time-step");
				System.out.println ("	-if [filename]	write info to the named file");
				System.out.println ("	-is [int] integration steps between info writing");
				System.exit(0);
			}
			if (inputParams[i].equals("-r")) {
				remote = true;
				running = true;
			}
			
			if (inputParams[i].equals("-rs")) {
				Double rStep = Double.valueOf(inputParams[i+1]);
				remoteReportStep = (int) rStep.doubleValue();
			}
			
			if (inputParams[i].equals("-dt")) {
				Double dt = Double.valueOf(inputParams[i+1]);
				deltaT = dt.doubleValue();
				Thing.deltaT = deltaT;
			}
		
			if (inputParams[i].equals("-if")) {
				File infodir = new File(inputParams[i+1]);
				File altdir = new File (inputParams[i+1]);
				int j=1;
				while (altdir.isDirectory()) {
					System.out.println (altdir.getName() + " exists.... keeping file name BUT changing directory name");
					altdir = new File(inputParams[i+1] + "." + String.valueOf(j));
					j++;
				}
				altdir.mkdir();	// make directory for files
				String nameToUse = infodir.getName();
				FileOps.mkInfoFile(altdir.getAbsolutePath() + File.separator + nameToUse);
				//*******
				infoWrite = true;
			}
			
			if (inputParams[i].equals("-is")) {
				Double iStep = Double.valueOf(inputParams[i+1]);
				infoWriteStep = (int) iStep.doubleValue();
			}
			
		}
	}

	public static final void talk(String stg) {
		System.out.println(stg); //  this is how you print text to the java Console.
		}

}
		
		
		
		
		
		
		
		
		
		
		
