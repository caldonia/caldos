package net.caldonia.bukkit.plugins.caldos;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Caldos weather control plugin, not designed to do stupid things, just customise the ratios of weather types.
 */
public class Caldos extends JavaPlugin implements Listener, Runnable {
    /** Period between weather checks (in ticks). */
    public static int PERIODIC_DELAY = 20;

    /** Storage for weather patterns. */
    private Map<String, WeatherPattern> weatherPatterns;

    @Override
    public void onEnable() {
        /* Save default configuration to disk. */
        saveDefaultConfig();

        /* Load configuration. */
        loadConfiguration();

        /* Register event manager. */
        getServer().getPluginManager().registerEvents(this, this);

        /* Schedule periodic weather checks, wait for a second first. */
        getServer().getScheduler().scheduleSyncRepeatingTask(this, this, PERIODIC_DELAY, PERIODIC_DELAY);
    }

    @Override
    public void onDisable() {
        /* Unregister all events. */
        HandlerList.unregisterAll((Plugin) this);

        /* Cancel the periodic checks. */
        getServer().getScheduler().cancelTasks(this);
    }

    /**
     *  Every second evaluate worlds to see what weather state they desire.
     */
    @Override
    public void run() {
        /* Cycle through worlds. */
        for (String worldName : weatherPatterns.keySet()) {
            /* Get configured worlds. */
            World world = getServer().getWorld(worldName);

            /* Skip worlds which are in config but not loaded. */
            if (world != null) {
                WeatherPattern weatherPattern = weatherPatterns.get(worldName);

                /* Update the weather pattern to say how much time has passed, this may change desirable states. */
                weatherPattern.updateTime(world.getFullTime());

                /* Force the world weather to be as the weather pattern requires, though play safe as we do not
                 * know how the server will respond if we set the weather to the same thing. */
                if (world.hasStorm() != weatherPattern.desireWeather()) {
                    world.setStorm(weatherPattern.desireWeather());
                }

                if (world.isThundering() != weatherPattern.desireThunder()) {
                    world.setThundering(weatherPattern.desireThunder());
                }
            }
        }
    }

    /**
     * Receive notification when a thunder storm event starts on a world, this allows Caldos the opportunity to cancel
     * unauthorised changes.
     *
     * @param tce thunder change event
     */
    @EventHandler
    public void onThunderChangeEvent(ThunderChangeEvent tce) {
        WeatherPattern weatherPattern = weatherPatterns.get(tce.getWorld().getName());

        /* If we're controlling weather, then ensure that the new state is what the pattern desires. */
        if (weatherPattern != null && weatherPattern.desireThunder() != tce.toThunderState()) {
            tce.setCancelled(true);
            log(Level.INFO, "\"" + tce.getWorld().getName() + "\" thunder was set to \"" + tce.toThunderState() + "\" but not desired, canceled!");
        }
    }

    /**
     * Receive notification when a weather storm event starts on a world, this allows Caldos the opportunity to cancel
     * unauthorised changes.
     *
     * @param wce weather change event
     */
    @EventHandler
    public void onWeatherChangeEvent(WeatherChangeEvent wce) {
        WeatherPattern weatherPattern = weatherPatterns.get(wce.getWorld().getName());

        /* If we're controlling weather, then ensure that the new state is what the pattern desires. */
        if (weatherPattern != null && weatherPattern.desireWeather() != wce.toWeatherState()) {
            wce.setCancelled(true);
            log(Level.INFO, "\"" + wce.getWorld().getName() + "\" weather was set to \"" + wce.toWeatherState() + "\" but not desired, canceled!");
        }
    }

    /**
     * Load configuration from disk and update weather control.
     */
    public void loadConfiguration() {
        /* New patterns. */
        Map<String, WeatherPattern> newWeatherPatterns = new HashMap<>();

        /* Pre-populate weather patterns storage with empty array in case we break out.*/
        if (weatherPatterns == null) {
            weatherPatterns = newWeatherPatterns;
        }

        /* Load configuration from disk. */
        reloadConfig();

        /* Pull out configuration stanza. */
        ConfigurationSection worldConfigurationSection = getConfig().getConfigurationSection("worlds");

        /* Sanity check configuration .*/
        if (worldConfigurationSection == null) {
            log(Level.WARNING, "config.yml does not contain \"worlds\" stanza.");
            return;
        }

        /* Cycle through configured worlds. */
        for (String worldName : worldConfigurationSection.getKeys(false)) {
            log(Level.INFO, "Loading defined world config for \"" + worldName + "\"...");

            /* Find the worlds current time if its loaded, otherwise set to 0. */
            long currentTime = 0;
            World world = getServer().getWorld(worldName);

            if (world != null) {
                currentTime = world.getFullTime();
            }

            /* Create new weather pattern and store. */
            WeatherPattern weatherPattern = new WeatherPattern(worldConfigurationSection.getConfigurationSection(worldName), currentTime);
            newWeatherPatterns.put(worldName, weatherPattern);
        }

        /* Commit new patterns. */
        weatherPatterns = newWeatherPatterns;

        /* Cause recalculation and setting of weather. */
        run();
    }

    /**
     * Helper function for Bukkit/Spigot logging.
     *
     * @param level log level to log message as
     * @param message message to log
     */
    public void log(Level level, String message) {
        getLogger().log(level, message);
    }
}