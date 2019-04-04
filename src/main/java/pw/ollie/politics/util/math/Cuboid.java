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
import org.bukkit.World;

import java.util.Objects;

public final class Cuboid {
    private final Location base;
    private final Vector3f size;

    private final int x;
    private final int y;
    private final int z;

    private int hash = 0;

    public Cuboid(Location base, Vector3f size) {
        this.base = base;
        this.size = size;

        this.x = (int) (base.getX() / size.getX());
        this.y = (int) (base.getY() / size.getY());
        this.z = (int) (base.getZ() / size.getZ());
    }

    public Location getBase() {
        return this.base;
    }

    public Vector3f getSize() {
        return this.size;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public boolean contains(Vector3f vec) {
        Vector3f max = MathUtil.add(base, size);
        return base.getX() <= vec.getX() && vec.getX() < max.getX()
                && base.getY() <= vec.getY() && vec.getY() < max.getY()
                && base.getZ() <= vec.getZ() && vec.getZ() < max.getZ();
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
        return cuboid.size.getX() == size.getX() && cuboid.size.getY() == size.getY() && cuboid.size.getZ() == size.getZ() && cuboid.getWorld().equals(getWorld()) && cuboid.getX() == getX() && cuboid.getY() == getY() && cuboid.getZ() == getZ();
    }

}
