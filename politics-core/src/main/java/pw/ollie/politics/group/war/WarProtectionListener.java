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
package pw.ollie.politics.group.war;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.event.plot.PlotProtectionTriggerEvent;
import pw.ollie.politics.event.plot.subplot.SubplotProtectionTriggerEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Listens to Politics' protection trigger events and cancels them if necessary due to ongoing {@link War}s.
 */
final class WarProtectionListener implements Listener {
    private final PoliticsPlugin plugin;

    WarProtectionListener(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlotProtectionTrigger(PlotProtectionTriggerEvent event) {
        // todo override protections which aren't valid during war
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSubplotProtectionTrigger(SubplotProtectionTriggerEvent event) {
        // todo override protections which aren't valid during war
    }
}
