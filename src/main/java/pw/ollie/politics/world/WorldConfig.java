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
package pw.ollie.politics.world;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Stores Politics configuration values specific to a single world.
 */
public final class WorldConfig {
    private final String name;

    /**
     * Constructs a blank new configuration for a world with the given name.
     *
     * @param name the world name for this configuration
     */
    public WorldConfig(String name) {
        this.name = name;
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
     * Saves configuration values to the given configuration node.
     *
     * @param config the node to save values to
     */
    public void save(ConfigurationSection config) {
    }

    /**
     * Loads a WorldConfig with the given world name from the given configuration node.
     *
     * @param name   the name of the world
     * @param config the configuration node to load from
     * @return a world configuration for the given world name from the given configuration node
     */
    public static WorldConfig load(String name, ConfigurationSection config) {
        WorldConfig wc = new WorldConfig(name);
        return wc;
    }
}
