package net.xtrafrancyz.skinservice.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.xtrafrancyz.skinservice.Config;
import net.xtrafrancyz.skinservice.SkinService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xtrafrancyz
 */
public class CloudflareUtil {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Logger log = LoggerFactory.getLogger(CloudflareUtil.class);
    
    public static String urlencode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {}
        return str;
    }
    
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
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + config.key);
                
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
                
                int code = conn.getResponseCode();
                if (code / 100 != 2) {
                    InputStream is = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        if (sb.length() > 0)
                            sb.append(System.lineSeparator());
                        sb.append(line);
                    }
                    log.warn("Error from Cloudflare (" + code + "): " + sb);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
        });
    }
}
