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

import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.universe.Universe;

import java.util.Set;
import java.util.UUID;

public final class Citizen {
    private final UUID id;
    private final Universe universe;

    private String name;

    public Citizen(UUID id, String name, Universe universe) {
        this.id = id;
        this.name = name;
        this.universe = universe;
    }

    public UUID getUniqueId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Group> getGroups() {
        return universe.getCitizenGroups(id);
    }

    public Group getGroup(GroupLevel level) {
        for (Group group : getGroups()) {
            if (group.getLevel().equals(level)) {
                return group;
            }
        }
        return null;
    }

    public Universe getUniverse() {
        return universe;
    }

    public void invalidateGroups() {
        universe.invalidateCitizenGroups(id);
    }
}
