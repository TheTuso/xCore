package io.papermc.paper.command;

import com.destroystokyo.paper.MSPTCommand;
import com.destroystokyo.paper.PaperCommand;
import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;

import java.util.HashMap;
import java.util.Map;

public final class PaperCommands {

    private PaperCommands() {
    }

    private static final Map<String, Command> COMMANDS = new HashMap<>();
    static {
        COMMANDS.put("paper", new PaperCommand("paper"));
        COMMANDS.put("mspt", new MSPTCommand("mspt"));
    }

    public static void registerCommands(final MinecraftServer server) {
        COMMANDS.forEach((s, command) -> {
            server.server.getCommandMap().register(s, "Paper", command);
        });
    }
}
