package net.xtrafrancyz.skinservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ro.pippo.core.Pippo;
import ro.pippo.core.route.RouteContext;
import ro.pippo.undertow.UndertowServer;

import net.xtrafrancyz.skinservice.handler.ImageProcessorLegacy;
import net.xtrafrancyz.skinservice.repository.SkinRepository;
import net.xtrafrancyz.skinservice.util.ImageUtil;

import java.awt.image.BufferedImage;
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
        
        pippo = new Pippo();
        pippo.getServer().getSettings().host(config.host);
        pippo.getServer().getSettings().port(config.port);
        
        // ### /head
        pippo.getApplication().GET("/head/{username: [a-zA-z0-9_]+}(\\.png)?", (context) -> {
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.head(username, 160));
        });
        pippo.getApplication().GET("/head/{username: [a-zA-z0-9_]+}/{size: [0-9]+}(\\.png)?", (context) -> {
            int size = context.getParameter("size").toInt(160);
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.head(username, size));
        });
        
        // ### /helm
        pippo.getApplication().GET("/helm/{username: [a-zA-z0-9_]+}(\\.png)?", (context) -> {
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.helm(username, 160));
        });
        pippo.getApplication().GET("/helm/{username: [a-zA-z0-9_]+}/{size: [0-9]+}(\\.png)?", (context) -> {
            int size = context.getParameter("size").toInt(160);
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.helm(username, size));
        });
        
        // ### /body
        pippo.getApplication().GET("/body/{username: [a-zA-z0-9_]+}(\\.png)?", (context) -> {
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.body(username, 160));
        });
        pippo.getApplication().GET("/body/{username: [a-zA-z0-9_]+}/{size: [0-9]+}(\\.png)?", (context) -> {
            int size = context.getParameter("size").toInt(160);
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.body(username, size));
        });
        
        // ### /skin
        pippo.getApplication().GET("/skin/{username}(\\.png)?", (context) -> {
            String username = context.getParameter("username").toString();
            BufferedImage skin = skinRepository.getSkin(username, false);
            if (skin == null) {
                context.getResponse().status(404);
                context.getResponse().send("Not found");
            } else {
                writeImage(context, ImageUtil.toByteArray(skin));
            }
        });
        
        // ### /cape
        pippo.getApplication().GET("/cape/{username}(\\.png)?", (context) -> {
            String username = context.getParameter("username").toString();
            BufferedImage cape = skinRepository.getCape(username);
            if (cape == null) {
                context.getResponse().status(404);
                context.getResponse().send("Not found");
            } else {
                writeImage(context, ImageUtil.toByteArray(cape));
            }
        });
        
        pippo.start();
        
        System.out.println("Listening at: http://" + config.host + ":" + config.port);
    }
    
    private static void writeImage(RouteContext context, byte[] image) {
        context.setHeader("Content-Type", "image/png");
        context.getResponse().ok();
        try {
            context.getResponse().getOutputStream().write(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
