package dev.Tre1ny.ezchat.commands;

import dev.Tre1ny.ezchat.EzChat;
import dev.Tre1ny.ezchat.mute.MuteEntry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class MuteInfoCommand implements CommandExecutor {

    private final EzChat plugin;
    public MuteInfoCommand(EzChat plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sendMessage("messages.muteinfo", sender, null, "&cИспользование: /muteinfo <игрок>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sendMessage("messages.muteinfo", sender, null, "&cИгрок не найден.");
            return true;
        }

        MuteEntry mute = plugin.getMuteManager().getMute(target);
        if (mute == null || !plugin.getMuteManager().isMuted(target)) {
            sendMessage("messages.muteinfo", sender, null, "&aИгрок не замьючен.");
            return true;
        }

        String reason = mute.getReason();
        String time = mute.isPermanent() ? "навсегда" : formatTime(mute.getEndTime() - System.currentTimeMillis());

        sender.sendMessage("§eИгрок: §f" + target.getName());
        sender.sendMessage("§eПричина: §f" + reason);
        sender.sendMessage("§eОсталось: §f" + time);

        return true;
    }

    private void sendMessage(String path, CommandSender sender, String playerName, String fallback) {
        if (!plugin.getConfig().getBoolean(path + ".enabled", true)) return;

        String text = plugin.getConfig().getString(path + ".text", fallback != null ? fallback : "");
        if (text == null || text.isEmpty()) return;

        if (playerName != null) text = text.replace("%player%", playerName);
        sender.sendMessage(text.replace("&", "§"));
    }

    private String formatTime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return (days>0?days+"d ":"") + (hours>0?hours+"h ":"") + (minutes>0?minutes+"m ":"") + seconds+"s";
    }
}
