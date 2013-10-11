/**
 * 
 */
package com.corista.bigtifftest;

import it.geosolutions.imageioimpl.plugins.tiff.TIFFImageReader;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author tmciver
 *
 */
public class BigTIFFImageList implements Iterable<BigTIFFTileList> {
	
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
	
	public int size() {
		return totalImages;
	}

	@Override
	public Iterator<BigTIFFTileList> iterator() {

		return new Iterator<BigTIFFTileList>() {
			
			private int imageIndex;
			
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException("Removing " + BigTIFFTileList.class.getName() + " not supported.");
			}
			
			@Override
			public BigTIFFTileList next() {
				
				if (pastEndOfImageList()) {
					throw new NoSuchElementException();
				}
				
				BigTIFFTileList tileList = null;
				try {
					tileList = new BigTIFFTileList(tiffReader, imageIndex);
				} catch (Exception e) {
					// do nothing
				} finally {
					imageIndex++;
				}
				
				return tileList;
			}
			
			private boolean pastEndOfImageList() {
				return imageIndex == totalImages;
			}
			
			@Override
			public boolean hasNext() {
				return !pastEndOfImageList();
			}
		};
	}

}
