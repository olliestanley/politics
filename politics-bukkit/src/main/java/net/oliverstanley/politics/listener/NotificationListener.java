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
package net.oliverstanley.politics.listener;

import net.oliverstanley.politics.PoliticsPlugin;
import net.oliverstanley.politics.event.player.PlayerPlotChangeEvent;
import net.oliverstanley.politics.group.Group;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listens to events to send relevant {@link Group}-related informational messages to players.
 */
public final class NotificationListener implements Listener {
    private final PoliticsPlugin plugin;

    public NotificationListener(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void sendJoinMessage(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // todo
//        plugin.getUniverseHandler().citizenGroups(player.getUniqueId())
//                .forEach(group -> notifier.notifyPlayerGroupMotd(player, group));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void sendTerritoryMessage(PlayerPlotChangeEvent event) {
        Player player = event.getPlayer();
        Group newPlotOwner = event.getTo().getOwner().orElse(null);
        Group oldPlotOwner = event.getFrom().getOwner().orElse(null);

        if (newPlotOwner == null) {
            if (oldPlotOwner != null) {
                // todo
//                notifier.notifyPlayerTerritoryExit(player, oldPlotOwner);
//                notifier.notifyPlayerWilderness(player, oldPlotOwner.getUniverse());
            }
        } else if (!newPlotOwner.equals(oldPlotOwner)) {
            // todo
//            notifier.notifyPlayerTerritoryEntry(player, newPlotOwner);

            if (oldPlotOwner != null) {
                // todo
//                notifier.notifyPlayerTerritoryExit(player, oldPlotOwner);
            }
        }
    }
}
