package pl.tuso.core.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import java.util.Objects;

public abstract class CommandHandler implements CommandExecutor, TabCompleter {
    public void register(PluginCommand pluginCommand) {
        Objects.requireNonNull(pluginCommand, "pluginCommand");
        pluginCommand.setExecutor(this::onCommand);
        pluginCommand.setTabCompleter(this::onTabComplete);
    }
}
