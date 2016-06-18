package net.xtrafrancyz.skinservice.repository;

import net.xtrafrancyz.skinservice.Config;
import net.xtrafrancyz.skinservice.SkinService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * @author xtrafrancyz
 */
public class SkinRepository {
    private HashMap<String, BufferedImage> skins;
    private HashMap<String, BufferedImage> capes;
    
    private Access access;
    private String skinPath;
    private String capePath;
    private BufferedImage defaultSkin;
    
    public SkinRepository(SkinService service) {
        Config.RepositoryConfig config = service.config.repository;
        access = Access.valueOf(config.type);
        skinPath = config.skinPath;
        capePath = config.capePath;
        try {
            defaultSkin = ImageIO.read(new File(config.defaultSkin));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot load default skin");
        }
        
        skins = new HashMap<>();
        capes = new HashMap<>();
    }
    
    public BufferedImage getSkin(String username, boolean orDefault) {
        BufferedImage img = null;
        if (!skins.containsKey(username)) {
            try {
                img = fetch(skinPath + username + ".png");
            } catch (IOException ignored) {}
            skins.put(username, img);
        } else {
            img = skins.get(username);
        }
        if (img == null && orDefault)
            return defaultSkin;
        else
            return img;
    }
    
    public BufferedImage getCape(String username) {
        BufferedImage img = null;
        if (!capes.containsKey(username)) {
            try {
                img = fetch(capePath + username + ".png");
            } catch (IOException ignored) {}
            capes.put(username, img);
        } else {
            img = capes.get(username);
        }
        return img;
    }
    
    public void clearCapeCache(String username) {
        capes.remove(username);
    }
    
    public void clearSkinCache(String username) {
        skins.remove(username);
    }
    
    private BufferedImage fetch(String path) throws IOException {
        BufferedImage img = null;
        if (access == Access.URL)
            img = ImageIO.read(new URL(path));
        else if (access == Access.FILE)
            img = ImageIO.read(new File(path));
        return img;
    }
    
    private enum Access {
        URL,
        FILE
    }
}
