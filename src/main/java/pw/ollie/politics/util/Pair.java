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
package pw.ollie.politics.util;

/**
 * Immutable pair of objects.
 *
 * @param <K> the type of the first object
 * @param <V> the type of the second object
 */
public final class Pair<K, V> {
    private final K k;
    private final V v;

    /**
     * Constructs a new Pair.
     *
     * @param k the first item
     * @param v the second item
     */
    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    /**
     * Gets the first item of the pair.
     *
     * @return the first item
     */
    public K getFirst() {
        return k;
    }

    /**
     * Gets the second item of the pair.
     *
     * @return the second item
     */
    public V getSecond() {
        return v;
    }
}
