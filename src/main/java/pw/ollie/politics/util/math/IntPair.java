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

import java.util.Objects;

/**
 * Holds a pair of integers.
 */
public class IntPair {
    /**
     * Creates a new int pair of the given integers.
     *
     * @param x the first int
     * @param z the second int
     * @return an int pair of the given integers
     */
    public static IntPair of(int x, int z) {
        return new IntPair(x, z);
    }

    private final int x, z;

    private IntPair(int x, int z) {
        this.x = x;
        this.z = z;
    }

    /**
     * Gets the x integer, or the first integer.
     *
     * @return the first integer in the pair
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the z integer, or the second integer.
     *
     * @return the second integer in the pair
     */
    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IntPair) {
            IntPair oth = (IntPair) o;
            return oth.x == x && oth.z == z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}
