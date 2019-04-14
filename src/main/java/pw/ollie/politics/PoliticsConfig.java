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
package pw.ollie.politics;

import pw.ollie.politics.util.message.ColourScheme;
import pw.ollie.politics.util.serial.ConfigUtil;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class PoliticsConfig {
    private static final String CONFIG_FILE_NAME = "config.yml";

    private final PoliticsPlugin plugin;
    private final File configFile;

    private ColourScheme colourScheme;

    public PoliticsConfig(PoliticsPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getFileSystem().getBaseDir(), CONFIG_FILE_NAME);
    }

    public ColourScheme getColourScheme() {
        return colourScheme;
    }

    void loadConfig() {
        // save the default config file without overwriting an existing one
        plugin.saveResource("config.yml", false);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // load plugin messaging colour scheme
        ConfigurationSection colourSchemeSection = ConfigUtil.getOrCreateSection(config, "colour-scheme");
        ChatColor normal = ChatColor.valueOf(colourSchemeSection.getString("normal", "gray").toUpperCase());
        ChatColor error = ChatColor.valueOf(colourSchemeSection.getString("error", "red").toUpperCase());
        ChatColor highlight = ChatColor.valueOf(colourSchemeSection.getString("highlight", "yellow").toUpperCase());
        ChatColor prefix = ChatColor.valueOf(colourSchemeSection.getString("prefix", "dark_green").toUpperCase());
        colourScheme = new ColourScheme(normal, error, highlight, prefix);
    }
}
