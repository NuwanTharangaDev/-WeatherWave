package weatherdashboard.util;

/**
 * Pure utility functions for meteorological calculations and string formatting.
 */
public final class WeatherMathUtil {

    private WeatherMathUtil() {
        // Restrict instantiation
    }

    /**
     * Calculates dew point temperature using standard Magnus-Tetens formula.
     * @param temp temperature in Celsius
     * @param humidity relative humidity percentage (0-100)
     * @return calculated dew point in Celsius
     */
    public static double calculateDewPoint(double temp, int humidity) {
        if (humidity <= 0) return 0.0;
        double a = 17.27;
        double b = 237.7;
        double alpha = ((a * temp) / (b + temp)) + Math.log(humidity / 100.0);
        return (b * alpha) / (a - alpha);
    }

    /**
     * Capitalizes each word in a weather description string.
     */
    public static String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return str;

        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) result.append(" ");
            if (words[i].length() > 0) {
                result.append(Character.toUpperCase(words[i].charAt(0)));
                if (words[i].length() > 1) {
                    result.append(words[i].substring(1).toLowerCase());
                }
            }
        }
        return result.toString();
    }
}
