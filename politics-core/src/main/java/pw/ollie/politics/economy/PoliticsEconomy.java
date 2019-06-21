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
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.event.group.GroupBalanceChangeEvent;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupProperty;

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

    /**
     * Attempts to load the economy implementation.
     *
     * @return whether economy features were successfully enabled
     */
    public abstract boolean loadEconomy();

    /**
     * Gets the current balance of a group.
     * <p>
     * Note that group balances are treated as a double property of a group.
     *
     * @param group the group to get balance for
     * @return the balance of the group
     */
    public double getBalance(Group group) {
        return group.getDoubleProperty(GroupProperty.BALANCE, 0);
    }

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
    public PoliticsEconomyResult give(Group group, double amount, PoliticsTransferReason reason) {
        if (amount < 0) {
            return take(group, -amount, reason);
        }

        double current = getBalance(group);
        GroupBalanceChangeEvent event = PoliticsEventFactory.callGroupBalanceChangeEvent(group, amount, reason);
        if (event.isCancelled()) {
            return PoliticsEconomyResult.FAILURE;
        }

        group.setProperty(GroupProperty.BALANCE, current + amount);
        return PoliticsEconomyResult.SUCCESS;
    }

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
    public PoliticsEconomyResult take(Group group, double amount, PoliticsTransferReason reason) {
        if (amount < 0) {
            return give(group, -amount, reason);
        }

        double current = getBalance(group);
        if (current < amount) {
            return PoliticsEconomyResult.INSUFFICIENT_BALANCE;
        }

        GroupBalanceChangeEvent event = PoliticsEventFactory.callGroupBalanceChangeEvent(group, -amount, reason);
        if (event.isCancelled()) {
            return PoliticsEconomyResult.FAILURE;
        }

        group.setProperty(GroupProperty.BALANCE, current - amount);
        return PoliticsEconomyResult.SUCCESS;
    }

    /**
     * Taxes the given member of the given group by the given amount.
     *
     * @param group  the group to tax the member of
     * @param member the member to tax
     * @param amount the amount to tax the member
     * @return the result of the attempt to tax the member
     */
    public abstract PoliticsEconomyResult taxMember(Group group, UUID member, double amount);

    public PoliticsPlugin getPlugin() {
        return plugin;
    }
}
