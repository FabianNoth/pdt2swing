package pdt.gui.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class ImageUtils {

	/**
	 * loads an image from a file
	 * @param file that contains the image
	 * @return the BufferedImage or null if loading failed
	 */
	public static BufferedImage loadImage(File file) {
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * saves image to a JPEG file. Shows dialog if file already exists.
	 * @param outputImage image which shall be written to a file
	 * @param destFile destination
	 * @return true if file was saved, false otherwise
	 */
	public static boolean saveImage(BufferedImage outputImage, File destFile) {
		if (destFile.isFile()) {
			int answer = JOptionPane.showConfirmDialog(PrologUtils.getActiveFrame(), "Das Bild exisitert bereits. Soll es überschrieben werden?", "Bild überschreiben", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.NO_OPTION) {
				return false;
			}
		}
		if (!destFile.getParentFile().isDirectory()) {
			destFile.getParentFile().mkdirs();
		}
		try {
			return ImageIO.write(outputImage, "jpg", destFile);
		} catch (IOException e) {
			SimpleLogger.error(e.getMessage());
			return false;
		}
	}
	
	public static BufferedImage scaleImageFast(File file, int maxWidth, int maxHeight) {
		return scaleImageProportional(file, maxWidth, maxHeight, Image.SCALE_FAST);
	}
	
	public static BufferedImage scaleImageSmooth(File file, int maxWidth, int maxHeight) {
		return scaleImageProportional(file, maxWidth, maxHeight, Image.SCALE_SMOOTH);
	}
	
	public static BufferedImage scaleImageFast(BufferedImage img, int maxWidth, int maxHeight) {
		return scaleImageProportional(img, maxWidth, maxHeight, Image.SCALE_FAST);
	}
	
	public static BufferedImage scaleImageSmooth(BufferedImage img, int maxWidth, int maxHeight) {
		return scaleImageProportional(img, maxWidth, maxHeight, Image.SCALE_SMOOTH);
	}
	
	public static BufferedImage scaleUnprop(BufferedImage input, int width, int height) {
		Image scaledInstance = input.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage output = new BufferedImage(width, height, input.getType());
		Graphics2D g = output.createGraphics();
		g.drawImage(scaledInstance, 0, 0, null);
		g.dispose();
		
		return output;
	}
	
	private static BufferedImage scaleImageProportional(BufferedImage input, int maxWidth, int maxHeight, int scaleMethod) {

		int origWidth = input.getWidth();
		int origHeight = input.getHeight();

		double scale = 1.0;
		BufferedImage output = input;
		
		if ((origWidth > maxWidth && maxWidth != -1) || (origHeight > maxHeight && maxHeight != -1)) {
			// image is larger than area, so scaling required
			double scaleWidth = (1.0 * maxWidth) / origWidth;
			double scaleHeight = (1.0 * maxHeight) / origHeight;
			
			if (scaleWidth < 0) {
				scaleWidth = Double.MAX_VALUE;
			}
			
			if (scaleHeight < 0) {
				scaleHeight = Double.MAX_VALUE;
			}

			// use smallest scale factor (so that both sides match)
			scale = Math.min(scaleWidth, scaleHeight);
			int newWidth = (int) (origWidth * scale);
			int newHeight = (int) (origHeight * scale);
			
			Image scaledInstance = input.getScaledInstance(newWidth, newHeight, scaleMethod);
			
			// scale image
			output = new BufferedImage(newWidth, newHeight, input.getType());
			Graphics2D g = output.createGraphics();
			g.drawImage(scaledInstance, 0, 0, null);
			g.dispose();
		}
		return output;
	}

	private static BufferedImage scaleImageProportional(File file, int maxWidth, int maxHeight, int scaleMethod) {
		try {
			BufferedImage img = ImageIO.read(file);
			return scaleImageProportional(img, maxWidth, maxHeight, scaleMethod);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	



	
}
