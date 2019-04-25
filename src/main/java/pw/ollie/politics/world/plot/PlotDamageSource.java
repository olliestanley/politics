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
package pw.ollie.politics.world.plot;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a source of damage to a plot. May represent either an {@link Entity} (including {@link Player}s) or a
 * {@link Block}.
 */
public class PlotDamageSource {
    private final Entity entity;
    private final Block block;

    /**
     * Construct a new {@link Entity} PlotDamageSource. If the Entity is a {@link Player}, this will also be a Player
     * PlotDamageSource.
     *
     * @param entity the entity causing damage
     */
    public PlotDamageSource(Entity entity) {
        this(entity, null);
    }

    /**
     * Construct a new {@link Block} PlotDamageSource.
     *
     * @param block the block causing damage
     */
    public PlotDamageSource(Block block) {
        this(null, block);
    }

    private PlotDamageSource(Entity entity, Block block) {
        this.entity = entity;
        this.block = block;
    }

    /**
     * Gets the unique id of the {@link Player} represented by this PlotDamageSource object, or null if this object does
     * not represent a Player.
     *
     * @return the unique id of the relevant Player, or {@code null} if there is no relevant Player
     */
    public UUID getPlayerId() {
        if (!isPlayer()) {
            return null;
        }
        return getPlayer().getUniqueId();
    }

    /**
     * Gets the {@link Player} represented by this PlotDamageSource object, or null if this object does not represent a
     * Player.
     *
     * @return the relevant Player, or {@code null} if there is no relevant Player
     */
    public Player getPlayer() {
        if (!isPlayer()) {
            return null;
        }
        return (Player) entity;
    }

    /**
     * Checks whether this PlotDamageSource object represents a {@link Player}.
     *
     * @return whether the source of damage is a Player
     */
    public boolean isPlayer() {
        return entity instanceof Player;
    }

    /**
     * Gets the {@link Entity} represented by this PlotDamageSource object, or null if this object does not represent a
     * Entity.
     *
     * @return the relevant Entity, or {@code null} if there is no relevant Entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Checks whether this PlotDamageSource object represents an {@link Entity}.
     *
     * @return whether the source of damage is an Entity
     */
    public boolean isEntity() {
        return getEntity() != null;
    }

    /**
     * Gets the {@link Block} represented by this PlotDamageSource object, or null if this object does not represent a
     * Block.
     *
     * @return the relevant Block, or {@code null} if there is no relevant Block
     */
    public Block getBlock() {
        return block;
    }

    /**
     * Checks whether this PlotDamageSource object represents a {@link Block}.
     *
     * @return whether the source of damage is an Block
     */
    public boolean isBlock() {
        return getBlock() != null;
    }
}
