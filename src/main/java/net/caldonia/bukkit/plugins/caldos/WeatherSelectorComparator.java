package net.caldonia.bukkit.plugins.caldos;

import java.util.Comparator;

/**
 * Comparator used to order Arrays/Collections in their likelihood to be used for a new weather selection.
 */
public class WeatherSelectorComparator implements Comparator<WeatherState> {
    /** How many total ticks have been allocated. */
    private long totalAvailable;

    /**
     * Creates a working Comparator preloaded with the total number of allocated ticks.
     *
     * @param totalAvailable total number of allocated ticks
     */
    public WeatherSelectorComparator(long totalAvailable) {
        this.totalAvailable = totalAvailable;
    }

    /**
     * Compares two WeatherStates and orders in likelihood of use for next WeatherState. WeatherStates which are
     * available are prioritised over none, then ordered by Likelihood.
     *
     * @param o1 first WeatherState
     * @param o2 second WeatherState
     * @return the order of WeatherState's as -1, 0, 1
     */
    @Override
    public int compare(WeatherState o1, WeatherState o2) {
        if (o1.isAvailable() == o2.isAvailable()) {
            if (o1.getLikelihood(totalAvailable) > o2.getLikelihood(totalAvailable)) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (o1.isAvailable()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
