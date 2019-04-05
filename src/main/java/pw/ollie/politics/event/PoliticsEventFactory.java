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
package pw.ollie.politics.event;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.event.group.GroupPropertySetEvent;
import pw.ollie.politics.event.player.PlayerChangePlotEvent;
import pw.ollie.politics.event.plot.PlotOwnerChangeEvent;
import pw.ollie.politics.event.universe.UniverseCreateEvent;
import pw.ollie.politics.event.universe.UniverseDestroyEvent;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.world.plot.Plot;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public final class PoliticsEventFactory {
    private final PoliticsPlugin plugin;

    public PoliticsEventFactory(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    public GroupPropertySetEvent callGroupPropertySetEvent(Group group, int property, Object value) {
        return callEvent(new GroupPropertySetEvent(group, property, value));
    }

    public PlotOwnerChangeEvent callPlotOwnerChangeEvent(Plot plot, int groupId, boolean add) {
        return callEvent(new PlotOwnerChangeEvent(plot, groupId, add));
    }

    public PlayerChangePlotEvent callPlayerChangePlotEvent(Player player, Plot from, Plot to) {
        return callEvent(new PlayerChangePlotEvent(player, from, to));
    }

    public UniverseCreateEvent callUniverseCreateEvent(Universe universe) {
        return callEvent(new UniverseCreateEvent(universe));
    }

    public UniverseDestroyEvent callUniverseDestroyEvent(Universe universe) {
        return callEvent(new UniverseDestroyEvent(universe));
    }

    public <T extends Event> T callEvent(T event) {
        plugin.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public PoliticsPlugin getPlugin() {
        return this.plugin;
    }
}
