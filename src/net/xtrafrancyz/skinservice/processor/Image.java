package net.xtrafrancyz.skinservice.processor;

import com.objectplanet.image.PngEncoder;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author xtrafrancyz
 */
public class Image {
    public static final int DEFAULT_IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;
    private static final ThreadLocal<PngEncoder> PNG_ENCODER = ThreadLocal.withInitial(() ->
        new PngEncoder(PngEncoder.COLOR_TRUECOLOR_ALPHA, PngEncoder.BEST_SPEED)
    );
    
    private final BufferedImage handle;
    private int[] data;
    private byte[] encoded = null;
    private boolean dirty = true;
    
    public Image(BufferedImage handle) {
        if (handle.getType() != DEFAULT_IMAGE_TYPE) {
            this.handle = new BufferedImage(handle.getWidth(), handle.getHeight(), DEFAULT_IMAGE_TYPE);
            this.handle.getGraphics().drawImage(handle, 0, 0, null);
        } else {
            this.handle = handle;
        }
    }
    
    public Image(int width, int height) {
        this.handle = new BufferedImage(width, height, DEFAULT_IMAGE_TYPE);
    }
    
    public void copyFrom(Image from, int fromX1, int fromY1, int fromX2, int fromY2) {
        copyFrom(from, fromX1, fromY1, fromX2, fromY2, 0, 0);
    }
    
    public void copyFrom(Image from, int fromX1, int fromY1, int fromX2, int fromY2, int toX1, int toY1) {
        int w = fromX2 - fromX1;
        int h = fromY2 - fromY1;
        int[] selfData = getData();
        int[] fromData = from.getData();
        int selfWidth = getWidth();
        int fromWidth = from.getWidth();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                selfData[toX1 + x + (toY1 + y) * selfWidth] = fromData[fromX1 + x + (fromY1 + y) * fromWidth];
            }
        }
    }
    
    public void copyWithAlphaFrom(Image from, int fromX1, int fromY1, int fromX2, int fromY2, int toX1, int toY1) {
        int w = fromX2 - fromX1;
        int h = fromY2 - fromY1;
        int[] selfData = getData();
        int[] fromData = from.getData();
        int selfWidth = getWidth();
        int fromWidth = from.getWidth();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int argb = fromData[fromX1 + x + (fromY1 + y) * fromWidth];
                if ((argb >> 24 & 255) > 5)
                    selfData[toX1 + x + (toY1 + y) * selfWidth] = argb;
            }
        }
    }
    
    public void copyFlippedXFrom(Image from, int fromX1, int fromY1, int fromX2, int fromY2, int toX1, int toY1) {
        int w = fromX2 - fromX1;
        int h = fromY2 - fromY1;
        int[] selfData = getData();
        int[] fromData = from.getData();
        int selfWidth = getWidth();
        int fromWidth = from.getWidth();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                selfData[toX1 + x + (toY1 + y) * selfWidth] = fromData[fromX1 + (w - x - 1) + (fromY1 + y) * fromWidth];
            }
        }
    }
    
    public Image scale(int width, int height) {
        if (width == getWidth() && height == getHeight())
            return this;
        Image scaled = new Image(width, height);
        int[] newData = scaled.getData();
        int[] myData = getData();
        float factorX = (float) getWidth() / width;
        float factorY = (float) getHeight() / height;
        int myWidth = getWidth();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                newData[x + y * width] = myData[(int) (x * factorX) + (int) (y * factorY) * myWidth];
            }
        }
        //scaled.handle.getGraphics().drawImage(handle, 0, 0, width, height, null);
        return scaled;
    }
    
    public int getWidth() {
        return handle.getWidth();
    }
    
    public int getHeight() {
        return handle.getHeight();
    }
    
    public BufferedImage getHandle() {
        return handle;
    }
    
    public byte[] encode() {
        if (dirty) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
            try {
                PNG_ENCODER.get().encode(handle, stream);
            } catch (IOException ignored) {}
            encoded = stream.toByteArray();
            dirty = false;
        }
        return encoded;
    }
    
    private int[] getData() {
        if (data == null)
            data = ((DataBufferInt) handle.getRaster().getDataBuffer()).getData();
        return data;
    }
}
