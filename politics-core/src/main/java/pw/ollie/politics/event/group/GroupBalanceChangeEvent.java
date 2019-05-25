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
package pw.ollie.politics.event.group;

import pw.ollie.politics.economy.PoliticsTransferReason;
import pw.ollie.politics.group.Group;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class GroupBalanceChangeEvent extends GroupEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final double amount;
    private final PoliticsTransferReason reason;

    private boolean cancelled;

    public GroupBalanceChangeEvent(Group group, double amount, PoliticsTransferReason reason) {
        super(group);
        this.amount = amount;
        this.reason = reason;
    }

    public double getAmount() {
        return amount;
    }

    public PoliticsTransferReason getReason() {
        return reason;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
