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
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.util.IntPair;
import pw.ollie.politics.world.plot.ChunkPlot;
import pw.ollie.politics.world.plot.Plot;
import pw.ollie.politics.world.plot.PlotType;
import pw.ollie.politics.world.plot.RegionPlot;

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
    private final Map<IntPair, Plot> plots;

    public PoliticsWorld(String name, WorldConfig config) {
        this(name, config, new HashMap<>());
    }

    private PoliticsWorld(String name, WorldConfig config, Map<IntPair, Plot> plots) {
        this.name = name;
        this.config = config;
        this.plots = plots;
    }

    public PoliticsWorld(String name, WorldConfig config, BasicBSONObject object) {
        this.name = object.getString("name", null);
        plots = new HashMap<>();
        BasicBSONList list = (BasicBSONList) object.get("plots");
        for (Object o : list) {
            if (!(o instanceof BasicBSONObject)) {
                throw new IllegalArgumentException("List must only contain more objects!");
            }
            BasicBSONObject plotObj = (BasicBSONObject) o;
            String string = plotObj.getString("type", null);
            if (string == null || PlotType.valueOf(string) == null) {
                throw new IllegalArgumentException("Type is not a recognized string");
            }
            switch (PlotType.valueOf(string)) {
                case CHUNK: {
                    ChunkPlot p = new ChunkPlot(plotObj);
                    plots.put(IntPair.of(p.getX(), p.getZ()), p);
                    break;
                }
                case REGION: {
                    RegionPlot p = new RegionPlot(plotObj);
                    plots.put(IntPair.of(p.getX(), p.getZ()), p);
                    break;
                }
                default:
                    throw new IllegalStateException("Was unable to handle Type");
            }
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

//    /**
//     * Gets the internal list of owners of a given location. Creates this list
//     * if it doesn't exist.
//     *
//     * @param x
//     * @param y
//     * @param z
//     * @return the internal list of owners for given location
//     */
//    TIntList getInternalOwnerList(int x, int y, int z) {
//        plots.get(x, y, z).geto
//        TIntList list = owners.get(x, y, z);
//        if (list == null) {
//            list = new TIntArrayList();
//            owners.put(x, y, z, list);
//        }
//        return list;
//    }
//    /**
//     * Gets the list of owners at the given location.
//     *
//     * @param x
//     * @param y
//     * @param z
//     * @return
//     */
//    public TIntList getOwnerIds(final int x, final int y, final int z) {
//        return new TIntArrayList(getInternalOwnerList(x, y, z));
//    }

    public List<Group> getOwners(int x, int z) {
        return plots.get(IntPair.of(x, z)).getOwners();
    }

    public Universe getUniverse(GroupLevel level) {
        return Politics.getUniverseManager().getUniverse(this, level);
    }

    public Plot getPlotAt(int x, int z) {
        return plots.get(IntPair.of(x, z));
    }

    public ChunkPlot getPlotAtChunkPosition(int x, int z) {
        return new ChunkPlot(this, x, z);
    }

    public List<GroupLevel> getLevels() {
        return Politics.getUniverseManager().getLevelsOfWorld(this);
    }

    @Override
    public BSONObject toBSONObject() {
        BasicBSONObject bson = new BasicBSONObject();
        bson.put("name", name);
        BasicBSONList plotList = new BasicBSONList();
        for (Plot plot : plots.values()) {
            if (!plot.canStore()) {
                continue;
            }
            plotList.add(plot.toBSONObject());
        }
        bson.put("plots", plots);
        return bson;
    }

    @Override
    public boolean canStore() {
        return true;
    }
}
