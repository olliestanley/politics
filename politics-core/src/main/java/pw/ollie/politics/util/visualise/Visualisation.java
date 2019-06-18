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

import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.Politics;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Represents a visualised collection of fake blocks sent to a player to show spaces.
 */
public class Visualisation {
    private final Set<VisualisedBlock> blocks;

    /**
     * Creates a blank Visualisation, with no included blocks.
     */
    public Visualisation() {
        this(new THashSet<>());
    }

    /**
     * Creates a new Visualisation including all of the given blocks.
     *
     * @param blocks visualised blocks to include
     */
    public Visualisation(Set<VisualisedBlock> blocks) {
        this.blocks = blocks;
    }

    /**
     * Attempts to apply this Visualisation to the given {@link Player}, through the given {@link Visualiser}.
     *
     * @param visualiser the Visualiser to use to apply the Visualisation
     * @param player     the Player to send the Visualisation to
     * @return whether the Visualisation was successfully applied
     */
    public boolean apply(Visualiser visualiser, Player player) {
        if (Politics.getActivityManager().isActive(player)) {
            return false;
        }

        for (VisualisedBlock block : blocks) {
            if (!block.getLocation().getChunk().isLoaded()) {
                continue;
            }

            player.sendBlockChange(block.getLocation(), block.getFakeBlock());
        }

        visualiser.setCurrentVisualisation(player, this);
        return true;
    }

    /**
     * Stops applying this Visualisation to the given {@link Player}, through the given {@link Visualiser}.
     *
     * @param visualiser the Visualiser used to apply the Visualisation
     * @param player     the Player to stop sending the Visualisation to
     */
    public void revert(Visualiser visualiser, Player player) {
        if (!this.equals(visualiser.getCurrentVisualisation(player))) {
            return;
        }
        if (!player.isOnline()) {
            return;
        }

        int minx = player.getLocation().getBlockX() - 100;
        int minz = player.getLocation().getBlockZ() - 100;
        int maxx = player.getLocation().getBlockX() + 100;
        int maxz = player.getLocation().getBlockZ() + 100;

        removeOutOfRange(minx, minz, maxx, maxz);

        boolean run = false;
        for (VisualisedBlock block : blocks) {
            if (!run) {
                if (!player.getWorld().equals(block.getLocation().getWorld())) {
                    return;
                }
                run = true;
            }
            player.sendBlockChange(block.getLocation(), block.getRealBlock());
        }

        visualiser.setCurrentVisualisation(player, null);
    }

    /**
     * Gets a {@link Set} of all {@link VisualisedBlock}s in this Visualisation.
     *
     * @return all VisualisedBlocks this Visualisation includes
     */
    public Set<VisualisedBlock> getBlocks() {
        return new THashSet<>(blocks);
    }

    /**
     * Adds the given {@link VisualisedBlock} to this Visualisation.
     *
     * @param block the block to add
     */
    public void addBlock(VisualisedBlock block) {
        blocks.add(block);
    }

    /**
     * Removes the given {@link VisualisedBlock} from this Visualisation.
     *
     * @param block the block to remove
     */
    public void removeBlock(VisualisedBlock block) {
        blocks.remove(block);
    }

    /**
     * Removes any {@link VisualisedBlock}(s) at the given {@link Location} from this Visualisation.
     *
     * @param location the Location to remove block(s) at
     */
    public void removeBlockAt(Location location) {
        blocks.removeIf(b -> b.getLocation().equals(location));
    }

    private void removeOutOfRange(int minX, int minZ, int maxX, int maxZ) {
        blocks.removeIf(b -> b.getX() < minX || b.getX() > maxX || b.getZ() < minZ || b.getZ() > maxZ);
    }
}
