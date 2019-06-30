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
package pw.ollie.politics.util.message;

import gnu.trove.map.hash.THashMap;

import java.util.Map;

public class VarMapFiller {
    private String[] vars;
    private String[] vals;

    private VarMapFiller() {
    }

    public VarMapFiller vars(String... keys) {
        this.vars = keys;
        return this;
    }

    public VarMapFiller vals(String... vals) {
        this.vals = vals;
        return this;
    }

    public int size() {
        return Math.min(vars == null ? 0 : vars.length, vals == null ? 0 : vals.length);
    }

    public Map<String, String> fill() {
        return fill(new THashMap<>(size()));
    }

    public Map<String, String> fill(Map<String, String> map) {
        if (vars == null || vals == null) {
            return map;
        }

        for (int i = 0; i < size(); i++) {
            map.put(vars[i], vals[i]);
        }

        return map;
    }

    public static VarMapFiller filler() {
        return new VarMapFiller();
    }

    public static VarMapFiller of(String k, String v) {
        return filler().vars(k).vals(v);
    }

    public static Map<String, String> fill(Map<String, String> map, String k, String v) {
        map.put(k, v);
        return map;
    }

    public static Map<String, String> map(String k, String v) {
        return fill(new THashMap<>(1), k, v);
    }
}
