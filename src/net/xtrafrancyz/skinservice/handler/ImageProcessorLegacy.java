package net.xtrafrancyz.skinservice.handler;

import net.xtrafrancyz.skinservice.SkinService;
import net.xtrafrancyz.skinservice.util.ImageUtil;

import java.awt.image.BufferedImage;

/**
 * @author xtrafrancyz
 */
public class ImageProcessorLegacy {
    public static byte[] head(String username, int size) {
        if (size > 300)
            size = 300;
        BufferedImage skin = SkinService.instance().skinRepository.getSkin(username, true);
        BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        
        ImageUtil.copy(skin, 8, 8, 16, 16, img, 0, 0);
        img = ImageUtil.scale(img, size, size);
        
        return ImageUtil.toByteArray(img);
    }
    
    public static byte[] helm(String username, int size) {
        if (size > 300)
            size = 300;
        BufferedImage skin = SkinService.instance().skinRepository.getSkin(username, true);
        BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
        
        ImageUtil.copy(skin, 8, 8, 16, 16, img, 0, 0);
        ImageUtil.copyWithAltha(skin, 40, 8, 48, 16, img, 0, 0);
        img = ImageUtil.scale(img, size, size);
        
        return ImageUtil.toByteArray(img);
    }
    
    public static byte[] body(String username, int size) {
        if (size > 300)
            size = 300;
        BufferedImage skin = SkinService.instance().skinRepository.getSkin(username, true);
        BufferedImage img = new BufferedImage(16, 32, BufferedImage.TYPE_INT_ARGB);
        
        ImageUtil.copy(skin, 8, 8, 16, 16, img, 4, 0); // head
        ImageUtil.copy(skin, 44, 20, 48, 32, img, 0, 8); // left arm (real right)
        ImageUtil.copy(skin, 20, 20, 28, 32, img, 4, 8); // body
        ImageUtil.copyFlippedX(skin, 44, 20, 48, 32, img, 12, 8); // right arm (real left)
        ImageUtil.copy(skin, 4, 20, 8, 32, img, 4, 20); // left leg (real right)
        ImageUtil.copyFlippedX(skin, 4, 20, 8, 32, img, 8, 20); // right leg (real right)
        
        img = ImageUtil.scale(img, size, size * 2);
        
        return ImageUtil.toByteArray(img);
    }
}
