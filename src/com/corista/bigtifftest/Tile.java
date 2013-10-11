/**
 * 
 */
package com.corista.bigtifftest;

import java.awt.image.BufferedImage;

/**
 * @author tmciver
 */
public class Tile {
	
	private BufferedImage image;
	private int level;
	private int x, y;
	
	/**
	 * @param image
	 * @param level
	 * @param x
	 * @param y
	 */
	public Tile(BufferedImage image, int level, int x, int y) {
		this.image = image;
		this.level = level;
		this.x = x;
		this.y = y;
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getLevel() {
		return level;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public String toString() {
		return "Tile [level=" + level + ", x=" + x
				+ ", y=" + y + "]";
	}
}
