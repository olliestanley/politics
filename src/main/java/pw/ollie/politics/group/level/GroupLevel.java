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

import pw.ollie.politics.Politics;
import pw.ollie.politics.group.privilege.Privilege;
import pw.ollie.politics.util.ConfigUtil;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public final class GroupLevel {
    private static final String DEFAULT_TRACK = "default";

    private final String id;
    private final String name;
    private final int rank;
    private final Map<String, Role> roles;
    private final String plural;
    private final Map<String, List<String>> commands;
    private final Map<String, RoleTrack> tracks;
    private final Role initial;
    private final Role founder;

    private Set<GroupLevel> allowedChildren;

    public GroupLevel(String id, String name, int rank, Map<String, Role> roles, String plural,
                      Map<String, List<String>> commands, Map<String, RoleTrack> tracks, Role initial, Role founder) {
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.roles = roles;
        this.plural = plural;
        this.commands = commands;
        this.tracks = tracks;
        this.initial = initial;
        this.founder = founder;
    }

    public void setAllowedChildren(Set<GroupLevel> set) {
        allowedChildren = set;
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

    public List<String> getAliases(String command) {
        return new ArrayList<>(commands.get(command.toLowerCase()));
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

    public boolean canFound() {
        return founder != null;
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
    }

    public static GroupLevel load(String id, ConfigurationSection node, Map<GroupLevel, List<String>> levels) {
        String levelName = node.getString("name", id);

        id = id.toLowerCase();

        int rank = node.getInt("rank");

        // Load children
        List<String> children = node.getStringList("children");

        // Load roles
        Map<String, Role> rolesMap = new HashMap<>();
        ConfigurationSection rolesNode = node.getConfigurationSection("roles");
        if (rolesNode != null) {
            for (String roleId : rolesNode.getKeys(false)) {
                ConfigurationSection roleNode = rolesNode.getConfigurationSection(roleId);
                Role role = Role.load(roleId, roleNode);
                rolesMap.put(roleId, role);
            }
        }

        String plural = node.getString("plural", levelName + "s");

        Map<String, List<String>> commands = new HashMap<>();
        // Set for checking for alias overlaps.
        Set<String> alreadyLoadedCommands = new HashSet<>();
        ConfigurationSection commandsNode = node.getConfigurationSection("commands");
        for (String commandId : commandsNode.getKeys(false)) {
            String commandName = commandId.toLowerCase();

            // Get the list we're putting aliases in
            List<String> theAliases = commands.computeIfAbsent(commandName, k -> new ArrayList<>());
            List<String> commandAliases = commandsNode.getStringList(commandId);
            if (commandAliases.size() > 0) {
                for (String alias : commandAliases) {
                    alias = alias.toLowerCase();
                    if (alreadyLoadedCommands.contains(alias)) {
                        Politics.getLogger().log(Level.WARNING,
                                "Duplicate entry for command `" + alias + "'; not adding it to aliases for " + commandName + ".");
                        continue;
                    }

                    theAliases.add(alias);
                    alreadyLoadedCommands.add(alias);
                }
            }
        }

        Map<String, RoleTrack> tracks = new HashMap<>();
        Role initial = null;
        Role founder = null;

        if (!rolesMap.isEmpty()) {
            ConfigurationSection tracksNode = node.getConfigurationSection("tracks");
            for (String trackKey : tracksNode.getKeys(false)) {
                List<String> rolesNames = tracksNode.getStringList(trackKey);
                RoleTrack track = RoleTrack.load(trackKey, rolesNames, rolesMap);
                tracks.put(track.getId(), track);
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
                initial = rolesMap.get(initialName.toLowerCase());
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
                founder = rolesMap.get(founderName.toLowerCase());
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

        GroupLevel theLevel = new GroupLevel(id, levelName, rank, rolesMap, plural, commands, tracks, initial, founder);
        // Children so we can get our allowed children in the future
        levels.put(theLevel, children);
        return theLevel;
    }
}
