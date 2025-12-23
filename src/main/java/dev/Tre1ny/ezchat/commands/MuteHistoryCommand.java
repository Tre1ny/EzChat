package dev.Tre1ny.ezchat.commands;

import dev.Tre1ny.ezchat.EzChat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class MuteHistoryCommand implements CommandExecutor {

    private final EzChat plugin;
    public MuteHistoryCommand(EzChat plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sendMessage("messages.mutehistory", sender, null, "&cИспользование: /mutehistory <игрок>");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayerExact(targetName);
        String uuid = target != null ? target.getUniqueId().toString() : null;

        List<Map<?, ?>> history = plugin.getMuteStorage().getConfig().getMapList("history");
        boolean found = false;

        sender.sendMessage("§eИстория мутов/размьютов игрока §f" + targetName + "§e:");

        for (Map<?, ?> entry : history) {
            if (entry.get("player").equals(targetName) || (uuid != null && entry.get("uuid").equals(uuid))) {
                found = true;
                String action = (String) entry.get("action");
                String reason = (String) entry.get("reason");
                String time = (String) entry.get("time");
                String by = (String) entry.get("by");
                String msg = "§7[" + action + "] §f";
                if (reason != null) msg += "Причина: " + reason + " ";
                if (time != null) msg += "Время: " + time + " ";
                msg += "§eКем: " + by;
                sender.sendMessage(msg);
            }
        }

        if (!found) sender.sendMessage("§cИстория не найдена.");
        return true;
    }

    private void sendMessage(String path, CommandSender sender, String playerName, String fallback) {
        if (!plugin.getConfig().getBoolean(path + ".enabled", true)) return;

        String text = plugin.getConfig().getString(path + ".text", fallback != null ? fallback : "");
        if (text == null || text.isEmpty()) return;

        if (playerName != null) text = text.replace("%player%", playerName);
        sender.sendMessage(text.replace("&", "§"));
    }
}
