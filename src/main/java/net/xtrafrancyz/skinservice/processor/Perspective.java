package net.xtrafrancyz.skinservice.processor;

import net.xtrafrancyz.skinservice.SkinService;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * @author xtrafrancyz
 */
public class Perspective {
    public static Image head(String username, int size, boolean helm) {
        if (size > 300)
            size = 300;
        
        float scale = size / 20f;
        
        Image skin = SkinService.instance().skinRepository.getSkin(username, true);
        
        Image image = new Image(size, size);
        Graphics2D graphics = (Graphics2D) image.getHandle().getGraphics();
        graphics.translate(0, 0.5 * scale);
        
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform transform = new AffineTransform();
        
        double helmOffsetX = -0.9;
        double helmOffsetY = -0.9;
        
        if (helm) {
            // helm back
            transform.setToIdentity();
            transform.scale(scale * 1.1, scale * 1.1);
            transform.translate(9.1, -0.68);
            transform.shear(0, -0.5);
            transform.scale(-1.06, 1.29);
            graphics.drawImage(darken(skin.getHandle().getSubimage(56, 8, 8, 8)), transform, null);
            
            // helm left
            transform.setToIdentity();
            transform.scale(scale * 1.1, scale * 1.1);
            transform.translate(17.57, 3.55);
            transform.shear(0, 0.5);
            transform.scale(-1.06, 1.29);
            graphics.drawImage(darken(skin.getHandle().getSubimage(48, 8, 8, 8)), transform, null);
        }
        
        // front
        transform.setToIdentity();
        transform.scale(scale, scale);
        transform.translate(9.99, 8.68);
        transform.shear(0, -0.5);
        transform.scale(1.06, 1.29);
        graphics.drawImage(lighten(skin.getHandle().getSubimage(8, 8, 8, 8)), transform, null);
        
        // top
        transform.setToIdentity();
        transform.scale(scale, scale);
        transform.translate(1.51, 4.44);
        transform.scale(1.5, 0.75);
        transform.rotate(-Math.toRadians(45));
        graphics.drawImage(skin.getHandle().getSubimage(8, 0, 8, 8), transform, null);
        
        // right
        transform.setToIdentity();
        transform.scale(scale, scale);
        transform.translate(1.505, 4.44);
        transform.shear(0, 0.5);
        transform.scale(1.06, 1.29);
        graphics.drawImage(darken(skin.getHandle().getSubimage(0, 8, 8, 8)), transform, null);
        
        if (helm) {
            // helm front
            transform.setToIdentity();
            transform.scale(scale * 1.1, scale * 1.1);
            transform.translate(9.99 + helmOffsetX, 8.68 + helmOffsetY);
            transform.shear(0, -0.5);
            transform.scale(1.06, 1.29);
            graphics.drawImage(lighten(skin.getHandle().getSubimage(40, 8, 8, 8)), transform, null);
            
            // helm top
            transform.setToIdentity();
            transform.scale(scale * 1.1, scale * 1.1);
            transform.translate(1.52 + helmOffsetX, 4.46 + helmOffsetY);
            transform.scale(1.5, 0.75);
            transform.rotate(-Math.toRadians(45));
            graphics.drawImage(skin.getHandle().getSubimage(40, 0, 8, 8), transform, null);
            
            // helm right
            transform.setToIdentity();
            transform.scale(scale * 1.1, scale * 1.1);
            transform.translate(1.505 + helmOffsetX, 4.44 + helmOffsetY);
            transform.shear(0, 0.5);
            transform.scale(1.06, 1.29);
            graphics.drawImage(darken(skin.getHandle().getSubimage(32, 8, 8, 8)), transform, null);
        }
        
        return image;
    }
    
    private static BufferedImage lighten(BufferedImage img) {
        BufferedImage clone = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        clone.getGraphics().drawImage(img, 0, 0, null);
        int[] pixels = ((DataBufferInt) clone.getRaster().getDataBuffer()).getData();
        int argb;
        for (int i = 0; i < pixels.length; i++) {
            argb = pixels[i];
            pixels[i] = (argb & 0xff000000) |
                (clamp((int) (((argb >> 16) & 0xff) * 1.05f)) << 16) |
                (clamp((int) (((argb >> 8) & 0xff) * 1.05f)) << 8) |
                clamp((int) ((argb & 0xff) * 1.05f));
        }
        return clone;
    }
    
    private static BufferedImage darken(BufferedImage img) {
        BufferedImage clone = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        clone.getGraphics().drawImage(img, 0, 0, null);
        int[] pixels = ((DataBufferInt) clone.getRaster().getDataBuffer()).getData();
        int argb;
        for (int i = 0; i < pixels.length; i++) {
            argb = pixels[i];
            pixels[i] = (argb & 0xff000000) |
                ((int) (((argb >> 16) & 0xff) * 0.95f) << 16) |
                ((int) (((argb >> 8) & 0xff) * 0.95f) << 8) |
                (int) ((argb & 0xff) * 0.95f);
        }
        return clone;
    }
    
    private static int clamp(int c) {
        if (c < 0)
            return c;
        return Math.min(255, c);
    }
}
