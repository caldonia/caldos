package net.caldonia.bukkit.plugins.caldos;

import org.bukkit.configuration.ConfigurationSection;

/**
 * WeatherPattern tracks and controls the behaviour of weather and thunder in a world.
 */
public class WeatherPattern {
    /** Cache of if the weather pattern currently desires thunder. */
    private boolean desireThunder = false;

    /** Cache of if the weather pattern currently desires weather. */
    private boolean desireWeather = false;

    /** Current time of the world in ticks. */
    private long lastTime;

    /**
     * Constructs a weather pattern object from a configuration section, initialises the object ready for query.
     *
     * @param configurationSection the configuration section to base this weather pattern off of
     * @param initialTime the time of the world when the weather pattern was created
     */
    public WeatherPattern(ConfigurationSection configurationSection, long initialTime) {
        // TODO: LOAD CONFIGURATION STATE!

        /* Force update of time which will cause a weather calculation to be made. */
        updateTime(initialTime);
    }

    public void updateTime(long time) {
        // TODO: UPDATE DESIRED WEATHER HERE!

        /* Update the last time we were polled. */
        lastTime = time;
    }

    /**
     * Returns if the weather pattern requires thunder.
     *
     * @return true if thunder is required
     */
    public boolean desireThunder() {
        return desireThunder;
    }

    /**
     * Returns if the weather pattern requires weather.
     *
     * @return true if weather is required
     */
    public boolean desireWeather() {
        return desireWeather;
    }
}
