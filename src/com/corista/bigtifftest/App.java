package com.corista.bigtifftest;

import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader;
import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReaderSpi;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageReader;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.sun.media.jai.widget.DisplayJAI;

public class App {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// validate args
		if (args.length < 1) {
			System.err.println("Must supply a path to a BigTIFF file.");
			return;
		}
		
		// get the path to the BigTiff file
		String tiffPath = args[0];
		
		// create a reader
		ImageReader rdr = new TIFFImageReaderSpi().createReaderInstance(new Object());
		TIFFImageReader tiffRdr = null;
		if (rdr instanceof TIFFImageReader) {
			System.out.println("It's a TIFFImageReader!");
			tiffRdr = (TIFFImageReader)rdr;
		}
		tiffRdr.setInput(new File(tiffPath));
		
		// read the image
		RenderedImage image = rdr.read(0);
		
		// Create a frame for display.
		JFrame frame = new JFrame();
		frame.setTitle("DisplayJAI: "+args[0]);
		// Get the JFrame's ContentPane.
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		// Create an instance of DisplayJAI.
		DisplayJAI dj = new DisplayJAI(image);
		// Add to the JFrame's ContentPane an instance of JScrollPane containing the
		// DisplayJAI instance.
		contentPane.add(new JScrollPane(dj),BorderLayout.CENTER);
		// Add a text label with the image information.
		//contentPane.add(new JLabel(imageInfo),BorderLayout.SOUTH);
		// Set the closing operation so the application is finished.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400,400); // adjust the frame size.
		frame.setVisible(true); // show the frame.
	}

}
