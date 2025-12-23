package dev.Tre1ny.ezchat.mute;

import dev.Tre1ny.ezchat.EzChat;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MuteStorage {

    private final EzChat plugin;
    private final File file;
    private FileConfiguration config;

    // 🔒 ОДИН поток на запись
    private final ExecutorService writeExecutor = Executors.newSingleThreadExecutor();

    public MuteStorage(EzChat plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "mutes.yml");
        load();
    }

    /* ================= LOAD ================= */
    private synchronized void load() {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            this.config = YamlConfiguration.loadConfiguration(file);
            if (!config.contains("mutes")) config.set("mutes", new HashMap<>());
            if (!config.contains("history")) config.set("history", new ArrayList<>());
            saveSync();
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось загрузить mutes.yml");
            e.printStackTrace();
        }
    }

    /* ================= SAVE ================= */
    private synchronized void saveSync() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Ошибка сохранения mutes.yml");
            e.printStackTrace();
        }
    }

    private void saveAsync() {
        writeExecutor.execute(this::saveSync);
    }

    /* ================= MUTES ================= */
    public synchronized void saveMute(UUID uuid, String name, String reason, long endTime) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("reason", reason);
        data.put("end", endTime);

        config.set("mutes." + uuid.toString(), data);
        saveAsync();
    }

    public synchronized void removeMute(UUID uuid) {
        config.set("mutes." + uuid.toString(), null);
        saveAsync();
    }

    public synchronized Map<String, Object> getMute(UUID uuid) {
        return config.getConfigurationSection("mutes." + uuid.toString()) != null
                ? config.getConfigurationSection("mutes." + uuid.toString()).getValues(false)
                : null;
    }

    public synchronized Set<String> getMutedPlayers() {
        return config.getConfigurationSection("mutes") != null
                ? config.getConfigurationSection("mutes").getKeys(false)
                : Collections.emptySet();
    }

    /* ================= HISTORY ================= */
    public synchronized void addHistory(
            String player,
            UUID uuid,
            String action,
            String reason,
            String time,
            String by
    ) {
        List<Map<String, Object>> history =
                (List<Map<String, Object>>) config.getList("history", new ArrayList<>());

        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("player", player);
        entry.put("uuid", uuid.toString());
        entry.put("action", action);
        entry.put("reason", reason);
        entry.put("time", time);
        entry.put("by", by);
        entry.put("timestamp", System.currentTimeMillis());

        history.add(entry);
        config.set("history", history);
        saveAsync();
    }

    public synchronized FileConfiguration getConfig() {
        return config;
    }

    /* ================= SHUTDOWN ================= */
    public void shutdown() {
        writeExecutor.shutdown();
        saveSync();
    }
}
