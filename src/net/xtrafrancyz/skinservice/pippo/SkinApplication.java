package net.xtrafrancyz.skinservice.pippo;

import ro.pippo.core.Application;
import ro.pippo.core.route.RouteContext;

import net.xtrafrancyz.skinservice.SkinService;
import net.xtrafrancyz.skinservice.processor.Humanizer;
import net.xtrafrancyz.skinservice.processor.Resizer;
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
        if (service.config.debug) {
            ALL(".*", context -> {
                context.setLocal("_start", System.nanoTime());
                context.next();
            });
        }
        
        // ### /head
        GET("/head/{username: [a-zA-z0-9_]+}\\.png", context -> {
            String username = context.getParameter("username").toString();
            writeImage(context, Humanizer.head(username, 160));
        });
        GET("/head/{username: [a-zA-z0-9_]+}/{size: [0-9]+}\\.png", context -> {
            int size = context.getParameter("size").toInt(160);
            String username = context.getParameter("username").toString();
            writeImage(context, Humanizer.head(username, size));
        });
        
        
        // ### /helm
        GET("/helm/{username: [a-zA-z0-9_]+}\\.png", context -> {
            String username = context.getParameter("username").toString();
            writeImage(context, Humanizer.helm(username, 160));
        });
        GET("/helm/{username: [a-zA-z0-9_]+}/{size: [0-9]+}\\.png", context -> {
            int size = context.getParameter("size").toInt(160);
            String username = context.getParameter("username").toString();
            writeImage(context, Humanizer.helm(username, size));
        });
        
        
        // ### /body
        GET("/body/{username: [a-zA-z0-9_]+}\\.png", context -> {
            String username = context.getParameter("username").toString();
            writeImage(context, Humanizer.body(username, 160));
        });
        GET("/body/{username: [a-zA-z0-9_]+}/{size: [0-9]+}\\.png", context -> {
            int size = context.getParameter("size").toInt(160);
            String username = context.getParameter("username").toString();
            writeImage(context, Humanizer.body(username, size));
        });
        
        
        // ### /cape
        GET("/cape/{username: [a-zA-z0-9_]+}\\.png", context -> {
            String username = context.getParameter("username").toString();
            writeImage(context, Humanizer.cape(username));
        });
        
        
        // ### /game
        GET("/game/v1/skin/{username: [a-zA-z0-9_]+}\\.png", context -> {
            String username = context.getParameter("username").toString();
            writeImage(context, Resizer.getSkin(username, true, 64, 32));
        });
        
        GET("/game/v1/cape/{username: [a-zA-z0-9_]+}\\.png", context -> {
            String username = context.getParameter("username").toString();
            writeImage(context, Resizer.getCape(username, 22, 17));
        });
        
        GET("/game/v2/skin/{username: [a-zA-z0-9_]+}\\.png", context -> {
            String username = context.getParameter("username").toString();
            writeImage(context, service.skinRepository.getSkin(username, true));
        });
        
        GET("/game/v2/cape/{username: [a-zA-z0-9_]+}\\.png", context -> {
            String username = context.getParameter("username").toString();
            writeImage(context, Resizer.getCape(username, 64, 32));
        });
        
        
        // ### /private/0 auth
        ALL("/private/{token}/.*", context -> {
            String token = context.getParameter("token").toString();
            if (service.config.tokens.contains(token)) {
                context.next();
            } else {
                context.status(403);
                context.send("Invalid token");
            }
        });
        
        // ### /private/0/cache/cape
        DELETE("/private/{token}/cache/cape/{username: [a-zA-z0-9_]+}", context -> {
            String username = context.getParameter("username").toString();
            service.skinRepository.invalidateCape(username);
            CloudFlareUtil.clearCache(
                "/game/v1/cape/" + username + ".png",
                "/game/v2/cape/" + username + ".png",
                "/cape/" + username + ".png"
            );
            context.status(200);
            context.send("OK");
        });
        
        // ### /private/0/cache/skin
        DELETE("/private/{token}/cache/skin/{username: [a-zA-z0-9_]+}", context -> {
            String username = context.getParameter("username").toString();
            service.skinRepository.invalidateSkin(username);
            CloudFlareUtil.clearCache(
                "/game/v1/skin/" + username + ".png",
                "/game/v2/skin/" + username + ".png",
                "/helm/" + username + ".png",
                "/head/" + username + ".png",
                "/body/" + username + ".png"
            );
            context.status(200);
            context.send("OK");
        });
        
        ALL(".*", context -> {
            if (service.config.debug) {
                long start = context.getLocal("_start");
                Log.info("[" + Math.round((System.nanoTime() - start) / 10000f) / 100f + " ms] " + context.getRequestMethod() + " " + context.getRequestUri());
            } else {
                Log.info(context.getRequestMethod() + " " + context.getRequestUri());
            }
            context.next();
        }).runAsFinally();
        
        setErrorHandler(new SkinErrorHandler(this));
    }
    
    private static void writeImage(RouteContext context, BufferedImage image) {
        writeImage(context, ImageUtil.toByteArray(image));
    }
    
    private static void writeImage(RouteContext context, byte[] image) {
        if (image == null) {
            context.getResponse().status(404);
            context.getResponse().send("404 Not found");
            return;
        }
        context.setHeader("Content-Type", "image/png");
        context.getResponse().ok();
        try {
            context.getResponse().getOutputStream().write(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
