/**
 * 
 */
package com.corista.bigtifftest;

import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader;

import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;

/**
 * @author tmciver
 *
 */
public class TIFFMetaData {
	
	private String desc;

	/**
	 * 
	 */
	public TIFFMetaData(TIFFImageReader tiffReader) {

		String tiffFile = ((FileImageInputStream) tiffReader.getInput()).toString();
		int numImages = -1;
		try {
			numImages = tiffReader.getNumImages(true);
		} catch (IOException e) {
			// do nothing
		}
		
		desc = "TIFF file has " + numImages + " images\n";
		
		for (int imageNum = 0; imageNum < numImages; imageNum++) {
			desc += new TIFFImagePlaneMetaData(tiffReader, imageNum).toString() + "\n";
		}
	}

	@Override
	public String toString() {
		return desc;
	}
}

class TIFFImagePlaneMetaData {
	
	private String desc;

	/**
	 * 
	 */
	public TIFFImagePlaneMetaData(TIFFImageReader tiffReader, int imageNumber) {
		
		int xTiles = -1;
		int yTiles = -1;
		try {
			xTiles = tiffReader.getWidth(imageNumber) / tiffReader.getTileWidth(imageNumber);
			yTiles = tiffReader.getHeight(imageNumber) / tiffReader.getTileHeight(imageNumber);
		} catch (IOException e) {
			// do nothing
		}
		
		int tileHeight = -1;
		int tileWidth = -1;
		try {
			tileHeight = tiffReader.getTileHeight(imageNumber);
			tileWidth = tiffReader.getTileWidth(imageNumber);
		} catch (IOException e) {
			// do nothing
		}
		
		int imageWidth = -1;
		int imageHeight = -1;
		try {
			imageWidth = tiffReader.getWidth(imageNumber);
			imageHeight = tiffReader.getHeight(imageNumber);
		} catch (IOException e) {
			// do nothing
		}

		desc = "\tImage level " + imageNumber + "\n"
				+ "\t\tTile size: " + tileHeight + "x" + tileWidth + "\n"
				+ "\t\tNumber of X tiles: " + xTiles + "\n"
				+ "\t\tNumber of Y tiles: " + yTiles+ "\n"
				+ "\t\tImage width: " + imageWidth+ "\n"
				+ "\t\tImage height: " + imageHeight;
	}

	@Override
	public String toString() {
		return desc;
	}
	
}
