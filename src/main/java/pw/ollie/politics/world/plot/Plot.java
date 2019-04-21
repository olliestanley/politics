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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.Politics;
import pw.ollie.politics.data.Storable;
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.event.plot.PlotOwnerChangeEvent;
import pw.ollie.politics.event.plot.subplot.SubplotCreateEvent;
import pw.ollie.politics.event.plot.subplot.SubplotDestroyEvent;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.group.privilege.PrivilegeType;
import pw.ollie.politics.util.Position;
import pw.ollie.politics.world.PoliticsWorld;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A plot in Politics is made up of exactly one chunk, and may have sub-plots.
 */
public final class Plot implements Storable {
    private final PoliticsWorld world;
    private final Chunk chunk;
    private final int baseX;
    private final int baseZ;
    private final TIntObjectMap<Subplot> subplots;

    private int owner;

    public Plot(PoliticsWorld world, int x, int z) {
        this(world, -1, x, z, new TIntObjectHashMap<>());
    }

    public Plot(PoliticsWorld world, int owner, int x, int z, TIntObjectMap<Subplot> subplots) {
        this.world = world;
        this.owner = owner;
        this.subplots = new TIntObjectHashMap<>(subplots);

        World bukkitWorld = world.getWorld();
        chunk = bukkitWorld.getChunkAt(x, z);

        baseX = x * 16;
        baseZ = z * 16;
    }

    public Plot(BasicBSONObject bObj) {
        world = Politics.getWorld(bObj.getString("world", null));
        owner = bObj.getInt("owner", -1);

        subplots = new TIntObjectHashMap<>();
        if (bObj.containsField("subplots")) {
            BasicBSONList subplotsList = (BasicBSONList) bObj.get("subplots");
            for (Object element : subplotsList) {
                BasicBSONObject subplotBson = (BasicBSONObject) element;
                Subplot subplot = new Subplot(subplotBson);
                subplots.put(subplot.getId(), subplot);
            }
        }

        Object x = bObj.get("x");
        Object z = bObj.get("z");
        if (!(x instanceof Integer)) {
            throw new IllegalArgumentException("X was not available.");
        }
        if (!(z instanceof Integer)) {
            throw new IllegalArgumentException("Z was not available.");
        }
        World bukkitWorld = getPoliticsWorld().getWorld();
        chunk = bukkitWorld.getChunkAt((Integer) x, (Integer) z);
        baseX = chunk.getX() * 16;
        baseZ = chunk.getZ() * 16;
    }

    public PoliticsWorld getPoliticsWorld() {
        return world;
    }

    public final int getX() {
        return getBasePoint().getBlockX();
    }

    public final int getZ() {
        return getBasePoint().getBlockZ();
    }

    public Chunk getChunk() {
        return chunk;
    }

    public Location getBasePoint() {
        return new Location(chunk.getWorld(), baseX, 0, baseZ);
    }

    public Location getMaxPoint() {
        return new Location(chunk.getWorld(), baseX + 15, 255, baseZ + 15);
    }

    public boolean contains(Location location) {
        return baseX <= location.getBlockX() && baseX + 16 <= location.getBlockX()
                && baseZ <= location.getBlockZ() && baseZ + 16 <= location.getBlockZ();
    }

    public boolean contains(Position position) {
        return contains(position.toLocation());
    }

    public Set<Subplot> getSubplots() {
        return new THashSet<>(subplots.valueCollection());
    }

    public Subplot getSubplot(int id) {
        return subplots.get(id);
    }

    public int getSubplotQuantity() {
        return subplots.size();
    }

    public boolean addSubplot(Subplot subplot) {
        if (subplots.containsKey(subplot.getId())) {
            return false;
        }

        SubplotCreateEvent event = PoliticsEventFactory.callSubplotCreateEvent(this, subplot);
        if (event.isCancelled()) {
            return false;
        }

        subplots.put(subplot.getId(), subplot);
        return true;
    }

    public boolean removeSubplot(Subplot subplot) {
        if (!subplots.containsKey(subplot.getId())) {
            return false;
        }

        SubplotDestroyEvent event = PoliticsEventFactory.callSubplotDestroyEvent(this, subplot);
        if (event.isCancelled()) {
            return false;
        }

        subplots.remove(subplot.getId());
        return true;
    }

    public Subplot getSubplotAt(Location location) {
        for (Subplot subplot : subplots.valueCollection()) {
            if (subplot.contains(location)) {
                return subplot;
            }
        }
        return null;
    }

    public Subplot getSubplotAt(Position position) {
        return getSubplotAt(position.toLocation());
    }

    public Group getOwner() {
        if (owner == -1) {
            return null;
        }
        return Politics.getUniverseManager().getGroupById(owner);
    }

    public int getOwnerId() {
        return owner;
    }

    public List<Group> getOwners() {
        List<Group> owners = new ArrayList<>();
        Group group = getOwner();
        while (group != null) {
            owners.add(group);
            group = group.getParent();
        }
        return owners;
    }

    public boolean setOwner(int id) {
        PlotOwnerChangeEvent event = PoliticsEventFactory.callPlotOwnerChangeEvent(this, id, true);
        if (event.isCancelled()) {
            return false;
        }

        owner = id;
        return true;
    }

    public boolean setOwner(Group group) {
        return setOwner(group.getUid());
    }

    public boolean removeOwner(int id) {
        if (id != owner) {
            return false;
        }
        PlotOwnerChangeEvent event = PoliticsEventFactory.callPlotOwnerChangeEvent(this, id, false);
        if (event.isCancelled()) {
            return false;
        }
        owner = -1;
        return true;
    }

    public boolean removeOwner(Group group) {
        return removeOwner(group.getUid());
    }

    public boolean isOwner(int id) {
        return id == owner;
    }

    public boolean isOwner(Group group) {
        return isOwner(group.getUid());
    }

    public Set<Privilege> getPrivileges(Player player) {
        return getOwner().getPrivileges(player).stream()
                .filter(privilege -> privilege.getTypes().contains(PrivilegeType.PLOT))
                .collect(Collectors.toSet());
    }

    public boolean can(Player player, Privilege privilege) {
        return getOwner().getPrivileges(player).contains(privilege);
    }

    public int generateSubplotId() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (subplots.get(i) != null) {
                continue;
            }
            return i;
        }

        throw new IllegalStateException("This plot has " + Integer.MAX_VALUE + " subplots. Oh dear.");
    }

    @Override
    public BSONObject toBSONObject() {
        BasicBSONObject obj = new BasicBSONObject();
        obj.put("world", world.getName());
        obj.put("owner", owner);
        obj.put("x", getX());
        obj.put("z", getZ());
        if (!subplots.isEmpty()) {
            BasicBSONList subplotList = new BasicBSONList();
            for (Subplot subplot : subplots.valueCollection()) {
                if (!subplot.canStore()) {
                    continue;
                }
                subplotList.add(subplot.toBSONObject());
            }
            obj.put("subplots", subplotList);
        }
        return obj;
    }

    @Override
    public boolean canStore() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Plot)) {
            return false;
        }
        Plot other = (Plot) obj;
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
