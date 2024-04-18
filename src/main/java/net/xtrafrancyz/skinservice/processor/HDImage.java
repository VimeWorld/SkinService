package net.xtrafrancyz.skinservice.processor;

public class HDImage {
    private final int scale;
    private final Image img;
    
    public HDImage(int width, int height, Image skin) {
        this(width, height, getSkinResolution(skin));
    }
    
    public HDImage(int width, int height, int scale) {
        this.scale = scale;
        this.img = new Image(width * scale, height * scale);
    }
    
    public void copyFrom(Image from, int fromX1, int fromY1, int fromX2, int fromY2) {
        img.copyFrom(from, fromX1 * scale, fromY1 * scale, fromX2 * scale, fromY2 * scale);
    }
    
    public void copyFrom(Image from, int fromX1, int fromY1, int fromX2, int fromY2, int toX1, int toY1) {
        img.copyFrom(from, fromX1 * scale, fromY1 * scale, fromX2 * scale, fromY2 * scale, toX1 * scale, toY1 * scale);
    }
    
    public void copyWithAlphaFrom(Image from, int fromX1, int fromY1, int fromX2, int fromY2) {
        img.copyWithAlphaFrom(from, fromX1 * scale, fromY1 * scale, fromX2 * scale, fromY2 * scale);
    }
    
    public void copyWithAlphaFrom(Image from, int fromX1, int fromY1, int fromX2, int fromY2, int toX1, int toY1) {
        img.copyWithAlphaFrom(from, fromX1 * scale, fromY1 * scale, fromX2 * scale, fromY2 * scale, toX1 * scale, toY1 * scale);
    }
    
    public void copyFlippedXFrom(Image from, int fromX1, int fromY1, int fromX2, int fromY2, int toX1, int toY1) {
        img.copyFlippedXFrom(from, fromX1 * scale, fromY1 * scale, fromX2 * scale, fromY2 * scale, toX1 * scale, toY1 * scale);
    }
    
    public Image scale(int width, int height) {
        return img.scale(width, height);
    }
    
    private static int getSkinResolution(Image skin) {
        return skin.getWidth() / 64;
    }
}
