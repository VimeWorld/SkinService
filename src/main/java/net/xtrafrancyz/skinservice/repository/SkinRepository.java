package net.xtrafrancyz.skinservice.repository;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.xtrafrancyz.skinservice.Config;
import net.xtrafrancyz.skinservice.SkinService;
import net.xtrafrancyz.skinservice.processor.Image;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @author xtrafrancyz
 */
public class SkinRepository {
    private static final Logger LOG = LoggerFactory.getLogger(SkinRepository.class);
    
    private LoadingCache<String, ImageContainer> skins;
    private LoadingCache<String, ImageContainer> capes;
    
    private Access access;
    private String skinPath;
    private String capePath;
    private Image defaultSkin;
    
    private ImageReader pngReader;
    
    public SkinRepository(SkinService service) {
        Config.RepositoryConfig config = service.config.repository;
        
        access = Access.valueOf(config.type);
        skinPath = config.skinPath;
        capePath = config.capePath;
        
        try {
            defaultSkin = new Image(ImageIO.read(getClass().getResourceAsStream("/char.png")));
        } catch (Exception ex) {
            throw new RuntimeException("Can't load default skin /char.png", ex);
        }
        
        skins = Caffeine.newBuilder()
            .weakValues()
            .expireAfterAccess(config.cacheExpireMinutes, TimeUnit.MINUTES)
            .build(username -> new ImageContainer(this.fetch(skinPath.replace("{username}", username))));
        
        capes = Caffeine.newBuilder()
            .weakValues()
            .expireAfterAccess(config.cacheExpireMinutes, TimeUnit.MINUTES)
            .build(username -> new ImageContainer(this.fetch(capePath.replace("{username}", username))));
        
        ImageIO.setUseCache(false);
        
        pngReader = ImageIO.getImageReadersByFormatName("png").next();
    }
    
    @SuppressWarnings("ConstantConditions")
    public Image getSkin(String username, boolean orDefault) {
        ImageContainer img = skins.get(username);
        if (img.img == null && orDefault)
            return defaultSkin;
        else
            return img.img;
    }
    
    @SuppressWarnings("ConstantConditions")
    public Image getCape(String username) {
        return capes.get(username).img;
    }
    
    public void invalidateCape(String username) {
        LOG.trace("Cape of player {} purged", username);
        capes.invalidate(username);
    }
    
    public void invalidateSkin(String username) {
        LOG.trace("Skin of player {} purged", username);
        skins.invalidate(username);
    }
    
    private Image fetch(String path) {
        BufferedImage img = null;
        try {
            if (access == Access.URL) {
                LOG.debug("Read image from url: {}", path);
                img = ImageIO.read(new URL(path));
            } else if (access == Access.FILE) {
                LOG.debug("Read image from file: {}", path);
                try (ImageInputStream stream = ImageIO.createImageInputStream(new File(path))) {
                    pngReader.setInput(stream);
                    IIOImage image = pngReader.readAll(0, pngReader.getDefaultReadParam());
                    img = (BufferedImage) image.getRenderedImage();
                }
                pngReader.reset();
            }
        } catch (Exception ignored) {}
        
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
