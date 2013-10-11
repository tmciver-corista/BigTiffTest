/**
 * 
 */
package com.corista.bigtifftest;

import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author tmciver
 *
 */
public class BigTIFFTileList implements Iterable<Tile> {
	
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
	
	public int size() {
		return xTiles * yTiles;
	}

	@Override
	public Iterator<Tile> iterator() {

		return new Iterator<Tile>() {
			
			private int xTile = 0, yTile = 0;

			@Override
			public boolean hasNext() {
				return !pastEndOfTileArray();
			}

			@Override
			public Tile next() {

				if (pastEndOfTileArray()) {
					throw new NoSuchElementException();
				}
				
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
				} finally {
					incrementTileIndices();
				}

				return new Tile(tileImage, imagePlane, xTile, yTile);
			}
			
			private void incrementTileIndices() {
				if (++xTile == xTiles) {
					xTile = 0;
					yTile++;
				}
			}
			
			private boolean pastEndOfTileArray() {
				return yTile >= yTiles ||
						(yTile == yTiles - 1 && xTile >= xTiles);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Removing tiles not supported.");
			}
		};
	}

}
