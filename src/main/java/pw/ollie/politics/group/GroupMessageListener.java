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
import pw.ollie.politics.event.player.PlayerPlotChangeEvent;
import pw.ollie.politics.util.StringUtil;
import pw.ollie.politics.util.collect.stream.StreamUtil;
import pw.ollie.politics.util.message.MessageBuilder;
import pw.ollie.politics.util.message.MessageUtil;

import com.google.mu.util.stream.BiStream;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listens to events to send relevant {@link Group}-related informational messages to players.
 */
final class GroupMessageListener implements Listener {
    private final PoliticsPlugin plugin;

    GroupMessageListener(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void sendJoinMessage(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        StreamUtil.biStream(plugin.getGroupManager().getAllCitizenGroups(player.getUniqueId()).stream(), group -> group.getStringProperty(GroupProperty.MOTD, ""))
                .filterValues(StringUtil::notEmpty)
                .mapValues((group, motd) -> MessageBuilder.beginHighlight(group.getName() + " MOTD: ").normal(motd))
                .forEach((group, builder) -> builder.send(player));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void sendTerritoryMessage(PlayerPlotChangeEvent event) {
        Player player = event.getPlayer();
        Group newPlotOwner = event.getTo().getOwner();
        Group oldPlotOwner = event.getFrom().getOwner();

        if (newPlotOwner == null) {
            if (oldPlotOwner != null) {
                String exitMessage = oldPlotOwner.getStringProperty(GroupProperty.EXIT_MESSAGE);
                if (exitMessage != null) {
                    MessageUtil.message(player, exitMessage);
                }

                MessageUtil.message(player, oldPlotOwner.getUniverse().getRules().getWildernessMessage());
            }
            return;
        }

        if (!newPlotOwner.equals(oldPlotOwner)) {
            String entryMessage = newPlotOwner.getStringProperty(GroupProperty.ENTRY_MESSAGE);
            if (entryMessage != null) {
                MessageUtil.message(player, entryMessage);
            }

            if (oldPlotOwner != null) {
                String exitMessage = oldPlotOwner.getStringProperty(GroupProperty.EXIT_MESSAGE);
                if (exitMessage != null) {
                    MessageUtil.message(player, exitMessage);
                }
            }
        }
    }
}
