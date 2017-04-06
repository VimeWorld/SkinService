package net.xtrafrancyz.skinservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ro.pippo.core.Pippo;
import ro.pippo.undertow.UndertowServer;

import net.xtrafrancyz.skinservice.pippo.SkinApplication;
import net.xtrafrancyz.skinservice.repository.SkinRepository;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/**
 * @author xtrafrancyz
 */
public class SkinService {
    public static final int DEFAULT_IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;
    
    private static SkinService instance;
    
    public final Gson gson = new GsonBuilder().create();
    public Config config;
    public Pippo pippo;
    public SkinRepository skinRepository;
    
    private SkinService() throws IOException {
        readConfig();
        skinRepository = new SkinRepository(this);
        
        UndertowServer.class.getName();
        
        pippo = new Pippo(new SkinApplication(this));
        pippo.getServer().getSettings().host(config.host);
        pippo.getServer().getSettings().port(config.port);
        pippo.start();
        
        System.out.println("Listening at: http://" + config.host + ":" + config.port);
    }
    
    public void readConfig() throws IOException {
        this.config = gson.fromJson(
            Files.readAllLines(FileSystems.getDefault().getPath("config.json")).stream()
                .map(String::trim)
                .filter(s -> !s.startsWith("#") && !s.isEmpty())
                .reduce((a, b) -> a += b)
                .orElse(""),
            Config.class
        );
    }
    
    public static void main(String[] args) throws Exception {
        instance = new SkinService();
    }
    
    public static SkinService instance() {
        return instance;
    }
}
