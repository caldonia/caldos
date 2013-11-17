package net.caldonia.bukkit.plugins.caldos;

/**
 * Representation of the state of the weather.
 */
public enum WeatherType {
    /** Clear skies. */
    CLEAR(false, false, 1.0),
    /** Just raining. */
    RAIN(true, false, 0.0),
    /** Rain with thunder. */
    STORM(true, true, 0.0),
    /** Just thunder. */
    THUNDER(false, true, 0.0);

    /** If weather is required. */
    private boolean weather;

    /** If thunder is required. */
    private boolean thunder;

    /** Default ratio to use when calculating weather patterns. */
    private double defaultRatio;

    /**
     * Create a WeatherType enum, pre setup.
     *
     * @param weather true if weather is required
     * @param thunder true if thunder is required
     * @param defaultRatio the default ratio to use if not specified in configuration file
     */
    WeatherType(boolean weather, boolean thunder, double defaultRatio) {
        this.weather = weather;
        this.thunder = thunder;
        this.defaultRatio = defaultRatio;
    }

    /**
     * Return if this WeatherType wants weather.
     *
     * @return true if weather is required
     */
    public boolean hasWeather() {
        return weather;
    }

    /**
     * Return if this WeatherSate wants thunder.
     *
     * @return true if thunder is required
     */
    public boolean hasThunder() {
        return thunder;
    }

    /**
     * Return the default ratio of this WeatherType.
     *
     * @return default ratio of weather
     */
    public double getDefaultRatio() {
        return defaultRatio;
    }
}
