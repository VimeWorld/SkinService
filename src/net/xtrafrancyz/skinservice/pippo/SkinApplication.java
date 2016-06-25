package net.xtrafrancyz.skinservice.pippo;

import ro.pippo.core.Application;
import ro.pippo.core.route.RouteContext;

import net.xtrafrancyz.skinservice.SkinService;
import net.xtrafrancyz.skinservice.processor.ImageProcessorLegacy;
import net.xtrafrancyz.skinservice.util.CloudFlareUtil;
import net.xtrafrancyz.skinservice.util.ImageUtil;
import net.xtrafrancyz.skinservice.util.Log;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author xtrafrancyz
 */
public class SkinApplication extends Application {
    public SkinApplication(SkinService service) {
        // ### /head
        GET("/head/{username: [a-zA-z0-9_]+}\\.png", (context) -> {
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.head(username, 160));
        });
        GET("/head/{username: [a-zA-z0-9_]+}/{size: [0-9]+}\\.png", (context) -> {
            int size = context.getParameter("size").toInt(160);
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.head(username, size));
        });
        
        // ### /helm
        GET("/helm/{username: [a-zA-z0-9_]+}\\.png", (context) -> {
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.helm(username, 160));
        });
        GET("/helm/{username: [a-zA-z0-9_]+}/{size: [0-9]+}\\.png", (context) -> {
            int size = context.getParameter("size").toInt(160);
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.helm(username, size));
        });
        
        // ### /body
        GET("/body/{username: [a-zA-z0-9_]+}\\.png", (context) -> {
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.body(username, 160));
        });
        GET("/body/{username: [a-zA-z0-9_]+}/{size: [0-9]+}\\.png", (context) -> {
            int size = context.getParameter("size").toInt(160);
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.body(username, size));
        });
        
        // ### /cape
        GET("/cape/{username}\\.png", (context) -> {
            String username = context.getParameter("username").toString();
            writeImage(context, ImageProcessorLegacy.cape(username));
        });
        
        // ### /raw/skin
        GET("/raw/skin/{username}\\.png", (context) -> {
            String username = context.getParameter("username").toString();
            BufferedImage skin = service.skinRepository.getSkin(username, false);
            if (skin == null) {
                context.getResponse().status(404);
                context.getResponse().send("404 Not found");
            } else {
                writeImage(context, ImageUtil.toByteArray(skin));
            }
        });
        
        // ### /raw/cape
        GET("/raw/cape/{username}\\.png", (context) -> {
            String username = context.getParameter("username").toString();
            BufferedImage cape = service.skinRepository.getCape(username);
            if (cape == null) {
                context.getResponse().status(404);
                context.getResponse().send("404 Not found");
            } else {
                writeImage(context, ImageUtil.toByteArray(cape));
            }
        });
        
        // ### /private/0 auth
        ALL("/private/{token}/.*", (context) -> {
            String token = context.getParameter("token").toString();
            if (service.config.tokens.contains(token)) {
                context.next();
            } else {
                context.status(403);
                context.send("Invalid token");
            }
        });
        
        // ### /private/0/cache/cape
        DELETE("/private/{token}/cache/cape/{username: [a-zA-z0-9_]+}", (context) -> {
            String username = context.getParameter("username").toString();
            service.skinRepository.clearCapeCache(username);
            CloudFlareUtil.clearCache(
                "/raw/cape/" + username + ".png",
                "/cape/" + username + ".png"
            );
            context.status(200);
            context.send("OK");
        });
        
        // ### /private/0/cache/skin
        DELETE("/private/{token}/cache/skin/{username: [a-zA-z0-9_]+}", (context) -> {
            String username = context.getParameter("username").toString();
            service.skinRepository.clearSkinCache(username);
            CloudFlareUtil.clearCache(
                "/raw/skin/" + username + ".png",
                "/helm/" + username + ".png",
                "/head/" + username + ".png",
                "/body/" + username + ".png"
            );
            context.status(200);
            context.send("OK");
        });
        
        ALL(".*", (context) -> {
            Log.info(context.getRequestMethod() + " " + context.getRequestUri());
        }).runAsFinally();
        
        setErrorHandler(new SkinErrorHandler(this));
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
}
