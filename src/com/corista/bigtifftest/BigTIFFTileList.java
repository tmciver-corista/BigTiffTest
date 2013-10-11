/**
 * 
 */
package com.corista.bigtifftest;

import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.AbstractList;

/**
 * @author tmciver
 *
 */
public class BigTIFFTileList extends AbstractList<Tile> {
	
	private TIFFImageReader tiffReader;
	private int imagePlane;
	private int xTiles, yTiles;

	/**
	 * @param tiffReader
	 * @param imagePlane
	 * @throws IOException 
	 */
	public BigTIFFTileList(TIFFImageReader tiffReader, int imagePlane) throws IOException {
		this.tiffReader = tiffReader;
		this.imagePlane = imagePlane;
		
		// calc x and y tiles
		xTiles = (int)Math.ceil((double)tiffReader.getWidth(imagePlane) / tiffReader.getTileWidth(imagePlane));
		yTiles = (int)Math.ceil((double)tiffReader.getHeight(imagePlane) / tiffReader.getTileHeight(imagePlane));
	}
	
	public int getImageNumber() {
		return imagePlane;
	}
	
	@Override
	public int size() {
		return xTiles * yTiles;
	}

	@Override
	public Tile get(int index) {
		
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		
		// convert index to row major tile indices
		int yTile = index / xTiles;
		int xTile = index % xTiles;
		
		// get the image tile
		BufferedImage tileImage = null;
		try {
			if (tiffReader.isImageTiled(imagePlane)) {
				tileImage = tiffReader.readTile(imagePlane, xTile, yTile);
			} else {
				tileImage = tiffReader.read(imagePlane);
			}
		} catch (Exception e) {
			// do nothing
		}

		return new Tile(tileImage, imagePlane, xTile, yTile);
	}
}
