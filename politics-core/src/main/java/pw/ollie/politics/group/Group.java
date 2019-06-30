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
import pw.ollie.politics.util.stream.CollectorUtil;
import pw.ollie.politics.util.stream.StreamUtil;
import pw.ollie.politics.world.plot.Plot;

import com.google.mu.util.stream.BiStream;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Stream;

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

    public Stream<Group> streamChildren() {
        return universe.streamChildGroups(this);
    }

    public Stream<UUID> streamImmediatePlayers() {
        return players.keySet().stream();
    }

    public Stream<Player> streamImmediateOnlinePlayers() {
        return streamImmediatePlayers().map(Politics.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public Stream<UUID> streamPlayers() {
        return Stream.concat(streamImmediatePlayers(), streamChildren().flatMap(Group::streamImmediatePlayers))
                .distinct();
    }

    public Stream<Privilege> streamPrivileges(UUID playerId) {
        return getRole(playerId).map(Role::streamPrivileges).orElseGet(Stream::empty);
    }

    public Universe getUniverse() {
        return universe;
    }

    public int getUid() {
        return uid;
    }

    public GroupLevel getLevel() {
        return level;
    }

    public Optional<Group> getParent() {
        return universe.streamGroups().filter(this::isParent).findAny();
    }

    public boolean isParent(Group other) {
        return other.hasChild(this);
    }

    public boolean hasChild(Group other) {
        return universe.streamChildGroups(other).anyMatch(this::equals);
    }

    public boolean addChild(Group group) {
        GroupChildAddEvent event = PoliticsEventFactory.callGroupChildAddEvent(this, group);
        if (event.isCancelled()) {
            return false;
        }

        return universe.addChildGroup(this, group);
    }

    public boolean removeChild(Group group) {
        return removeChild(group, Bukkit.getConsoleSender());
    }

    public boolean removeChild(Group group, CommandSender source) {
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
        if (group == null || group.getParent().isPresent() || group.getLevel().getRank() >= level.getRank()) {
            return false;
        }

        PoliticsEventFactory.callGroupChildInviteEvent(this, group, invitationSource);
        invitedChildren.add(group.getUid());
        return true;
    }

    public boolean disinviteChild(Group group) {
        return group != null && invitedChildren.remove(group.getUid());
    }

    public boolean isInvitedChild(Group group) {
        return group != null && invitedChildren.contains(group.getUid());
    }

    public boolean isImmediateMember(UUID player) {
        return players.containsKey(player);
    }

    public boolean isMember(UUID player) {
        return isImmediateMember(player) || streamChildren().anyMatch(child -> child.isMember(player));
    }

    public int getNumPlayers() {
        return (int) streamPlayers().count();
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

    public Optional<Role> getRole(UUID player) {
        return Optional.ofNullable(players.get(player));
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

    public boolean can(CommandSender source, Privilege privilege) {
        if (source instanceof Player) {
            return getRole(((Player) source).getUniqueId()).map(role -> role.can(privilege)).orElse(false);
        }
        return true;
    }

    public Optional<Object> getProperty(int property) {
        return Optional.ofNullable(properties.get(property));
    }

    public String getName() {
        return getStringProperty(GroupProperty.NAME).get();
    }

    public String getTag() {
        return getStringProperty(GroupProperty.TAG).get();
    }

    public boolean hasProperty(int property) {
        return properties.containsKey(property);
    }

    public Optional<String> getStringProperty(int property) {
        Optional<Object> p = getProperty(property);
        if (p.isPresent()) {
            return p.map(String.class::cast);
        }
        return Optional.empty();
    }

    public String getStringProperty(int property, String def) {
        Optional<Object> p = getProperty(property);
        if (p.isPresent()) {
            return p.get().toString();
        }
        return def;
    }

    public OptionalInt getIntProperty(int property) {
        Object obj = getProperty(property).orElse(null);
        if (obj instanceof Number) {
            return OptionalInt.of(((Number) obj).intValue());
        }
        return OptionalInt.empty();
    }

    public int getIntProperty(int property, int def) {
        Object obj = getProperty(property).orElse(null);
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return def;
    }

    public OptionalDouble getDoubleProperty(int property) {
        Object obj = getProperty(property).orElse(null);
        if (obj instanceof Number) {
            return OptionalDouble.of(((Number) obj).doubleValue());
        }
        return OptionalDouble.empty();
    }

    public double getDoubleProperty(int property, double def) {
        Object p = getProperty(property).orElse(null);
        if (p instanceof Number) {
            return ((Number) p).doubleValue();
        }
        return def;
    }

    public Optional<Boolean> getBooleanProperty(int property) {
        Optional<Object> obj = getProperty(property);
        if (obj.isPresent() && obj.get() instanceof Boolean) {
            return Optional.of((Boolean) obj.get());
        }
        return Optional.empty();
    }

    public boolean getBooleanProperty(int property, boolean def) {
        Object p = getProperty(property).orElse(null);
        if (p instanceof Boolean) {
            return (Boolean) p;
        }
        return def;
    }

    public Location getLocationProperty(int property) {
        return getLocationProperty(property, null);
    }

    public Location getLocationProperty(int property, Location def) {
        Optional<String> s = getStringProperty(property);
        if (!s.isPresent()) {
            return def;
        }

        try {
            return PropertySerializer.deserializeLocation(s.get());
        } catch (PropertyDeserializationException ex) {
            Politics.getLogger().log(Level.WARNING, "Property '" + Integer.toHexString(property) + "' is not a Location!", ex);
            return def;
        }
    }

    public void setProperty(int property, Location value) {
        setProperty(property, PropertySerializer.serializeLocation(value));
    }

    public void setProperty(int property, Object value) {
        if (GroupProperty.isKeyProperty(property) && value == null) {
            return; // exception??
        }

        GroupPropertySetEvent event = PoliticsEventFactory.callGroupPropertySetEvent(this, property, value);
        properties.put(property, event.getValue());
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
        object.put("players", BiStream.from(players).mapKeys(UUID::toString).mapValues(Role::getId)
                .collect(CollectorUtil.toBasicBSONObject()));
        return object;
    }

    public static Group fromBSONObject(UniverseRules rules, BSONObject object) {
        if (!(object instanceof BasicBSONObject)) {
            throw new IllegalStateException("object is not a BasicBsonObject! ERROR ERROR ERROR!");
        }

        BasicBSONObject bobject = (BasicBSONObject) object;

        int uid = bobject.getInt("uid");

        String levelName = bobject.getString("level");
        Optional<GroupLevel> levelLookup = rules.getGroupLevel(levelName);
        if (!levelLookup.isPresent()) {
            throw new IllegalStateException("Unknown level type '" + levelName + "'! (Did the universe rules change?)");
        }

        GroupLevel level = levelLookup.get();
        // Properties
        Object propertiesObj = bobject.get("properties");
        if (!(propertiesObj instanceof BasicBSONObject)) {
            throw new IllegalStateException("WTF you screwed up the properties! CORRUPT!");
        }

        TIntObjectMap<Object> properties = BiStream.from((BasicBSONObject) propertiesObj)
                .mapKeys(key -> Integer.valueOf(key, 16)).collect(CollectorUtil.toIntObjectHashMap());

        // Players
        Object playersObj = bobject.get("players");
        if (!(playersObj instanceof BasicBSONObject)) {
            throw new IllegalStateException("Stupid server admin... don't mess with the data!");
        }

        return new Group(uid, level, properties, new THashMap<>(BiStream.from((BasicBSONObject) playersObj)
                .mapKeys(UUID::fromString).mapValues(Object::toString).mapValues(level::getRole).mapValues(Optional::get)
                .toMap()));
    }

    @Override
    public boolean shouldStore() {
        return true;
    }

    public void initialize(Universe universe) {
        if (universe == null || this.universe != null) {
            throw new IllegalStateException("attempt to initialize a group twice!");
        }
        this.universe = universe;
    }
}
