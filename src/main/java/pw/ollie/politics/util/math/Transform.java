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

public class Transform implements Serializable {
    private static final long serialVersionUID = 2L;

    private final Position point;
    private final Vector2f rotation;

    public Transform(Position point, Vector2f rotation) {
        this.point = point;
        this.rotation = rotation;
    }

    public Position getPoint() {
        return point;
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
        Transform transform = (Transform) o;
        return Objects.equals(point, transform.point) &&
                Objects.equals(rotation, transform.rotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, rotation);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("point", point)
                .add("rotation", rotation)
                .toString();
    }
}
