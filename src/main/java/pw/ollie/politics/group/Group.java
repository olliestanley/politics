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
import gnu.trove.map.hash.TIntObjectHashMap;

import pw.ollie.politics.Politics;
import pw.ollie.politics.data.Storable;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.group.level.Role;
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.universe.Universe;
import pw.ollie.politics.universe.UniverseRules;
import pw.ollie.politics.util.Position;
import pw.ollie.politics.util.math.Transform;
import pw.ollie.politics.util.serial.PropertyDeserializationException;
import pw.ollie.politics.util.serial.PropertySerializationException;
import pw.ollie.politics.util.serial.PropertySerializer;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public final class Group implements Comparable<Group>, Storable {
    private final int uid;
    private final GroupLevel level;
    private final TIntObjectMap<Object> properties;
    private final Map<String, Role> players;

    private Universe universe;

    public Group(int uid, GroupLevel level) {
        this(uid, level, new TIntObjectHashMap<>(), new HashMap<>());
    }

    private Group(int uid, GroupLevel level, TIntObjectMap<Object> properties, Map<String, Role> players) {
        this.uid = uid;
        this.level = level;
        this.properties = properties;
        this.players = players;
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
        return universe.addChildGroup(this, group);
    }

    public boolean removeChildGroup(Group group) {
        return universe.removeChildGroup(this, group);
    }

    public GroupLevel getLevel() {
        return level;
    }

    public Object getProperty(int property) {
        return properties.get(property);
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
        if (p != null) {
            if (p instanceof Integer) {
                return (Integer) p;
            } else {
                return def;
            }
        }
        return def;
    }

    public Transform getTransformProperty(int property) {
        return getTransformProperty(property, null);
    }

    public Transform getTransformProperty(int property, Transform def) {
        String s = getStringProperty(property);
        if (s == null) {
            return def;
        }

        try {
            return PropertySerializer.deserializeTransform(s);
        } catch (PropertyDeserializationException ex) {
            Politics.getLogger().log(Level.WARNING, "Property '" + Integer.toHexString(property) + "' is not a transform!", ex);
            return def;
        }
    }

    public void setProperty(int property, Transform value) {
        try {
            setProperty(property, PropertySerializer.serializeTransform(value));
        } catch (PropertySerializationException e) {
            Politics.getLogger().log(Level.SEVERE, "Error serializing property!", e);
        }
    }

    public Position getPositionProperty(int property) {
        return getPositionProperty(property, null);
    }

    public Position getPositionProperty(int property, Position def) {
        String s = getStringProperty(property);
        if (s == null) {
            return def;
        }

        try {
            return PropertySerializer.deserializePosition(s);
        } catch (PropertyDeserializationException ex) {
            Politics.getLogger().log(Level.WARNING, "Property '" + Integer.toHexString(property) + "' is not a point!", ex);
            return def;
        }
    }


    public void setProperty(int property, Position value) {
        try {
            setProperty(property, PropertySerializer.serializePosition(value));
        } catch (PropertySerializationException e) {
            Politics.getLogger().log(Level.SEVERE, "Error serializing property!", e);
        }
    }

    public void setProperty(int property, Object value) {
        Politics.getEventFactory().callGroupPropertySetEvent(this, property, value);
        properties.put(property, value);
    }

    public List<String> getImmediatePlayers() {
        return new ArrayList<>(players.keySet());
    }

    public List<Player> getImmediateOnlinePlayers() {
        List<Player> players = new ArrayList<>();
        for (String pn : getImmediatePlayers()) {
            Player player = Politics.getServer().getPlayer(pn);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    public List<String> getPlayers() {
        List<String> players = new ArrayList<>();
        for (Group group : getGroups()) {
            players.addAll(group.getPlayers());
        }
        players.addAll(this.players.keySet());
        return players;
    }

    public boolean isImmediateMember(String player) {
        return players.containsKey(player);
    }

    public boolean isMember(String player) {
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

    public Role getRole(String player) {
        return players.get(player);
    }

    public void setRole(String player, Role role) {
        players.put(player, role);
    }

    public void removeRole(String player) {
        players.remove(player);
    }

    public boolean can(CommandSender source, Privilege privilege) {
        if (source instanceof Player) {
            Role role = getRole(source.getName());
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
        for (Map.Entry<String, Role> roleEntry : players.entrySet()) {
            playersBson.put(roleEntry.getKey(), roleEntry.getValue().getId());
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
            throw new IllegalStateException("Unknown level type '" + level + "'! (Did the universe rules change?)");
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
        Map<String, Role> players = new HashMap<>();
        for (Map.Entry<String, Object> entry : playersBson.entrySet()) {
            String roleId = entry.getValue().toString();
            Role role = level.getRole(roleId);
            players.put(entry.getKey(), role);
        }

        return new Group(uid, level, properties, players);
    }

    @Override
    public boolean canStore() {
        return true;
    }
}
