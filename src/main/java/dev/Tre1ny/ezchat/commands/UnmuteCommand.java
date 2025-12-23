package dev.Tre1ny.ezchat.commands;

import dev.Tre1ny.ezchat.EzChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnmuteCommand implements CommandExecutor {

    private final EzChat plugin;

    public UnmuteCommand(EzChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 1) {
            sendMessage("messages.mute.unmute-sender", sender, null, "&cИспользование: /unmute <игрок>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sendMessage("messages.mute.unmute-sender", sender, null, "&cИгрок не найден.");
            return true;
        }

        if (!plugin.getMuteManager().isMuted(target)) {
            sendMessage("messages.mute.unmute-sender", sender, null, "&cИгрок не замьючен.");
            return true;
        }

        plugin.getMuteManager().unmute(target);
        plugin.getMuteStorage().removeMute(target.getUniqueId());

        plugin.getMuteStorage().addHistory(
                target.getName(),
                target.getUniqueId(),
                "UNMUTE",
                null,
                null,
                sender.getName()
        );

        sendMessage("mute.unmute-target", target, null, null);
        sendMessage("mute.unmute-sender", sender, target.getName(), null);

        return true;
    }

    private void sendMessage(String path, CommandSender sender, String playerName, String fallback) {
        if (!plugin.getConfig().getBoolean(path + ".enabled", true)) return;

        String text = plugin.getConfig().getString(path + ".text", fallback != null ? fallback : "");
        if (text == null || text.isEmpty()) return;

        if (playerName != null) {
            text = text.replace("%player%", playerName);
        }

        sender.sendMessage(text.replace("&", "§"));
    }
}
