/**
 * 
 */
package com.corista.bigtifftest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author tmciver
 *
 */
public class TileWriter {

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
