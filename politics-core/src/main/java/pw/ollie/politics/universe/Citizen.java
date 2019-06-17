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
import pw.ollie.politics.util.stream.CollectorUtil;

import java.util.Set;
import java.util.UUID;

/**
 * A Citizen is a player's representation in a single Universe in Politics.
 */
public final class Citizen {
    private final UUID id;
    private final Universe universe;

    private final String name;

    Citizen(UUID id, String name, Universe universe) {
        this.id = id;
        this.name = name;
        this.universe = universe;
    }

    /**
     * Gets the unique identifier of the player represented by this {@link Citizen}.
     *
     * @return the player's unique id
     */
    public UUID getUniqueId() {
        return id;
    }

    /**
     * Gets the in-game name of the player represented by this {@link Citizen}/
     *
     * @return the player's in-game name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a {@link Set} of all {@link Group}s of the {@link Citizen} - that is, all Groups the player is part of in
     * the {@link Universe} this Citizen is active in. Groups the player is part of, but which are not present in the
     * particular universe, are not included.
     *
     * @return the player's Group's in this Citizen's particular Universe
     */
    public Set<Group> getGroups() {
        return universe.getCitizenGroups(id);
    }

    /**
     * Gets the {@link Set} of all {@link Group} of the specified {@link GroupLevel} the player is a member of, or an
     * empty Set if the player is not a member of a Group of that level in this Citizen's {@link Universe}.
     *
     * @param level the GroupLevel to get the player's Groups in this Citizen's Universe of
     * @return the player's Groups of the given level in this Citizen's particular Universe
     */
    public Set<Group> getGroups(GroupLevel level) {
        return getGroups().stream().filter(group -> group.getLevel().equals(level)).collect(CollectorUtil.toMutableSet());
    }

    /**
     * Gets the {@link Universe} this {@link Citizen} is active within.
     *
     * @return this Citizen's Universe
     */
    public Universe getUniverse() {
        return universe;
    }
}
