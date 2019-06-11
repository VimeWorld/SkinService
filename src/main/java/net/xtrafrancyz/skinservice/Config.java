package net.xtrafrancyz.skinservice;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.Set;

/**
 * @author xtrafrancyz
 */
public class Config {
    public String host = "127.0.0.1";
    public int port = 991;
    @SerializedName("log-detailed-queries")
    public boolean logDetailedQueries = true;
    public Set<String> tokens = Collections.singleton("AuThToKeN");
    
    public RepositoryConfig repository = new RepositoryConfig();
    public CloudflareConfig cloudflare = new CloudflareConfig();
    
    public class RepositoryConfig {
        public String type = "URL";
        
        @SerializedName("skin-path")
        public String skinPath = "http://s3.amazonaws.com/MinecraftSkins/{username}.png";
        
        @SerializedName("cape-path")
        public String capePath = "http://s3.amazonaws.com/MinecraftCloaks/{username}.png";
        
        @SerializedName("cache-expire-minutes")
        public int cacheExpireMinutes = 60;
    }
    
    public class CloudflareConfig {
        public boolean enabled = false;
        
        @SerializedName("zone-id")
        public String zoneId = "";
        
        @SerializedName("auth-email")
        public String email = "";
        
        @SerializedName("auth-key")
        public String key = "";
        
        @SerializedName("cache-url")
        public String cacheUrl = "skin.example.com";
        
        @SerializedName("clear-https-cache")
        public boolean httpsCache = true;
    }
}
