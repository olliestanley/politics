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

import gnu.trove.map.hash.THashMap;

import pw.ollie.politics.Politics;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.util.serial.ConfigUtil;
import pw.ollie.politics.util.stream.CollectorUtil;
import pw.ollie.politics.util.stream.StreamUtil;

import com.google.mu.util.stream.BiStream;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * A configured set of rules for a {@link Universe}.
 */
public final class UniverseRules {
    // todo docs
    private final String name;
    private final String description;
    private final String wildernessMessage;
    private final boolean warsEnabled;
    private final Map<String, GroupLevel> groupLevels;

    private UniverseRules(String name, String description, String wildernessMessage, boolean warsEnabled, Map<String, GroupLevel> groupLevels) {
        this.name = name;
        this.description = description;
        this.wildernessMessage = wildernessMessage;
        this.warsEnabled = warsEnabled;
        this.groupLevels = groupLevels;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getWildernessMessage() {
        return wildernessMessage;
    }

    public Stream<GroupLevel> streamGroupLevels() {
        return groupLevels.values().stream();
    }

    public GroupLevel getGroupLevel(String name) {
        return groupLevels.get(name.toLowerCase());
    }

    public boolean hasGroupLevel(GroupLevel level) {
        return groupLevels.containsValue(level);
    }

    public boolean areWarsEnabled() {
        return warsEnabled;
    }

    public void save(ConfigurationSection config) {
        config.set("description", description);

        ConfigurationSection warsSection = ConfigUtil.getOrCreateSection(config, "wars");
        warsSection.set("enabled", warsEnabled);

        groupLevels.values().forEach(level -> level.save(ConfigUtil.getOrCreateSection(config, "levels" + level.getId())));
    }

    public static UniverseRules load(String name, ConfigurationSection config) {
        String description = config.getString("description", "No description given.");
        String wildernessMessage = config.getString("wilderness-message", "Wilderness");

        ConfigurationSection warsSection = ConfigUtil.getOrCreateSection(config, "wars");
        boolean warsEnabled = warsSection.getBoolean("enabled", false);

        Map<String, GroupLevel> levelMap = new THashMap<>();
        // Get the levels turned into objects
        Map<GroupLevel, List<String>> levels = new THashMap<>();

        ConfigurationSection levelsNode = ConfigUtil.getOrCreateSection(config, "levels");
        StreamUtil.biStream(levelsNode.getKeys(false), levelsNode::getConfigurationSection).forEach((levelKey, levelNode) -> {
            if (levelNode == null) {
                Politics.getLogger().log(Level.SEVERE, "Failed to load a level for a universe ruleset",
                        new InvalidConfigurationException("Error in universe ruleset '" + name + "': node '" + levelKey + "' formatted badly."));
                return;
            }

            GroupLevel level = GroupLevel.load(levelKey, levelNode, levels);
            if (levelMap.putIfAbsent(level.getId(), level) != null) {
                Politics.getLogger().log(Level.SEVERE, "Duplicate ids for universe rules: " + level.getId());
                Politics.getLogger().log(Level.SEVERE, "Only the first universe rules will be loaded...");
            }
        });

        // Turn these levels into only objects
        BiStream.from(levels).forEach((level, list) -> level.setAllowedChildren(
                list.stream().map(String::toLowerCase).map(levelMap::get).collect(CollectorUtil.toTHashSet())));
        return new UniverseRules(name, description, wildernessMessage, warsEnabled, levelMap);
    }
}
