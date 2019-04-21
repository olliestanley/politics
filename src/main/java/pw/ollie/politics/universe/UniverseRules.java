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
package pw.ollie.politics.universe;

import pw.ollie.politics.Politics;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.util.serial.ConfigUtil;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * A (configured) ruleset for a universe.
 */
public final class UniverseRules {
    private final String name;
    private final String description;
    private final Map<String, GroupLevel> groupLevels;
    private final boolean warsEnabled;

    private UniverseRules(String name, String description, Map<String, GroupLevel> groupLevels, boolean warsEnabled) {
        this.name = name;
        this.description = description;
        this.groupLevels = groupLevels;
        this.warsEnabled = warsEnabled;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<GroupLevel> getGroupLevels() {
        return new ArrayList<>(groupLevels.values());
    }

    public GroupLevel getGroupLevel(String name) {
        return groupLevels.get(name.toLowerCase());
    }

    public boolean areWarsEnabled() {
        return warsEnabled;
    }

    public void save(ConfigurationSection config) {
        config.set("description", description);

        ConfigurationSection warsSection = ConfigUtil.getOrCreateSection(config, "wars");
        warsSection.set("enabled", warsEnabled);

        for (GroupLevel level : groupLevels.values()) {
            ConfigurationSection node = ConfigUtil.getOrCreateSection(config, "levels." + level.getId());
            level.save(node);
        }
    }

    public static UniverseRules load(String name, ConfigurationSection config) {
        String description = config.getString("description", "No description given.");

        ConfigurationSection warsSection = ConfigUtil.getOrCreateSection(config, "wars");
        boolean warsEnabled = warsSection.getBoolean("enabled", false);

        Map<String, GroupLevel> levelMap = new HashMap<>();
        // Get the levels turned into objects
        Map<GroupLevel, List<String>> levels = new HashMap<>();

        ConfigurationSection levelsNode = ConfigUtil.getOrCreateSection(config, "levels");
        for (String levelKey : levelsNode.getKeys(false)) {
            ConfigurationSection levelNode = levelsNode.getConfigurationSection(levelKey);
            if (levelNode == null) {
                Politics.getLogger().log(Level.SEVERE, "Failed to load a level for a universe ruleset",
                        new InvalidConfigurationException("Error in universe ruleset '" + name + "': node '" + levelKey + "' formatted badly."));
                continue;
            }

            ConfigurationSection levelSection = levelsNode.getConfigurationSection(levelKey);
            if (levelSection == null) {
                Politics.getLogger().log(Level.SEVERE, "Failed to load a level for a universe ruleset",
                        new InvalidConfigurationException("Error in universe ruleset '" + name + "': node '" + levelKey + "' formatted badly."));
                continue;
            }

            GroupLevel level = GroupLevel.load(levelKey, levelSection, levels);
            if (levelMap.containsKey(level.getId())) {
                Politics.getLogger().log(Level.SEVERE, "Duplicate ids for universe rules: " + level.getId());
                Politics.getLogger().log(Level.SEVERE, "Only the first universe rules will be loaded...");
                continue;
            }
            levelMap.put(level.getId(), level);
        }

        // Turn these levels into only objects
        for (Map.Entry<GroupLevel, List<String>> levelEntry : levels.entrySet()) {
            Set<GroupLevel> allowed = new HashSet<>();
            for (String ln : levelEntry.getValue()) {
                GroupLevel level = levelMap.get(ln.toLowerCase());
                allowed.add(level);
            }
            levelEntry.getKey().setAllowedChildren(allowed);
        }

        return new UniverseRules(name, description, levelMap, warsEnabled);
    }
}
