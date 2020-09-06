package net.xtrafrancyz.skinservice;

import ch.qos.logback.classic.Level;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.Pippo;
import ro.pippo.core.RuntimeMode;
import ro.pippo.undertow.UndertowServer;

import net.xtrafrancyz.skinservice.pippo.SkinApplication;
import net.xtrafrancyz.skinservice.repository.SkinRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author xtrafrancyz
 */
public class SkinService {
    private static final Logger LOG = LoggerFactory.getLogger(SkinService.class);
    private static SkinService instance;
    
    public final Gson gson = new GsonBuilder().create();
    public Config config;
    public Pippo pippo;
    public SkinRepository skinRepository;
    
    private SkinService() throws IOException {
        if (RuntimeMode.getCurrent() == RuntimeMode.DEV)
            ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger("net.xtrafrancyz.skinservice")).setLevel(Level.TRACE);
        
        readConfig();
        skinRepository = new SkinRepository(this);
        
        UndertowServer.class.getName();
        
        pippo = new Pippo(new SkinApplication(this));
        pippo.getServer().getSettings().host(config.host);
        pippo.getServer().getSettings().port(config.port);
        pippo.start();
        
        LOG.info("Listening at: http://{}:{}", config.host, config.port);
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
            LOG.info("Created config.json");
        } else {
            this.config = gson.fromJson(
                Files.readAllLines(confFile.toPath()).stream()
                    .map(String::trim)
                    .filter(s -> !s.startsWith("#") && !s.startsWith("//") && !s.isEmpty())
                    .reduce((a, b) -> a += b)
                    .orElse(""),
                Config.class
            );
        }
    }
    
    public static void main(String[] args) {
        try {
            instance = new SkinService();
        } catch (Exception ex) {
            LOG.error("Application cannot be started", ex);
        }
    }
    
    public static SkinService instance() {
        return instance;
    }
}
