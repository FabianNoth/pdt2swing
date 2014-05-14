package pdt.gui.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {

	private static BufferedImage scaleImage(BufferedImage input, int maxWidth, int maxHeight) {

		int origWidth = input.getWidth();
		int origHeight = input.getHeight();

		double scale = 1.0;
		BufferedImage output = input;
		
		if (origWidth > maxWidth || origHeight > maxHeight) {
			// image is larger than area, so scaling required
			double scaleWidth = (1.0 * maxWidth) / origWidth;
			double scaleHeigth = (1.0 * maxHeight) / origHeight;

			// use smallest scale factor (so that both sides match)
			scale = Math.min(scaleWidth, scaleHeigth);
			int newWidth = (int) (origWidth * scale);
			int newHeight = (int) (origHeight * scale);
			
			Image scaledInstance = input.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
			
			// scale image
			output = new BufferedImage(newWidth, newHeight, input.getType());
			Graphics2D g = output.createGraphics();
			g.drawImage(scaledInstance, 0, 0, null);
			g.dispose();
		}
		return output;
	}

	public static BufferedImage scaleImage(File file, int maxWidth, int maxHeight) {
		try {
			BufferedImage img = ImageIO.read(file);
			return scaleImage(img, maxWidth, maxHeight);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
