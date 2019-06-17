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
package pw.ollie.politics.group;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TIntHashSet;

import pw.ollie.politics.Politics;
import pw.ollie.politics.data.Storable;
import pw.ollie.politics.event.PoliticsEventFactory;
import pw.ollie.politics.event.group.GroupChildAddEvent;
import pw.ollie.politics.event.group.GroupChildRemoveEvent;
import pw.ollie.politics.event.group.GroupPropertySetEvent;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.level.Role;
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.universe.UniverseRules;
import pw.ollie.politics.util.serial.PropertyDeserializationException;
import pw.ollie.politics.util.serial.PropertySerializer;
import pw.ollie.politics.world.plot.Plot;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Represents a group of a certain {@link GroupLevel} in Politics which exists in a single {@link Universe}. A Group may
 * have child or parent Groups and may or may not have direct members. It may also own land in the form of
 * {@link Plot}s.
 */
public final class Group implements Comparable<Group>, Storable {
    // todo docs
    private final int uid;
    private final GroupLevel level;
    private final TIntObjectMap<Object> properties;
    private final Map<UUID, Role> players;
    private final Set<UUID> invitedPlayers;
    private final TIntSet invitedChildren;

    private Universe universe;

    public Group(int uid, GroupLevel level) {
        this(uid, level, new TIntObjectHashMap<>(), new THashMap<>());
    }

    private Group(int uid, GroupLevel level, TIntObjectMap<Object> properties, Map<UUID, Role> players) {
        this.uid = uid;
        this.level = level;
        this.properties = properties;
        this.players = players;
        this.invitedPlayers = new THashSet<>();
        this.invitedChildren = new TIntHashSet();
    }

    public void initialize(Universe universe) {
        if (universe == null || this.universe != null) {
            throw new IllegalStateException("Someone is trying to screw with the plugin!");
        }
        this.universe = universe;
    }

    public Universe getUniverse() {
        return universe;
    }

    public int getUid() {
        return uid;
    }

    public Set<Group> getGroups() {
        return universe.getChildGroups(this);
    }

    public boolean addChildGroup(Group group) {
        GroupChildAddEvent event = PoliticsEventFactory.callGroupChildAddEvent(this, group);
        if (event.isCancelled()) {
            return false;
        }

        return universe.addChildGroup(this, group);
    }

    public boolean removeChildGroup(Group group) {
        return removeChildGroup(group, Bukkit.getConsoleSender());
    }

    public boolean removeChildGroup(Group group, CommandSender source) {
        GroupChildRemoveEvent event = PoliticsEventFactory.callGroupChildRemoveEvent(this, group, source);
        if (event.isCancelled()) {
            return false;
        }

        return universe.removeChildGroup(this, group);
    }

    public boolean inviteChild(Group group) {
        return inviteChild(group, Bukkit.getConsoleSender());
    }

    public boolean inviteChild(Group group, CommandSender invitationSource) {
        if (group == null || group.getParent() != null || group.getLevel().getRank() >= level.getRank()) {
            return false;
        }

        PoliticsEventFactory.callGroupChildInviteEvent(this, group, invitationSource);
        invitedChildren.add(group.getUid());
        return true;
    }

    public boolean uninviteChild(Group group) {
        return group != null && invitedChildren.remove(group.getUid());
    }

    public boolean isInvitedChild(Group group) {
        return group != null && invitedChildren.contains(group.getUid());
    }

    public GroupLevel getLevel() {
        return level;
    }

    public Object getProperty(int property) {
        return properties.get(property);
    }

    public String getName() {
        return getStringProperty(GroupProperty.NAME);
    }

    public String getTag() {
        return getStringProperty(GroupProperty.TAG);
    }

    public String getStringProperty(int property) {
        return getStringProperty(property, null);
    }

    public String getStringProperty(int property, String def) {
        Object p = getProperty(property);
        if (p != null) {
            return p.toString();
        }
        return def;
    }

    public int getIntProperty(int property) {
        return getIntProperty(property, -1);
    }

    public int getIntProperty(int property, int def) {
        Object p = getProperty(property);
        if (p instanceof Number) {
            return ((Number) p).intValue();
        }
        return def;
    }

    public double getDoubleProperty(int property) {
        return getDoubleProperty(property, 0.0);
    }

    public double getDoubleProperty(int property, double def) {
        Object p = getProperty(property);
        if (p instanceof Number) {
            return ((Number) p).doubleValue();
        }
        return def;
    }

    public boolean getBooleanProperty(int property) {
        return getBooleanProperty(property, false);
    }

    public boolean getBooleanProperty(int property, boolean def) {
        Object p = getProperty(property);
        if (p instanceof Boolean) {
            return (Boolean) p;
        }
        return def;
    }

    public Location getLocationProperty(int property) {
        return getLocationProperty(property, null);
    }

    public Location getLocationProperty(int property, Location def) {
        String s = getStringProperty(property);
        if (s == null) {
            return def;
        }

        try {
            return PropertySerializer.deserializeLocation(s);
        } catch (PropertyDeserializationException ex) {
            Politics.getLogger().log(Level.WARNING, "Property '" + Integer.toHexString(property) + "' is not a transform!", ex);
            return def;
        }
    }

    public void setProperty(int property, Location value) {
        setProperty(property, PropertySerializer.serializeLocation(value));
    }

    public void setProperty(int property, Object value) {
        GroupPropertySetEvent event = PoliticsEventFactory.callGroupPropertySetEvent(this, property, value);
        properties.put(property, event.getValue());
    }

    public List<UUID> getImmediatePlayers() {
        return new ArrayList<>(players.keySet());
    }

    public List<Player> getImmediateOnlinePlayers() {
        List<Player> players = new ArrayList<>();
        for (UUID pn : getImmediatePlayers()) {
            Player player = Politics.getServer().getPlayer(pn);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    public List<UUID> getPlayers() {
        List<UUID> players = new ArrayList<>();
        for (Group group : getGroups()) {
            players.addAll(group.getPlayers());
        }
        players.addAll(this.players.keySet());
        return players;
    }

    public boolean isImmediateMember(UUID player) {
        return players.containsKey(player);
    }

    public boolean isMember(UUID player) {
        if (isImmediateMember(player)) {
            return true;
        }

        for (Group group : getGroups()) {
            if (group.isMember(player)) {
                return true;
            }
        }
        return false;
    }

    public void addInvitation(UUID player) {
        invitedPlayers.add(player);
    }

    public void removeInvitation(UUID player) {
        invitedPlayers.remove(player);
    }

    public boolean isInvited(UUID player) {
        return invitedPlayers.contains(player);
    }

    public boolean isInvited(Player player) {
        return isInvited(player.getUniqueId());
    }

    public Role getRole(UUID player) {
        return players.get(player);
    }

    public void setRole(UUID player, Role role) {
        players.put(player, role);
    }

    public void removeRole(UUID player) {
        players.remove(player);

        if (level.hasImmediateMembers() && players.isEmpty()) {
            universe.destroyGroup(this);
        }
    }

    public Set<Privilege> getPrivileges(UUID playerId) {
        Role role = getRole(playerId);
        if (role == null) {
            return new HashSet<>();
        }
        return role.getPrivileges();
    }

    public Set<Privilege> getPrivileges(Player player) {
        return getPrivileges(player.getUniqueId());
    }

    public boolean can(CommandSender source, Privilege privilege) {
        if (source instanceof Player) {
            Role role = getRole(((Player) source).getUniqueId());
            return role != null && role.hasPrivilege(privilege);
        }
        return true;
    }

    public Group getParent() {
        for (Group group : universe.getGroups()) {
            if (group.getGroups().contains(group)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public int compareTo(Group o) {
        return getProperty(GroupProperty.TAG).toString().compareTo(o.getProperty(GroupProperty.TAG).toString());
    }

    @Override
    public BasicBSONObject toBSONObject() {
        BasicBSONObject object = new BasicBSONObject();

        object.put("uid", uid);
        object.put("level", level.getId());

        BasicBSONObject propertiesBson = new BasicBSONObject();
        TIntObjectIterator<Object> pit = properties.iterator();
        while (pit.hasNext()) {
            pit.advance();
            propertiesBson.put(Integer.toHexString(pit.key()), pit.value());
        }
        object.put("properties", propertiesBson);

        BasicBSONObject playersBson = new BasicBSONObject();
        for (Map.Entry<UUID, Role> roleEntry : players.entrySet()) {
            playersBson.put(roleEntry.getKey().toString(), roleEntry.getValue().getId());
        }
        object.put("players", playersBson);

        return object;
    }

    public static Group fromBSONObject(UniverseRules rules, BSONObject object) {
        if (!(object instanceof BasicBSONObject)) {
            throw new IllegalStateException("object is not a BasicBsonObject! ERROR ERROR ERROR!");
        }

        BasicBSONObject bobject = (BasicBSONObject) object;

        int uid = bobject.getInt("uid");

        String levelName = bobject.getString("level");
        GroupLevel level = rules.getGroupLevel(levelName);
        if (level == null) {
            throw new IllegalStateException("Unknown level type '" + levelName + "'! (Did the universe rules change?)");
        }

        // Properties
        Object propertiesObj = bobject.get("properties");
        if (!(propertiesObj instanceof BasicBSONObject)) {
            throw new IllegalStateException("WTF you screwed up the properties! CORRUPT!");
        }

        BasicBSONObject propertiesBson = (BasicBSONObject) propertiesObj;
        TIntObjectMap<Object> properties = new TIntObjectHashMap<>();
        for (Map.Entry<String, Object> entry : propertiesBson.entrySet()) {
            int realKey = Integer.valueOf(entry.getKey(), 16);
            Object value = entry.getValue();
            properties.put(realKey, value);
        }

        // Players
        Object playersObj = bobject.get("players");
        if (!(playersObj instanceof BasicBSONObject)) {
            throw new IllegalStateException("Stupid server admin... don't mess with the data!");
        }

        BasicBSONObject playersBson = (BasicBSONObject) playersObj;
        Map<UUID, Role> players = new THashMap<>();
        for (Map.Entry<String, Object> entry : playersBson.entrySet()) {
            String roleId = entry.getValue().toString();
            Role role = level.getRole(roleId);
            players.put(UUID.fromString(entry.getKey()), role);
        }

        return new Group(uid, level, properties, players);
    }

    @Override
    public boolean canStore() {
        return true;
    }
}
