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

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.universe.Universe;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

    public Stream<Group> streamGroups() {
        return plugin.getUniverseManager().streamUniverses().flatMap(Universe::streamGroups);
    }

    public Stream<Group> streamCitizenGroups(UUID playerId) {
        return plugin.getUniverseManager().streamUniverses()
                .map(universe -> universe.streamCitizenGroups(playerId))
                .flatMap(Function.identity());
    }

    public Stream<GroupLevel> streamGroupLevels() {
        return plugin.getUniverseManager().streamGroupLevels();
    }

    public Group getGroupById(int id) {
        return plugin.getUniverseManager().getGroupById(id);
    }

    public Group getGroupByTag(String tag) {
        return plugin.getUniverseManager().getGroupByTag(tag);
    }

    public GroupLevel getGroupLevel(String name) {
        return streamGroupLevels().filter(l -> l.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public boolean hasGroupOfLevel(Player player, GroupLevel level) {
        return streamCitizenGroups(player.getUniqueId()).map(Group::getLevel).anyMatch(level::equals);
    }

    public PoliticsPlugin getPlugin() {
        return plugin;
    }
}
