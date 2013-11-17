package net.caldonia.bukkit.plugins.caldos;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

/**
 * WeatherState tracks the behaviour, requirements and availability of a WeatherType within a WeatherPattern.
 */
public class WeatherState {
    /** Default number of ticks to calculate initial allocation with. */
    public static final long INITIAL_ALLOCATION_TICKS = 24000;

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

    /** The number of ticks which are available for this WeatherType. */
    private double ticksAvailable;

    /** Internal Random object. */
    private Random random = new Random();

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
        ticksAvailable = (INITIAL_ALLOCATION_TICKS * ratio) + (100 * random.nextDouble());
    }

    /**
     * Increase the amount of ticks of this WeatherType available based upon the number of ticks which have passed in
     * game.
     *
     * @param ticksPassed the number of ticks which have passed
     */
    public void passTime(long ticksPassed) {
        ticksAvailable += ticksPassed * ratio;
    }

    /**
     * Check to see if the WeatherState has enough ticks available to meet it's minimum requirement.
     *
     * @return true if there are enough ticks available to use
     */
    public boolean isAvailable() {
        return (ticksAvailable > minimum);
    }

    /**
     * Get the number of ticks which are available for this WeatherState.
     *
     * @return number of ticks available
     */
    public double getTicksAvailable() {
        return ticksAvailable;
    }

    /**
     * Allocate a number of ticks from this WeatherState, consuming them from the total available ticks. Does not check
     * to see if minimum number of ticks is available, will consume regardless and may result in negative availability.
     *
     * @return number of ticks to use
     */
    public long allocateTicks() {
        long randomTicks = minimum;

        /* Only attempt to add a random number of ticks if the available count is over the minimum allocation. */
        if (ticksAvailable > minimum) {
            randomTicks += (long) (random.nextDouble() * ((ticksAvailable > maximum ? maximum : ticksAvailable) - minimum));
        }

        ticksAvailable -= randomTicks;
        return randomTicks;
    }

    /**
     * Calculate how close the actual ratio of total allocated blocks is to the desired ratio. The ratio of that is
     * the resulting value.
     *
     * @param totalAllocated the total number of blocks currently allocated
     * @return how likely this weather state should be used
     */
    public double getLikelihood(double totalAllocated) {
        double totalRatio = ticksAvailable / totalAllocated;
        return (totalRatio / ratio);
    }
}