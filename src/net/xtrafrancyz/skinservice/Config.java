package net.xtrafrancyz.skinservice;

import com.google.gson.annotations.SerializedName;

/**
 * @author xtrafrancyz
 */
public class Config {
    public String host;
    public int port;
    
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
    }
    
    public class CloudFlareConfig {
        public boolean enabled;
        
        @SerializedName("zone-id")
        public String zoneId;
        
        @SerializedName("auth-email")
        public String email;
        
        @SerializedName("auth-key")
        public String key;
    }
}
