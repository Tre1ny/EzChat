package dev.Tre1ny.ezchat;

import dev.Tre1ny.ezchat.chat.ChatListener;
import dev.Tre1ny.ezchat.chat.ChatManager;
import dev.Tre1ny.ezchat.commands.*;
import dev.Tre1ny.ezchat.mute.MuteManager;
import dev.Tre1ny.ezchat.mute.MuteStorage;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class EzChat extends JavaPlugin {

    private MiniMessage miniMessage;
    private ChatManager chatManager;
    private MuteManager muteManager;
    private MuteStorage muteStorage;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.miniMessage = MiniMessage.miniMessage();
        this.chatManager = new ChatManager(this);
        this.muteManager = new MuteManager();
        this.muteStorage = new MuteStorage(this);

        muteManager.loadFromFile(muteStorage);

        // listeners
        getServer().getPluginManager().registerEvents(
                new ChatListener(chatManager, this), this
        );

        // commands — безопасная регистрация с null-check
        if (getCommand("ezchat") != null) getCommand("ezchat").setExecutor(new EzChatCommand(this));
        else getLogger().log(Level.WARNING, "Command 'ezchat' not registered in plugin.yml");

        if (getCommand("mute") != null) getCommand("mute").setExecutor(new MuteCommand(this));
        else getLogger().log(Level.WARNING, "Command 'mute' not registered in plugin.yml");

        if (getCommand("unmute") != null) getCommand("unmute").setExecutor(new UnmuteCommand(this));
        else getLogger().log(Level.WARNING, "Command 'unmute' not registered in plugin.yml");

        if (getCommand("muteinfo") != null) getCommand("muteinfo").setExecutor(new MuteInfoCommand(this));
        else getLogger().log(Level.WARNING, "Command 'muteinfo' not registered in plugin.yml");

        if (getCommand("mutehistory") != null) getCommand("mutehistory").setExecutor(new MuteHistoryCommand(this));
        else getLogger().log(Level.WARNING, "Command 'mutehistory' not registered in plugin.yml");

        getLogger().log(Level.INFO, "EzChat успешно запущен.");
    }

    @Override
    public void onDisable() {
        if (muteStorage != null) {
            muteStorage.shutdown();
        }
    }

    /* ===== getters ===== */
    public MiniMessage getMiniMessage() { return miniMessage; }
    public ChatManager getChatManager() { return chatManager; }
    public MuteManager getMuteManager() { return muteManager; }
    public MuteStorage getMuteStorage() { return muteStorage; }
}
