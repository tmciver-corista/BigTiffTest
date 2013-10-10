package com.corista.bigtifftest;

import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader;
import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReaderSpi;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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
		for (int imagePlane = 0; imagePlane < tiffRdr.getNumImages(true); imagePlane++) {
			
			// processing differs depending on whether this plane is tiled or not
			BufferedImage image = null;
			if (tiffRdr.isImageTiled(imagePlane)) {
				
				// calc x and y tiles
				int xTiles = (int)Math.ceil((double)tiffRdr.getWidth(imagePlane) / tiffRdr.getTileWidth(imagePlane));
				int yTiles = (int)Math.ceil((double)tiffRdr.getHeight(imagePlane) / tiffRdr.getTileHeight(imagePlane));
				
				// read all tiles
				for (int yTile = 0; yTile < yTiles; yTile++) {
					for (int xTile = 0; xTile < xTiles; xTile++) {
						try {
							//System.out.println("Reading tile (" + xTile + ", " + yTile + ")");
							image = tiffRdr.readTile(imagePlane, xTile, yTile);
						} catch (Exception e) {
							//System.err.println("Caught an exception while trying to read tile at image level " + imagePlane + ", x = " + xTile + ", y = " + yTile + "; continuing.");
							continue;
						}
						
						numTilesSoFar = yTile * xTiles + xTile;
						//System.out.println("Successful read! Read tile " + numTilesSoFar + " of " + numTiles + " (" + 100.0 * numTilesSoFar / numTiles + "%)");
						
						// write the tile
						try {
							tileWriter.write(image, imagePlane, xTile, yTile);
						} catch (IOException e) {
							//System.err.println("Caught exception while trying to write tile imaege to file.");
							continue;
						}
						
						// update progress reporting
						int progressStringLength = progressString.length();
						while (progressStringLength-- > 0) {
							System.out.print("\b");
						}
						progressString = String.format(progressFormatStr, (double)numTilesSoFar++/numTiles*100.0);
						System.out.print(progressString);
					}
				}
			} else {
				try {
					image = tiffRdr.read(imagePlane);
					
					numTilesSoFar += 1;
					//System.out.println("Successful read! Read tile " + numTilesSoFar + " of " + numTiles + " (" + 100.0 * numTilesSoFar / numTiles + "%)");
					
					// write the tile
					try {
						tileWriter.write(image, imagePlane, 0, 0);
					} catch (IOException e) {
						//System.err.println("Caught exception while trying to write tile imaege to file.");
						continue;
					}
					
					// update progress reporting
					int progressStringLength = progressString.length();
					while (progressStringLength-- > 0) {
						System.out.print("\b");
					}
					progressString = String.format(progressFormatStr, (double)numTilesSoFar++/numTiles*100.0);
					System.out.print(progressString);
				} catch (IOException e) {
					continue;
				}
			}
		}
	}
}

class TileWriter {
	
	private File outputDir;
	
	/**
	 * @param outputDir
	 */
	public TileWriter(File outputDir) {
		this.outputDir = outputDir;
	}

	public void write(BufferedImage tileImage, int level, int x, int y) throws IOException {
		
		// create the directory for the level if it does not exist
		File levelDir = new File(outputDir, Integer.toString(level));
		levelDir.mkdir();
		
		// create a file object for the image to be written
		File imageFile = new File(levelDir, x + "_" + y + ".jpg");
		
		// write the image to file
		ImageIO.write(tileImage, "jpg", imageFile);
	}
}
