package dev.Tre1ny.ezchat.commands;

import dev.Tre1ny.ezchat.EzChat;
import dev.Tre1ny.ezchat.mute.TimeParser;
import dev.Tre1ny.ezchat.permissions.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor {

    private final EzChat plugin;

    public MuteCommand(EzChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Проверка прав
        if (!PermissionsManager.hasMute(sender)) {
            sender.sendMessage("§cУ вас нет прав на мут.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /mute <игрок> <причина> [время]");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage("§cИгрок не найден.");
            return true;
        }

        String reason = args[1];
        long duration = -1;

        if (args.length >= 3) {
            duration = TimeParser.parse(args[2]);
            if (duration <= 0) {
                sender.sendMessage("§cНеверный формат времени.");
                return true;
            }
        }

        plugin.getMuteManager().mute(target, reason, duration);

        long endTime = duration == -1 ? -1 : System.currentTimeMillis() + duration;
        plugin.getMuteStorage().saveMute(
                target.getUniqueId(),
                target.getName(),
                reason,
                endTime
        );

        plugin.getMuteStorage().addHistory(
                target.getName(),
                target.getUniqueId(),
                "MUTE",
                reason,
                duration == -1 ? "навсегда" : "временно",
                sender.getName()
        );

        sendMessage("messages.mute.target", target, reason,
                duration == -1 ? "навсегда" : "временно");

        sendSenderMessage("messages.mute.sender", sender, target.getName());

        return true;
    }

    private void sendMessage(String path, Player player, String reason, String time) {
        if (!plugin.getConfig().getBoolean("messages." + path + ".enabled", true)) return;
        String text = plugin.getConfig().getString("messages." + path + ".text", "");
        if (text == null || text.isEmpty()) return;

        player.sendMessage(
                text.replace("%reason%", reason != null ? reason : "")
                        .replace("%time%", time != null ? time : "")
                        .replace("&", "§")
        );
    }

    private void sendSenderMessage(String path, CommandSender sender, String player) {
        if (!plugin.getConfig().getBoolean("messages." + path + ".enabled", true)) return;
        String text = plugin.getConfig().getString("messages." + path + ".text", "");
        if (text == null || text.isEmpty()) return;

        if (player != null) text = text.replace("%player%", player);
        sender.sendMessage(text.replace("&", "§"));
    }

}
