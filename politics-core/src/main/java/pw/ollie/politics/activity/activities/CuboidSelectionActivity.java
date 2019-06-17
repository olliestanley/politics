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
import pw.ollie.politics.util.math.Position;
import pw.ollie.politics.util.math.Cuboid;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Represents an activity in which a player is selecting a cuboid area by picking two points.
 */
public final class CuboidSelectionActivity implements PoliticsActivity {
    private final UUID playerId;
    private final Consumer<CuboidSelectionActivity> callback;

    private Position firstPoint = null;
    private Position secondPoint = null;

    private boolean completed = false;

    public CuboidSelectionActivity(UUID playerId, Consumer<CuboidSelectionActivity> callback) {
        this.playerId = playerId;
        this.callback = callback;
    }

    public Position getFirstPoint() {
        return firstPoint;
    }

    public void setFirstPoint(Position firstPoint) {
        this.firstPoint = firstPoint;
    }

    public boolean isFirstPointSet() {
        return firstPoint != null;
    }

    public Position getSecondPoint() {
        return secondPoint;
    }

    public void setSecondPoint(Position secondPoint) {
        this.secondPoint = secondPoint;
    }

    public boolean isSecondPointSet() {
        return secondPoint != null;
    }

    public Cuboid getCuboid() {
        if (!isFirstPointSet() || !isSecondPointSet()) {
            return null;
        }
        return new Cuboid(getFirstPoint(), getSecondPoint());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCompleted() {
        return completed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean complete() {
        if (completed || !isFirstPointSet() || !isSecondPointSet()) {
            return false;
        }

        callback.accept(this);
        completed = true;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Cuboid Selection";
    }
}
