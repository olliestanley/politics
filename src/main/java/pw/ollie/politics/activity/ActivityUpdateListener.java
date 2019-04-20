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
package pw.ollie.politics.activity;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.activity.activities.CuboidSelectionActivity;
import pw.ollie.politics.util.Position;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class ActivityUpdateListener implements Listener {
    private final PoliticsPlugin plugin;

    public ActivityUpdateListener(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void updateCuboidSelectionActivity(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        PoliticsActivity activity = plugin.getActivityManager().getActivity(event.getPlayer());
        if (!(activity instanceof CuboidSelectionActivity)) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Location location = block.getLocation();
        Position position = Position.fromLocation(location);

        CuboidSelectionActivity selectionActivity = (CuboidSelectionActivity) activity;
        if (!selectionActivity.isFirstPointSet()) {
            selectionActivity.setFirstPoint(position);
            return;
        }

        if (!selectionActivity.isSecondPointSet()) {
            selectionActivity.setSecondPoint(position);
            plugin.getActivityManager().endActivity(event.getPlayer());
            return;
        }

        // should never get here
        throw new IllegalStateException("cuboid selection activity still active with both points set");
    }
}