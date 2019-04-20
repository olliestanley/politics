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

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.world.plot.Plot;
import pw.ollie.politics.world.plot.Subplot;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class Visualiser {
    private final PoliticsPlugin plugin;
    private final Map<UUID, Visualisation> current;

    public Visualiser(PoliticsPlugin plugin) {
        this.plugin = plugin;
        this.current = new HashMap<>();
    }

    public PoliticsPlugin getPlugin() {
        return plugin;
    }

    public Visualisation visualisePlot(Plot plot) {
        Set<VisualisedBlock> blocks = new HashSet<>();
        // todo
        return new Visualisation(blocks);
    }

    public Visualisation visualiseSubplot(Subplot subplot) {
        Set<VisualisedBlock> blocks = new HashSet<>();
        // todo
        return new Visualisation(blocks);
    }

    public Visualisation getCurrentVisualisation(UUID playerId) {
        return current.get(playerId);
    }

    public Visualisation getCurrentVisualisation(Player player) {
        return getCurrentVisualisation(player.getUniqueId());
    }

    void setCurrentVisualisation(UUID playerId, Visualisation visualisation) {
        current.put(playerId, visualisation);
    }

    void setCurrentVisualisation(Player player, Visualisation visualisation) {
        setCurrentVisualisation(player.getUniqueId(), visualisation);
    }
}
