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
package pw.ollie.politics.war;

import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.event.war.WarBeginEvent;
import pw.ollie.politics.event.war.WarFinishEvent;
import pw.ollie.politics.group.Group;

import java.util.Set;
import java.util.stream.Collectors;

public final class WarManager {
    private final PoliticsPlugin plugin;
    private final Set<War> activeWars;

    public WarManager(PoliticsPlugin plugin) {
        if (!plugin.getPoliticsConfig().areWarsEnabled()) {
            throw new IllegalStateException("attempt to create war manager when wars are not enabled");
        }

        this.plugin = plugin;
        this.activeWars = new THashSet<>();
    }

    public War getWarBetween(int one, int two) {
        return activeWars.stream()
                .filter(war -> war.involves(one) && war.involves(two))
                .findAny().orElse(null);
    }

    public War getWarBetween(Group group1, Group group2) {
        return getWarBetween(group1.getUid(), group2.getUid());
    }

    public Set<War> getInvolvedWars(int groupId) {
        return activeWars.stream()
                .filter(war -> war.involves(groupId))
                .collect(Collectors.toSet());
    }

    public Set<War> getInvolvedWars(Group group) {
        return getInvolvedWars(group.getUid());
    }

    public boolean beginWar(War war) {
        if (activeWars.contains(war) || getWarBetween(war.getAggressorId(), war.getDefenderId()) != null) {
            return false;
        }

        WarBeginEvent event = PoliticsEventFactory.callWarBeginEvent(war);
        if (event.isCancelled()) {
            return false;
        }

        activeWars.add(war);
        return true;
    }

    public boolean finishWar(War war) {
        if (!activeWars.contains(war)) {
            return false;
        }

        WarFinishEvent event = PoliticsEventFactory.callWarFinishEvent(war);
        if (event.isCancelled()) {
            return false;
        }

        activeWars.remove(war);
        return true;
    }

    public void loadWars() {
        // todo
    }

    public void saveWars() {
        // todo
    }
}
