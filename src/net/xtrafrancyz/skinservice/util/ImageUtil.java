package net.xtrafrancyz.skinservice.util;

import com.objectplanet.image.PngEncoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author xtrafrancyz
 */
public class ImageUtil {
    public static void fill(BufferedImage image, int color) {
        int[] rgb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        Arrays.fill(rgb, color);
        image.setRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0, image.getWidth());
    }
    
    public static BufferedImage scale(BufferedImage image, int width, int height) {
        int[] rgb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        int[] temp = resizePixels(rgb, image.getWidth(), image.getHeight(), width, height);
        BufferedImage scaled = new BufferedImage(width, height, image.getType());
        scaled.setRGB(0, 0, width, height, temp, 0, width);
        return scaled;
    }
    
    private static int[] resizePixels(int[] pixels, int w1, int h1, int w2, int h2) {
        int[] temp = new int[w2 * h2];
        double x_ratio = w1 / (double) w2;
        double y_ratio = h1 / (double) h2;
        double px, py;
        for (int i = 0; i < h2; i++) {
            for (int j = 0; j < w2; j++) {
                px = Math.floor(j * x_ratio);
                py = Math.floor(i * y_ratio);
                temp[(i * w2) + j] = pixels[(int) ((py * w1) + px)];
            }
        }
        return temp;
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
            return new PngEncoder(PngEncoder.COLOR_TRUECOLOR_ALPHA);
        }
    };
    
    public static byte[] toByteArray(BufferedImage img) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            //TODO Fix: Скины Lucy, dimka на выходе полностью прозрачные
            //ImageIO.write(img, "png", stream);
            pngEncoder.get().encode(img, stream);
        } catch (IOException ignored) {}
        return stream.toByteArray();
    }
}
