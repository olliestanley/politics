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

import com.google.common.base.MoreObjects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

/**
 * Represents a point in three dimensions in a specific world.
 */
public class Position {
    private final String world;
    private final float x, y, z;

    /**
     * Construct a new Position.
     *
     * @param world the name of the world the Position is within
     * @param x     x coordinate of the Position
     * @param y     y coordinate of the Position
     * @param z     z coordinate of the Position
     */
    public Position(String world, float x, float y, float z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the name of the world this Position is within.
     *
     * @return the name of the world of this Position
     */
    public String getWorld() {
        return world;
    }

    /**
     * Gets the x coordinate of this Position.
     *
     * @return the Position x coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Gets the y coordinate of this Position.
     *
     * @return the Position y coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Gets the z coordinate of this Position.
     *
     * @return the Position z coordinate
     */
    public float getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position)) {
            return false;
        }
        Position position = (Position) o;
        return Float.compare(position.x, x) == 0 &&
                Float.compare(position.y, y) == 0 &&
                Float.compare(position.z, z) == 0 &&
                Objects.equals(world, position.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("world", world)
                .add("x", x)
                .add("y", y)
                .add("z", z)
                .toString();
    }

    /**
     * Converts this Position to an equivalent {@link Location} object, if possible. If the world name specified for
     * this Position does not correspond to an existing {@link World}, returns null.
     *
     * @return equivalent Location to this Position, if one exists
     */
    public Location toLocation() {
        World world = Bukkit.getWorld(this.world);
        if (world == null) {
            return null;
        }

        return new Location(world, x, y, z);
    }

    /**
     * Converts the given {@link Location} to an equivalent Position object, if possible. If the {@link World} of the
     * given Location does not exist, returns null.
     *
     * @param location the Location to convert to equivalent Position
     * @return equivalent Position to the given Location, if one exists
     */
    public static Position fromLocation(Location location) {
        if (location.getWorld() == null) {
            return null;
        }
        return new Position(location.getWorld().getName(), (float) location.getX(), (float) location.getY(),
                (float) location.getZ());
    }
}
