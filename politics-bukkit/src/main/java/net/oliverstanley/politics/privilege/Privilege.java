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
package net.oliverstanley.politics.privilege;

import net.oliverstanley.politics.util.collect.CollectionUtil;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Represents a privilege which allows the holder to perform certain actions.
 */
public final class Privilege {
    // todo docs
    private final String name;
    private final Set<PrivilegeType> types;

    public Privilege(String name, PrivilegeType... types) {
        name = name.replaceAll(" ", "_");

        if (name.matches(":")) {
            throw new IllegalStateException("Colons not allowed in privilege names!");
        }

        this.name = name.toUpperCase();
        this.types = EnumSet.of(types[0], types);
    }

    public String getName() {
        return name;
    }

    public Stream<PrivilegeType> types() {
        return types.stream();
    }

    public boolean isOfType(PrivilegeType type) {
        return types.contains(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Privilege privilege = (Privilege) o;
        return name.equals(privilege.name) && CollectionUtil.contentsEqual(types, privilege.types);
    }

    @Override
    public int hashCode() {
        int h = Objects.hash(name);
        for (PrivilegeType type : types) {
            h *= Objects.hash(type);
        }
        return h;
    }
}
