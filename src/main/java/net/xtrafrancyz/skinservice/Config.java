package net.xtrafrancyz.skinservice;

import com.google.gson.annotations.SerializedName;

import java.util.Set;

/**
 * @author xtrafrancyz
 */
public class Config {
    public String host;
    public int port;
    public boolean debug;
    public Set<String> tokens;
    
    public RepositoryConfig repository;
    public CloudFlareConfig cloudflare;
    
    public class RepositoryConfig {
        public String type;
        
        @SerializedName("skin-path")
        public String skinPath;
        
        @SerializedName("cape-path")
        public String capePath;
        
        @SerializedName("default-skin")
        public String defaultSkin;
        
        @SerializedName("cache-size")
        public int cacheSize;
        
        @SerializedName("cache-expire-minutes")
        public int cacheExpireMinutes;
    }
    
    public class CloudFlareConfig {
        public boolean enabled;
        
        @SerializedName("zone-id")
        public String zoneId;
        
        @SerializedName("auth-email")
        public String email;
        
        @SerializedName("auth-key")
        public String key;
        
        @SerializedName("cache-url")
        public String cacheUrl;
        
        @SerializedName("clear-https-cache")
        public boolean httpsCache;
    }
}
