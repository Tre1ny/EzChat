package dev.Tre1ny.ezchat.chat;

import dev.Tre1ny.ezchat.EzChat;
import dev.Tre1ny.ezchat.mute.MuteEntry;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class ChatListener implements Listener {

    private final ChatManager chatManager;
    private final EzChat plugin;

    // Анти-спам
    private final Map<Player, Long> lastMessageTime = new HashMap<>();
    private final Map<Player, String> lastMessageContent = new HashMap<>();

    public ChatListener(ChatManager chatManager, EzChat plugin) {
        this.chatManager = chatManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        String messageText = PlainTextComponentSerializer.plainText()
                .serialize(event.message())
                .trim();

        if (messageText.isEmpty()) {
            Bukkit.getScheduler().runTask(plugin,
                    () -> sendSimpleMessage("empty-message", player));
            return;
        }

        if (messageText.length() > 512) {
            Bukkit.getScheduler().runTask(plugin,
                    () -> player.sendMessage("§cСообщение слишком длинное."));
            return;
        }

        // ================= Анти-спам =================
        if (plugin.getConfig().getBoolean("chat.spam.enabled", true)) {
            long now = System.currentTimeMillis();
            long cooldown = plugin.getConfig().getLong("chat.spam.cooldown", 2000);
            boolean blockDuplicate = plugin.getConfig().getBoolean("chat.spam.block-duplicate", true);

            Long lastTime = lastMessageTime.get(player);
            String lastMsg = lastMessageContent.get(player);

            if (lastTime != null && (now - lastTime < cooldown)) {
                if (plugin.getConfig().getBoolean("chat.spam.notify-player", true)) {
                    String text = plugin.getConfig().getString("messages.spam.cooldown.text",
                            "&cПожалуйста, не спамьте! Осталось: %seconds% сек.");
                    int secondsLeft = (int) Math.ceil((cooldown - (now - lastTime)) / 1000.0);
                    player.sendMessage(text.replace("&", "§")
                            .replace("%seconds%", String.valueOf(secondsLeft)));
                }
                return;
            }

            if (blockDuplicate && lastMsg != null && lastMsg.equalsIgnoreCase(messageText)) {
                if (plugin.getConfig().getBoolean("chat.spam.notify-player", true)) {
                    String text = plugin.getConfig().getString("messages.spam.duplicate.text",
                            "&cПовторяющееся сообщение заблокировано.");
                    player.sendMessage(text.replace("&", "§"));
                }
                return;
            }

            lastMessageTime.put(player, now);
            lastMessageContent.put(player, messageText);
        }

        // ================= Тип сообщения =================
        String type = "local";
        if (messageText.startsWith("!")) {
            messageText = messageText.substring(1).trim();
            if (messageText.isEmpty()) {
                Bukkit.getScheduler().runTask(plugin,
                        () -> sendSimpleMessage("empty-global", player));
                return;
            }
            type = "global";
        }

        String finalMessage = messageText;
        String finalType = type;

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (plugin.getMuteManager().isMuted(player)) {
                MuteEntry mute = plugin.getMuteManager().getMute(player);
                if (mute == null) return;

                String time = mute.isPermanent()
                        ? "навсегда"
                        : formatTime(mute.getEndTime() - System.currentTimeMillis());

                sendMuteMessage(
                        "mute.blocked",
                        player,
                        mute.getReason(),
                        time
                );
                return;
            }

            chatManager.sendChat(player, finalMessage, finalType);
        });
    }

    private void sendSimpleMessage(String path, Player player) {
        if (!plugin.getConfig().getBoolean("messages." + path + ".enabled", true)) return;
        String text = plugin.getConfig().getString("messages." + path + ".text", "");
        if (text == null || text.isEmpty()) return;
        player.sendMessage(text.replace("&", "§"));
    }

    private void sendMuteMessage(String path, Player player, String reason, String time) {
        if (!plugin.getConfig().getBoolean("messages." + path + ".enabled", true)) return;
        String text = plugin.getConfig().getString("messages." + path + ".text", "");
        if (text == null || text.isEmpty()) return;

        player.sendMessage(
                text.replace("%reason%", reason)
                        .replace("%time%", time)
                        .replace("&", "§")
        );
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000 % 60;
        long minutes = millis / (1000 * 60) % 60;
        long hours = millis / (1000 * 60 * 60) % 24;
        long days = millis / (1000 * 60 * 60 * 24);
        return (days > 0 ? days + "d " : "")
                + (hours > 0 ? hours + "h " : "")
                + (minutes > 0 ? minutes + "m " : "")
                + seconds + "s";
    }
}
