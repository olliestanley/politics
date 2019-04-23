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
import pw.ollie.politics.group.GroupProperty;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public final class TaxationCollectionTask extends BukkitRunnable {
    private final PoliticsPlugin plugin;

    TaxationCollectionTask(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

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
            int lastCollection = taxationManager.getLastCollection(playerId);

            if (lastCollection >= collectionPeriod) {
                for (Group group : plugin.getGroupManager().getAllCitizenGroups(playerId)) {
                    if (!group.getLevel().canTax()) {
                        continue;
                    }

                    double taxAmount = Math.min(group.getDoubleProperty(GroupProperty.FIXED_TAX, 0.0),
                            plugin.getPoliticsConfig().getMaxFixedTax());
                    economy.taxMember(group, playerId, taxAmount);
                }

                taxationManager.resetLastCollection(playerId);
            } else {
                taxationManager.incrementLastCollection(playerId);
            }
        }
    }
}
