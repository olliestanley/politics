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

import pw.ollie.politics.Politics;
import pw.ollie.politics.data.Storable;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.util.math.IntPair;
import pw.ollie.politics.world.plot.Plot;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

import org.bukkit.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PoliticsWorld implements Storable {
    private final String name;
    private final WorldConfig config;
    private final Map<IntPair, Plot> chunkPlots;

    public PoliticsWorld(String name, WorldConfig config) {
        this(name, config, new HashMap<>());
    }

    private PoliticsWorld(String name, WorldConfig config, Map<IntPair, Plot> chunkPlots) {
        this.name = name;
        this.config = config;
        this.chunkPlots = chunkPlots;
    }

    public PoliticsWorld(String name, WorldConfig config, BasicBSONObject object) {
        this.name = object.getString("name", name);
        chunkPlots = new HashMap<>();
        BasicBSONList list = (BasicBSONList) object.get("plots");
        for (Object o : list) {
            if (!(o instanceof BasicBSONObject)) {
                throw new IllegalArgumentException("List must only contain more objects!");
            }
            BasicBSONObject plotObj = (BasicBSONObject) o;
            String string = plotObj.getString("type", null);
            if (string == null) {
                throw new IllegalArgumentException("Type is not a recognized string");
            }

            Plot p = new Plot(plotObj);
            chunkPlots.put(IntPair.of(p.getX(), p.getZ()), p);
        }
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public WorldConfig getConfig() {
        return config;
    }

    public World getWorld() {
        return Politics.getServer().getWorld(name);
    }

    public Universe getUniverse(GroupLevel level) {
        return Politics.getUniverseManager().getUniverse(this, level);
    }

    public Plot getPlotAtChunkPosition(int x, int z) {
        return new Plot(this, x, z);
    }

    public List<GroupLevel> getLevels() {
        return Politics.getUniverseManager().getLevelsOfWorld(this);
    }

    /*
     * Note: PoliticsWorld does not have a fromBSONObject method due to the Constructor with similar functionality
     */

    @Override
    public BSONObject toBSONObject() {
        BasicBSONObject bson = new BasicBSONObject();
        bson.put("name", name);
        BasicBSONList chunkPlotList = new BasicBSONList();
        for (Plot plot : chunkPlots.values()) {
            if (!plot.canStore()) {
                continue;
            }
            chunkPlotList.add(plot.toBSONObject());
        }
        bson.put("plots", chunkPlotList);
        return bson;
    }

    @Override
    public boolean canStore() {
        return true;
    }
}
