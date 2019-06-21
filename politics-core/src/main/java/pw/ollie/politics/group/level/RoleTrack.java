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
import pw.ollie.politics.util.FunctionalUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoleTrack implements Iterable<Role> {
    // todo docs
    private final String id;
    private final List<Role> roles;

    RoleTrack(String id, List<Role> roles) {
        this.id = id;
        this.roles = roles;
    }

    public Stream<Role> streamRoles() {
        return roles.stream();
    }

    public String getId() {
        return id;
    }

    public Optional<Role> getPreviousRole(Role role) {
        int index = roles.indexOf(role);
        if (index < 0 || index >= roles.size()) {
            return Optional.empty();
        }
        return Optional.of(roles.get(index - 1));
    }

    public Optional<Role> getNextRole(Role role) {
        int index = roles.indexOf(role);
        if (index < 0 || index + 2 > roles.size()) {
            return Optional.empty();
        }
        return Optional.of(roles.get(index + 1));
    }

    @Override
    public Iterator<Role> iterator() {
        return roles.listIterator();
    }

    public static RoleTrack load(String id, List<String> rolesNames, Map<String, Role> roles) {
        if (rolesNames.stream().anyMatch(FunctionalUtil.negate(roles::containsKey))) {
            Politics.getLogger().log(Level.SEVERE, "RoleTrack '" + id + "' has non-existent role! The track will not be loaded.");
            return null;
        }

        return new RoleTrack(id, rolesNames.stream()
                .map(roles::get).filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedList::new)));
    }
}
