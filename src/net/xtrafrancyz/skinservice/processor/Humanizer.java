package net.xtrafrancyz.skinservice.processor;

import net.xtrafrancyz.skinservice.SkinService;

/**
 * @author xtrafrancyz
 */
public class Humanizer {
    public static Image head(String username, int size) {
        if (size > 300)
            size = 300;
        Image skin = SkinService.instance().skinRepository.getSkin(username, true);
        Image img = new Image(8, 8);
        
        img.copyFrom(skin, 8, 8, 16, 16, 0, 0);
        
        return img.scale(size, size);
    }
    
    public static Image helm(String username, int size) {
        if (size > 300)
            size = 300;
        Image skin = SkinService.instance().skinRepository.getSkin(username, true);
        Image img = new Image(8, 8);
        
        img.copyFrom(skin, 8, 8, 16, 16, 0, 0);
        img.copyWithAlphaFrom(skin, 40, 8, 48, 16, 0, 0);
        
        return img.scale(size, size);
    }
    
    public static Image body(String username, int size) {
        if (size > 300)
            size = 300;
        Image skin = SkinService.instance().skinRepository.getSkin(username, true);
        Image img = new Image(16, 32);
        
        img.copyFrom(skin, 8, 8, 16, 16, 4, 0); // head
        img.copyFrom(skin, 44, 20, 48, 32, 0, 8); // left arm (real right)
        img.copyFrom(skin, 20, 20, 28, 32, 4, 8); // body
        img.copyFlippedXFrom(skin, 44, 20, 48, 32, 12, 8); // right arm (real left)
        img.copyFrom(skin, 4, 20, 8, 32, 4, 20); // left leg (real right)
        img.copyFlippedXFrom(skin, 4, 20, 8, 32, 8, 20); // right leg (real right)
        
        return img.scale(size, size * 2);
    }
    
    public static Image cape(String username) {
        Image cape = SkinService.instance().skinRepository.getCape(username);
        if (cape == null)
            return null;
        Image img = new Image(10, 16);
        
        img.copyFrom(cape, 1, 1, 11, 17, 0, 0);
        
        return img.scale(150, 240);
    }
}
