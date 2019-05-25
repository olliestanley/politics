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
package pw.ollie.politics.activity;

import java.util.UUID;

/**
 * Represents and holds state data for an activity being performed by a player.
 */
public interface PoliticsActivity {
    /**
     * Gets the unique id of the player engaging in the activity.
     *
     * @return the relevant player's unique id
     */
    UUID getPlayerId();

    /**
     * Attempts to complete the activity.
     *
     * @return whether the activity successfully completed
     */
    boolean complete();

    /**
     * Checks whether the activity has already been completed.
     *
     * @return whether the activity is complete
     */
    boolean hasCompleted();

    /**
     * Gets the name of the activity. This should be the same for all instances of the activity.
     *
     * @return this activity's name
     */
    String getName();
}
