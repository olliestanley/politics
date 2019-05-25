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

/**
 * Represents a reason for the transfer of money in Politics.
 */
public enum PoliticsTransferReason {
    /**
     * A group taxing a member.
     */
    TAXATION,
    /**
     * Money going from a group's balance to a member.
     */
    GROUP_TO_PLAYER,
    /**
     * Money being voluntarily transferred from one group to another.
     */
    BETWEEN_GROUPS_VOLUNTARY,
    /**
     * Money being paid by the loser of a war to the winner.
     */
    WAR_REPARATIONS,
    /**
     * Money being voluntarily paid from one player to another.
     */
    BETWEEN_PLAYERS_VOLUNTARY,
    /**
     * Money being paid from one player to another as part of a transaction, such as a purchase of an item.
     */
    BETWEEN_PLAYERS_TRANSACTION
}
