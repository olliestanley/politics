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
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.util.serial.ConfigUtil;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

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

    public Set<GroupLevel> getAllowedChildren() {
        return new HashSet<>(allowedChildren);
    }

    public boolean canBeChild(final GroupLevel level) {
        return allowedChildren.contains(level);
    }

    public Map<String, Role> getRoles() {
        return new HashMap<>(roles);
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

    public void save(ConfigurationSection node) {
        node.set("name", name);
        node.set("rank", rank);
        List<String> children = new ArrayList<>();
        for (GroupLevel child : getAllowedChildren()) {
            children.add(child.getId());
        }
        node.set("children", children);
        node.set("plural", plural);

        ConfigurationSection rolesNode = ConfigUtil.getOrCreateSection(node, "roles");
        for (Map.Entry<String, Role> role : roles.entrySet()) {
            String roleName = role.getKey();
            Role value = role.getValue();
            List<String> privNames = new ArrayList<>();
            for (Privilege priv : value.getPrivileges()) {
                privNames.add(priv.getName());
            }

            rolesNode.set(roleName + ".name", value.getName());
            rolesNode.set(roleName + ".privileges", privNames);
        }

        ConfigurationSection tracksNode = ConfigUtil.getOrCreateSection(node, "tracks");
        for (Map.Entry<String, RoleTrack> trackEntry : tracks.entrySet()) {
            List<String> roleNames = new LinkedList<>();
            for (Role role : trackEntry.getValue().getRoles()) {
                roleNames.add(role.getName());
            }

            tracksNode.set(trackEntry.getKey(), roleNames);
        }

        node.set("initial", initial.getId());
        node.set("founder", founder.getId());
        node.set("friendly-fire", friendlyFire);
        node.set("has-immediate-members", immediateMembers);
        node.set("can-own-land", ownsLand);
        node.set("allowed-multiple", allowedMultiple);
        node.set("can-war", canWar);
        node.set("may-be-peaceful", mayBePeaceful);

        for (Map.Entry<String, String> setting : otherSettings.entrySet()) {
            node.set(setting.getKey(), setting.getValue());
        }
    }

    public static GroupLevel load(String id, ConfigurationSection node, Map<GroupLevel, List<String>> levels) {
        String levelName = node.getString("name", id);
        String plural = node.getString("plural", levelName + "s");
        int rank = node.getInt("rank");

        // Load children
        List<String> children = node.getStringList("children");

        // Load roles
        Map<String, Role> rolesMap = new HashMap<>();
        ConfigurationSection rolesNode = node.getConfigurationSection("roles");
        if (rolesNode != null) {
            for (String roleId : rolesNode.getKeys(false)) {
                ConfigurationSection roleNode = rolesNode.getConfigurationSection(roleId);
                if (roleNode != null) {
                    Role role = Role.load(roleId, roleNode);
                    rolesMap.put(roleId, role);
                }
            }
        }

        Map<String, RoleTrack> tracks = new HashMap<>();
        Role initial = null;
        Role founder = null;

        if (!rolesMap.isEmpty()) {
            ConfigurationSection tracksNode = node.getConfigurationSection("tracks");
            if (tracksNode != null) {
                for (String trackKey : tracksNode.getKeys(false)) {
                    List<String> rolesNames = tracksNode.getStringList(trackKey);
                    RoleTrack track = RoleTrack.load(trackKey, rolesNames, rolesMap);
                    tracks.put(track.getId(), track);
                }
            }

            if (!tracks.containsKey(DEFAULT_TRACK)) {
                RoleTrack def;
                if (tracks.isEmpty()) {
                    List<Role> rolesSorted = new LinkedList<>(rolesMap.values());
                    Collections.sort(rolesSorted);
                    def = new RoleTrack(DEFAULT_TRACK, rolesSorted);
                } else {
                    def = tracks.entrySet().iterator().next().getValue();
                }

                tracks.put(DEFAULT_TRACK, def);
            }

            // initial = starting role
            String initialName = node.getString("initial");
            if (initialName != null) {
                initial = rolesMap.get(initialName);
            }
            if (initial == null) {
                int lowest = Integer.MAX_VALUE;
                Role lowestRole = null;
                for (Role role : rolesMap.values()) {
                    if (role.getRank() <= lowest) {
                        lowest = role.getRank();
                        lowestRole = role;
                    }
                }
                initial = lowestRole;
                Politics.getLogger().log(Level.WARNING, "No initial role specified in configuration, defaulting to lowest rank...");
            }

            // founder role
            String founderName = node.getString("founder");
            if (founderName != null) {
                founder = rolesMap.get(founderName);
            }

            if (founderName == null) {
                int highest = 0;
                Role highestRole = null;
                for (Role role : rolesMap.values()) {
                    if (role.getRank() > highest) {
                        highest = role.getRank();
                        highestRole = role;
                    }
                }

                founder = highestRole;
                Politics.getLogger().log(Level.WARNING, "No initial role specified in configuration, defaulting to lowest rank...");
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
        node.getKeys(false).stream().filter(GroupLevel::notUsedKey).forEach(key -> {
            String value = node.getString(key);
            if (value != null) {
                otherSettings.put(key, value);
            }
        });

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
        return id.equals(that.id) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
