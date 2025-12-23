package dev.Tre1ny.ezchat.mute;

public class MuteEntry {

    private final String reason;
    private final long endTime;

    public MuteEntry(String reason, long endTime) {
        this.reason = reason;
        this.endTime = endTime;
    }

    public String getReason() { return reason; }
    public long getEndTime() { return endTime; }
    public boolean isPermanent() { return endTime == -1; }
    public boolean isExpired() { return !isPermanent() && System.currentTimeMillis() > endTime; }
}
