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
package pw.ollie.politicstax.tax;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import pw.ollie.politics.Politics;
import pw.ollie.politics.economy.PoliticsEconomy;
import pw.ollie.politics.economy.PoliticsEconomyResult;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupProperty;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politicstax.PoliticsTaxPlugin;

import java.io.File;
import java.util.Map;
import java.util.UUID;

public final class TaxationManager {
    // todo docs
    // run the collection update task once a minute
    private static final long TASK_PERIOD = 20 * 60;

    private final PoliticsTaxPlugin plugin;
    private final Map<UUID, TObjectIntMap<Universe>> lastCollections;

    private TaxationCollectionTask collectionTask;

    public TaxationManager(PoliticsTaxPlugin plugin) {
        this.plugin = plugin;
        this.lastCollections = new THashMap<>();
    }

    public int getLastCollection(UUID playerId, Universe universe) {
        return getLastCollections(playerId).get(universe);
    }

    /**
     * Taxes the given member of the given group by the Group's fixed tax amount.
     *
     * @param group    the group to tax the member of
     * @param playerId the player id of the member to tax
     * @return the result of the attempt to tax the member
     */
    public PoliticsEconomyResult applyFixedTax(Group group, UUID playerId) {
        PoliticsEconomy economy = Politics.getPoliticsEconomy();
        return economy.taxMember(group, playerId, Math.min(group.getDoubleProperty(GroupProperty.FIXED_TAX, 0.0), plugin.getTaxConfig().getMaxFixedTax()));
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

        File dataDir = Politics.getFileSystem().getDataDir();
        File taxFile = new File(dataDir, "taxdata.ptx");
        // todo
    }
}
