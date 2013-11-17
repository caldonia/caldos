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

    /** Ratio of clear to any other weather to aim for. */
    private double clearRatio;

    /** The minimum length of a clear period in ticks. */
    private long minimumClear;

    /** The maximum length of a clear period in ticks. */
    private long maximumClear;

    /** Ratio of rain to any other weather to aim for. */
    private double rainRatio;

    /** The minimum length of a rain period in ticks. */
    private long minimumRain;

    /** The maximum length of a rain period in ticks. */
    private long maximumRain;

    /** Ratio of storm to any other weather to aim for. */
    private double stormRatio;

    /** The minimum length of a storm period in ticks. */
    private long minimumStorm;

    /** The maximum length of a storm period in ticks. */
    private long maximumStorm;

    /** Ratio of thunder to any other weather to aim for. */
    private double thunderRatio;

    /** The minimum length of a thunder period in ticks. */
    private long minimumThunder;

    /** The maximum length of a thunder period in ticks. */
    private long maximumThunder;

    /**
     * Constructs a weather pattern object from a configuration section, initialises the object ready for query.
     *
     * @param configurationSection the configuration section to base this weather pattern off of
     * @param initialTime the time of the world when the weather pattern was created
     */
    public WeatherPattern(ConfigurationSection configurationSection, long initialTime) {
        // Load values in from configuration, add default of totally clear.
        double rawClearRatio = configurationSection.getDouble("clear.ratio", 1.0D);
        minimumThunder = configurationSection.getLong("clear.minimum", 600);
        maximumThunder = configurationSection.getLong("clear.maximum", 0);

        double rawRainRatio = configurationSection.getDouble("rain.ratio", 0D);
        minimumRain = configurationSection.getLong("rain.minimum", 600);
        maximumRain = configurationSection.getLong("rain.maximum", 0);

        double rawStormRatio = configurationSection.getDouble("storm.ratio", 0D);
        minimumStorm = configurationSection.getLong("storm.minimum", 600);
        maximumStorm = configurationSection.getLong("storm.maximum", 0);

        double rawThunderRatio = configurationSection.getDouble("thunder.ratio", 0D);
        minimumThunder = configurationSection.getLong("thunder.minimum", 600);
        maximumThunder = configurationSection.getLong("thunder.maximum", 0);

        // Sum up the whole
        double totalRatio = rawClearRatio + rawRainRatio + rawStormRatio + rawThunderRatio;

        // Put actual ratios in weather pattern, this prevents the user having to make ratios add up to 1.0.
        clearRatio = rawClearRatio / totalRatio;
        rainRatio = rawRainRatio / totalRatio;
        stormRatio = rawStormRatio / totalRatio;
        thunderRatio = rawThunderRatio / totalRatio;

        /* Set initial state to clear. */
        weatherState = WeatherState.CLEAR;

        /* Force update of time which will cause a weather calculation to be made. */
        updateTime(initialTime);
    }

    /**
     * Update the current time in this WeatherPattern, here decisions about the weather are made.
     *
     * @param time new full time from world
     */
    public void updateTime(long time) {
        // Valid Transitions
        // Clear -> Rain, Thunder
        // Rain -> Clear, Storm
        // Storm -> Rain
        // Thunder -> Clear, Storm

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
