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

import pw.ollie.politics.group.level.GroupLevel;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A (configured) ruleset for a universe.
 */
public final class UniverseRules {
    private final String name;
    private final String description;
    private final Map<String, GroupLevel> groupLevels;

    private UniverseRules(String name, String description, Map<String, GroupLevel> groupLevels) {
        this.name = name;
        this.description = description;
        this.groupLevels = groupLevels;
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

//    public void save(Configuration config) {
// TODO: implement saving to configuration - Bukkit doesn't provide as simple a way to do this as Spout did
//    }

    public static UniverseRules load(String name, YamlConfiguration config) {
        return null;
// TODO: implement loading from configuration - Bukkit doesn't provide as simple a way to do this as Spout did
    }
}
