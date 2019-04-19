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
package pw.ollie.politics.event.activity;

import pw.ollie.politics.activity.PoliticsActivity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.UUID;

public abstract class ActivityEvent extends Event {
    private final PoliticsActivity activity;

    protected ActivityEvent(PoliticsActivity activity) {
        this.activity = activity;
    }

    public UUID getPlayerId() {
        return getActivity().getPlayerId();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getPlayerId());
    }

    public PoliticsActivity getActivity() {
        return activity;
    }
}
