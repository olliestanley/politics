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
package pw.ollie.politics.group.war;

import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.event.war.WarBeginEvent;
import pw.ollie.politics.event.war.WarFinishEvent;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.universe.Universe;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stores data on and provides access to {@link War}s in Politics.
 */
public final class WarManager {
    // todo docs
    private final PoliticsPlugin plugin;
    private final Set<War> activeWars;

    public WarManager(PoliticsPlugin plugin) {
        if (!plugin.getPoliticsConfig().areWarsEnabled()) {
            throw new IllegalStateException("attempt to create war manager when wars are not enabled");
        }

        this.plugin = plugin;
        this.activeWars = new THashSet<>();
    }

    public Set<War> getActiveWars() {
        return new THashSet<>(activeWars);
    }

    public Set<War> getActiveWars(Universe universe) {
        return activeWars.stream()
                .filter(war -> war.getUniverse().equals(universe))
                .collect(Collectors.toSet());
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
        return beginWar(war, Bukkit.getConsoleSender());
    }

    public boolean beginWar(War war, CommandSender source) {
        if (war.isActive() || activeWars.contains(war) || getWarBetween(war.getAggressorId(), war.getDefenderId()) != null) {
            return false;
        }

        Group defender = war.getDefender();
        Group aggressor = war.getAggressor();
        if (defender == null || aggressor == null) {
            throw new IllegalArgumentException("cannot start war with one or more null participants");
        }
        if (!aggressor.getLevel().equals(defender.getLevel())) {
            throw new IllegalArgumentException("cannot start war between groups of different levels");
        }

        if (!aggressor.getLevel().canWar()) {
            return false;
        }
        if (aggressor.getBooleanProperty(GroupProperty.PEACEFUL)
                || defender.getBooleanProperty(GroupProperty.PEACEFUL)) {
            return false;
        }

        WarBeginEvent event = PoliticsEventFactory.callWarBeginEvent(war, source);
        if (event.isCancelled()) {
            return false;
        }

        war.setActive(true);
        activeWars.add(war);
        return true;
    }

    public boolean finishWar(War war) {
        return finishWar(war, false);
    }

    public boolean finishWar(War war, boolean force) {
        if (!war.isActive() || !activeWars.contains(war)) {
            return false;
        }

        WarFinishEvent event = PoliticsEventFactory.callWarFinishEvent(war);
        if (!force && event.isCancelled()) {
            return false;
        }

        // update victory and defeat statistics for involved groups
        Group winner = war.getWinningGroup();
        winner.setProperty(GroupProperty.WAR_VICTORIES, winner.getIntProperty(GroupProperty.WAR_VICTORIES, 0) + 1);
        Group loser = war.getLosingGroup();
        loser.setProperty(GroupProperty.WAR_DEFEATS, winner.getIntProperty(GroupProperty.WAR_DEFEATS, 0) + 1);

        war.setActive(false);
        activeWars.remove(war);
        return true;
    }

    public void loadWars() {
        // todo save all wars to wars.pws file in data directory

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvents(new WarProtectionListener(plugin), plugin);
        pluginManager.registerEvents(new WarScoringListener(plugin), plugin);
    }

    public void saveWars() {
        // todo
    }
}
