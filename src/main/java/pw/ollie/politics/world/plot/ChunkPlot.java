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

import pw.ollie.politics.world.PoliticsWorld;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public class ChunkPlot extends Plot {
    private final Chunk chunk;

    public ChunkPlot(PoliticsWorld world, int x, int z) {
        super(world);
        World bukkitWorld = world.getWorld();
        chunk = bukkitWorld.getChunkAt(x, z);
    }

    public ChunkPlot(BasicBSONObject object) {
        super(object);
        Object x = object.get("x");
        Object z = object.get("z");
        if (!(x instanceof Integer)) {
            throw new IllegalArgumentException("X was not available.");
        }
        if (!(z instanceof Integer)) {
            throw new IllegalArgumentException("Z was not available.");
        }
        World bukkitWorld = this.getPoliticsWorld().getWorld();
        chunk = bukkitWorld.getChunkAt((Integer) x, (Integer) z);
    }

    public Chunk getChunk() {
        return chunk;
    }

    @Override
    public Location getBasePoint() {
        return null;
        //return chunk.getBase(); // todo this was Spout implementation, need to work out how to produce the same point in Bukkit
    }

    @Override
    public boolean contains(Location point) {
        return false;
        //return chunk.contains(point); // todo this was Spout implementation
    }

    @Override
    public BSONObject toBSONObject() {
        BSONObject obj = super.toBSONObject();
        obj.put("x", getX());
        obj.put("z", getZ());
        obj.put("type", PlotType.CHUNK.name());
        return obj;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChunkPlot other = (ChunkPlot) obj;
        if (!Objects.equals(chunk, other.chunk)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (chunk != null ? chunk.hashCode() : 0);
        return hash;
    }
}
