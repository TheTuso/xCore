package pl.tuso.core;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tuso.core.info.ServerInfo;
import pl.tuso.core.info.ServerInfoPuller;
import pl.tuso.core.lettuce.messaging.MessagingService;
import pl.tuso.core.whereami.WhereAmICommand;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XCore extends JavaPlugin { // TODO config
    private RedisClient redisClient;
    private MessagingService messagingService;
    private ExecutorService executorService;
    private ServerInfo serverInfo;
    private static XCore INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        this.messagingService = new MessagingService(this);
        this.executorService = Executors.newCachedThreadPool();

        this.messagingService.registerListener(new ServerInfoPuller(this));
        this.serverInfo = this.getServerInfo();

        new WhereAmICommand(this).register(this.getCommand("whereami"));

        this.getLogger().info("Heyo! I'm ready to work!");
    }

    @Override
    public void onDisable() {
        try {
            this.messagingService.terminate();
            this.redisClient.shutdown();
        } catch (Exception exception) {
            // Ignore
        }
        this.getLogger().info("Bayo! Time to rest!");
    }

    public RedisClient getRedisClient() {
        return this.redisClient;
    }

    public MessagingService getMessagingService() {
        return this.messagingService;
    }

    public ServerInfo getServerInfo() {
        if (!hasServerInfo()) {
            this.executorService.submit(() -> this.serverInfo = ServerInfoPuller.pull());
        }
        return this.serverInfo;
    }

    public boolean hasServerInfo() {
        return this.serverInfo != null;
    }

    public static XCore getInstance() { // Only for other plugins
        return INSTANCE;
    }
}