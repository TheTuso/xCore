package pl.tuso.core.info;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.tuso.core.XCore;
import pl.tuso.core.lettuce.messaging.Message;
import pl.tuso.core.lettuce.messaging.MessagingListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ServerInfoPuller implements MessagingListener {
    private final static CompletableFuture<ServerInfo> completableFuture = new CompletableFuture();
    private final static String defaultHostname = "172.18.0.1"; // Node address
    private final static String originalAddress = Bukkit.getIp().isEmpty() ? defaultHostname : Bukkit.getIp();
    private final static int originalPort = Bukkit.getPort();
    private static XCore xCore;

    public ServerInfoPuller(XCore xCore) {
        this.xCore = xCore;
    }

    @Override
    public void action(@NotNull Message message) {
        if (xCore.hasServerInfo()) return;
        if (!message.containsParam("address") || !message.containsParam("port") || !message.containsParam("name")) return;
        String address = message.getParam("address");
        int port = Integer.parseInt(message.getParam("port"));
        String name = message.getParam("name");
        if (this.originalAddress.equals(address) && this.originalPort == port) {
            this.completableFuture.complete(new ServerInfo(name));
            xCore.getLogger().info("This server is known on the network as " + name + ".");
        }
    }

    @Override
    public @NotNull String getType() {
        return "SERVER_INFO";
    }

    public static @Nullable ServerInfo pull() {
        xCore.getMessagingService().sendOutgoingMessage(
                new Message("SERVER_INFO")
                        .setParam("address", originalAddress)
                        .setParam("port", String.valueOf(originalPort))
        );
        try {
            return completableFuture.get(4, TimeUnit.SECONDS);
        } catch (Exception exception) {
            return null;
        }
    }
}
