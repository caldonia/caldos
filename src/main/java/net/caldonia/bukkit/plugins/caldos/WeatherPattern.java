package net.caldonia.bukkit.plugins.caldos;

import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * WeatherPattern tracks and controls the behaviour of weather and thunder in a world.
 */
public class WeatherPattern {
    /** The current WeatherSate in this WeatherPattern, default state is clear. */
    private WeatherState currentWeatherState;

    /** Current WeatherState is held until this tick. */
    private long currentHeldUntil;

    /** Current time of the world in ticks. */
    private long lastTime;

    /** WeatherPattern specific information about WeatherTypes. */
    private Map<WeatherType, WeatherState> weatherStates = new HashMap<>();

    /**
     * Constructs a weather pattern object from a configuration section, initialises the object ready for query.
     *
     * @param configurationSection the configuration section to base this weather pattern off of
     * @param initialTime the time of the world when the weather pattern was created
     */
    public WeatherPattern(ConfigurationSection configurationSection, long initialTime) {
        /* Store total ratio in configuration file. */
        double totalRatio = 0;

        /* Cycle through all enums and create new weather states. */
        for (WeatherType wt : WeatherType.values()) {
            WeatherState weatherState = new WeatherState(wt, configurationSection.getConfigurationSection(wt.name()));
            weatherStates.put(wt, weatherState);

            /* Sum the ratio into total ratio. */
            totalRatio += weatherState.getRawRatio();
        }

        /* Update all states with the final total ratio, this allows users to use any total number of units in the
         * configuration file, rather then having to sum to 1.0. */
        for (WeatherState ws : weatherStates.values()) {
            ws.updateRatio(totalRatio);
        }

        /* Find WeatherState for WeatherType.CLEAR and set to the current WeatherState. */
        currentWeatherState = weatherStates.get(WeatherType.CLEAR);
        currentHeldUntil = initialTime;

        /* Force update of time which will cause a weather calculation to be made. */
        updateTime(initialTime);
    }

    /**
     * Update the current time in this WeatherPattern, here decisions about the weather are made.
     *
     * @param time new full time from world
     */
    public void updateTime(long time) {
        /* If this isn't the first time run then cycle through the states and tell them how much time has passed so
         * that they can calculate the number of ticks of their WeatherType has available. */
        if (lastTime != 0) {
            for (WeatherState ws : weatherStates.values()) {
                ws.passTime(time - lastTime);
            }
        }

        /* Check to see if the weather is currently being held, if we haven't passed that point do nothing. */
        if (time >= currentHeldUntil) {
            /* Copy the list of WeatherStates. */
            List<WeatherState> ws = new ArrayList<>();
            ws.addAll(weatherStates.values());

            /* Sum all the allocated ticks together. */
            long totalTicks = 0;
            for (WeatherState wss : ws) {
                totalTicks += wss.getTicksAvailable();
            }

            /* Order the array with the Comparator. */
            Collections.sort(ws, new WeatherSelectorComparator(totalTicks));

            /* Select the first state. */
            WeatherState wss = ws.get(0);

            /* If there's a top entry then set it, else sleep for a delay. */
            if (wss != null) {
                currentWeatherState = wss;
                currentHeldUntil = time + wss.allocateTicks();
            } else {
                currentHeldUntil = time + WeatherState.INITIAL_ALLOCATION_TICKS;
            }
        }

        /* Update the last time we were polled. */
        lastTime = time;
    }

    /**
     * Returns if the weather pattern requires thunder.
     *
     * @return true if thunder is required
     */
    public boolean desireThunder() {
        return currentWeatherState.getWeatherType().hasThunder();
    }

    /**
     * Returns if the weather pattern requires weather.
     *
     * @return true if weather is required
     */
    public boolean desireWeather() {
        return currentWeatherState.getWeatherType().hasWeather();
    }
}
