package pl.tuso.core;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.lettuce.core.RedisClient;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.tuso.core.config.Configuration;
import pl.tuso.core.info.ServerInfo;
import pl.tuso.core.info.ServerInfoPuller;
import pl.tuso.core.lettuce.messaging.MessagingService;
import pl.tuso.core.whereami.WhereAmICommand;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XCore extends JavaPlugin { // TODO config
    private Configuration configuration;
    private RedisClient redisClient;
    private MongoClient mongoClient;
    private MessagingService messagingService;
    private ExecutorService executorService;
    private ServerInfo serverInfo;
    private static XCore INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        this.configuration = new Configuration(this);
        this.redisClient = this.createRedisConnection();
        this.mongoClient = this.createMongoConnection();
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

    private @NotNull RedisClient createRedisConnection() {
        RedisClient redisClient = RedisClient.create(this.configuration.getRedisUri());
        return redisClient;
    }

    private @NotNull MongoClient createMongoConnection() {
        MongoClient mongoClient = MongoClients.create(this.configuration.getMongoUri());
        try {
            this.getLogger().info("Checking mongo connection → " + mongoClient.getClusterDescription().getShortDescription());
        } catch (Exception ignore) {
            // Ignore
        }
        return mongoClient;
    }

    public RedisClient getRedisClient() {
        return this.redisClient;
    }

    public MongoClient getMongoClient() {
        return this.mongoClient;
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