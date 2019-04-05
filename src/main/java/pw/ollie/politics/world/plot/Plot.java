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

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import pw.ollie.politics.Politics;
import pw.ollie.politics.data.Storable;
import pw.ollie.politics.event.plot.PlotOwnerChangeEvent;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.world.PoliticsWorld;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class Plot implements Storable {
    private final PoliticsWorld world;
    private final TIntList owners;

    public Plot(PoliticsWorld world) {
        this(world, new TIntArrayList());
    }

    public Plot(PoliticsWorld world, TIntList owners) {
        this.world = world;
        this.owners = owners;
    }

    public Plot(BasicBSONObject object) {
        world = Politics.getWorld(object.getString("world", null));
        if (object.containsField("owners")) {
            TIntList ownersList = new TIntArrayList();
            BasicBSONList ownersBSON = (BasicBSONList) object.get("owners");
            for (Object obj : ownersBSON) {
                if (!(obj instanceof Integer)) {
                    throw new IllegalArgumentException("obj is not an Integer!");
                }
                int val = (Integer) obj;
                ownersList.add(val);
            }
            owners = ownersList;
        } else {
            owners = new TIntArrayList();
        }
    }

    public PoliticsWorld getPoliticsWorld() {
        return world;
    }

    public final int getX() {
        return getBasePoint().getBlockX();
    }

    public final int getY() {
        return getBasePoint().getBlockY();
    }

    public final int getZ() {
        return getBasePoint().getBlockZ();
    }

    public abstract Location getBasePoint();

    public abstract boolean contains(Location location);

    public TIntList getOwnerIds() {
        return new TIntArrayList(owners);
    }

    public List<Group> getOwners() {
        List<Group> ret = new ArrayList<>();
        TIntIterator it = owners.iterator();
        while (it.hasNext()) {
            int id = it.next();
            Group group = Politics.getUniverseManager().getGroupById(id);
            if (group == null) {
                owners.remove(id); // Group no longer exists
            } else {
                ret.add(group);
            }
        }
        return ret;
    }

    public Group getOwner(Universe universe) {
        for (Group owner : getOwners()) {
            if (owner.getUniverse().equals(universe)) {
                return owner;
            }
        }
        return null;
    }

    public List<Group> getOwners(Universe universe) {
        List<Group> owners = new ArrayList<>();
        Group group = getOwner(universe);
        while (group != null) {
            owners.add(group);
            group = group.getParent();
        }
        return owners;
    }

    public boolean addOwner(int id) {
        return addOwner(Politics.getUniverseManager().getGroupById(id));
    }

    public boolean addOwner(Group group) {
        PlotOwnerChangeEvent event = Politics.getEventFactory().callPlotOwnerChangeEvent(this, group.getUid(), true);
        if (event.isCancelled()) {
            return false;
        }

        for (Group g : getOwners()) {
            if (g.equals(group)) {
                return false; // Already owns the plot
            }

            if (g.getUniverse().equals(group.getUniverse()) && g.equals(group.getParent())) {
                removeOwner(g);
                break; // We are a sub-plot
            }
        }

        return owners.add(group.getUid());
    }

    public boolean removeOwner(int id) {
        if (!owners.contains(id)) {
            return true; // Not in there
        }
        PlotOwnerChangeEvent event = Politics.getEventFactory().callPlotOwnerChangeEvent(this, id, true);
        if (event.isCancelled()) {
            return false;
        }
        return owners.remove(id);
    }

    public boolean removeOwner(Group group) {
        return removeOwner(group.getUid());
    }

    public boolean isOwner(int id) {
        return owners.contains(id);
    }

    public boolean isOwner(Group group) {
        return isOwner(group.getUid());
    }

    public Set<Privilege> getPrivileges(Player player) {
        Set<Privilege> privileges = new HashSet<>();
        // TODO implement this
        return privileges;
    }

    @Override
    public BSONObject toBSONObject() {
        BasicBSONObject obj = new BasicBSONObject();
        obj.put("world", world.getName());
        obj.put("owners", owners);
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
        return Objects.equals(world, other.world) && Objects.equals(owners, other.owners);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash *= 73 + (world != null ? world.hashCode() : 0) + (owners != null ? owners.hashCode() : 0);
        return hash;
    }
}
