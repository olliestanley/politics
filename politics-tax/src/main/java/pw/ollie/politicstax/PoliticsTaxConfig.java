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
package pw.ollie.politicstax;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class PoliticsTaxConfig {
    /**
     * The file name for the configuration file.
     */
    private static final String CONFIG_FILE_NAME = "config.yml";

    private final PoliticsTaxPlugin plugin;
    private final File configFile;

    private double maxFixedTax;
    private int taxPeriod;

    public PoliticsTaxConfig(PoliticsTaxPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), CONFIG_FILE_NAME);
    }

    /**
     * Gets the configured maximum value for fixed-amount taxation by groups.
     *
     * @return the maximum taxation amount
     */
    public double getMaxFixedTax() {
        return maxFixedTax;
    }

    /**
     * Gets the period between taxation collection, measured in minutes.
     *
     * @return the time between tax collections
     */
    public int getTaxPeriod() {
        return taxPeriod;
    }

    void loadConfig() {
        // save the default config file without overwriting an existing one
        plugin.saveResource("config.yml", false);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        maxFixedTax = config.getDouble("max-fixed-tax", 10);
        taxPeriod = config.getInt("tax-period", 1440);
    }
}
