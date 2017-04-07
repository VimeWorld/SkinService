package net.xtrafrancyz.skinservice.pippo;

import ro.pippo.core.Application;
import ro.pippo.core.DefaultErrorHandler;
import ro.pippo.core.route.RouteContext;

/**
 * @author xtrafrancyz
 */
public class SkinErrorHandler extends DefaultErrorHandler {
    public SkinErrorHandler(Application application) {
        super(application);
    }
    
    @Override
    protected void renderDirectly(int statusCode, RouteContext routeContext) {
        routeContext.send("Hello. There's nothing here");
    }
}
