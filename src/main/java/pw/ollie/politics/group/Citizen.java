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
package pw.ollie.politics.group;

import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.universe.Universe;

import java.util.Set;

public final class Citizen {
    private final String name;
    private final Universe universe;

    public Citizen(String name, Universe universe) {
        this.name = name;
        this.universe = universe;
    }

    public String getName() {
        return name;
    }

    public Set<Group> getGroups() {
        return universe.getCitizenGroups(name);
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
        universe.invalidateCitizenGroups(name);
    }
}
