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
package pw.ollie.politics.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * General utilities relating to {@link Player}s.
 */
public final class PlayerUtil {
    /**
     * Gets the offline player object for the given name, if one exists. If no player with that name has played on the
     * server, returns null.
     * <p>
     * This does not involve a blocking web request whereas the Bukkit getOfflinePlayer(name) method may.
     *
     * @param name the name of the player to get
     * @return the offline player for the given name, if any
     */
    public static OfflinePlayer getOfflinePlayer(String name) {
        Player online = Bukkit.getPlayer(name);
        if (online != null) {
            return online;
        }

        for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
            if (name.equals(offline.getName())) {
                return offline;
            }
        }

        return null;
    }

    private PlayerUtil() {
        throw new UnsupportedOperationException();
    }
}
