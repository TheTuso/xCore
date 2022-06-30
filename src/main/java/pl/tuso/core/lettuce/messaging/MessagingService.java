package pl.tuso.core.lettuce.messaging;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.jetbrains.annotations.NotNull;
import pl.tuso.core.XCore;
import pl.tuso.core.util.ExpiringSet;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MessagingService {
    private final static String CHANNEL = "redstone";
    private final XCore xCore;
    private final ExpiringSet<UUID> receivedMessages;
    private final LinkedHashSet<MessagingListener> listeners;
    private final Subscriber subscriber;
    private final RedisClient redisClient;
    private final StatefulRedisPubSubConnection<String, String> subscribeConnection;
    private final StatefulRedisPubSubConnection<String, String> publishConnection;
    private boolean terminated;

    public MessagingService(@NotNull XCore xCore) {
        this.receivedMessages = new ExpiringSet<>(1, TimeUnit.HOURS);
        this.xCore = xCore;
        this.listeners = new LinkedHashSet<>();
        this.subscriber = new Subscriber();
        this.redisClient = xCore.getRedisClient();
        this.subscribeConnection = this.redisClient.connectPubSub();
        this.publishConnection = this.redisClient.connectPubSub();
        this.terminated = false;
        this.init();
    }

    private void init() {
        try {
            this.xCore.getLogger().info("Checking Redis connection: PING → " + this.publishConnection.async().ping().get());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        this.subscribeConnection.addListener(this.subscriber);
        this.subscribeConnection.async().subscribe(CHANNEL);
    }

    public void terminate() {
        if (isTerminated()) return;
        this.terminated = true;
        this.publishConnection.close();
        this.subscribeConnection.close();
        this.listeners.forEach(listener -> this.unregisterListener(listener));
    }

    public boolean isTerminated() {
        return this.terminated;
    }

    public void registerListener(MessagingListener listener) {
        if (this.listeners.contains(listener)) return;
        this.listeners.add(listener);
    }

    public void unregisterListener(MessagingListener listener) {
        if (!this.listeners.contains(listener)) return;
        this.listeners.remove(listener);
    }

    public void sendOutgoingMessage(@NotNull Message message) {
        this.publishConnection.async().publish(CHANNEL, message.serialize());
        this.receivedMessages.add(message.getId());
    }

    private void consumeIncomingMessage(Message message) {
        Objects.requireNonNull(message, "message");
        if (!this.receivedMessages.add(message.getId())) return;
        this.processMessage(message);
    }

    private void consumeIncomingMessageAsString(String serializedString) {
        Objects.requireNonNull(serializedString, "serializedString");
        this.consumeIncomingMessage(Message.deserialize(serializedString));
    }

    private void processMessage(Message message) {
        this.listeners.stream()
                .filter(listener -> listener.getType().equals(message.getType()))
                .forEach(listener -> listener.action(message));
    }

    private class Subscriber implements RedisPubSubListener<String, String> {

        @Override
        public void message(@NotNull String channel, String message) {
            if (!channel.equals(CHANNEL)) return;
            MessagingService.this.consumeIncomingMessageAsString(message);
        }

        @Override
        public void message(String pattern, String channel, String message) {
            // Ignore
        }

        @Override
        public void subscribed(String channel, long count) {
            MessagingService.this.xCore.getLogger().info("Subscribed → " + channel + " [" + count + "]");
        }

        @Override
        public void psubscribed(String pattern, long count) {
            MessagingService.this.xCore.getLogger().info("Psubscribed → " + pattern + " [" + count + "]");
        }

        @Override
        public void unsubscribed(String channel, long count) {
            MessagingService.this.xCore.getLogger().info("Unsubscribed → " + channel + " [" + count + "]");
        }

        @Override
        public void punsubscribed(String pattern, long count) {
            MessagingService.this.xCore.getLogger().info("Punsubscribed → " + pattern + " [" + count + "]");
        }
    }
}
