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
package pw.ollie.politics.world;

import org.bukkit.configuration.ConfigurationSection;

public final class WorldConfig {
    private final String name;

    private int plotSizeX = 1;
    private int plotSizeY = 1;
    private int plotSizeZ = 1;

    public WorldConfig(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPlotSizeX() {
        return plotSizeX;
    }

    public void setPlotSizeX(int plotSizeX) {
        this.plotSizeX = plotSizeX;
    }

    public int getPlotSizeY() {
        return plotSizeY;
    }

    public void setPlotSizeY(int plotSizeY) {
        this.plotSizeY = plotSizeY;
    }

    public int getPlotSizeZ() {
        return plotSizeZ;
    }

    public void setPlotSizeZ(int plotSizeZ) {
        this.plotSizeZ = plotSizeZ;
    }

    public void save(ConfigurationSection config) {
        config.set("plotsize.x", plotSizeX);
        config.set("plotsize.z", plotSizeZ);
    }

    public static WorldConfig load(String name, ConfigurationSection config) {
        WorldConfig wc = new WorldConfig(name);
        wc.plotSizeX = config.getInt("plotsize.x", 1);
        wc.plotSizeZ = config.getInt("plotsize.z", 1);
        return wc;
    }
}
