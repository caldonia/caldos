package net.caldonia.bukkit.plugins.caldos;

import org.bukkit.configuration.ConfigurationSection;

/**
 * WeatherPattern tracks and controls the behaviour of weather and thunder in a world.
 */
public class WeatherPattern {
    /** The current WeatherSate in this WeatherPattern. */
    private WeatherState weatherState;

    /** Current time of the world in ticks. */
    private long lastTime;

    /**
     * Constructs a weather pattern object from a configuration section, initialises the object ready for query.
     *
     * @param configurationSection the configuration section to base this weather pattern off of
     * @param initialTime the time of the world when the weather pattern was created
     */
    public WeatherPattern(ConfigurationSection configurationSection, long initialTime) {
        weatherState = WeatherState.CLEAR;

        // TODO: LOAD CONFIGURATION STATE!

        /* Force update of time which will cause a weather calculation to be made. */
        updateTime(initialTime);
    }

    /**
     * Update the current time in this WeatherPattern, here decisions about the weather are made.
     *
     * @param time new full time from world
     */
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
        return weatherState.hasThunder();
    }

    /**
     * Returns if the weather pattern requires weather.
     *
     * @return true if weather is required
     */
    public boolean desireWeather() {
        return weatherState.hasWeather();
    }
}
