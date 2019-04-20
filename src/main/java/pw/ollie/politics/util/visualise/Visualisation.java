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

import pw.ollie.politics.Politics;
import pw.ollie.politics.activity.activities.VisualisationActivity;
import pw.ollie.politics.util.message.MessageBuilder;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Visualisation {
    private final Set<VisualisedBlock> blocks;

    public Visualisation() {
        this(new HashSet<>());
    }

    public Visualisation(Set<VisualisedBlock> blocks) {
        this.blocks = blocks;
    }

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

        Politics.getActivityManager().beginActivity(new VisualisationActivity(player.getUniqueId(), () -> revert(visualiser, player)));
        visualiser.setCurrentVisualisation(player, this);
        MessageBuilder.begin("Type ").highlight("/politics cancel").normal(" at any time to exit the visualisation.").send(player);
        return true;
    }

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

    public Set<VisualisedBlock> getBlocks() {
        return new HashSet<>();
    }

    public void addBlock(VisualisedBlock block) {
        blocks.add(block);
    }

    public void removeBlock(VisualisedBlock block) {
        blocks.remove(block);
    }

    public void removeBlockAt(Location location) {
        blocks.removeIf(b -> b.getLocation().equals(location));
    }

    private void removeOutOfRange(int minX, int minZ, int maxX, int maxZ) {
        blocks.removeIf(b -> b.getX() < minX || b.getX() > maxX || b.getZ() < minZ || b.getZ() > maxZ);
    }
}
