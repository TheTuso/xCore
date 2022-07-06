package pl.tuso.core.lettuce.wrapper;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

@TestOnly
public class RedisWrapper {
    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> statefulRedisConnection;
    private final RedisAsyncCommands<String, String> redisAsyncCommands;
    private final RedisCommands<String, String> redisCommands;

    public RedisWrapper(@NotNull RedisClient redisClient) {
        this.redisClient = redisClient;
        this.statefulRedisConnection = redisClient.connect();
        this.redisAsyncCommands = statefulRedisConnection.async();
        this.redisCommands = statefulRedisConnection.sync();
    }

    public void setAsync(String key, String value) {
        this.redisAsyncCommands.set(key, value);
    }

    public void set(String key, String value) {
        this.redisCommands.set(key, value);
    }

    public RedisFuture<String> getAsync(String key) {
        return this.redisAsyncCommands.get(key);
    }

    public String get(String key) {
        return this.redisCommands.get(key);
    }

    public void removeAsync(String... keys) {
        this.redisAsyncCommands.del(keys);
    }

    public void remove(String... keys) {
        this.redisCommands.del(keys);
    }
}
