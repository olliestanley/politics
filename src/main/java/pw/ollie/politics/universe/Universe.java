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
package pw.ollie.politics.universe;

import pw.ollie.politics.data.Storable;

import org.bson.BSONObject;

public final class Universe implements Storable {
    private String name;
    private UniverseRules rules;

    public Universe(String name, UniverseRules rules) {
        this.name = name;
        this.rules = rules;
    }

    public String getName() {
        return this.name;
    }

    public UniverseRules getRules() {
        return this.rules;
    }

    @Override
    public BSONObject toBSONObject() {
        return null;
    }

    @Override
    public boolean canStore() {
        return false;
    }

    public static Universe fromBSONObject(BSONObject bson) {
        return null;
    }
}
