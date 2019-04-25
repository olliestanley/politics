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

import pw.ollie.politics.util.Position;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

/**
 * Represents a cuboid space in a Minecraft world.
 */
public class Cuboid {
    // todo docs
    private final Location base;
    private final Vector3i size;

    private int hash = 0;

    public Cuboid(Location base, Vector3i size) {
        this.base = base;
        this.size = size;
    }

    public Cuboid(Position base, Vector3i size) {
        this(base.toLocation(), size);
    }

    public Cuboid(Location base, Location opposite) {
        if (!base.getWorld().equals(opposite.getWorld())) {
            throw new IllegalArgumentException("cuboid cannot span multiple worlds");
        }

        this.base = new Location(base.getWorld(), Math.min(base.getBlockX(), opposite.getBlockX()), Math.min(base.getBlockY(), opposite.getBlockY()), Math.min(base.getBlockZ(), opposite.getBlockZ()));
        size = new Vector3i(Math.abs(opposite.getBlockX() - base.getBlockX()), Math.abs(opposite.getBlockY() - base.getBlockY()), Math.abs(opposite.getBlockZ() - base.getBlockZ()));
    }

    public Cuboid(Position base, Position opposite) {
        this(base.toLocation(), opposite.toLocation());
    }

    public Location getBase() {
        return this.base;
    }

    public Location getMinPoint() {
        return base;
    }

    public Location getMaxPoint() {
        return MathUtil.add(size, base);
    }

    public int getMinX() {
        return getMinPoint().getBlockX();
    }

    public int getMinY() {
        return getMinPoint().getBlockY();
    }

    public int getMinZ() {
        return getMinPoint().getBlockZ();
    }

    public int getMaxX() {
        return getMaxPoint().getBlockX();
    }

    public int getMaxY() {
        return getMaxPoint().getBlockY();
    }

    public int getMaxZ() {
        return getMaxPoint().getBlockZ();
    }

    public Vector3i getSize() {
        return this.size;
    }

    public int getXSize() {
        return size.getX();
    }

    public int getYSize() {
        return size.getY();
    }

    public int getZSize() {
        return size.getZ();
    }

    public boolean contains(Vector3i vec) {
        Vector3i max = MathUtil.add(base, size);
        return base.getX() <= vec.getX() && vec.getX() < max.getX()
                && base.getY() <= vec.getY() && vec.getY() < max.getY()
                && base.getZ() <= vec.getZ() && vec.getZ() < max.getZ();
    }

    public boolean contains(Position position) {
        return contains(position.toLocation());
    }

    public boolean contains(Location location) {
        if (!Objects.equals(location.getWorld(), base.getWorld())) {
            return false;
        }

        Vector3i max = MathUtil.add(base, size);
        return base.getX() <= location.getBlockX() && location.getBlockX() < max.getX()
                && base.getY() <= location.getBlockY() && location.getBlockY() < max.getY()
                && base.getZ() <= location.getBlockZ() && location.getBlockZ() < max.getZ();
    }

    public boolean intersects(Cuboid o) {
        return !notIntersects(o);
    }

    private boolean notIntersects(Cuboid o) {
        // compares z before y for a micro performance gain - in minecraft many cuboids will be similar on the y axis
        return o.getMinX() > getMaxX() || o.getMaxX() < getMinX()
                || o.getMinZ() > getMaxZ() || o.getMaxZ() < getMinZ()
                || o.getMinY() > getMaxY() || o.getMaxY() < getMinY();
    }

    public World getWorld() {
        return this.base.getWorld();
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

    public static Cuboid fromChunk(Chunk chunk) {
        Location min = new Location(chunk.getWorld(), chunk.getX() * 16, 0, chunk.getZ() * 16);
        Location max = new Location(chunk.getWorld(), (chunk.getX() * 16) + 15, 255, (chunk.getZ() * 16) + 15);
        return new Cuboid(min, max);
    }
}
