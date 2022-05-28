package pl.tuso.core.whereami;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.tuso.core.XCore;
import pl.tuso.core.command.CommandHandler;
import pl.tuso.core.util.Color;

import java.util.List;

public class WhereAmICommand extends CommandHandler {
    private final XCore xCore;

    public WhereAmICommand(XCore xCore) {
        this.xCore = xCore;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length != 0) {
            sender.sendMessage(Component.text("This command doesn't exist, or does it?").color(Color.MAXIMUM_RED));
            return false;
        }
        if (this.xCore.getServerInfo() == null) {
            sender.sendMessage(Component.text("Sorry, we don't have any info about that server!").color(Color.MAXIMUM_RED));
            return false;
        }
        sender.sendMessage(Component.text("You're on ")
                .append(Component.text(this.xCore.getServerInfo().getName()).color(Color.MAJORELLE_BLUE))
                .append(Component.text(".")).color(Color.SHEEN_GREEN));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return ImmutableList.of();
    }
}
