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

import pw.ollie.politics.Politics;
import pw.ollie.politics.economy.PoliticsEconomy;
import pw.ollie.politics.group.Group;
import pw.ollie.politicstax.PoliticsTaxPlugin;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Collects taxation revenue from players depending on configured settings and Group properties.
 */
final class TaxationCollectionTask extends BukkitRunnable {
    private final PoliticsTaxPlugin plugin;

    TaxationCollectionTask(PoliticsTaxPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        PoliticsEconomy economy = Politics.getPoliticsEconomy();
        TaxationManager taxationManager = plugin.getTaxationManager();
        if (economy == null || taxationManager == null) {
            return;
        }

        int collectionPeriod = plugin.getTaxConfig().getTaxPeriod();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();

            Politics.getUniverseManager().streamUniverses(player.getWorld()).forEach(universe -> {
                int lastCollection = taxationManager.getLastCollection(playerId, universe);

                if (lastCollection >= collectionPeriod) {
                    universe.streamCitizenGroups(playerId).filter(this::canTax)
                            .forEach(group -> taxationManager.applyFixedTax(group, playerId));
                    taxationManager.resetLastCollection(playerId, universe);
                } else {
                    taxationManager.incrementLastCollection(playerId, universe);
                }
            });
        }
    }

    private boolean canTax(Group group) {
        return group.getLevel().canTax();
    }
}
