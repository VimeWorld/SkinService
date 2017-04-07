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
        
        img.copyFrom(skin, 8, 8, 16, 16);
        
        return img.scale(size, size);
    }
    
    public static Image helm(String username, int size) {
        if (size > 300)
            size = 300;
        Image skin = SkinService.instance().skinRepository.getSkin(username, true);
        Image img = new Image(8, 8);
        
        img.copyFrom(skin, 8, 8, 16, 16);
        img.copyWithAlphaFrom(skin, 40, 8, 48, 16);
        
        return img.scale(size, size);
    }
    
    public static Image body(String username, int size) {
        if (size > 300)
            size = 300;
        Image skin = SkinService.instance().skinRepository.getSkin(username, true);
        Image img = new Image(16, 32);
        
        // Head
        img.copyFrom(skin, 8, 8, 16, 16, 4, 0);
        // Torso
        img.copyFrom(skin, 20, 20, 28, 32, 4, 8);
        
        // Left arm
        img.copyFrom(skin, 44, 20, 48, 32, 0, 8);
        // Right arm
        if (skin.getHeight() == 64)
            img.copyFrom(skin, 36, 52, 40, 64, 12, 8);
        else
            img.copyFlippedXFrom(skin, 44, 20, 48, 32, 12, 8);
        
        // Left leg
        img.copyFrom(skin, 4, 20, 8, 32, 4, 20);
        // Right leg
        if (skin.getHeight() == 64)
            img.copyFrom(skin, 20, 52, 24, 64, 8, 20);
        else
            img.copyFlippedXFrom(skin, 4, 20, 8, 32, 8, 20);
        
        
        return img.scale(size, size * 2);
    }
    
    public static Image cape(String username) {
        Image cape = SkinService.instance().skinRepository.getCape(username);
        if (cape == null)
            return null;
        Image img = new Image(10, 16);
        
        img.copyFrom(cape, 1, 1, 11, 17);
        
        return img.scale(150, 240);
    }
}