/**
 * 
 */
package com.corista.bigtifftest;

import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader;

import java.io.IOException;
import java.util.AbstractList;

/**
 * @author tmciver
 *
 */
public class BigTIFFImageList extends AbstractList<BigTIFFTileList> {
	
	private TIFFImageReader tiffReader;
	private int totalImages;

	/**
	 * @param tiffReader
	 * @throws IOException 
	 */
	public BigTIFFImageList(TIFFImageReader tiffReader) throws IOException {
		this.tiffReader = tiffReader;
		this.totalImages = tiffReader.getNumImages(true);
	}
	
	@Override
	public int size() {
		return totalImages;
	}

	@Override
	public BigTIFFTileList get(int index) {

		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		
		BigTIFFTileList tileList = null;
		try {
			tileList = new BigTIFFTileList(tiffReader, index);
		} catch (Exception e) {
			// do nothing
		}
		
		return tileList;
	}
}
