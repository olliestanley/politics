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
package pw.ollie.politics.group.level;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.Politics;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.util.collect.CollectionUtil;
import pw.ollie.politics.util.serial.ConfigUtil;
import pw.ollie.politics.util.stream.StreamUtil;

import com.google.mu.util.stream.BiStream;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A configured type of Group, with a name, rank, roles, role tracks and configuration settings.
 * <p>
 * Each Group in Politics must have (only) one GroupLevel.
 */
public final class GroupLevel {
    // todo docs
    private static final String DEFAULT_TRACK = "default";

    private final String id;
    private final String name;
    private final int rank;
    private final Map<String, Role> roles;
    private final String plural;
    private final Map<String, RoleTrack> tracks;
    private final Role initial;
    private final Role founder;
    private final boolean friendlyFire;
    private final boolean immediateMembers;
    private final boolean ownsLand;
    private final boolean allowedMultiple;
    private final boolean canWar;
    private final boolean mayBePeaceful;
    private final boolean canTax;
    private final Map<String, String> otherSettings;

    private Set<GroupLevel> allowedChildren;

    private GroupLevel(String id, String name, int rank, Map<String, Role> roles, String plural,
                       Map<String, RoleTrack> tracks, Role initial, Role founder, boolean friendlyFire,
                       boolean immediateMembers, boolean ownsLand, boolean allowedMultiple, boolean canWar,
                       boolean mayBePeaceful, boolean canTax, Map<String, String> otherSettings) {
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.roles = roles;
        this.plural = plural;
        this.tracks = tracks;
        this.initial = initial;
        this.founder = founder;
        this.friendlyFire = friendlyFire;
        this.immediateMembers = immediateMembers;
        this.ownsLand = ownsLand;
        this.allowedMultiple = allowedMultiple;
        this.canWar = canWar;
        this.mayBePeaceful = mayBePeaceful;
        this.canTax = canTax;
        this.otherSettings = otherSettings;
    }

    public Stream<GroupLevel> streamAllowedChildren() {
        return allowedChildren.stream();
    }

    public Stream<Role> streamRoles() {
        return roles.values().stream();
    }

    public void setAllowedChildren(Set<GroupLevel> allowedChildren) {
        this.allowedChildren = allowedChildren;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRank() {
        return rank;
    }

    public String getPlural() {
        return plural;
    }

    public boolean canBeChild(final GroupLevel level) {
        return allowedChildren.contains(level);
    }

    public Role getRole(String roleId) {
        return roles.get(roleId.toLowerCase());
    }

    public RoleTrack getTrack(String id) {
        return tracks.get(id.toLowerCase());
    }

    public RoleTrack getDefaultTrack() {
        return getTrack(DEFAULT_TRACK);
    }

    public Role getInitial() {
        return initial;
    }

    public Role getFounder() {
        return founder;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public boolean hasImmediateMembers() {
        return immediateMembers;
    }

    public boolean canOwnLand() {
        return ownsLand;
    }

    public boolean allowedMultiple() {
        return allowedMultiple;
    }

    public boolean canFound() {
        return founder != null;
    }

    public boolean canWar() {
        return canWar;
    }

    public boolean canTax() {
        return canTax;
    }

    public boolean canBePeaceful() {
        return mayBePeaceful;
    }

    public String getAdditionalSetting(String key) {
        return otherSettings.get(key);
    }

    public boolean contains(Group group) {
        return equals(group.getLevel());
    }

    public void save(ConfigurationSection node) {
        node.set("name", name);
        node.set("rank", rank);
        node.set("children", streamAllowedChildren().map(GroupLevel::getId).collect(Collectors.toList()));
        node.set("plural", plural);

        ConfigurationSection rolesNode = ConfigUtil.getOrCreateSection(node, "roles");
        BiStream.from(roles).forEach((roleName, role) -> {
            rolesNode.set(roleName + ".name", role.getName());
            rolesNode.set(roleName + ".privileges", role.streamPrivileges()
                    .map(Privilege::getName).collect(Collectors.toList()));
        });

        ConfigurationSection tracksNode = ConfigUtil.getOrCreateSection(node, "tracks");
        BiStream.from(tracks).mapValues(track -> track.streamRoles().map(Role::getName).collect(Collectors.toList()))
                .forEach(tracksNode::set);

        node.set("initial", initial.getId());
        node.set("founder", founder.getId());
        node.set("friendly-fire", friendlyFire);
        node.set("has-immediate-members", immediateMembers);
        node.set("can-own-land", ownsLand);
        node.set("allowed-multiple", allowedMultiple);
        node.set("can-war", canWar);
        node.set("may-be-peaceful", mayBePeaceful);

        BiStream.from(otherSettings).forEach(node::set);
    }

    public static GroupLevel load(String id, ConfigurationSection node, Map<GroupLevel, List<String>> levels) {
        String levelName = node.getString("name", id);
        String plural = node.getString("plural", levelName + "s");
        int rank = node.getInt("rank");

        // Load children
        List<String> children = node.getStringList("children");

        // Load roles
        Map<String, Role> rolesMap = new THashMap<>();
        ConfigurationSection rolesNode = node.getConfigurationSection("roles");
        if (rolesNode != null) {
            StreamUtil.biStream(rolesNode.getKeys(false).stream(), rolesNode::getConfigurationSection)
                    .filterValues(Objects::nonNull).mapValues(Role::load).forEach(rolesMap::put);
        }

        Map<String, RoleTrack> tracks = new THashMap<>();
        Role initial = null;
        Role founder = null;

        if (!rolesMap.isEmpty()) {
            ConfigurationSection tracksNode = node.getConfigurationSection("tracks");
            if (tracksNode != null) {
                StreamUtil.biStream(tracksNode.getKeys(false).stream(), tracksNode::getStringList)
                        .mapValues((trackKey, roleNames) -> RoleTrack.load(trackKey, roleNames, rolesMap))
                        .filterValues(Objects::nonNull).forEach(tracks::put);
            }

            tracks.putIfAbsent(DEFAULT_TRACK, tracks.isEmpty()
                    ? new RoleTrack(DEFAULT_TRACK, CollectionUtil.sorted(new LinkedList<>(rolesMap.values())))
                    : tracks.entrySet().iterator().next().getValue());

            // initial = starting role
            initial = rolesMap.get(node.getString("initial", "UNSPECIFIED ROLE NAME"));
            if (initial == null) {
                initial = rolesMap.values().stream().min(Comparator.comparing(Role::getRank)).get();
                Politics.getLogger().log(Level.WARNING, "No initial role specified in configuration, defaulting to lowest rank...");
            }

            // founder role
            founder = rolesMap.get(node.getString("founder", "UNSPECIFIED ROLE NAME"));
            if (founder == null) {
                founder = rolesMap.values().stream().max(Comparator.comparing(Role::getRank)).get();
                Politics.getLogger().log(Level.WARNING, "No founder role specified in configuration, defaulting to highest rank...");
            }
        }

        boolean friendlyFire = node.getBoolean("friendly-fire", false);
        boolean immediateMembers = node.getBoolean("has-immediate-members", true);
        boolean ownsLand = node.getBoolean("can-own-land", true);
        boolean allowedMultiple = node.getBoolean("allowed-multiple", false);
        boolean canWar = node.getBoolean("can-war", false);
        boolean mayBePeaceful = node.getBoolean("may-be-peaceful", true);
        boolean canTax = node.getBoolean("can-tax", false);

        Map<String, String> otherSettings = new THashMap<>();
        StreamUtil.biStream(node.getKeys(false).stream().filter(GroupLevel::notUsedKey), node::getString)
                .filterValues(Objects::nonNull).forEach(otherSettings::put);

        GroupLevel theLevel = new GroupLevel(id.toLowerCase(), levelName, rank, rolesMap, plural, tracks, initial,
                founder, friendlyFire, immediateMembers, ownsLand, allowedMultiple, canWar, mayBePeaceful, canTax,
                otherSettings);
        // Children so we can get our allowed children in the future
        levels.put(theLevel, children);
        return theLevel;
    }

    private static final Set<String> configKeys = new THashSet<>();

    static {
        configKeys.add("name");
        configKeys.add("plural");
        configKeys.add("rank");
        configKeys.add("children");
        configKeys.add("roles");
        configKeys.add("tracks");
        configKeys.add("initial");
        configKeys.add("founder");
        configKeys.add("friendly-fire");
        configKeys.add("has-immediate-members");
        configKeys.add("can-own-land");
        configKeys.add("allowed-multiple");
        configKeys.add("can-war");
        configKeys.add("may-be-peaceful");
        configKeys.add("can-tax");
    }

    private static boolean notUsedKey(String key) {
        return !configKeys.contains(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupLevel that = (GroupLevel) o;
        return id.equals(that.id) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
