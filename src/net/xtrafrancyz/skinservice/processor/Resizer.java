package net.xtrafrancyz.skinservice.processor;

import net.xtrafrancyz.skinservice.SkinService;
import net.xtrafrancyz.skinservice.util.ImageUtil;
import net.xtrafrancyz.skinservice.util.Log;

import java.awt.image.BufferedImage;

/**
 * @author xtrafrancyz
 */
public class Resizer {
    public static BufferedImage getSkin(String username, boolean orDefault, int width, int height) {
        BufferedImage skin = SkinService.instance().skinRepository.getSkin(username, orDefault);
        if (width == skin.getWidth() && height == skin.getHeight())
            return skin;
        
        if (width == 64 && height == 32) {
            if (skin.getWidth() == 64 && skin.getHeight() == 64) {
                BufferedImage result = new BufferedImage(width, height, SkinService.DEFAULT_IMAGE_TYPE);
                ImageUtil.copy(skin, 0, 0, 64, 32, result, 0, 0);
                return result;
            }
        }
        Log.warning("Can't transform akin of user '" + username + "' from " + skin.getWidth() + "x" + skin.getHeight() + " to " + width + "x" + height);
        return skin;
    }
    
    public static BufferedImage getCape(String username, int width, int height) {
        BufferedImage cape = SkinService.instance().skinRepository.getCape(username);
        if (cape == null)
            return null;
        if (width == cape.getWidth() && height == cape.getHeight())
            return cape;
        
        if (width == 64 && height == 32) {
            if (cape.getWidth() == 22 && cape.getHeight() == 17) {
                BufferedImage result = new BufferedImage(width, height, SkinService.DEFAULT_IMAGE_TYPE);
                ImageUtil.copy(cape, 0, 0, 22, 17, result, 0, 0);
                return result;
            }
        } else if (width == 22 && height == 17) {
            if (cape.getWidth() == 64 && cape.getHeight() == 32) {
                BufferedImage result = new BufferedImage(width, height, SkinService.DEFAULT_IMAGE_TYPE);
                ImageUtil.copy(cape, 0, 0, 22, 17, result, 0, 0);
                return result;
            }
        }
        Log.warning("Can't transform cape of user '" + username + "' from " + cape.getWidth() + "x" + cape.getHeight() + " to " + width + "x" + height);
        return cape;
    }
}
