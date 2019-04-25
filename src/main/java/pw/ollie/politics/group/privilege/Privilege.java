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
package pw.ollie.politics.group.privilege;

import pw.ollie.politics.util.collect.CollectionUtil;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    public Set<PrivilegeType> getTypes() {
        return EnumSet.copyOf(types);
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

    public static Set<Privilege> all(Set<Privilege>... sets) {
        Set<Privilege> result = new HashSet<>();
        for (Set<Privilege> set : sets) {
            result.addAll(set);
        }

        return result;
    }

    public static Set<Privilege> common(Set<Privilege>... sets) {
        boolean first = true;
        Set<Privilege> result = new HashSet<>();
        for (Set<Privilege> set : sets) {
            if (first) {
                result.addAll(set);
                first = false;
                continue;
            }

            Set<Privilege> copy = new HashSet<>(result);
            for (Privilege privilege : copy) {
                if (!set.contains(privilege)) {
                    result.remove(privilege);
                }
            }
        }

        return result;
    }

    public static Set<Privilege> filter(Set<Privilege> set, PrivilegeType... types) {
        Set<Privilege> result = new HashSet<>();
        Set<PrivilegeType> typesSet = EnumSet.of(types[0], types);
        for (Privilege priv : set) {
            if (priv.getTypes().containsAll(typesSet)) {
                result.add(priv);
            }
        }

        return result;
    }
}
