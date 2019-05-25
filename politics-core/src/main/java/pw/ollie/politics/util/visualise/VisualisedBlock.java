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
package pw.ollie.politics.util.visualise;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

/**
 * Represents a single block sent to a player as part of a {@link Visualisation}.
 */
public class VisualisedBlock {
    private final Location location;
    private final BlockData fakeBlock;
    private final BlockData realBlock;

    /**
     * Creates a new VisualisedBlock with specified properties.
     *
     * @param location  the {@link Location} at which the block is
     * @param fakeBlock the data for the fake block being shown as part of the visualisation
     * @param realBlock the data for the real block at the Location of the VisualisedBlock
     */
    public VisualisedBlock(Location location, BlockData fakeBlock, BlockData realBlock) {
        this.location = location;
        this.fakeBlock = fakeBlock;
        this.realBlock = realBlock;
    }

    /**
     * Gets the {@link Location} at which the VisualisedBlock is located.
     *
     * @return the VisualisedBlock's Location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the x coordinate of the VisualisedBlock.
     *
     * @return the block's x coordinate
     */
    public int getX() {
        return location.getBlockX();
    }

    /**
     * Gets the y coordinate of the VisualisedBlock.
     *
     * @return the block's y coordinate
     */
    public int getY() {
        return location.getBlockY();
    }

    /**
     * Gets the z coordinate of the VisualisedBlock.
     *
     * @return the block's z coordinate
     */
    public int getZ() {
        return location.getBlockZ();
    }

    /**
     * Gets the data of the fake block being visualised.
     *
     * @return the fake block data
     */
    public BlockData getFakeBlock() {
        return fakeBlock;
    }

    /**
     * Gets the data of the real block at the VisualisedBlock's {@link Location}.
     *
     * @return the real block data at the Location
     */
    public BlockData getRealBlock() {
        return realBlock;
    }
}
