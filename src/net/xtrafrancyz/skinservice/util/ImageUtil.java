package net.xtrafrancyz.skinservice.util;

import com.objectplanet.image.PngEncoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author xtrafrancyz
 */
public class ImageUtil {
    public static BufferedImage scale(BufferedImage image, int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, image.getType());
        scaled.getGraphics().drawImage(image, 0, 0, width, height, null);
        return scaled;
    }
    
    public static void copy(BufferedImage from,
                            int fromX1,
                            int fromY1,
                            int fromX2,
                            int fromY2,
                            BufferedImage to,
                            int toX1,
                            int toY1) {
        int w = fromX2 - fromX1;
        int h = fromY2 - fromY1;
        int[] rgb = from.getRGB(fromX1, fromY1, w, h, null, 0, w);
        to.setRGB(toX1, toY1, w, h, rgb, 0, w);
    }
    
    public static void copyWithAlpha(BufferedImage from,
                                     int fromX1,
                                     int fromY1,
                                     int fromX2,
                                     int fromY2,
                                     BufferedImage to,
                                     int toX1,
                                     int toY1) {
        int w = fromX2 - fromX1;
        int h = fromY2 - fromY1;
        int[] rgb = from.getRGB(fromX1, fromY1, w, h, null, 0, w);
        int[] torgb = to.getRGB(toX1, toY1, w, h, null, 0, w);
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int index = y * w + x;
                if ((rgb[index] >> 24 & 255) > 5)
                    torgb[index] = rgb[index];
            }
        }
        
        to.setRGB(toX1, toY1, w, h, torgb, 0, w);
    }
    
    public static void copyFlippedX(BufferedImage from,
                                    int fromX1,
                                    int fromY1,
                                    int fromX2,
                                    int fromY2,
                                    BufferedImage to,
                                    int toX1,
                                    int toY1) {
        int w = fromX2 - fromX1;
        int h = fromY2 - fromY1;
        int[] rgb = from.getRGB(fromX1, fromY1, w, h, null, 0, w);
        int[] temp = new int[rgb.length];
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                temp[y * w + x] = rgb[y * w + w - x - 1];
            }
        }
        
        to.setRGB(toX1, toY1, w, h, temp, 0, w);
    }
    
    private static final ThreadLocal<PngEncoder> pngEncoder = new ThreadLocal<PngEncoder>() {
        @Override
        protected PngEncoder initialValue() {
            return new PngEncoder(PngEncoder.COLOR_TRUECOLOR_ALPHA, PngEncoder.BEST_SPEED);
        }
    };
    
    public static byte[] toByteArray(BufferedImage img) {
        if (img == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            pngEncoder.get().encode(img, stream);
        } catch (IOException ignored) {}
        return stream.toByteArray();
    }
}
