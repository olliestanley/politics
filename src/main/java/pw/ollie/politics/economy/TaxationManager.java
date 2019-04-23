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

import java.util.UUID;

public final class TaxationManager {
    // run the collection update task once a minute
    private static final long TASK_PERIOD = 20 * 60;

    private final PoliticsPlugin plugin;
    private final TObjectIntMap<UUID> lastCollections;

    private TaxationCollectionTask collectionTask;

    public TaxationManager(PoliticsPlugin plugin) {
        this.plugin = plugin;
        this.lastCollections = new TObjectIntHashMap<>();
    }

    public int getLastCollection(UUID playerId) {
        lastCollections.putIfAbsent(playerId, 0);
        return lastCollections.get(playerId);
    }

    public void resetLastCollection(UUID playerId) {
        lastCollections.put(playerId, 0);
    }

    public void incrementLastCollection(UUID playerId) {
        lastCollections.put(playerId, getLastCollection(playerId) + 1);
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
