package net.xtrafrancyz.skinservice.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.xtrafrancyz.skinservice.SkinService;

/**
 * @author xtrafrancyz
 */
public class Resizer {
    private static final Logger LOG = LoggerFactory.getLogger(Resizer.class);
    
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
        LOG.warn("Can't transform skin of user '{}' from {}x{} to {}x{}", username, skin.getWidth(), skin.getHeight(), width, height);
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
        LOG.warn("Can't transform cape of user '{}' from {}x{} to {}x{}", username, cape.getWidth(), cape.getHeight(), width, height);
        return cape;
    }
}
