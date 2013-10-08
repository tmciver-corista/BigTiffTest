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

		int tileWidth = tiffRdr.getTileWidth(0);
		int tileHeight = tiffRdr.getTileHeight(0);
		
		int xTiles = tiffRdr.getWidth(0) / tiffRdr.getTileWidth(0);
		int yTiles = tiffRdr.getHeight(0) / tiffRdr.getTileHeight(0);
		
		// set up tile indices for sub-image
		int topLeftTileX = 0;
		int topLeftTileY = 0;
		int subImageTilesX = xTiles;
		int subImageTilesY = yTiles;
		
		int numTiles = xTiles * yTiles;
		System.out.println("Tile width: " + tileWidth + ", tile height: " + tileHeight);
		System.out.println("Image width: " + xTiles + ", image height: " + yTiles);
		int[] bitmapData = new int[3 * xTiles * yTiles];
		
		for (int yTile = topLeftTileY; yTile < topLeftTileY + subImageTilesY; yTile++) {
			for (int xTile = topLeftTileX; xTile < topLeftTileX + subImageTilesX; xTile++) {
				try {
					System.out.println("Reading tile (" + xTile + ", " + yTile + ")");
					BufferedImage image = tiffRdr.readTile(0, xTile, yTile);
				} catch (Exception e) {
					System.err.println("Caught an exception while trying to read a tile; continuing.");
					continue;
				}
				
				int numTilesSoFar = yTile * xTiles + xTile;
				System.out.println("Successful read! Read tile " + numTilesSoFar + " of " + numTiles + " (" + 100.0 * numTilesSoFar / numTiles + "%)");
				
				// it was a successful read
				int pixelCoord = 3 * (yTile * xTiles + xTile);
				bitmapData[pixelCoord] = 0xFF;
				bitmapData[pixelCoord + 1] = 0xFF;
				bitmapData[pixelCoord + 2] = 0xFF;
			}
		}
		
		BufferedImage bitmap = new BufferedImage(xTiles, yTiles, BufferedImage.TYPE_INT_RGB);
		bitmap.getRaster().setPixels(0, 0, xTiles, yTiles, bitmapData);
		
		ImageIO.write(bitmap, "jpg", new File(new File(tiffPath).getParent(), "tile-pass-fail.jpg"));
	}
}
