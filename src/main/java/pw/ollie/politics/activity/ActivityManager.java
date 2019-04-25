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
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.event.activity.ActivityBeginEvent;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Manages players' ongoing activities.
 */
public final class ActivityManager {
    // todo docs
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

    public boolean beginActivity(PoliticsActivity activity) {
        if (isActive(activity.getPlayerId())) {
            return false;
        }
        ActivityBeginEvent event = PoliticsEventFactory.callActivityBeginEvent(activity);
        if (event.isCancelled()) {
            return false;
        }
        activities.put(activity.getPlayerId(), activity);
        return true;
    }

    public boolean endActivity(UUID playerId) {
        PoliticsActivity activity = activities.get(playerId);
        if (activity == null) {
            return false;
        }
        PoliticsEventFactory.callActivityEndEvent(activity);
        activities.remove(playerId);
        return true;
    }

    public boolean endActivity(Player player) {
        return endActivity(player.getUniqueId());
    }
}
