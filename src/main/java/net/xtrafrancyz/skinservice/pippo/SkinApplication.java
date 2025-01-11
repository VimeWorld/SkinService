package net.xtrafrancyz.skinservice.pippo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.pippo.core.Application;
import ro.pippo.core.route.RouteContext;
import ro.pippo.core.route.RouteGroup;

import net.xtrafrancyz.skinservice.SkinService;
import net.xtrafrancyz.skinservice.processor.Humanizer;
import net.xtrafrancyz.skinservice.processor.Image;
import net.xtrafrancyz.skinservice.processor.Perspective;
import net.xtrafrancyz.skinservice.processor.Resizer;
import net.xtrafrancyz.skinservice.util.CloudflareUtil;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author xtrafrancyz
 */
public class SkinApplication extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(SkinApplication.class);
    
    private static final String USERNAME = "{username: [a-zA-Zа-яА-ЯІіЇїЄєЁё0-9_-]+}";
    
    public SkinApplication(SkinService service) {
        boolean detailed = service.config.logDetailedQueries;
        boolean log = service.config.logHttp;
        if (log && detailed) {
            ANY(".*", context -> {
                context.setLocal("_start", System.nanoTime());
                context.next();
            });
        }
        
        // ### /head
        addRouteGroup("/head", group -> {
            group.GET("/3d/" + USERNAME + "\\.png", context -> {
                String username = context.getParameter("username").toString();
                writeImage(context, Perspective.head(username, 160, false));
            });
            group.GET("/3d/" + USERNAME + "/{size: [0-9]+}\\.png", context -> {
                int size = context.getParameter("size").toInt(160);
                String username = context.getParameter("username").toString();
                writeImage(context, Perspective.head(username, size, false));
            });
            group.GET("/" + USERNAME + "\\.png", context -> {
                String username = context.getParameter("username").toString();
                writeImage(context, Humanizer.head(username, 160));
            });
            group.GET("/" + USERNAME + "/{size: [0-9]+}\\.png", context -> {
                int size = context.getParameter("size").toInt(160);
                String username = context.getParameter("username").toString();
                writeImage(context, Humanizer.head(username, size));
            });
        });
        
        
        // ### /helm
        addRouteGroup("/helm", group -> {
            group.GET("/3d/" + USERNAME + "\\.png", context -> {
                String username = context.getParameter("username").toString();
                writeImage(context, Perspective.head(username, 160, true));
            });
            group.GET("/3d/" + USERNAME + "/{size: [0-9]+}\\.png", context -> {
                int size = context.getParameter("size").toInt(160);
                String username = context.getParameter("username").toString();
                writeImage(context, Perspective.head(username, size, true));
            });
            group.GET("/" + USERNAME + "\\.png", context -> {
                String username = context.getParameter("username").toString();
                writeImage(context, Humanizer.helm(username, 160));
            });
            group.GET("/" + USERNAME + "/{size: [0-9]+}\\.png", context -> {
                int size = context.getParameter("size").toInt(160);
                String username = context.getParameter("username").toString();
                writeImage(context, Humanizer.helm(username, size));
            });
        });
        
        
        // ### /body
        GET("/body/" + USERNAME + "\\.png", context -> {
            String username = context.getParameter("username").toString();
            writeImage(context, Humanizer.body(username, 160));
        });
        GET("/body/" + USERNAME + "/{size: [0-9]+}\\.png", context -> {
            int size = context.getParameter("size").toInt(160);
            String username = context.getParameter("username").toString();
            writeImage(context, Humanizer.body(username, size));
        });
        
        
        // ### /back
        GET("/back/" + USERNAME + "\\.png", context -> {
            String username = context.getParameter("username").toString();
            writeImage(context, Humanizer.back(username, 160));
        });
        GET("/back/" + USERNAME + "/{size: [0-9]+}\\.png", context -> {
            int size = context.getParameter("size").toInt(160);
            String username = context.getParameter("username").toString();
            writeImage(context, Humanizer.back(username, size));
        });
        
        
        // ### /cape
        GET("/cape/" + USERNAME + "\\.png", context -> {
            String username = context.getParameter("username").toString();
            writeImage(context, Humanizer.cape(username));
        });
        
        
        // ### /game
        addRouteGroup("/game", group -> {
            group.GET("/v1/skin/" + USERNAME + "\\.png", context -> {
                String username = context.getParameter("username").toString();
                writeImage(context, Resizer.getSkin(username, false, 64, 32));
            });
            group.GET("/v1/cape/" + USERNAME + "\\.png", context -> {
                String username = context.getParameter("username").toString();
                writeImage(context, Resizer.getCape(username, 22, 17));
            });
            group.GET("/v2/skin/" + USERNAME + "\\.png", context -> {
                String username = context.getParameter("username").toString();
                writeImage(context, service.skinRepository.getSkin(username, false));
            });
            group.GET("/v2/cape/" + USERNAME + "\\.png", context -> {
                String username = context.getParameter("username").toString();
                writeImage(context, Resizer.getCape(username, 64, 32));
            });
        });
        
        
        // ### /raw
        addRouteGroup("/raw", group -> {
            group.GET("/cape/" + USERNAME + "\\.png", context -> {
                String username = context.getParameter("username").toString();
                writeImage(context, service.skinRepository.getCape(username));
            });
            group.GET("/skin/" + USERNAME + "\\.png", context -> {
                String username = context.getParameter("username").toString();
                writeImage(context, service.skinRepository.getSkin(username, false));
            });
        });
        
        
        // ### /private
        addRouteGroup("/private", group -> {
            group.ANY("/{token}/.*", context -> {
                String token = context.getParameter("token").toString();
                if (service.config.tokens.contains(token)) {
                    context.next();
                } else {
                    context.status(403);
                    context.send("Invalid token");
                }
            });
            group.DELETE("/{token}/cache/cape/" + USERNAME, context -> {
                String username = context.getParameter("username").toString();
                service.skinRepository.invalidateCape(username);
                username = CloudflareUtil.urlencode(username);
                CloudflareUtil.clearCache(
                    "/game/v1/cape/" + username + ".png",
                    "/game/v2/cape/" + username + ".png",
                    "/cape/" + username + ".png"
                );
                context.status(200);
                context.send("OK");
            });
            group.DELETE("/{token}/cache/skin/" + USERNAME, context -> {
                String username = context.getParameter("username").toString();
                service.skinRepository.invalidateSkin(username);
                username = CloudflareUtil.urlencode(username);
                CloudflareUtil.clearCache(
                    "/game/v1/skin/" + username + ".png",
                    "/game/v2/skin/" + username + ".png",
                    "/raw/cape/" + username + ".png",
                    "/raw/skin/" + username + ".png",
                    "/helm/" + username + ".png",
                    "/head/" + username + ".png",
                    "/helm/3d/" + username + ".png",
                    "/head/3d/" + username + ".png",
                    "/body/" + username + ".png",
                    "/back/" + username + ".png"
                );
                context.status(200);
                context.send("OK");
            });
        });
        
        if (log) {
            ANY(".*", context -> {
                if (detailed) {
                    String ip = context.getHeader("CF-Connecting-IP");
                    if (ip == null)
                        ip = context.getHeader("X-Forwarded-For");
                    if (ip == null)
                        ip = context.getRequest().getClientIp();
                    long start = context.getLocal("_start");
                    LOG.info("[{} ms] {} {} ({})", Math.round((System.nanoTime() - start) / 10000f) / 100f, context.getRequestMethod(), context.getRequestUri(), ip);
                } else {
                    LOG.info("{} {}", context.getRequestMethod(), context.getRequestUri());
                }
                context.next();
            }).runAsFinally();
        }
        
        setErrorHandler(new SkinErrorHandler(this));
    }
    
    private static void writeImage(RouteContext context, Image image) {
        if (image == null) {
            context.getResponse().status(404);
            context.send("404 Not found");
            return;
        }
        context.setHeader("Content-Type", "image/png");
        context.getResponse().ok();
        try {
            context.getResponse().getOutputStream().write(image.encode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void addRouteGroup(String pattern, Consumer<RouteGroup> filler) {
        RouteGroup group = new RouteGroup(pattern);
        filler.accept(group);
        addRouteGroup(group);
    }
}
