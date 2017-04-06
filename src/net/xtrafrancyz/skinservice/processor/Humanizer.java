package net.xtrafrancyz.skinservice.processor;

import net.xtrafrancyz.skinservice.SkinService;
import net.xtrafrancyz.skinservice.util.ImageUtil;

import java.awt.image.BufferedImage;

/**
 * @author xtrafrancyz
 */
public class Humanizer {
    public static byte[] head(String username, int size) {
        if (size > 300)
            size = 300;
        BufferedImage skin = SkinService.instance().skinRepository.getSkin(username, true);
        BufferedImage img = new BufferedImage(8, 8, SkinService.DEFAULT_IMAGE_TYPE);
        
        ImageUtil.copy(skin, 8, 8, 16, 16, img, 0, 0);
        img = ImageUtil.scale(img, size, size);
        
        return ImageUtil.toByteArray(img);
    }
    
    public static byte[] helm(String username, int size) {
        if (size > 300)
            size = 300;
        BufferedImage skin = SkinService.instance().skinRepository.getSkin(username, true);
        BufferedImage img = new BufferedImage(8, 8, SkinService.DEFAULT_IMAGE_TYPE);
        
        ImageUtil.copy(skin, 8, 8, 16, 16, img, 0, 0);
        ImageUtil.copyWithAlpha(skin, 40, 8, 48, 16, img, 0, 0);
        img = ImageUtil.scale(img, size, size);
        
        return ImageUtil.toByteArray(img);
    }
    
    public static byte[] body(String username, int size) {
        if (size > 300)
            size = 300;
        BufferedImage skin = SkinService.instance().skinRepository.getSkin(username, true);
        BufferedImage img = new BufferedImage(16, 32, SkinService.DEFAULT_IMAGE_TYPE);
        
        ImageUtil.copy(skin, 8, 8, 16, 16, img, 4, 0); // head
        ImageUtil.copy(skin, 44, 20, 48, 32, img, 0, 8); // left arm (real right)
        ImageUtil.copy(skin, 20, 20, 28, 32, img, 4, 8); // body
        ImageUtil.copyFlippedX(skin, 44, 20, 48, 32, img, 12, 8); // right arm (real left)
        ImageUtil.copy(skin, 4, 20, 8, 32, img, 4, 20); // left leg (real right)
        ImageUtil.copyFlippedX(skin, 4, 20, 8, 32, img, 8, 20); // right leg (real right)
        
        img = ImageUtil.scale(img, size, size * 2);
        
        return ImageUtil.toByteArray(img);
    }
    
    public static byte[] cape(String username) {
        BufferedImage cape = SkinService.instance().skinRepository.getCape(username);
        if (cape == null)
            return null;
        
        BufferedImage img = new BufferedImage(10, 16, SkinService.DEFAULT_IMAGE_TYPE);
        ImageUtil.copy(cape, 1, 1, 11, 17, img, 0, 0);
        img = ImageUtil.scale(img, 150, 240);
        return ImageUtil.toByteArray(img);
    }
}
