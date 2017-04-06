package net.xtrafrancyz.skinservice.repository;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import net.xtrafrancyz.skinservice.Config;
import net.xtrafrancyz.skinservice.SkinService;
import net.xtrafrancyz.skinservice.processor.Image;
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
    private LoadingCache<String, ImageContainer> skins;
    private LoadingCache<String, ImageContainer> capes;
    
    private Access access;
    private String skinPath;
    private String capePath;
    private Image defaultSkin;
    
    public SkinRepository(SkinService service) {
        Config.RepositoryConfig config = service.config.repository;
        
        access = Access.valueOf(config.type);
        skinPath = config.skinPath;
        capePath = config.capePath;
        
        try {
            defaultSkin = new Image(ImageIO.read(new File(config.defaultSkin)));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Cannot load default skin");
        }
        
        skins = Caffeine.newBuilder()
            .maximumSize(config.cacheSize)
            .expireAfterAccess(config.cacheExpireMinutes, TimeUnit.MINUTES)
            .build(username -> new ImageContainer(this.fetch(skinPath + username + ".png")));
        
        capes = Caffeine.newBuilder()
            .maximumSize(config.cacheSize)
            .expireAfterAccess(config.cacheExpireMinutes, TimeUnit.MINUTES)
            .build(username -> new ImageContainer(this.fetch(capePath + username + ".png")));
        
        ImageIO.setUseCache(false);
    }
    
    public Image getSkin(String username, boolean orDefault) {
        ImageContainer img = skins.get(username);
        if (img.img == null && orDefault)
            return defaultSkin;
        else
            return img.img;
    }
    
    public Image getCape(String username) {
        return capes.get(username).img;
    }
    
    public void invalidateCape(String username) {
        capes.invalidate(username);
    }
    
    public void invalidateSkin(String username) {
        skins.invalidate(username);
    }
    
    private Image fetch(String path) {
        BufferedImage img = null;
        try {
            if (access == Access.URL)
                img = ImageIO.read(new URL(path));
            else if (access == Access.FILE)
                img = ImageIO.read(new File(path));
        } catch (Exception ignored) {}
        
        Log.info("Fetched: " + path);
        
        if (img == null)
            return null;
        else
            return new Image(img);
    }
    
    private enum Access {
        URL,
        FILE
    }
    
    private static class ImageContainer {
        final Image img;
        
        public ImageContainer(Image img) {
            this.img = img;
        }
    }
}
