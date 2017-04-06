package net.xtrafrancyz.skinservice.processor;

import net.xtrafrancyz.skinservice.SkinService;
import net.xtrafrancyz.skinservice.util.Log;

/**
 * @author xtrafrancyz
 */
public class Resizer {
    public static Image getSkin(String username, boolean orDefault, int width, int height) {
        Image skin = SkinService.instance().skinRepository.getSkin(username, orDefault);
        if (width == skin.getWidth() && height == skin.getHeight())
            return skin;
        
        if (width == 64 && height == 32) {
            if (skin.getWidth() == 64 && skin.getHeight() == 64) {
                Image result = new Image(width, height);
                result.copyFrom(skin, 0, 0, 64, 32);
                return result;
            }
        }
        Log.warning("Can't transform akin of user '" + username + "' from " + skin.getWidth() + "x" + skin.getHeight() + " to " + width + "x" + height);
        return skin;
    }
    
    public static Image getCape(String username, int width, int height) {
        Image cape = SkinService.instance().skinRepository.getCape(username);
        if (cape == null)
            return null;
        if (width == cape.getWidth() && height == cape.getHeight())
            return cape;
        
        if (width == 64 && height == 32) {
            if (cape.getWidth() == 22 && cape.getHeight() == 17) {
                Image result = new Image(width, height);
                result.copyFrom(cape, 0, 0, 22, 17);
                return result;
            }
        } else if (width == 22 && height == 17) {
            if (cape.getWidth() == 64 && cape.getHeight() == 32) {
                Image result = new Image(width, height);
                result.copyFrom(cape, 0, 0, 22, 17);
                return result;
            }
        }
        Log.warning("Can't transform cape of user '" + username + "' from " + cape.getWidth() + "x" + cape.getHeight() + " to " + width + "x" + height);
        return cape;
    }
}
