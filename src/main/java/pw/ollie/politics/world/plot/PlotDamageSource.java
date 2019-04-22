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

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a source of damage to a plot.
 */
public class PlotDamageSource {
    private final UUID playerId;
    private final Entity nonPlayerEntity;
    private final Block block;

    public PlotDamageSource(UUID playerId) {
        this(playerId, null, null);
    }

    public PlotDamageSource(Entity nonPlayerEntity) {
        this(null, nonPlayerEntity, null);
    }

    public PlotDamageSource(Block block) {
        this(null, null, block);
    }

    private PlotDamageSource(UUID playerId, Entity nonPlayerEntity, Block block) {
        this.playerId = playerId;
        this.nonPlayerEntity = nonPlayerEntity;
        this.block = block;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerId);
    }

    public Entity getNonPlayerEntity() {
        return nonPlayerEntity;
    }

    public Block getBlock() {
        return block;
    }
}
