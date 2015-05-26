/*
	Jpegger ...  a class that writes jpegs
*/

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.util.Iterator;

public class Jpegger {
	 
	public static void jpegFromBufferedImage(BufferedImage bImg, String fileName) {
	    try {
		  // create output stream
	    	  File outFile = new File(fileName);
		  		FileImageOutputStream fios = new FileImageOutputStream (outFile);
		  
		  // conversion for colors etc ?
		  BufferedImage altBI = new BufferedImage( bImg.getWidth(), bImg.getHeight(), BufferedImage.TYPE_INT_RGB );
		  Graphics g = altBI.getGraphics();
		  g.drawImage( bImg, 0, 0, altBI.getWidth(), altBI.getHeight(), null );
		

	      // Get Writer and set compression
	      Iterator iter = ImageIO.getImageWritersByFormatName( "JPG" );
	      if( iter.hasNext() ) {
	        ImageWriter writer = (ImageWriter)iter.next();
	        ImageWriteParam iwp = writer.getDefaultWriteParam();
	        iwp.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
	        iwp.setCompressionQuality( 1.0f );
	        writer.setOutput( fios );
	        IIOImage image = new IIOImage(altBI, null, null);
	        writer.write(null, image, iwp);
	        fios.close();
	        fios=null;
	        writer.dispose();
	        writer=null;
	        g.dispose();
	        g=null;
	        iwp=null;
	        altBI=null;
	      }
	    }
	    catch( Exception e )
	    {
	      e.printStackTrace();
	    }
	 }
}
