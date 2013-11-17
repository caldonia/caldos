package net.caldonia.bukkit.plugins.caldos;

/**
 * Representation of the state of the weather.
 */
public enum WeatherState {
    /** Clear skies. */
    CLEAR(false, false),
    /** Just raining. */
    RAIN(true, false),
    /** Rain with thunder. */
    STORM(true, true),
    /** Just thunder. */
    THUNDER(false, true);

    /** If weather is required. */
    private boolean weather;
    /** If thunder is required. */
    private boolean thunder;

    /**
     * Create a WeatherState enum, pre setup.
     *
     * @param weather true if weather is required
     * @param thunder true if thunder is required
     */
    WeatherState(boolean weather, boolean thunder) {
        this.weather = weather;
        this.thunder = thunder;
    }

    /**
     * Return if this WeatherState wants weather.
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
}
