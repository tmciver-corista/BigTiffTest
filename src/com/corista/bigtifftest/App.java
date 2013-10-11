package com.corista.bigtifftest;

import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader;
import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReaderSpi;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;

public class App {
	
	private static final String USAGE = App.class.getName() + " </path/to/tiff-file> [-m (print meta data and exit)]";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		// validate args
		if (args.length < 1) {
			System.err.println(USAGE);
			return;
		}
		
		// get the path to the BigTiff file
		String tiffPath = args[0];
		
		// create a reader
		TIFFImageReader tiffRdr = (TIFFImageReader)new TIFFImageReaderSpi().createReaderInstance(new Object());
		tiffRdr.setInput(new FileImageInputStream(new File(tiffPath)));
		
		// we will continue only if all image planes are tiled
		boolean noImagesAreTiled = true;
		for (int imageNum = 0; imageNum < tiffRdr.getNumImages(true); imageNum++) {
			if (tiffRdr.isImageTiled(imageNum)) {
				//System.out.println("Image plane " + imageNum + " is not tiled.");
				noImagesAreTiled = false;
			}
		}
		if (noImagesAreTiled) {
			System.err.println("No tiled image planes found. Aborting.");
			return;
		}
		
		// should we print the meta data?
		if (args.length == 2) {
			if (args[1].equals("-m")) {
				System.out.println(new TIFFMetaData(tiffRdr));
			} else {
				System.err.println("Invalid argument: " + args[1]);
				System.err.println(USAGE);
			}
			return;
		} else if (args.length > 2) {
			System.err.println("Too many arguments provided.");
			System.err.println(USAGE);
			return;
		}
		
		// create the output directory
		File outputDir = new File(tiffPath.substring(0, tiffPath.lastIndexOf('.')) + "_tiles");
		if (!outputDir.mkdir()) {
			System.err.println("Could not create output directory.");
			return;
		}
		
		// create a TileWriter
		TileWriter tileWriter = new TileWriter(outputDir);
		
		// progress
		String progressFormatStr = "%.1f%% complete";
		String progressString = String.format(progressFormatStr, 0.0);
		System.out.print(progressString);
		
		// loop over all image planes
		int numTiles = new TIFFMetaData(tiffRdr).getTotalNumberOfTiles();
		int numTilesSoFar = 0;
		for (BigTIFFTileList tileList : new BigTIFFImageList(tiffRdr)) {
			for (Tile tile : tileList) {

				// update progress reporting
				int progressStringLength = progressString.length();
				while (progressStringLength-- > 0) {
					System.out.print("\b");
				}
				progressString = String.format(progressFormatStr, (double)numTilesSoFar++/numTiles*100.0);
				System.out.print(progressString);
				
				// null?
				if (tile.getImage() == null) {
					continue;
				}
				
				// write the tile
				try {
					tileWriter.write(tile.getImage(), tile.getLevel(), tile.getX(), tile.getY());
				} catch (IOException e) {
					//System.err.println("Caught exception while trying to write tile image to file.");
					continue;
				}
			}
		}
		
		System.out.println();
	}
}
