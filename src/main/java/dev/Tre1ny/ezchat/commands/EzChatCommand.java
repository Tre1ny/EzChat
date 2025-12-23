package dev.Tre1ny.ezchat.commands;

import dev.Tre1ny.ezchat.EzChat;
import dev.Tre1ny.ezchat.permissions.PermissionsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class EzChatCommand implements CommandExecutor {

    private final EzChat plugin;

    public EzChatCommand(EzChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Проверка прав администратора
        if (!PermissionsManager.hasAdmin(sender)) {
            sender.sendMessage("§cУ вас нет прав администратора EzChat.");
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage("Использование: /ezchat reload");
            return true;
        }

        // Перезагрузка конфигурации
        plugin.reloadConfig();
        sender.sendMessage("§aEzChat: Конфигурация успешно перезагружена!");

        plugin.getLogger().log(Level.INFO, "EzChat конфигурация перезагружена через команду.");

        return true;
    }
}
