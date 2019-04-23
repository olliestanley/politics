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

import java.util.Map;
import java.util.UUID;

/**
 * Provides access to economic functions relevant to Politics.
 * <p>
 * This mainly deals with the economic side of groups and their balances, taxation, etc.
 */
public abstract class PoliticsEconomy {
    private final PoliticsPlugin plugin;

    protected PoliticsEconomy(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    public PoliticsPlugin getPlugin() {
        return plugin;
    }

    /**
     * Gets the current balance of a group.
     *
     * @param group the group to get balance for
     * @return the balance of the group
     */
    public abstract double getBalance(Group group);

    /**
     * Gives the specified group the specified amount of money.
     * <p>
     * This method should call a GroupBalanceChangeEvent with the specified reason.
     *
     * @param group  the group to give money to
     * @param amount the amount to give the group
     * @param reason the reason for the group receiving money
     * @return the result of the attempted transfer
     */
    public abstract PoliticsEconomyResult give(Group group, double amount, PoliticsTransferReason reason);

    /**
     * Takes from the specified group the specified amount of money.
     * <p>
     * This method should call a GroupBalanceChangeEvent with the specified reason.
     *
     * @param group  the group to take money from
     * @param amount the amount to take from the group
     * @param reason the reason for the group losing money
     * @return the result of the attempted transfer
     */
    public abstract PoliticsEconomyResult take(Group group, double amount, PoliticsTransferReason reason);

    /**
     * Takes from all members of the group the specified amount, depositing it into the group's balance.
     *
     * @param group   the group to tax the members of
     * @param details the description of the tax to impose
     * @return a map of the unique ids of members to the results of the attempt to tax them
     */
    public abstract Map<UUID, PoliticsEconomyResult> taxMembers(Group group, TaxDetails details);
}
