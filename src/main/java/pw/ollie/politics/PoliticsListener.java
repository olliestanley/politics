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

import pw.ollie.politics.event.player.PlayerChangePlotEvent;
import pw.ollie.politics.world.plot.Plot;
import pw.ollie.politics.world.WorldManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Listener for general/simple purposes.
 */
// for block-based plot protection, see PlotBlockProtectionListener
// for combat protection, see GroupCombatProtectionListener
public final class PoliticsListener implements Listener {
    private final PoliticsPlugin plugin;
    private final WorldManager worldManager;

    PoliticsListener(PoliticsPlugin plugin) {
        this.plugin = plugin;
        worldManager = plugin.getWorldManager();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Plot from = worldManager.getPlotAt(event.getFrom());
        Plot to = worldManager.getPlotAt(event.getTo());

        if (!from.equals(to)) {
            PlayerChangePlotEvent pcpe = Politics.getEventFactory().callPlayerChangePlotEvent(player, from, to);
            if (pcpe.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }
}
