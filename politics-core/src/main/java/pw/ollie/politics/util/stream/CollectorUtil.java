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
package pw.ollie.politics.util.stream;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.util.collect.PagedArrayList;

import com.google.mu.util.stream.BiCollector;

import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;

/**
 * Additional {@link Collector}s on top of those provided by {@link Collectors}. Effectively shortcuts for calling
 * {@link Collectors#toCollection(java.util.function.Supplier)} using an appropriate Supplier for the collection type in
 * question.
 */
public final class CollectorUtil {
    public static <T> Collector<T, ?, THashSet<T>> toTHashSet() {
        return Collectors.toCollection(THashSet::new);
    }

    public static <T> Collector<T, ?, PagedArrayList<T>> toPagedList() {
        return Collectors.toCollection(PagedArrayList::new);
    }

    public static <T> Collector<? super T, ?, BasicBSONList> toBSONList() {
        return Collectors.toCollection(BasicBSONList::new);
    }

    public static BiCollector<String, Object, BasicBSONObject> toBasicBSONObject() {
        return CollectorUtil::toBasicBSONObject;
    }

    public static <V> BiCollector<Integer, V, TIntObjectHashMap<V>> toIntObjectHashMap() {
        return CollectorUtil::toIntObjectHashMap;
    }

    private static <T, V> Collector<T, ?, TIntObjectHashMap<V>> toIntObjectHashMap(Function<? super T, Integer> toKey,
                                                                                   Function<? super T, ? extends V> toVal) {
        return Collector.of(TIntObjectHashMap::new,
                (m, input) -> m.put(toKey.apply(input), toVal.apply(input)),
                (m1, m2) -> {
                    TIntObjectHashMap<V> result = new TIntObjectHashMap<>(m1.size() + m2.size());
                    result.putAll(m1);
                    result.putAll(m2);
                    return result;
                }, Characteristics.IDENTITY_FINISH);
    }

    private static <T> Collector<T, Object, BasicBSONObject> toBasicBSONObject(Function<? super T, String> toKey,
                                                                               Function<? super T, ?> toVal) {
        return Collectors.collectingAndThen(Collectors.toMap(toKey, toVal), m -> {
            BasicBSONObject result = new BasicBSONObject();
            result.putAll(m);
            return result;
        });
    }

    private CollectorUtil() {
        throw new UnsupportedOperationException();
    }
}
