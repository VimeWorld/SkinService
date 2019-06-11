package net.xtrafrancyz.skinservice.processor;

import com.objectplanet.image.PngEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.xtrafrancyz.skinservice.SkinService;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author xtrafrancyz
 */
public class Image {
    private static final Logger LOG = LoggerFactory.getLogger(SkinService.class);
    private static final int DEFAULT_IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;
    private static final ThreadLocal<PngEncoder> PNG_ENCODER = ThreadLocal.withInitial(() ->
        new PngEncoder(PngEncoder.COLOR_TRUECOLOR_ALPHA, PngEncoder.BEST_SPEED)
    );
    
    private final BufferedImage handle;
    private int[] data;
    private byte[] encoded = null;
    private boolean dirty = true;
    
    public Image(BufferedImage handle) {
        if (handle.getType() != DEFAULT_IMAGE_TYPE) {
            LOG.trace("Convert image to correct image type {} -> {}", handle.getType(), DEFAULT_IMAGE_TYPE);
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
        dirty = true;
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
    
    public void copyWithAlphaFrom(Image from, int fromX1, int fromY1, int fromX2, int fromY2) {
        copyWithAlphaFrom(from, fromX1, fromY1, fromX2, fromY2, 0, 0);
    }
    
    public void copyWithAlphaFrom(Image from, int fromX1, int fromY1, int fromX2, int fromY2, int toX1, int toY1) {
        dirty = true;
        int w = fromX2 - fromX1;
        int h = fromY2 - fromY1;
        int[] selfData = getData();
        int[] fromData = from.getData();
        int selfWidth = getWidth();
        int fromWidth = from.getWidth();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int argb = fromData[fromX1 + x + (fromY1 + y) * fromWidth];
                int toIndex = toX1 + x + (toY1 + y) * selfWidth;
                selfData[toIndex] = blendAlpha(selfData[toIndex], argb);
            }
        }
    }
    
    public void copyFlippedXFrom(Image from, int fromX1, int fromY1, int fromX2, int fromY2, int toX1, int toY1) {
        dirty = true;
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
    
    public byte[] encode() {
        if (dirty) {
            LOG.trace("Encoding image {}x{}", getWidth(), getHeight());
            ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
            try {
                PNG_ENCODER.get().encode(handle, stream);
            } catch (IOException ignored) {}
            encoded = stream.toByteArray();
            dirty = false;
        } else {
            LOG.trace("Image already encoded. Cache used");
        }
        return encoded;
    }
    
    private int[] getData() {
        if (data == null)
            data = ((DataBufferInt) handle.getRaster().getDataBuffer()).getData();
        return data;
    }
    
    /**
     * Code from https://stackoverflow.com/a/727339/6620659
     */
    private static int blendAlpha(int c1, int c2) {
        float a1 = (c1 >> 24 & 255) / 255f;
        float a2 = (c2 >> 24 & 255) / 255f;
        float a = 1 - (1 - a2) * (1 - a1);
        if (a < 1.0e-6)
            return 0x00000000;
        float r = ((c2 >> 16 & 255) / 255f) * a2 / a + ((c1 >> 16 & 255) / 255f) * a1 * (1 - a2) / a;
        float g = ((c2 >> 8 & 255) / 255f) * a2 / a + ((c1 >> 8 & 255) / 255f) * a1 * (1 - a2) / a;
        float b = ((c2 & 255) / 255f) * a2 / a + ((c1 & 255) / 255f) * a1 * (1 - a2) / a;
        return (int) (a * 255) << 24 | (int) (r * 255) << 16 | (int) (g * 255) << 8 | (int) (b * 255);
    }
}
