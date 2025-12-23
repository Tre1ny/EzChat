package dev.Tre1ny.ezchat.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionsManager {

    public static final String EZCHAT_ADMIN = "ezchat.admin";
    public static final String CHAT_LOCAL = "ezchat.chat.local";
    public static final String CHAT_GLOBAL = "ezchat.chat.global";

    public static final String MUTE = "ezchat.mute";
    public static final String UNMUTE = "ezchat.unmute";
    public static final String MUTEINFO = "ezchat.muteinfo";
    public static final String MUTEHISTORY = "ezchat.mutehistory";

    public static boolean has(CommandSender sender, String permission) {
        if (!(sender instanceof Player)) return true;
        return sender.hasPermission(permission);
    }

    public static boolean hasMute(CommandSender sender) { return has(sender, MUTE); }
    public static boolean hasUnmute(CommandSender sender) { return has(sender, UNMUTE); }
    public static boolean hasMuteInfo(CommandSender sender) { return has(sender, MUTEINFO); }
    public static boolean hasMuteHistory(CommandSender sender) { return has(sender, MUTEHISTORY); }
    public static boolean hasAdmin(CommandSender sender) { return has(sender, EZCHAT_ADMIN); }
    public static boolean hasChatLocal(CommandSender sender) { return has(sender, CHAT_LOCAL); }
    public static boolean hasChatGlobal(CommandSender sender) { return has(sender, CHAT_GLOBAL); }
}
