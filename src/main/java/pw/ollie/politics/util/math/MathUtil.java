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
package pw.ollie.politics.util.math;

import org.bukkit.Location;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilities for mathematical methods in Politics.
 */
public final class MathUtil {
    public static Vector3i add(Location one, Vector3i two) {
        return new Vector3i(one.getBlockX() + two.getX(), one.getBlockY() + two.getY(), one.getBlockZ() + two.getZ());
    }

    public static Location add(Vector3i one, Location two) {
        return new Location(two.getWorld(), two.getBlockX() + one.getX(), two.getBlockY() + one.getY(), two.getBlockZ() + one.getZ());
    }

    public static int randomInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    private MathUtil() {
        throw new UnsupportedOperationException();
    }
}
