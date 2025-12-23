    package dev.Tre1ny.ezchat.mute;

    import org.bukkit.entity.Player;

    import java.util.Map;
    import java.util.UUID;
    import java.util.concurrent.ConcurrentHashMap;

    public class MuteManager {

        private final Map<UUID, MuteEntry> mutes = new ConcurrentHashMap<>();

        public void mute(Player player, String reason, long durationMillis) {
            long endTime = durationMillis <= 0
                    ? -1
                    : System.currentTimeMillis() + durationMillis;

            mutes.put(player.getUniqueId(), new MuteEntry(reason, endTime));
        }

        public boolean isMuted(Player player) {
            MuteEntry entry = mutes.get(player.getUniqueId());
            if (entry == null) return false;

            if (entry.isExpired()) {
                mutes.remove(player.getUniqueId());
                return false;
            }
            return true;
        }

        public MuteEntry getMute(Player player) {
            return mutes.get(player.getUniqueId());
        }

        public void unmute(Player player) {
            mutes.remove(player.getUniqueId());
        }

        public void loadFromFile(MuteStorage storage) {
            var section = storage.getConfig().getConfigurationSection("active");
            if (section == null) return;

            for (String key : section.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String reason = section.getString(key + ".reason");
                    long end = section.getLong(key + ".end");
                    mutes.put(uuid, new MuteEntry(reason, end));
                } catch (Exception ignored) {}
            }
        }
    }
