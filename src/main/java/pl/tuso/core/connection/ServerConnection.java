package pl.tuso.core.connection;

import org.bukkit.entity.Player;
import pl.tuso.core.XCore;
import pl.tuso.core.lettuce.messaging.Message;

import java.util.Objects;

public class ServerConnection {
    private final XCore xCore;

    public ServerConnection(XCore xCore) {
        this.xCore = xCore;
    }

    public void connect(Player player, String serverName) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(serverName, "serverName");
        if (this.xCore.getServerInfo().getName().equals(serverName)) return;
        this.xCore.getMessagingService().sendOutgoingMessage(
                new Message("CONNECT")
                        .setParam("player", player.getUniqueId().toString())
                        .setParam("server", serverName));
    }
}
