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

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.universe.Universe;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Collects taxation revenue from players depending on configured settings and Group properties.
 */
final class TaxationCollectionTask extends BukkitRunnable {
    private final PoliticsPlugin plugin;

    TaxationCollectionTask(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        PoliticsEconomy economy = plugin.getEconomy();
        TaxationManager taxationManager = plugin.getTaxationManager();
        if (economy == null || taxationManager == null) {
            return;
        }

        int collectionPeriod = plugin.getPoliticsConfig().getTaxPeriod();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();

            for (Universe universe : plugin.getUniverseManager().getUniverses(player.getWorld())) {
                int lastCollection = taxationManager.getLastCollection(playerId, universe);

                if (lastCollection >= collectionPeriod) {
                    universe.getCitizenGroups(player).stream()
                            .filter(this::canTax).forEach(group -> economy.applyFixedTax(group, playerId));
                    taxationManager.resetLastCollection(playerId, universe);
                } else {
                    taxationManager.incrementLastCollection(playerId, universe);
                }
            }
        }
    }

    private boolean canTax(Group group) {
        return group.getLevel().canTax();
    }
}
