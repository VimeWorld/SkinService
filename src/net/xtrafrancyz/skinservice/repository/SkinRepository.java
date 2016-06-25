package net.xtrafrancyz.skinservice.repository;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import net.xtrafrancyz.skinservice.Config;
import net.xtrafrancyz.skinservice.SkinService;
import net.xtrafrancyz.skinservice.util.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @author xtrafrancyz
 */
public class SkinRepository {
    private LoadingCache<String, BufferedImage> skins;
    private LoadingCache<String, BufferedImage> capes;
    
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
        
        skins = Caffeine.newBuilder()
            .maximumSize(config.cacheSize)
            .expireAfterAccess(config.cacheExpireMinutes, TimeUnit.MINUTES)
            .build(username -> this.fetch(skinPath + username + ".png"));
        
        capes = Caffeine.newBuilder()
            .maximumSize(config.cacheSize)
            .expireAfterAccess(config.cacheExpireMinutes, TimeUnit.MINUTES)
            .build(username -> this.fetch(capePath + username + ".png"));
        
        ImageIO.setUseCache(false);
    }
    
    public BufferedImage getSkin(String username, boolean orDefault) {
        BufferedImage img = skins.get(username);
        if (img == null && orDefault)
            return defaultSkin;
        else
            return img;
    }
    
    public BufferedImage getCape(String username) {
        return capes.get(username);
    }
    
    public void clearCapeCache(String username) {
        capes.invalidate(username);
    }
    
    public void clearSkinCache(String username) {
        skins.invalidate(username);
    }
    
    private BufferedImage fetch(String path) {
        BufferedImage img = null;
        try {
            if (access == Access.URL)
                img = ImageIO.read(new URL(path));
            else if (access == Access.FILE)
                img = ImageIO.read(new File(path));
            
            // Преобразование типа изображения
            if (img != null && img.getType() != BufferedImage.TYPE_INT_ARGB) {
                BufferedImage temp = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
                temp.getGraphics().drawImage(img, 0, 0, null);
                img = temp;
            }
        } catch (Exception ignored) {}
        
        Log.info("Fetched: " + path);
        
        return img;
    }
    
    private enum Access {
        URL,
        FILE
    }
}
