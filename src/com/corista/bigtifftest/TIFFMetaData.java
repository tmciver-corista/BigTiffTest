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
	private int totalNumberOfTiles;

	/**
	 * @throws IOException 
	 * 
	 */
	public TIFFMetaData(TIFFImageReader tiffReader) throws IOException {

		int numImages = tiffReader.getNumImages(true);
		totalNumberOfTiles = 0;

		desc = "TIFF file has " + numImages + " images\n";
		
		for (int imageNum = 0; imageNum < numImages; imageNum++) {
			TIFFImagePlaneMetaData imageMeta = new TIFFImagePlaneMetaData(tiffReader, imageNum);
			desc += imageMeta.toString() + "\n";
			totalNumberOfTiles += imageMeta.getNumberOfTiles();
		}
	}
	
	public int getTotalNumberOfTiles() {
		return totalNumberOfTiles;
	}

	@Override
	public String toString() {
		return desc;
	}
}

class TIFFImagePlaneMetaData {
	
	private String desc;
	private int numberOfTiles;	// 1 when the image is not tiled

	/**
	 * @throws IOException 
	 * 
	 */
	public TIFFImagePlaneMetaData(TIFFImageReader tiffReader, int imageNumber) throws IOException {
		
		int xTiles = tiffReader.getWidth(imageNumber) / tiffReader.getTileWidth(imageNumber);
		int yTiles = tiffReader.getHeight(imageNumber) / tiffReader.getTileHeight(imageNumber);

		int tileHeight = tiffReader.getTileHeight(imageNumber);
		int tileWidth = tiffReader.getTileWidth(imageNumber);

		int imageWidth = tiffReader.getWidth(imageNumber);
		int imageHeight = tiffReader.getHeight(imageNumber);

		desc = "\tImage level " + imageNumber + "\n"
				+ "\t\tTile size: " + tileHeight + "x" + tileWidth + "\n"
				+ "\t\tNumber of X tiles: " + xTiles + "\n"
				+ "\t\tNumber of Y tiles: " + yTiles+ "\n"
				+ "\t\tImage width: " + imageWidth+ "\n"
				+ "\t\tImage height: " + imageHeight;
		
		// calc the number of tiles
		numberOfTiles = 1;
		if (tiffReader.isImageTiled(imageNumber)) {
			numberOfTiles = xTiles * yTiles;
		}
	}
	
	public int getNumberOfTiles() {
		return numberOfTiles;
	}

	@Override
	public String toString() {
		return desc;
	}
	
}
