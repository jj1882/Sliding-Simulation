/*
	FileOps.... a class definining all the file i/o
*/
import java.awt.Component;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class FileOps {
	static String sepString = ";";
	// file, file writer, and print writer for information file
	static boolean infoFileMade = false;
	static File infoFile;
	static FileWriter infoFW;
	static PrintWriter infoPW;

	public FileOps () {
	}
	
	public static String getFileToSave (String dialogTitle, JFrame parent) {
		File file = null;
		String fileName = null;
		JFileChooser saveTo = new JFileChooser();
		saveTo.setDialogTitle(dialogTitle);
		int fileQuery = saveTo.showSaveDialog(parent);
		if (fileQuery == JFileChooser.APPROVE_OPTION) {
            file = saveTo.getSelectedFile();
            fileName = file.getAbsolutePath();
            System.out.println(dialogTitle + fileName);
        } else {
            System.out.println("Save cancelled by user");
            return null;
        }
        return fileName;
	}
	
	public static String getDirectoryToSave (String dialogTitle, Component parent) {
		File file = null;
		String fileName = null;
		JFileChooser saveTo = new JFileChooser();
		saveTo.setDialogTitle(dialogTitle);
		saveTo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int fileQuery = saveTo.showSaveDialog(parent);
		if (fileQuery == JFileChooser.APPROVE_OPTION) {
            file = saveTo.getSelectedFile();
            if (!file.exists()) { file.mkdir(); }
            fileName = file.getAbsolutePath();
            System.out.println(dialogTitle + fileName);
        } else {
            System.out.println("Save cancelled by user");
            return null;
        }
        return fileName;
	}
		
	public static void mkInfoFile (String fileName) {
		try {
			infoFile = new File (fileName + "Info");
			infoFW = new FileWriter(infoFile);
			infoPW = new PrintWriter(infoFW,true);
			Date timeNow = new Date();
			infoPW.println("This is the info file" + " run on " + timeNow.toString());
			infoPW.print("time;");
			infoPW.print("gluttonCount;muttonCount;");
			infoPW.println("other info here; and here; and here;");
		} catch (IOException ioe) { System.out.println("An error creating mkInfoFile"); }
		infoFileMade = true;
	}
	
	
	public static void writeInfo () {
		printValue(Sim2D.simulationTime,infoPW);
		// put your info here.. makes a text file you can import into spreadsheets, etc
		//printValue(Glutton.gluttonCt,infoPW);
		//printValue(Mutton.muttonCt,infoPW);
		
		printValue(0,infoPW);
		printValue(0,infoPW);
		printValue(0,infoPW);
		infoPW.println("");
	}
	
	private static void printDivider (PrintWriter pw) {
		pw.print(sepString);
	}
	
	private static void printValue (int val, PrintWriter pw) {
		pw.print(String.valueOf(val));
		printDivider(pw);
	}
	
	private static void printValue (double val, PrintWriter pw) {
		pw.print(String.valueOf(val));
		printDivider(pw);
	}

}
		
