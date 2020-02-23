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
package net.oliverstanley.politics.util.math;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Utilities for geometrical methods in Politics.
 */
public final class GeometryUtil {
    public static Vector3i toVector3i(Location location) {
        return new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Location toLocation(World world, Vector3i vector) {
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

    private GeometryUtil() {
        throw new UnsupportedOperationException();
    }
}
