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
package net.oliverstanley.politics.util.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Utilities relating to {@link Collection}s used by Politics.
 */
public final class CollectionUtil {
    /**
     * Checks whether the contents of the two given {@link Collection}s are identical.
     *
     * @param collection  the first Collection
     * @param collection2 the second Collection
     * @param <T>         the type of the Collections
     * @return whether the two Collections have identical contents
     */
    public static <T> boolean contentsEqual(Collection<T> collection, Collection<T> collection2) {
        if (collection.size() != collection2.size()) {
            return false;
        }

        return collection2.containsAll(collection); // inverse check not needed due to size comparison
    }

    public static <E extends Comparable<E>> List<E> sorted(List<E> collection) {
        Collections.sort(collection);
        return collection;
    }

    private CollectionUtil() {
        throw new UnsupportedOperationException();
    }

}
