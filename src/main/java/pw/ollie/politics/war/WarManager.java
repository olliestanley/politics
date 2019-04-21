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

import java.util.Set;

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

    public void loadWars() {
        // todo
    }

    public void saveWars() {
        // todo
    }
}