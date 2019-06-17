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

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

/**
 * Represents a cuboid space in a Minecraft world.
 */
public class Cuboid {
    private final Location base;
    private final Vector3i size;

    private int hash = 0;

    /**
     * Constructs a new Cuboid, using the given {@link Location} as the base (minimum) point, and the {@link Vector3i}
     * as the size of the Cuboid extending from the base point.
     *
     * @param base the base Location of the Cuboid
     * @param size the size of the Cuboid in each dimension
     */
    public Cuboid(Location base, Vector3i size) {
        this.base = base;
        this.size = size;
    }

    /**
     * Constructs a new Cuboid, using the given {@link Position} as the base (minimum) point, and the {@link Vector3i}
     * as the size of the Cuboid extending from the base point.
     *
     * @param base the base Position of the Cuboid
     * @param size the size of the Cuboid in each dimension
     */
    public Cuboid(Position base, Vector3i size) {
        this(base.toLocation(), size);
    }

    /**
     * Constructs a new Cuboid, with the given {@link Location} objects as opposite corners of the Cuboid.
     *
     * @param base     one corner of the Cuboid
     * @param opposite the opposite corner of the Cuboid
     */
    public Cuboid(Location base, Location opposite) {
        if (!Objects.equals(base.getWorld(), opposite.getWorld())) {
            throw new IllegalArgumentException("cuboid cannot span multiple worlds");
        }

        this.base = new Location(base.getWorld(), Math.min(base.getBlockX(), opposite.getBlockX()), Math.min(base.getBlockY(), opposite.getBlockY()), Math.min(base.getBlockZ(), opposite.getBlockZ()));
        size = new Vector3i(Math.abs(opposite.getBlockX() - base.getBlockX()), Math.abs(opposite.getBlockY() - base.getBlockY()), Math.abs(opposite.getBlockZ() - base.getBlockZ()));
    }

    /**
     * Constructs a new Cuboid, with the given {@link Position} objects as opposite corners of the Cuboid.
     *
     * @param base     one corner of the Cuboid
     * @param opposite the opposite corner of the Cuboid
     */
    public Cuboid(Position base, Position opposite) {
        this(base.toLocation(), opposite.toLocation());
    }

    /**
     * Gets the minimum point of this Cuboid.
     *
     * @return the Cuboid's minimum point
     */
    public Location getMinPoint() {
        return base;
    }

    /**
     * Gets the maximum point of this Cuboid.
     *
     * @return the Cuboid's maximum point
     */
    public Location getMaxPoint() {
        return GeometryUtil.toLocation(base.getWorld(), GeometryUtil.toVector3i(base).add(size));
    }

    /**
     * Gets the {@link World} in which this Cuboid is located.
     *
     * @return the Cuboid's World
     */
    public World getWorld() {
        return base.getWorld();
    }

    /**
     * Gets the block x coordinate of the minimum point of this Cuboid.
     *
     * @return the Cuboid's minimum x coordinate
     */
    public int getMinX() {
        return getMinPoint().getBlockX();
    }

    /**
     * Gets the block y coordinate of the minimum point of this Cuboid.
     *
     * @return the Cuboid's minimum y coordinate
     */
    public int getMinY() {
        return getMinPoint().getBlockY();
    }

    /**
     * Gets the block z coordinate of the minimum point of this Cuboid.
     *
     * @return the Cuboid's minimum z coordinate
     */
    public int getMinZ() {
        return getMinPoint().getBlockZ();
    }

    /**
     * Gets the block x coordinate of the maximum point of this Cuboid.
     *
     * @return the Cuboid's maximum x coordinate
     */
    public int getMaxX() {
        return getMaxPoint().getBlockX();
    }

    /**
     * Gets the block y coordinate of the maximum point of this Cuboid.
     *
     * @return the Cuboid's maximum y coordinate
     */
    public int getMaxY() {
        return getMaxPoint().getBlockY();
    }

    /**
     * Gets the block z coordinate of the maximum point of this Cuboid.
     *
     * @return the Cuboid's maximum z coordinate
     */
    public int getMaxZ() {
        return getMaxPoint().getBlockZ();
    }

    /**
     * Gets the {@link Vector3i} representing the size of this Cuboid from the base point in each of the x, y and z
     * directions.
     *
     * @return the Vector size of this Cuboid
     */
    public Vector3i getSize() {
        return size;
    }

    /**
     * Gets the size of this Cuboid from the base point in the x direction.
     *
     * @return the Cuboid's x direction size
     */
    public int getXSize() {
        return size.getX();
    }

    /**
     * Gets the size of this Cuboid from the base point in the y direction.
     *
     * @return the Cuboid's y direction size
     */
    public int getYSize() {
        return size.getY();
    }

    /**
     * Gets the size of this Cuboid from the base point in the z direction.
     *
     * @return the Cuboid's z direction size
     */
    public int getZSize() {
        return size.getZ();
    }

    /**
     * Checks whether the Cuboid contains the given point.
     *
     * @param vec the point to check whether the Cuboid contains
     * @return whether the given point is contained within this Cuboid
     */
    public boolean contains(Vector3i vec) {
        Vector3i max = GeometryUtil.toVector3i(base).add(size);
        return base.getX() <= vec.getX() && vec.getX() <= max.getX()
                && base.getY() <= vec.getY() && vec.getY() <= max.getY()
                && base.getZ() <= vec.getZ() && vec.getZ() <= max.getZ();
    }

    /**
     * Checks whether the Cuboid contains the given point.
     *
     * @param position the point to check whether the Cuboid contains
     * @return whether the given point is contained within this Cuboid
     */
    public boolean contains(Position position) {
        return contains(position.toLocation());
    }

    /**
     * Checks whether the Cuboid contains the given point.
     *
     * @param location the point to check whether the Cuboid contains
     * @return whether the given point is contained within this Cuboid
     */
    public boolean contains(Location location) {
        if (!Objects.equals(location.getWorld(), base.getWorld())) {
            return false;
        }

        Vector3i max = GeometryUtil.toVector3i(base).add(size);
        return base.getX() <= location.getBlockX() && location.getBlockX() < max.getX()
                && base.getY() <= location.getBlockY() && location.getBlockY() < max.getY()
                && base.getZ() <= location.getBlockZ() && location.getBlockZ() < max.getZ();
    }

    /**
     * Checks whether the given Cuboid}is <b>entirely</b> contained within this Cuboid. For a check for whether another
     * Cuboid intersects this Cuboid, see {@link #intersects(Cuboid)}.
     *
     * @param o the other Cuboid to check
     * @return whether the given Cuboid is wholly contained within this Cuboid
     */
    public boolean contains(Cuboid o) {
        return o.getMaxX() <= getMaxX() && o.getMinX() >= getMinX()
                && o.getMaxY() <= getMaxY() && o.getMinY() >= getMinY()
                && o.getMaxZ() <= getMaxZ() && o.getMinZ() >= getMinZ();
    }

    /**
     * Checks whether there is any point at which the given Cuboid intersects this Cuboid.
     *
     * @param o the other Cuboid to check
     * @return whether the given Cuboid and this Cuboid intersect at any point
     */
    public boolean intersects(Cuboid o) {
        return !notIntersects(o);
    }

    private boolean notIntersects(Cuboid o) {
        // compares z before y for a micro performance gain - in minecraft many cuboids will be similar on the y axis
        return o.getMinX() > getMaxX() || o.getMaxX() < getMinX()
                || o.getMinZ() > getMaxZ() || o.getMaxZ() < getMinZ()
                || o.getMinY() > getMaxY() || o.getMaxY() < getMinY();
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = Objects.hash(base, size);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Cuboid)) {
            return false;
        }

        Cuboid cuboid = (Cuboid) obj;
        return cuboid.size.getX() == size.getX() && cuboid.size.getY() == size.getY() && cuboid.size.getZ() == size.getZ() && cuboid.getWorld().equals(getWorld()) && cuboid.base.getX() == base.getX() && cuboid.base.getY() == base.getY() && cuboid.base.getZ() == base.getZ();
    }

    /**
     * Creates a new Cuboid comprised of the physical region of the given {@link Chunk}.
     *
     * @param chunk the Chunk to create the Cuboid from
     * @return a Cuboid with the same boundaries as the given Chunk
     */
    public static Cuboid fromChunk(Chunk chunk) {
        Location min = new Location(chunk.getWorld(), chunk.getX() * 16, 0, chunk.getZ() * 16);
        Location max = new Location(chunk.getWorld(), (chunk.getX() * 16) + 15, 255, (chunk.getZ() * 16) + 15);
        return new Cuboid(min, max);
    }
}
