package net.xtrafrancyz.skinservice.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import net.xtrafrancyz.skinservice.Config;
import net.xtrafrancyz.skinservice.SkinService;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author xtrafrancyz
 */
public class CloudFlareUtil {
    public static void clearCache(String... urls) {
        Config.CloudFlareConfig config = SkinService.instance().config.cloudflare;
        if (!config.enabled)
            return;
        Thread th = new Thread(() -> {
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
                conn.setDoOutput(true);
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("User-Agent", "SkinService");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("X-Auth-Email", config.email);
                conn.setRequestProperty("X-Auth-Key", config.key);
                
                OutputStream out = conn.getOutputStream();
                JsonArray files = new JsonArray();
                for (String url0 : urls)
                    files.add(url0);
                JsonObject json = new JsonObject();
                json.add("files", files);
                JsonWriter writer = new JsonWriter(new OutputStreamWriter(out));
                Streams.write(json, writer);
                out.flush();
                
                conn.getResponseCode();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
        });
        th.setDaemon(true);
        th.setName("CloudFlare request");
        th.start();
    }
}
