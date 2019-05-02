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

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * Stores both a position (x, y, z) and a rotation (yaw, pitch).
 */
public class RotatedPosition {
    private final Position position;
    private final Vector2f rotation;

    /**
     * Constructs a new RotatedPosition with given positional and rotational components.
     *
     * @param position the Position component of the RotatedPosition
     * @param rotation the rotation of the RotatedPosition
     */
    public RotatedPosition(Position position, Vector2f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Gets the positional component of this RotatedPosition.
     *
     * @return the Position of this object
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the rotational component of this RotatedPosition.
     *
     * @return the rotation of this object
     */
    public Vector2f getRotation() {
        return rotation;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(position.getWorld()), position.getX(), position.getY(), position.getZ(), rotation.getX(), rotation.getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RotatedPosition rotatedPosition = (RotatedPosition) o;
        return Objects.equals(position, rotatedPosition.position) &&
                Objects.equals(rotation, rotatedPosition.rotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, rotation);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("position", position)
                .add("rotation", rotation)
                .toString();
    }
}
