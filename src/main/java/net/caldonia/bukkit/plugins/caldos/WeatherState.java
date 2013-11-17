package net.caldonia.bukkit.plugins.caldos;

import org.bukkit.configuration.ConfigurationSection;

/**
 * WeatherState tracks the behaviour, requirements and availability of a WeatherType within a WeatherPattern.
 */
public class WeatherState {
    /** WeatherType this WeatherState tracks. */
    private WeatherType weatherType;

    /** Ratio as is listed in configuration file, or WeatherType.getDefaultRatio(). */
    private double rawRatio;

    /** Actual ratio of WeatherType after summing all ratios within this WeatherPattern. */
    private double ratio;

    /** The minimum amount of ticks this WeatherType may be active for. */
    private long minimum;

    /** The maximum amount of ticks this WeatherType may be active for. */
    private long maximum;

    /**
     * Construct and initialise as WeatherState with variables populated and tracking state ready, updateRatio() must
     * be called before the WeatherState is used for calculations.
     *
     * @param weatherType which this WeatherState describes
     * @param configurationSection configuration section from config file which describes this state
     */
    public WeatherState(WeatherType weatherType, ConfigurationSection configurationSection) {
        this.weatherType = weatherType;

        /* Copy data from configuration file, length defaults are sensible and default ration is obtained from the
         * WeatherType enum. */
        ratio = rawRatio = configurationSection.getDouble("ratio", weatherType.getDefaultRatio());
        minimum = configurationSection.getLong("minimum", 600);
        maximum = configurationSection.getLong("maximum", 0);
    }

    /**
     * Provide the raw WeatherType enum which this WeatherState tracks.
     *
     * @return WeatherType which this object tracks
     */
    public WeatherType getWeatherType() {
        return weatherType;
    }

    /**
     * Return the raw ratio as defined in the configuration file or WeatherType enum, this MUST NOT be used in
     * calculations as it does not take into account other ratio values.
     *
     * @return raw ratio value as described by defaults or in configuration
     */
    public double getRawRatio() {
        return rawRatio;
    }

    /**
     * Update the actual ratio to be used with the total of all ratios in the WeatherPattern, likely only called
     * immediately after all WeatherStates have been initialised.
     *
     * @param total total of all WeatherState raw ratios
     */
    public void updateRatio(double total) {
        ratio = rawRatio / total;
    }
}