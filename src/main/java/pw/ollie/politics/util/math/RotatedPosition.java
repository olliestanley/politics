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

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.Objects;

public class RotatedPosition implements Serializable {
    private static final long serialVersionUID = 2L;

    private final Position position;
    private final Vector2f rotation;

    public RotatedPosition(Position position, Vector2f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Position getPosition() {
        return position;
    }

    public Vector2f getRotation() {
        return rotation;
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
