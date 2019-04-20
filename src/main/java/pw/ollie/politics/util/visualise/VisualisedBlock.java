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

public class VisualisedBlock {
    private final Location location;
    private final BlockData fakeBlock;
    private final BlockData realBlock;

    public VisualisedBlock(Location location, BlockData visualizedBlock, BlockData realBlock) {
        this.location = location;
        this.fakeBlock = visualizedBlock;
        this.realBlock = realBlock;
    }

    public Location getLocation() {
        return location;
    }

    public int getX() {
        return location.getBlockX();
    }

    public int getY() {
        return location.getBlockY();
    }

    public int getZ() {
        return location.getBlockZ();
    }

    public BlockData getFakeBlock() {
        return fakeBlock;
    }

    public BlockData getRealBlock() {
        return realBlock;
    }
}
