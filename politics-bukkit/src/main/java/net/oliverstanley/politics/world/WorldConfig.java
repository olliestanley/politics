/*
 * This file is part of Politics.
 *
 * Copyright (c) 2019 Oliver Stanley
 * Politics is licensed under the Affero General Public License Version 3.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.oliverstanley.politics.world;

import net.oliverstanley.politics.util.config.ConfigUtil;

import gnu.trove.map.hash.THashMap;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Stores Politics configuration values specific to a single world.
 */
public final class WorldConfig {
    private final String name;

    // plot settings
    private final boolean plots;

    // subplot settings
    private final boolean subplots;

    // other settings
    private final Map<String, String> stringSettings;
    private final Map<String, List<String>> listSettings;

    /**
     * Constructs a new configuration for a world with the given name.
     *
     * @param name     the world name for this configuration
     * @param plots    whether plots are enabled in the world
     * @param subplots whether subplots are enabled in the world
     */
    WorldConfig(String name, boolean plots, boolean subplots, Map<String, String> stringSettings, Map<String, List<String>> listSettings) {
        this.name = name;
        this.plots = plots;
        this.subplots = subplots;
        this.stringSettings = stringSettings;
        this.listSettings = listSettings;
    }

    /**
     * Gets the name of the world this configuration is for.
     *
     * @return the world name for this configuration
     */
    public String getName() {
        return name;
    }

    /**
     * Gets whether this world config allows plots.
     *
     * @return whether this config allows plots
     */
    public boolean hasPlots() {
        return plots;
    }

    /**
     * Gets whether this world config allows subplots.
     *
     * @return whether this config allows subplots
     */
    public boolean hasSubplots() {
        return subplots;
    }

    /**
     * Get a raw setting from the world config. Can be used by add-on plugins to add per-world settings easily.
     *
     * @param path the config path to get the setting for
     * @return the setting of the given node, or empty if it is not set
     */
    public Optional<String> getRawSetting(String path) {
        return Optional.ofNullable(stringSettings.get(path));
    }

    /**
     * Get a {@link List} setting from the world config. Can be used by add-on plugins to add per-world settings easily.
     *
     * @param path the config path to get the setting for
     * @return the setting of the given node, or empty List if it is not set
     */
    public List<String> getListSetting(String path) {
        List<String> list = listSettings.get(path);
        return list != null ? list : new ArrayList<>();
    }

    /**
     * Saves configuration values to the given configuration node. Only saves settings used by core Politics.
     *
     * @param config the node to save values to
     */
    public void save(ConfigurationSection config) {
        ConfigurationSection plotsSection = ConfigUtil.getOrCreateSection(config, "plots");
        plotsSection.set("enabled", plots);

        {
            ConfigurationSection subplotsSection = ConfigUtil.getOrCreateSection(plotsSection, "subplots");
            subplotsSection.set("enabled", subplots);
        }
    }

    /**
     * Loads a WorldConfig with the given world name from the given configuration node.
     *
     * @param name   the name of the world
     * @param config the configuration node to load from
     * @return a world configuration for the given world name from the given configuration node
     */
    public static WorldConfig load(String name, ConfigurationSection config) {
        ConfigurationSection plotsSection = ConfigUtil.getOrCreateSection(config, "plots");
        boolean plots = plotsSection.getBoolean("enabled", true);

        ConfigurationSection subplotsSection = ConfigUtil.getOrCreateSection(plotsSection, "subplots");
        boolean subplots = plots && subplotsSection.getBoolean("enabled", true);

        Map<String, String> stringSettings = searchSectionRecursiveStrings("", config);
        Map<String, List<String>> listSettings = searchSectionRecursiveLists("", config);

        return new WorldConfig(name, plots, subplots, stringSettings, listSettings);
    }

    private static Map<String, String> searchSectionRecursiveStrings(String nodePrefix, ConfigurationSection section) {
        String adjPrefix = (nodePrefix.endsWith(".") || nodePrefix.isEmpty()) ? nodePrefix : nodePrefix + ".";

        Map<String, String> result = new THashMap<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection subsection = section.getConfigurationSection(key);
            if (subsection != null) {
                result.putAll(searchSectionRecursiveStrings(adjPrefix + key, subsection));
            }

            if (section.isList(key) || section.isSet(key)) {
                continue;
            }

            result.put(adjPrefix + key, section.getString(key));
        }

        return result;
    }

    private static Map<String, List<String>> searchSectionRecursiveLists(String nodePrefix, ConfigurationSection section) {
        String adjPrefix = (nodePrefix.endsWith(".") || nodePrefix.isEmpty()) ? nodePrefix : nodePrefix + ".";

        Map<String, List<String>> result = new THashMap<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection subsection = section.getConfigurationSection(key);
            if (subsection != null) {
                result.putAll(searchSectionRecursiveLists(adjPrefix + key, subsection));
            }

            List<String> stringList = section.getStringList(key);
            if (stringList != null && !stringList.isEmpty()) {
                result.put(adjPrefix + key, stringList);
            }
        }

        return result;
    }
}
