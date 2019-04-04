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

import pw.ollie.politics.Politics;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import org.bukkit.Location;

import java.util.Objects;

public class RegionPlot extends Plot {
    private final Object cuboid; // private final Cuboid cuboid;

    public RegionPlot(Location basePoint, int xSize, int ySize, int zSize) {
        super(Politics.getWorld(basePoint.getWorld()));
        cuboid = null; //new Cuboid(basePoint, new Vector3(xSize, ySize, zSize)); // todo this was Spout code, need to change over
    }

    public RegionPlot(BasicBSONObject object) {
        super(object);
        Object x = object.get("x");
        Object y = object.get("y");
        Object z = object.get("z");
        Object xSize = object.get("xSize");
        Object ySize = object.get("ySize");
        Object zSize = object.get("zSize");
        if (!(x instanceof Integer)) {
            throw new IllegalArgumentException("x was not an Integer.");
        }
        if (!(y instanceof Integer)) {
            throw new IllegalArgumentException("y was not an Integer.");
        }
        if (!(z instanceof Integer)) {
            throw new IllegalArgumentException("z was not an Integer.");
        }
        if (!(xSize instanceof Integer)) {
            throw new IllegalArgumentException("xSize was not  an Integer.");
        }
        if (!(ySize instanceof Integer)) {
            throw new IllegalArgumentException("ySize was not  an Integer.");
        }
        if (!(zSize instanceof Integer)) {
            throw new IllegalArgumentException("zSize was not  an Integer.");
        }
        // todo below was Spout code
        cuboid = null; //new Cuboid(new Location(getPoliticsWorld().getWorld(), (Integer) x, (Integer) y, (Integer) z), new Vector3((Integer) xSize, (Integer) ySize, (Integer) zSize));
    }

    public Object getCuboid() { // public Cuboid getCuboid() {
        return cuboid;
    }

    @Override
    public Location getBasePoint() {
        return null;
        //return cuboid.getBase(); // todo move from Spout
    }

    @Override
    public boolean contains(Location point) {
        return false;
        //return cuboid.contains(point); // todo move from spout
    }

    @Override
    public BSONObject toBSONObject() {
        BSONObject obj = super.toBSONObject();
        obj.put("x", getX());
        obj.put("y", getY());
        obj.put("z", getZ());
        // todo move from Spout code
//        Vector3 size = cuboid.getSize();
//        obj.put("xSize", size.getX());
//        obj.put("ySize", size.getY());
//        obj.put("zSize", size.getZ());
        obj.put("type", PlotType.REGION.name());
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
        final RegionPlot other = (RegionPlot) obj;
        if (!Objects.equals(cuboid, other.cuboid)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (cuboid != null ? cuboid.hashCode() : 0);
        return hash;
    }
}
