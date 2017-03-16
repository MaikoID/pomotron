package com.maikoid.pomotron.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PomotronUtils {

	static Image img = null;

	/**
	 * Create a tray icon image.
	 * 
	 * @param textHeightScale
	 *            Let text occupy this percentage of the available height in the
	 *            tray icon. Range (0, 1]
	 * @param renderedString
	 *            The string to be rendered. Note that only the text height is
	 *            fixed, long strings will not be properly rendered.
	 */
	public static Image createTrayIconImage(String renderedString, double textHeightScale) {
		if (textHeightScale <= 0 || textHeightScale > 1) {
			throw new IllegalArgumentException("Text height percentage should be in (0, 1].");
		}

		Dimension trayIconSize = SystemTray.getSystemTray().getTrayIconSize();
		int w = trayIconSize.width;
		int h = trayIconSize.width;

		BufferedImage iconImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = iconImage.createGraphics();

		if (img == null) {
			try {
				img = ImageIO.read(PomotronUtils.class.getClassLoader().getResource("icons/24x24/pomotron.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.drawImage(img, 0, 0, null);

		int textHeight = (int) Math.floor((h - 2) * textHeightScale);
		Font font = new Font("Monospace", Font.PLAIN, textHeight);
		g2d.setFont(font);

		Rectangle2D textSize = font.getStringBounds(renderedString, g2d.getFontRenderContext());
		int textX = (int) Math.floor(.5 * w - .5 * textSize.getWidth());
		int textY = (int) Math.floor(textSize.getHeight() + 3);

		g2d.setPaint(Color.black);
		g2d.drawString(renderedString, textX, textY);
		g2d.dispose();

		return iconImage;
	}
}
