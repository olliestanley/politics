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
package pw.ollie.politics.economy;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.universe.Universe;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TaxationManager {
    // todo docs
    // run the collection update task once a minute
    private static final long TASK_PERIOD = 20 * 60;

    private final PoliticsPlugin plugin;
    private final Map<UUID, TObjectIntMap<Universe>> lastCollections;

    private TaxationCollectionTask collectionTask;

    public TaxationManager(PoliticsPlugin plugin) {
        this.plugin = plugin;
        this.lastCollections = new HashMap<>();
    }

    public int getLastCollection(UUID playerId, Universe universe) {
        return getLastCollections(playerId).get(universe);
    }

    TObjectIntMap<Universe> getLastCollections(UUID playerId) {
        lastCollections.putIfAbsent(playerId, new TObjectIntHashMap<>());
        return lastCollections.get(playerId);
    }

    void resetLastCollection(UUID playerId, Universe universe) {
        lastCollections.putIfAbsent(playerId, new TObjectIntHashMap<>());
        lastCollections.get(playerId).put(universe, 0);
    }

    void incrementLastCollection(UUID playerId, Universe universe) {
        lastCollections.putIfAbsent(playerId, new TObjectIntHashMap<>());
        lastCollections.get(playerId).put(universe, getLastCollection(playerId, universe) + 1);
    }

    public void loadTaxData() {
        // todo load from file

        this.collectionTask = new TaxationCollectionTask(plugin);
        this.collectionTask.runTaskTimer(plugin, TASK_PERIOD, TASK_PERIOD);
    }

    public void saveTaxData(boolean shutdown) {
        if (shutdown) {
            this.collectionTask.cancel();
        }

        // todo save to file
    }
}
