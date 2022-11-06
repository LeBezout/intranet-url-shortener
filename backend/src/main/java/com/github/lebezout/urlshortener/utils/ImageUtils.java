package com.github.lebezout.urlshortener.utils;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class ImageUtils {
    private static final String IMAGE_FORMAT_PNG = "PNG";
    private static final String SVG_BADGE_TEMPLATE = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"120\" height=\"20\">\n" +
        "    <linearGradient id=\"a\" x2=\"0\" y2=\"100%%\">\n" +
        "        <stop offset=\"0\" stop-color=\"#bbb\" stop-opacity=\".1\"/>\n" +
        "        <stop offset=\"1\" stop-opacity=\".1\"/>\n" +
        "    </linearGradient>\n" +
        "    <rect rx=\"3\" width=\"110\" height=\"20\" fill=\"#555\" />\n" +
        "    <rect rx=\"3\" x=\"37\" width=\"75\" height=\"20\" fill=\"#9f9f9f\" />\n" +
        "    <path fill=\"#9f9f9f\" d=\"M37 0h4v20h-4z\"/>\n" +
        "    <g fill=\"#fff\" text-anchor=\"middle\" font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\" font-size=\"11\">\n" +
        "        <text x=\"19\" y=\"15\" fill=\"#010101\" fill-opacity=\".3\">%s</text>\n" +
        "        <text x=\"19\" y=\"14\">%s</text>\n" +
        "        <text x=\"75\" y=\"15\" fill=\"#010101\" fill-opacity=\".3\">%s</text>\n" +
        "        <text x=\"75\" y=\"14\">%s</text>\n" +
        "    </g>\n" +
        "</svg>";

    /**
     * Generate a SVG Badge for the specified couple label/value
     * @param label the label (to the left)
     * @param value the value (to the right)
     * @return SVG (xml) as UTF-8 bytes
     */
    public byte[] svgBadge(String label, String value) {
        // FIXME size depends on the label/value
        return String.format(SVG_BADGE_TEMPLATE, label, label, value, value).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Generate a one px PNG image of the specified color
     * @param htmlColor color with {@code RRGGBB} format
     * @return bytes of the PNG image
     * @throws IOException if image cannot be generated
     */
    public byte[] pixel(String htmlColor) throws IOException {
        Color color = rgbHexaToColor(htmlColor);
        BufferedImage image = new BufferedImage(1, 1 , BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, 1, 1);
        return writeAs(image, IMAGE_FORMAT_PNG);
    }

    /**
     * Generate an image from the specified text as PNG image
     * @param text text to render
     * @param fontSize the size in pt
     * @param fontBold true if bold must be rendered
     * @param fontColor font color with {@code RRGGBB} format
     * @param bgColor background color with {@code RRGGBB} format
     * @return bytes of the PNG image
     * @throws IOException if image cannot be generated
     */
    public byte[] fromText(String text, int fontSize, boolean fontBold, String fontColor, String bgColor) throws IOException {
        Color color = rgbHexaToColor(fontColor);
        Color background = rgbHexaToColor(bgColor);
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Font font = new Font(Font.MONOSPACED, fontBold ? Font.BOLD : Font.PLAIN, fontSize);
        // get the height and width of the text
        Rectangle2D bounds = font.getStringBounds(text, frc);
        int textWidth = (int) bounds.getWidth();
        int textHeight = (int) bounds.getHeight();
        // Generate image
        BufferedImage image = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        // paint background
        g.setColor(background);
        g.fillRect(0,0, textWidth, textHeight);
        // render text
        g.setColor(color);
        g.setFont(font);
        g.drawString(text, (float) bounds.getX(), (float) -bounds.getY());
        g.dispose();
        return writeAs(image, IMAGE_FORMAT_PNG);
    }

    private static Color rgbHexaToColor(String rgb) {
        if (!StringUtils.hasText(rgb)) {
            return Color.WHITE;
        }
        String color = rgb.startsWith("#") ? rgb.substring(1) : rgb;
        if (color.length() != 6 && color.length() != 3) {
            return Color.WHITE;
        }
        return new Color(Integer.parseInt(color, 16));
    }

    private static byte[] writeAs(BufferedImage image, String formatName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, baos);
        return baos.toByteArray();
    }
}
