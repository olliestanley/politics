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
package pw.ollie.politics.group.level;

import pw.ollie.politics.Politics;
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.util.StringUtil;
import pw.ollie.politics.util.stream.CollectorUtil;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Represents a role held by a player within a Group. Holding a Role affords the holder the {@link Privilege}s which are
 * designated to the Role.
 */
public final class Role implements Comparable<Role> {
    // todo docs
    private final String id;
    private final String name;
    private final Set<Privilege> privileges;
    private final int rank;

    private Role(String id, String name, Set<Privilege> privileges, int rank) {
        this.id = id;
        this.name = name;
        this.privileges = privileges;
        this.rank = rank;
    }

    public Stream<Privilege> streamPrivileges() {
        return privileges.stream();
    }

    public String getId() {
        return id;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public boolean can(Privilege privilege) {
        return privileges.contains(privilege);
    }

    public static Role load(String id, ConfigurationSection node) {
        return new Role(id, node.getString("name", StringUtil.capitaliseFirst(id)),
                node.getStringList("privileges").stream().map(Politics.getPrivilegeManager()::getPrivilege)
                        .filter(Objects::nonNull).collect(CollectorUtil.toTHashSet()),
                node.getInt("rank", 1));
    }

    @Override
    public int compareTo(Role other) {
        if (rank == other.getRank()) {
            return id.compareToIgnoreCase(other.getId());
        }
        return rank - other.getRank();
    }
}
