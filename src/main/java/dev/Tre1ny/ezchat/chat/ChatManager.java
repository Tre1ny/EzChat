package dev.Tre1ny.ezchat.chat;

import dev.Tre1ny.ezchat.EzChat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class ChatManager {

    private final EzChat plugin;
    private final MiniMessage miniMessage;

    public ChatManager(EzChat plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    public Component formatChat(Player player, String message, String type) {
        String format = plugin.getConfig().getString(
                "chat." + type + ".format",
                "<gray>%player_name%:</gray> <white>%message%</white>"
        );

        format = convertColorsAndFormats(format);
        format = format.replace("%player_name%", player.getName());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            format = PlaceholderAPI.setPlaceholders(player, format);
        }

        String token = "%message%";
        String[] parts = format.split(Pattern.quote(token), -1);

        Component left = parts.length > 0 && !parts[0].isEmpty()
                ? miniMessage.deserialize(parts[0])
                : Component.empty();

        Component middle = Component.text(message);

        Component right = parts.length > 1 && !parts[1].isEmpty()
                ? miniMessage.deserialize(parts[1])
                : Component.empty();

        return left.append(middle).append(right);
    }

    public void sendChat(Player sender, String message, String type) {
        Component formattedMessage = formatChat(sender, message, type);

        if ("global".equalsIgnoreCase(type)) {
            Bukkit.broadcast(formattedMessage);
            return;
        }

        int radius = plugin.getConfig().getInt("chat.local.radius", 100);
        int radiusSq = radius * radius;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(sender.getWorld())) continue;
            if (player.getLocation().distanceSquared(sender.getLocation()) <= radiusSq) {
                player.sendMessage(formattedMessage);
            }
        }
    }

    private String convertColorsAndFormats(String text) {
        if (text == null) return "";

        text = text.replaceAll("&([A-Fa-f0-9]{6})", "<#$1>");

        String[][] colors = {
                {"0","black"},{"1","dark_blue"},{"2","dark_green"},{"3","dark_aqua"},
                {"4","dark_red"},{"5","dark_purple"},{"6","gold"},{"7","gray"},
                {"8","dark_gray"},{"9","blue"},{"a","green"},{"b","aqua"},
                {"c","red"},{"d","light_purple"},{"e","yellow"},{"f","white"}
        };

        for (String[] c : colors) {
            text = text.replaceAll("(?i)&" + Pattern.quote(c[0]),
                    "<color:" + c[1] + ">");
        }

        text = text.replaceAll("(?i)&k", "<obfuscated>");
        text = text.replaceAll("(?i)&l", "<bold>");
        text = text.replaceAll("(?i)&m", "<strikethrough>");
        text = text.replaceAll("(?i)&n", "<underlined>");
        text = text.replaceAll("(?i)&o", "<italic>");
        text = text.replaceAll("(?i)&r", "<reset>");

        return text;
    }
}
