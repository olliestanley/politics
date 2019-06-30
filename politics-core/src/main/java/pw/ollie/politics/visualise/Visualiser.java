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
package pw.ollie.politics.visualise;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.util.math.Cuboid;
import pw.ollie.politics.world.plot.Plot;
import pw.ollie.politics.world.plot.Subplot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Used to generate {@link Visualisation}s to display to players.
 */
public final class Visualiser {
    private final PoliticsPlugin plugin;
    private final Map<UUID, Visualisation> current;

    /**
     * Creates a new Visualiser instance for the given plugin.
     *
     * @param plugin the plugin instance
     */
    public Visualiser(PoliticsPlugin plugin) {
        this.plugin = plugin;
        this.current = new THashMap<>();
    }

    /**
     * Creates a new {@link Visualisation} showing the boundaries of the given {@link Plot}, using blue stained glass
     * as a fake block marker.
     *
     * @param plot the Plot to visualise the boundaries of
     * @return a Visualisation of the given Plot
     */
    public Visualisation visualisePlot(Plot plot) {
        return visualiseCuboid(plot.getCuboid(), Material.BLUE_STAINED_GLASS.createBlockData());
    }

    /**
     * Creates a new {@link Visualisation} showing the boundaries of the given {@link Subplot}, using yellow stained
     * glass as a fake block marker.
     *
     * @param subplot the Subplot to visualise the boundaries of
     * @return a Visualisation of the given Subplot
     */
    public Visualisation visualiseSubplot(Subplot subplot) {
        return visualiseCuboid(subplot.getCuboid(), Material.YELLOW_STAINED_GLASS.createBlockData());
    }

    /**
     * Creates a new {@link Visualisation} showing the boundaries of the given {@link Cuboid}, using the given block
     * data as a fake block marker.
     *
     * @param cuboid the Cuboid to visualise the boundaries of
     * @param fake   the {@link BlockData} to use as a fake block marker
     * @return a Visualisation of the given Cuboid
     */
    public Visualisation visualiseCuboid(Cuboid cuboid, BlockData fake) {
        // pre-specify size to avoid Set resizing, improving performance
        Set<VisualisedBlock> blocks = new THashSet<>(2 * (cuboid.getXSize() * cuboid.getYSize() + cuboid.getYSize() *
                cuboid.getZSize() + cuboid.getXSize() * cuboid.getZSize()));

        World world = cuboid.getWorld();
        for (int x = cuboid.getMinX(); x <= cuboid.getMaxX(); x++) {
            for (int z = cuboid.getMinZ(); z <= cuboid.getMaxZ(); z++) {
                for (int y = cuboid.getMinY(); y <= cuboid.getMaxY(); y++) {
                    if (!(y == cuboid.getMinY() || y == cuboid.getMaxY()
                            || x == cuboid.getMinX() || x == cuboid.getMaxX()
                            || z == cuboid.getMinZ() || z == cuboid.getMaxZ())) {
                        continue;
                    }

                    Location location = new Location(world, x, y, z);
                    blocks.add(new VisualisedBlock(location, fake, location.getBlock().getBlockData()));
                }
            }
        }

        return new Visualisation(blocks);
    }

    /**
     * Gets the current {@link Visualisation} for the {@link Player} with the given unique id.
     *
     * @param playerId the unique id of the Player to get the current Visualisation for
     * @return the current Visualisation of the Player with the given unique id
     */
    public Visualisation getCurrentVisualisation(UUID playerId) {
        return current.get(playerId);
    }

    /**
     * Gets the current {@link Visualisation} for the given {@link Player}.
     *
     * @param player the Player to get the current Visualisation for
     * @return the current Visualisation of the given Player
     */
    public Visualisation getCurrentVisualisation(Player player) {
        return getCurrentVisualisation(player.getUniqueId());
    }

    public PoliticsPlugin getPlugin() {
        return plugin;
    }

    void setCurrentVisualisation(UUID playerId, Visualisation visualisation) {
        current.put(playerId, visualisation);
    }

    void setCurrentVisualisation(Player player, Visualisation visualisation) {
        setCurrentVisualisation(player.getUniqueId(), visualisation);
    }
}
