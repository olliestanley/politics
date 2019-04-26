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
package pw.ollie.politics.group;

import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.universe.Universe;

import org.bukkit.plugin.PluginManager;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Provides helper methods for accessing {@link Group}s and related features.
 */
public final class GroupManager {
    // todo docs
    private final PoliticsPlugin plugin;

    public GroupManager(PoliticsPlugin plugin) {
        this.plugin = plugin;

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(new GroupMessageListener(plugin), plugin);
        pluginManager.registerEvents(new GroupProtectionListener(plugin), plugin);
    }

    public Set<Group> getAllCitizenGroups(UUID playerId) {
        Set<Group> result = new THashSet<>();
        for (Universe universe : plugin.getUniverseManager().getUniverses()) {
            result.addAll(universe.getCitizenGroups(playerId));
        }
        return result;
    }

    public List<GroupLevel> getGroupLevels() {
        return plugin.getUniverseManager().getGroupLevels();
    }

    public Group getGroupById(int id) {
        return plugin.getUniverseManager().getGroupById(id);
    }

    public Group getGroupByTag(String tag) {
        return plugin.getUniverseManager().getGroupByTag(tag);
    }

    public PoliticsPlugin getPlugin() {
        return plugin;
    }
}
