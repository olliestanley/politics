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
package pw.ollie.politics.util.collect;

import gnu.trove.map.hash.THashMap;

import java.util.Map;

public class MapBuilder {
    private String[] keys;
    private String[] vals;

    private MapBuilder() {
    }

    public MapBuilder keys(String... keys) {
        this.keys = keys;
        return this;
    }

    public MapBuilder vals(String... vals) {
        this.vals = vals;
        return this;
    }

    public int size() {
        return keys == null ? 0 : keys.length;
    }

    public Map<String, String> build() {
        return build(new THashMap<>(size()));
    }

    public Map<String, String> build(Map<String, String> map) {
        if (keys == null || vals == null) {
            return map;
        }

        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], vals.length > i ? vals[i] : null);
        }

        return map;
    }

    public static MapBuilder builder() {
        return new MapBuilder();
    }

    public static MapBuilder of(String k, String v) {
        return builder().keys(k).vals(v);
    }

    public static Map<String, String> of(Map<String, String> map, String k, String v) {
        map.put(k, v);
        return map;
    }

    public static Map<String, String> map(String k, String v) {
        return of(new THashMap<>(1), k, v);
    }
}
