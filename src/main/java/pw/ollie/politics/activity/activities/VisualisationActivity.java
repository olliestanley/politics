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
package pw.ollie.politics.activity.activities;

import pw.ollie.politics.activity.PoliticsActivity;

import java.util.UUID;

public final class VisualisationActivity implements PoliticsActivity {
    private final UUID playerId;
    private final Runnable callback;

    private boolean complete;

    public VisualisationActivity(UUID playerId, Runnable callback) {
        this.playerId = playerId;
        this.callback = callback;
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public boolean complete() {
        complete = true;
        callback.run();
        return true;
    }

    @Override
    public boolean hasCompleted() {
        return complete;
    }

    @Override
    public String getName() {
        return "Visualisation";
    }
}
