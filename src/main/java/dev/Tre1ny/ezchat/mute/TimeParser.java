package dev.Tre1ny.ezchat.mute;

public class TimeParser {

    public static long parse(String input) {
        try {
            long value = Long.parseLong(input.substring(0, input.length() - 1));
            char unit = input.charAt(input.length() - 1);

            return switch (unit) {
                case 's' -> value * 1000;
                case 'm' -> value * 60_000;
                case 'h' -> value * 3_600_000;
                case 'd' -> value * 86_400_000;
                default -> -1;
            };
        } catch (Exception e) {
            return -1;
        }
    }
}
