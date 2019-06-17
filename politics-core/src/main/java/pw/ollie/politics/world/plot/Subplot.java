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

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.Politics;
import pw.ollie.politics.data.Storable;
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.event.plot.subplot.SubplotOwnerChangeEvent;
import pw.ollie.politics.event.plot.subplot.SubplotPrivilegeChangeEvent;
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.group.privilege.PrivilegeType;
import pw.ollie.politics.util.math.Cuboid;
import pw.ollie.politics.util.math.Position;
import pw.ollie.politics.util.math.Vector3i;
import pw.ollie.politics.util.stream.CollectorUtil;
import pw.ollie.politics.world.PoliticsWorld;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A subplot must be wholly contained within a single plot. Subplots do not have owner groups as normal plots, as they
 * are always within a plot owned by a group already. Rather, subplots have a single player assigned as their owner.
 * This player is afforded all possible plot privileges within the subplot, and may also assign plot privileges to other
 * players. This player must be a member of a group owning the parent plot, as must the other players who are given
 * privileges within the subplot.
 * <p>
 * A subplot may have different privilege settings to its parent plot. Subplot privileges override plot privileges.
 */
public final class Subplot implements Storable, ProtectedRegion {
    private final PoliticsWorld world;
    private final int id;
    private final int parentX;
    private final int parentZ;

    private final int baseX;
    private final int baseY;
    private final int baseZ;
    private final int xSize;
    private final int ySize;
    private final int zSize;

    private final Map<UUID, Set<Privilege>> individualPrivileges;

    private UUID owner;

    /**
     * Constructs a new Subplot object with the given properties.
     * <p>
     * This does not register the Subplot with the parent {@link Plot}.
     *
     * @param world   the world the Subplot is within
     * @param id      the id of the Subplot
     * @param parentX the chunk x coordinate of the parent Plot
     * @param parentZ the chunk z coordinate of the parent Plot
     * @param baseX   the base x coordinate of the Subplot
     * @param baseY   the base y coordinate of the Subplot
     * @param baseZ   the base z coordinate of the Subplot
     * @param xSize   the distance the Subplot boundary extends from the base x coordinate in the x-direction
     * @param ySize   the distance the Subplot boundary extends from the base y coordinate in the y-direction
     * @param zSize   the distance the Subplot boundary extends from the base z coordinate in the z-direction
     * @param owner   the unique id of the {@link Player} who owns the Subplot
     */
    public Subplot(PoliticsWorld world, int id, int parentX, int parentZ, int baseX, int baseY, int baseZ, int xSize, int ySize, int zSize, UUID owner) {
        this.world = world;
        this.id = id;
        this.parentX = parentX;
        this.parentZ = parentZ;
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseZ = baseZ;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.owner = owner;
        this.individualPrivileges = new THashMap<>();
    }

    /**
     * Constructs a new Subplot object with the given properties.
     * <p>
     * This does not register the Subplot with the parent {@link Plot}.
     *
     * @param world   the world the Subplot is within
     * @param id      the id of the Subplot
     * @param parentX the chunk x coordinate of the parent Plot
     * @param parentZ the chunk z coordinate of the parent Plot
     * @param cuboid  the physical area the Subplot occupies
     * @param owner   the unique id of the {@link Player} who owns the Subplot
     */
    public Subplot(PoliticsWorld world, int id, int parentX, int parentZ, Cuboid cuboid, UUID owner) {
        this(world, id, parentX, parentZ, cuboid.getMinX(), cuboid.getMinY(), cuboid.getMinZ(), cuboid.getXSize(), cuboid.getYSize(), cuboid.getZSize(), owner);
    }

    Subplot(BasicBSONObject bObj) {
        world = Politics.getWorld(bObj.getString("world"));
        id = bObj.getInt("id");
        parentX = bObj.getInt("parent-x");
        parentZ = bObj.getInt("parent-z");
        baseX = bObj.getInt("base-x");
        baseY = bObj.getInt("base-y");
        baseZ = bObj.getInt("base-z");
        xSize = bObj.getInt("x-size");
        ySize = bObj.getInt("y-size");
        zSize = bObj.getInt("z-size");
        owner = UUID.fromString(bObj.getString("owner"));

        individualPrivileges = new THashMap<>();
        if (bObj.containsField("privileges")) {
            BasicBSONObject privilegesObj = (BasicBSONObject) bObj.get("privileges");

            for (String privilegesKey : privilegesObj.keySet()) {
                Set<Privilege> privileges = new THashSet<>();
                individualPrivileges.put(UUID.fromString(privilegesKey), privileges);

                for (Object object : (BasicBSONList) privilegesObj.get(privilegesKey)) {
                    privileges.add(Politics.getPrivilegeManager().getPrivilege(object.toString()));
                }
            }
        }
    }

    /**
     * Gets the {@link Cuboid} representing the physical space occupied by this Subplot.
     *
     * @return the Cuboid occupied by this Subplot
     */
    @Override
    public Cuboid getCuboid() {
        return new Cuboid(getBaseLocation(), getSize());
    }

    /**
     * Gets the {@link PoliticsWorld} object for the world this Subplot is in.
     *
     * @return this Subplot's PoliticsWorld
     */
    public PoliticsWorld getWorld() {
        return world;
    }

    /**
     * Gets the id of this Subplot.
     * <p>
     * Note: Subplot ids are unique per-{@link Plot}, i.e Subplots in different Plots may have the same ids but multiple
     * Subplots within the same Plot may not.
     *
     * @return the id of this Subplot
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the chunk x coordinate of the parent {@link Plot}.
     *
     * @return the parent Plot's chunk x coordinate
     */
    public int getParentX() {
        return parentX;
    }

    /**
     * Gets the chunk z coordinate of the parent {@link Plot}.
     *
     * @return the parent Plot's chunk z coordinate
     */
    public int getParentZ() {
        return parentZ;
    }

    /**
     * Gets the {@link Plot} which this Subplot is a child of.
     *
     * @return the Subplot's parent Plot
     */
    public Plot getParent() {
        return world.getPlotAtChunkPosition(parentX, parentZ);
    }

    /**
     * Gets the {@link Chunk} which the parent {@link Plot} of this Subplot occupies.
     *
     * @return the parent Plot's Chunk
     */
    public Chunk getParentChunk() {
        return getParent().getChunk();
    }

    /**
     * Gets the base x coordinate of this Subplot.
     *
     * @return the base x coordinate for the Subplot
     */
    public int getBaseX() {
        return baseX;
    }

    /**
     * Gets the base y coordinate of this Subplot.
     *
     * @return the base y coordinate for the Subplot
     */
    public int getBaseY() {
        return baseY;
    }

    /**
     * Gets the base z coordinate of this Subplot.
     *
     * @return the base z coordinate for the Subplot
     */
    public int getBaseZ() {
        return baseZ;
    }

    /**
     * Gets the {@link Position} for the base point of this Subplot.
     *
     * @return the base Position of this Subplot
     */
    public Position getBasePosition() {
        return new Position(world.getName(), baseX, baseY, baseZ);
    }

    /**
     * Gets the {@link Location} for the base point of this Subplot.
     *
     * @return the base Location of this Subplot
     */
    public Location getBaseLocation() {
        return new Location(world.getWorld(), baseX, baseY, baseZ);
    }

    /**
     * Gets the distance this Subplot extends in the x-direction from the base x coordinate.
     *
     * @return the Subplot's size in the x direction
     */
    public int getXSize() {
        return xSize;
    }

    /**
     * Gets the distance this Subplot extends in the y-direction from the base y coordinate.
     *
     * @return the Subplot's size in the y direction
     */
    public int getYSize() {
        return ySize;
    }

    /**
     * Gets the distance this Subplot extends in the z-direction from the base z coordinate.
     *
     * @return the Subplot's size in the z direction
     */
    public int getZSize() {
        return zSize;
    }

    /**
     * Gets the {@link Vector3i} representing the size of this Subplot in each of the x, y and z directions.
     *
     * @return the vector size of this Subplot
     */
    public Vector3i getSize() {
        return new Vector3i(xSize, ySize, zSize);
    }

    /**
     * Gets the {@link Player} who owns this Subplot.
     *
     * @return the owner of this Subplot
     */
    public Player getOwner() {
        if (owner == null) {
            return null;
        }
        return Bukkit.getPlayer(owner);
    }

    /**
     * Gets the {@link UUID} of the {@link Player} who owns this Subplot.
     *
     * @return the unique id of the owner of this Subplot
     */
    public UUID getOwnerId() {
        return owner;
    }

    /**
     * Attempts to set the owner of this Subplot to the {@link Player} with the given {@link UUID}.
     * <p>
     * This method calls {@link SubplotOwnerChangeEvent} and will fail if this event is cancelled.
     *
     * @param ownerId the unique id of the player to make the owner
     * @return whether the owner was successfully changed
     */
    public boolean setOwner(UUID ownerId) {
        SubplotOwnerChangeEvent event = PoliticsEventFactory.callSubplotOwnerChangeEvent(getParent(), this, owner, ownerId);
        if (event.isCancelled()) {
            return false;
        }

        this.owner = ownerId;
        return true;
    }

    /**
     * Checks whether the Subplot contains the given {@link Location}.
     *
     * @param location the Location to check whether the Subplot contains
     * @return whether given Location is inside this Subplot
     */
    public boolean contains(Location location) {
        return getCuboid().contains(location);
    }

    /**
     * Checks whether the Subplot contains the given {@link Position}.
     *
     * @param position the Position to check whether the Subplot contains
     * @return whether given Position is inside this Subplot
     */
    public boolean contains(Position position) {
        return getCuboid().contains(position);
    }

    /**
     * Check whether the {@link Player} with the given {@link UUID} is afforded the given {@link Privilege} while inside
     * this Subplot.
     * <p>
     * If the Privilege does not have {@code PrivilegeType.PLOT} as one of its types, {@code false} is always returned.
     *
     * @param playerId  the unique id of the Player to check the permissions of
     * @param privilege the Privilege to check for presence of
     * @return whether the Player with the given unique id has the specified Privilege
     */
    public boolean can(UUID playerId, Privilege privilege) {
        if (!privilege.getTypes().contains(PrivilegeType.PLOT)) {
            return false;
        }

        if (playerId.equals(owner)) {
            return true;
        }

        Set<Privilege> privileges = individualPrivileges.get(playerId);
        return privileges != null && privileges.contains(privilege);
    }

    /**
     * Check whether the given {@link Player} is afforded the given {@link Privilege} while inside this Subplot.
     * <p>
     * If the Privilege does not have {@code PrivilegeType.PLOT} as one of its types, {@code false} is always returned.
     *
     * @param player    the Player to check the permissions of
     * @param privilege the Privilege to check for presence of
     * @return whether the given Player has the specified Privilege
     */
    public boolean can(Player player, Privilege privilege) {
        return can(player.getUniqueId(), privilege);
    }

    /**
     * Attempts to grant the specified {@link Privilege} to the {@link Player} with the given {@link UUID}.
     * <p>
     * This method calls {@link SubplotPrivilegeChangeEvent} and will fail if this event is cancelled.
     *
     * @param playerId  the unique id of the Player to grant the Privilege to
     * @param privilege the Privilege to grant
     * @return whether the Player's privileges were successfully updated
     */
    public boolean givePrivilege(UUID playerId, Privilege privilege) {
        if (!privilege.getTypes().contains(PrivilegeType.PLOT)) {
            return false;
        }

        SubplotPrivilegeChangeEvent event = PoliticsEventFactory.callSubplotPrivilegeChangeEvent(getParent(), this, playerId, privilege, true);
        if (event.isCancelled()) {
            return false;
        }

        individualPrivileges.putIfAbsent(playerId, new THashSet<>());
        individualPrivileges.get(playerId).add(privilege);
        return true;
    }

    /**
     * Attempts to grant the specified {@link Privilege} to the given {@link Player}.
     * <p>
     * This method calls {@link SubplotPrivilegeChangeEvent} and will fail if this event is cancelled.
     *
     * @param player    the Player to grant the Privilege to
     * @param privilege the Privilege to grant
     * @return whether the Player's privileges were successfully updated
     */
    public boolean givePrivilege(Player player, Privilege privilege) {
        return givePrivilege(player.getUniqueId(), privilege);
    }

    /**
     * Attempts to revoke the specified {@link Privilege} from the {@link Player} with the given {@link UUID}.
     * <p>
     * This method calls {@link SubplotPrivilegeChangeEvent} and will fail if this event is cancelled.
     *
     * @param playerId  the unique id of the Player to revoke the Privilege from
     * @param privilege the Privilege to revoke
     * @return whether the Player's privileges were successfully updated
     */
    public boolean revokePrivilege(UUID playerId, Privilege privilege) {
        if (!privilege.getTypes().contains(PrivilegeType.PLOT)) {
            return false;
        }

        SubplotPrivilegeChangeEvent event = PoliticsEventFactory.callSubplotPrivilegeChangeEvent(getParent(), this, playerId, privilege, false);
        if (event.isCancelled()) {
            return false;
        }

        Set<Privilege> currentPrivileges = individualPrivileges.get(playerId);
        if (currentPrivileges == null) {
            return false;
        }

        return currentPrivileges.remove(privilege);
    }

    /**
     * Attempts to revoke the specified {@link Privilege} from the given {@link Player}.
     * <p>
     * This method calls {@link SubplotPrivilegeChangeEvent} and will fail if this event is cancelled.
     *
     * @param player    the Player to revoke the Privilege from
     * @param privilege the Privilege to revoke
     * @return whether the Player's privileges were successfully updated
     */
    public boolean revokePrivilege(Player player, Privilege privilege) {
        return revokePrivilege(player.getUniqueId(), privilege);
    }

    @Override
    public BSONObject toBSONObject() {
        BSONObject result = new BasicBSONObject();
        result.put("world", world.getName());
        result.put("id", id);
        result.put("parent-x", parentX);
        result.put("parent-z", parentZ);
        result.put("base-x", baseX);
        result.put("base-y", baseY);
        result.put("base-z", baseZ);
        result.put("x-size", xSize);
        result.put("y-size", ySize);
        result.put("z-size", zSize);
        result.put("owner", owner.toString());

        BasicBSONObject privilegesObj = new BasicBSONObject();
        for (UUID individualId : individualPrivileges.keySet()) {
            BasicBSONList privilegeList = new BasicBSONList();
            Set<Privilege> privileges = individualPrivileges.get(individualId);
            privilegeList.addAll(privileges.stream().map(Privilege::getName).collect(CollectorUtil.toMutableSet()));
            privilegesObj.put(individualId.toString(), privilegeList);
        }
        result.put("privileges", privilegesObj);

        return result;
    }

    @Override
    public boolean canStore() {
        return true;
    }
}
