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
import net.oliverstanley.politics.event.PoliticsEventFactory;
import net.oliverstanley.politics.event.player.PlayerPlotChangeEvent;
import net.oliverstanley.politics.group.GroupProperty;
import net.oliverstanley.politics.world.PoliticsWorld;
import net.oliverstanley.politics.world.WorldHandler;
import net.oliverstanley.politics.world.plot.Plot;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Comparator;
import java.util.UUID;

/**
 * Listener for player movement and player respawns. Player combat is handled by {@link CombatListener}. Notifications
 * sent on plot changes and other notifications are handled by {@link NotificationListener}.
 */
public final class PlayerListener implements Listener {
    private final PoliticsPlugin plugin;
    private final WorldHandler worldHandler;

    public PlayerListener(PoliticsPlugin plugin) {
        this.plugin = plugin;
        worldHandler = plugin.getWorldHandler();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Plot from = worldHandler.getPlotAt(event.getFrom());
        Plot to = worldHandler.getPlotAt(event.getTo());

        if (!from.equals(to)) {
            PlayerPlotChangeEvent pcpe = PoliticsEventFactory.callPlayerPlotChangeEvent(player, from, to);
            if (pcpe.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        PoliticsWorld world = worldHandler.getWorld(event.getRespawnLocation().getWorld());

        plugin.getUniverseHandler().citizenGroups(playerId)
                .filter(group -> group.getUniverse().containsWorld(world))
                .filter(group -> group.hasProperty(GroupProperty.SPAWN))
                .min(Comparator.comparing(group -> group.getLevel().getRank()))
                .ifPresent(group -> event.setRespawnLocation(group.getLocationProperty(GroupProperty.SPAWN)));
    }
}
