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

import gnu.trove.map.hash.THashMap;

import pw.ollie.politics.PoliticsPlugin;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Manages players' ongoing activities.
 */
public final class ActivityManager {
    private final PoliticsPlugin plugin;
    private final Map<UUID, PoliticsActivity> activities;

    public ActivityManager(PoliticsPlugin plugin) {
        this.plugin = plugin;
        this.activities = new THashMap<>();

        plugin.getServer().getPluginManager().registerEvents(new ActivityUpdateListener(plugin), plugin);
    }

    public boolean isActive(UUID playerId) {
        PoliticsActivity activity = getActivity(playerId);
        if (activity != null && activity.hasCompleted()) {
            activities.remove(playerId);
            activity = null;
        }
        return activity == null;
    }

    public boolean isActive(Player player) {
        return isActive(player.getUniqueId());
    }

    public PoliticsActivity getActivity(UUID playerId) {
        PoliticsActivity activity = getActivity(playerId);
        if (activity != null && activity.hasCompleted()) {
            activities.remove(playerId);
            activity = null;
        }
        return activity;
    }

    public PoliticsActivity getActivity(Player player) {
        return getActivity(player.getUniqueId());
    }

    public boolean beginActivity(UUID playerId, PoliticsActivity activity) {
        if (isActive(playerId)) {
            return false;
        }
        activities.put(playerId, activity);
        return true;
    }

    public boolean beginActivity(Player player, PoliticsActivity activity) {
        return beginActivity(player.getUniqueId(), activity);
    }

    public boolean endActivity(UUID playerId) {
        return activities.remove(playerId) != null;
    }

    public boolean endActivity(Player player) {
        return endActivity(player.getUniqueId());
    }
}
