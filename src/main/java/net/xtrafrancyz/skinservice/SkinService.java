package net.xtrafrancyz.skinservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import ro.pippo.core.Pippo;
import ro.pippo.undertow.UndertowServer;

import net.xtrafrancyz.skinservice.pippo.SkinApplication;
import net.xtrafrancyz.skinservice.repository.SkinRepository;
import net.xtrafrancyz.skinservice.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/**
 * @author xtrafrancyz
 */
public class SkinService {
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
        File confFile = new File("config.json");
        if (!confFile.exists()) {
            this.config = new Config();
            JsonWriter writer = new JsonWriter(new FileWriter(confFile));
            writer.setIndent("  ");
            writer.setHtmlSafe(false);
            gson.toJson(config, Config.class, writer);
            writer.close();
            Log.info("Created config.json");
        } else {
            this.config = gson.fromJson(
                Files.readAllLines(confFile.toPath()).stream()
                    .map(String::trim)
                    .filter(s -> !s.startsWith("#") && !s.isEmpty())
                    .reduce((a, b) -> a += b)
                    .orElse(""),
                Config.class
            );
        }
    }
    
    public static void main(String[] args) throws Exception {
        instance = new SkinService();
    }
    
    public static SkinService instance() {
        return instance;
    }
}
