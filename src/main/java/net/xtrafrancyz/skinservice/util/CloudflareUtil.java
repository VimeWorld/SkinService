package net.xtrafrancyz.skinservice.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.xtrafrancyz.skinservice.Config;
import net.xtrafrancyz.skinservice.SkinService;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xtrafrancyz
 */
public class CloudflareUtil {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    
    public static void clearCache(String... urls) {
        Config.CloudflareConfig config = SkinService.instance().config.cloudflare;
        if (!config.enabled)
            return;
        executor.submit(() -> {
            URL url;
            try {
                url = new URL("https://api.cloudflare.com/client/v4/zones/" + config.zoneId + "/purge_cache");
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                return;
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-Auth-Email", config.email);
                conn.setRequestProperty("X-Auth-Key", config.key);
                
                conn.setDoOutput(true);
                OutputStream out = conn.getOutputStream();
                JsonArray files = new JsonArray();
                for (String url0 : urls) {
                    files.add("http://" + config.cacheUrl + url0);
                    if (config.httpsCache)
                        files.add("https://" + config.cacheUrl + url0);
                }
                JsonObject json = new JsonObject();
                json.add("files", files);
                new DataOutputStream(out).writeBytes(json.toString());
                out.flush();
                out.close();
                
                conn.getResponseCode();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
        });
    }
}
