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

import com.google.mu.util.stream.BiStream;

import org.bson.types.BasicBSONList;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StreamUtil {
    // todo doc
    public static <K, V> BiStream<K, V> biStream(Stream<K> stream, Function<? super K, ? extends V> toValue) {
        return BiStream.biStream(stream, key -> key, toValue);
    }

    public static <K, V> BiStream<K, V> biStream(Collection<K> collection, Function<? super K, ? extends V> toValue) {
        return BiStream.biStream(collection.stream(), key -> key, toValue);
    }

    public static <T> BasicBSONList toBSONList(Stream<T> stream) {
        return stream.collect(Collectors.toCollection(BasicBSONList::new));
    }

    private StreamUtil() {
        throw new UnsupportedOperationException();
    }
}
