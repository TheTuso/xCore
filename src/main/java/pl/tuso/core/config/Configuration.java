package pl.tuso.core.config;

import io.lettuce.core.RedisURI;
import org.jetbrains.annotations.NotNull;
import pl.tuso.core.XCore;

public class Configuration {
    private final XCore xCore;

    public Configuration(@NotNull XCore xCore) {
        this.xCore = xCore;
        xCore.saveDefaultConfig();
    }

    public RedisURI getRedisUri() {
        String host = this.xCore.getConfig().getString("redis.host");
        int port = this.xCore.getConfig().getInt("redis.port");
        return RedisURI.create(host, port);
    }

    public String getMongoUri() {
        String uri = this.xCore.getConfig().getString("mongo.uri");
        if (uri.isEmpty()) return "mongodb://localhost";
        return uri;
    }

    public void reload() {
        this.xCore.reloadConfig();
        this.xCore.saveDefaultConfig();
        this.xCore.getConfig().options().copyDefaults(true);
        this.xCore.saveConfig();
    }
}
