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

    /**
     * Gets the {@link PoliticsWorld} object for the world this Plot is contained within.
     *
     * @return the PoliticsWorld this Plot is within
     */
    public PoliticsWorld getPoliticsWorld() {
        return world;
    }

    /**
     * Gets the base block x coordinate for this Plot.
     *
     * @return this Plot's base point x coordinate
     */
    public int getBaseX() {
        return getBasePoint().getBlockX();
    }

    /**
     * Gets the base block z coordinate for this Plot.
     *
     * @return this Plot's base point z coordinate
     */
    public int getBaseZ() {
        return getBasePoint().getBlockZ();
    }

    /**
     * Gets the {@link Chunk} this Plot occupies the space of.
     *
     * @return this Plot's relevant Chunk
     */
    public Chunk getChunk() {
        return chunk;
    }

    /**
     * Gets the base {@link Location} of this Plot, meaning the point of minimum x, y and z coordinates.
     *
     * @return the base point for this Plot
     */
    public Location getBasePoint() {
        return new Location(chunk.getWorld(), baseX, 0, baseZ);
    }

    /**
     * Gets the maximum {@link Location} of this Plot, meaning the point of maximum x, y and z coordinates.
     *
     * @return the maximum point for this Plot
     */
    public Location getMaxPoint() {
        return new Location(chunk.getWorld(), baseX + 15, 255, baseZ + 15);
    }

    /**
     * Checks whether the given {@link Location} is contained within this Plot.
     *
     * @param location the Location to check
     * @return whether the given Location is inside this Plot
     */
    public boolean contains(Location location) {
        return baseX <= location.getBlockX() && baseX + 16 <= location.getBlockX()
                && baseZ <= location.getBlockZ() && baseZ + 16 <= location.getBlockZ();
    }

    /**
     * Checks whether the given {@link Position} is contained within this Plot.
     *
     * @param position the Position to check
     * @return whether the given Position is inside this Plot
     */
    public boolean contains(Position position) {
        return contains(position.toLocation());
    }

    /**
     * Gets a {@link Set} of all {@link Subplot}s contained within this Plot.
     *
     * @return a Set of all this Plot's Subplots
     */
    public Set<Subplot> getSubplots() {
        return new THashSet<>(subplots.valueCollection());
    }

    /**
     * Gets the {@link Subplot} within this Plot with the given id.
     * <p>
     * Note that Subplot ids are unique within a Plot but multiple Subplots in different Plots may have the same id. If
     * there is no Subplot with the given id inside this Plot, null is returned.
     *
     * @param id the id of the Subplot to get
     * @return the Subplot with the given id, or {@code null} if there isn't one with that id
     */
    public Subplot getSubplot(int id) {
        if (!world.getConfig().hasSubplots()) {
            return null;
        }

        return subplots.get(id);
    }

    /**
     * Gets the number of {@link Subplot}s contained within this Plot.
     *
     * @return the number of Subplots in this Plot
     */
    public int getSubplotQuantity() {
        return subplots.size();
    }

    /**
     * Attempts to add the given {@link Subplot} to this Plot.
     * <p>
     * This method calls {@link SubplotCreateEvent}, and will fail if this event is cancelled.
     *
     * @param subplot the Subplot to attempt to add to the Plot
     * @return whether the Subplot was successfully added
     */
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

    /**
     * Attempts to remove the given {@link Subplot} from this Plot.
     * <p>
     * This method calls {@link SubplotDestroyEvent}, and will fail if this event is cancelled.
     *
     * @param subplot the Subplot to attempt to remove from the Plot
     * @return whether the Subplot was successfully removed
     */
    public boolean removeSubplot(Subplot subplot) {
        if (!world.getConfig().hasSubplots()) {
            return false;
        }

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

    /**
     * Gets the {@link Subplot} of this Plot at the given {@link Location}, if there is one.
     *
     * @param location the Location to get the Subplot at
     * @return the relevant Subplot, or {@code null} if there isn't one at the Location
     */
    public Subplot getSubplotAt(Location location) {
        if (!world.getConfig().hasSubplots()) {
            return null;
        }

        for (Subplot subplot : subplots.valueCollection()) {
            if (subplot.contains(location)) {
                return subplot;
            }
        }
        return null;
    }

    /**
     * Gets the {@link Subplot} of this Plot at the given {@link Position}, if there is one.
     *
     * @param position the Position to get the Subplot at
     * @return the relevant Subplot, or {@code null} if there isn't one at the Position
     */
    public Subplot getSubplotAt(Position position) {
        return getSubplotAt(position.toLocation());
    }

    /**
     * Gets the {@link Group} which directly owns this Plot, if there is one.
     *
     * @return the Group owning this Plot, or {@code null} if no Group owns the Plot
     */
    public Group getOwner() {
        if (owner == -1) {
            return null;
        }
        return Politics.getUniverseManager().getGroupById(owner);
    }

    /**
     * Gets the id of the {@link Group} directly owning this Plot.
     *
     * @return the group id of this Plot's owner, or -1 if there is no owner
     */
    public int getOwnerId() {
        return owner;
    }

    /**
     * Checks whether this Plot has an owner.
     *
     * @return whether the Plot has an owner
     */
    public boolean hasOwner() {
        return owner != -1;
    }

    /**
     * Gets a {@link List} of all {@link Group}s involved in ownership of this Plot. This means the direct owner of the
     * Plot plus all parent Groups of the direct owner.
     *
     * @return a List of all direct or indirect owners of the Plot, or an empty List if there is no owner
     */
    public List<Group> getOwners() {
        List<Group> owners = new ArrayList<>();
        Group group = getOwner();
        while (group != null) {
            owners.add(group);
            group = group.getParent();
        }
        return owners;
    }

    /**
     * Attempts to set the direct owner {@link Group} of this Plot to the Group with the given unique id.
     * <p>
     * This method calls {@link PlotOwnerChangeEvent} and will fail if this event is cancelled. It will also fail if no
     * Group with the given unique id exists.
     *
     * @param id the unique id of the Group to designate as this Plot's new owner
     * @return whether the owner of the Plot was successfully changed
     */
    public boolean setOwner(int id) {
        Group group = Politics.getGroupById(id);
        if (group == null) {
            return false;
        }

        PlotOwnerChangeEvent event = PoliticsEventFactory.callPlotOwnerChangeEvent(this, id, true);
        if (event.isCancelled()) {
            return false;
        }

        owner = id;
        return true;
    }

    /**
     * Attempts to set the direct owner {@link Group} of this Plot to the given Group.
     * <p>
     * This method calls {@link PlotOwnerChangeEvent} and will fail if this event is cancelled.
     *
     * @param group the Group to designate as this Plot's new owner
     * @return whether the owner of the Plot was successfully changed
     */
    public boolean setOwner(Group group) {
        return setOwner(group.getUid());
    }

    /**
     * Attempts to remove the direct owner {@link Group} of this Plot. This will also remove all Subplots from this Plot
     * without calling events for their removal.
     * <p>
     * This method calls {@link PlotOwnerChangeEvent} and will fail if this event is cancelled.
     *
     * @return whether the owner of the Plot was successfully changed
     */
    public boolean removeOwner() {
        PlotOwnerChangeEvent event = PoliticsEventFactory.callPlotOwnerChangeEvent(this, owner, false);
        if (event.isCancelled()) {
            return false;
        }

        subplots.clear();
        owner = -1;
        return true;
    }

    /**
     * Checks whether the Plot is directly owned by the {@link Group} with the given unique id.
     *
     * @param id the id of the Group to check for ownership by
     * @return whether the Group with the given unique id directly owns this Plot
     */
    public boolean isOwner(int id) {
        return id == owner;
    }

    /**
     * Checks whether the Plot is directly owned by the given {@link Group}.
     *
     * @param group the Group to check for ownership by
     * @return whether the given Group directly owns this Plot
     */
    public boolean isOwner(Group group) {
        return isOwner(group.getUid());
    }

    /**
     * Checks whether the Plot is directly or indirectly owned by the {@link Group} with the given unique id.
     *
     * @param id the id of the Group to check for indirect ownership by
     * @return whether the Group with the given unique id directly or indirectly owns this Plot
     */
    public boolean isIndirectOwner(int id) {
        return getOwners().stream().map(Group::getUid).anyMatch(uid -> uid == id);
    }

    /**
     * Checks whether the Plot is directly or indirectly owned by the given {@link Group}.
     *
     * @param group the Group to check for indirect ownership by
     * @return whether the given Group directly or indirectly owns this Plot
     */
    public boolean isIndirectOwner(Group group) {
        return getOwners().contains(group);
    }

    /**
     * Gets a {@link Set} of all plot-type {@link Privilege}s the given {@link Player} is afforded by the {@link Group}
     * which owns this Plot.
     * <p>
     * If the Plot has no owner, null is returned.
     *
     * @param player the Player to check the privileges of
     * @return the plot-type Privileges the given Player has in this Plot
     */
    public Set<Privilege> getPrivileges(Player player) {
        Group owner = getOwner();
        if (owner == null) {
            return null;
        }
        return owner.getPrivileges(player).stream()
                .filter(privilege -> privilege.getTypes().contains(PrivilegeType.PLOT))
                .collect(Collectors.toSet());
    }

    /**
     * Checks whether the given {@link Player} has the given {@link Privilege} in this Plot.
     *
     * @param player    the Player to check privileges of
     * @param privilege the Privilege to check for
     * @return whether the given Player has the given Privilege in this Plot
     */
    public boolean can(Player player, Privilege privilege) {
        Group owner = getOwner();
        if (owner == null) {
            return privilege.getTypes().contains(PrivilegeType.PLOT);
        }
        return owner.getPrivileges(player).contains(privilege);
    }

    /**
     * Generates the next unused {@link Subplot} id for this Plot.
     *
     * @return the next available Subplot id
     */
    public synchronized int generateSubplotId() {
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
        obj.put("x", getBaseX());
        obj.put("z", getBaseZ());
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
