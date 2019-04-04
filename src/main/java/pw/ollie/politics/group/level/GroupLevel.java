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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    // TODO: saving and loading of group level configuration. Below code is Spout Engine and needs significant changes
//    public void save(final ConfigurationNode node) {
//        node.getChild("name").setValue(name);
//        node.getChild("rank").setValue(rank);
//        List<String> children = new ArrayList<>();
//        for (GroupLevel child : getAllowedChildren()) {
//            children.add(child.getId());
//        }
//        node.getChild("children").setValue(children);
//        node.getChild("plural").setValue(plural);
//
//        ConfigurationNode rolesNode = node.getChild("roles");
//        for (Entry<String, Role> role : roles.entrySet()) {
//            String roleName = role.getKey();
//            Role value = role.getValue();
//            List<String> privNames = new ArrayList<>();
//            for (Privilege priv : value.getPrivileges()) {
//                privNames.add(priv.getName());
//            }
//
//            rolesNode.getChild(roleName).setValue(privNames);
//        }
//
//        // TODO track serialization
//
//        node.getChild("initial").setValue(initial.getId());
//        node.getChild("founder").setValue(founder.getId());
//    }
//
//    public static GroupLevel load(String id, final ConfigurationNode node, final Map<GroupLevel, List<String>> levels) {
//        // Load name
//        String levelName = node.getNode("name").getString(id);
//
//        // Make id lowercase
//        id = id.toLowerCase();
//
//        // Load rank
//        int rank = node.getNode("rank").getInt();
//
//        // Load children
//        List<String> children = node.getNode("children").getStringList();
//
//        // Load roles
//        Map<String, Role> rolesMap = new HashMap<String, Role>();
//        for (Entry<String, ConfigurationNode> roleEntry : node.getNode("roles").getChildren().entrySet()) {
//            String roleId = roleEntry.getKey();
//            Role role = Role.load(roleId, roleEntry.getValue());
//            rolesMap.put(roleId, role);
//        }
//
//        // Load plural
//        String plural = node.getNode("plural").getString(levelName + "s");
//
//        // Load allowed commands
//        Map<String, List<String>> commands = new HashMap<String, List<String>>();
//
//        // Set for checking for alias overlaps.
//        Set<String> alreadyLoadedCommands = new HashSet<>();
//
//        // Command node
//        ConfigurationNode commandNode = node.getNode("commands");
//        for (Entry<String, ConfigurationNode> commandAliasEntry : commandNode.getChildren().entrySet()) {
//            // Name of the command we want to alias
//            String commandName = commandAliasEntry.getKey().toLowerCase();
//
//            // Get the list we're putting aliases in
//            List<String> theAliases = commands.get(commandName);
//            if (theAliases == null) {
//                theAliases = new ArrayList<>();
//                commands.put(commandName, theAliases);
//            }
//
//            ConfigurationNode aliasesNode = commandAliasEntry.getValue();
//
//            // Check for list, if so add specified aliases. Does not
//            // include the normal name unless explicitly specified.
//            if (aliasesNode.getValue() instanceof List) {
//                List<String> aliases = aliasesNode.getStringList();
//                for (String alias : aliases) {
//                    alias = alias.toLowerCase();
//                    if (alreadyLoadedCommands.contains(alias)) {
//                        Politics.instance().getLogger().log(Level.WARNING,
//                                "Duplicate entry for command `" + alias + "'; not adding it to aliases for " + commandName + ".");
//                        continue;
//                    }
//                    theAliases.add(alias);
//                    alreadyLoadedCommands.add(alias);
//                }
//
//                // Else, we don't care, they specified it.
//            } else {
//                if (alreadyLoadedCommands.contains(commandName)) {
//                    Politics.instance().getLogger().log(Level.WARNING, "Duplicate entry for command `" + commandName + "'; not adding " + commandName + ".");
//                    continue;
//                }
//                theAliases.add(commandName);
//                alreadyLoadedCommands.add(commandName);
//            }
//        }
//
//        // Our variables
//        Map<String, RoleTrack> tracks = new HashMap<String, RoleTrack>();
//        Role initial;
//        Role founder;
//
//        if (rolesMap.isEmpty()) {
//            initial = null;
//            founder = null;
//        } else {
//            ConfigurationNode tracksNode = node.getChild("tracks");
//            for (Entry<String, ConfigurationNode> trackEntry : tracksNode.getChildren().entrySet()) {
//                RoleTrack track = RoleTrack.load(trackEntry.getKey(), trackEntry.getValue(), rolesMap);
//                tracks.put(track.getId(), track);
//            }
//            if (!tracks.containsKey(DEFAULT_TRACK)) {
//                RoleTrack def;
//                if (tracks.isEmpty()) {
//                    final List<Role> rolesSorted = new LinkedList<Role>(rolesMap.values());
//                    Collections.sort(rolesSorted);
//                    def = new RoleTrack(DEFAULT_TRACK, rolesSorted);
//                } else {
//                    def = tracks.entrySet().iterator().next().getValue();
//                }
//                tracks.put(DEFAULT_TRACK, def);
//            }
//
//            String initialName = node.getChild("initial").getString();
//            if (initialName == null) {
//                int lowest = Integer.MAX_VALUE;
//                Role lowestRole = null;
//                for (Role role : rolesMap.values()) {
//                    if (role.getRank() <= lowest) { // Incase of max value for
//                        // rank
//                        lowest = role.getRank();
//                        lowestRole = role;
//                    }
//                }
//                initial = lowestRole;
//            } else {
//                initial = rolesMap.get(initialName.toLowerCase());
//                if (initial == null) {
//                    throw new IllegalStateException("Invalid initial role '" + initialName + "'.");
//                }
//            }
//
//            String founderName = node.getChild("founder").getString();
//            if (founderName == null) {
//                int highest = 0;
//                Role highestRole = null;
//                for (final Role role : rolesMap.values()) {
//                    if (role.getRank() > highest) {
//                        highest = role.getRank();
//                        highestRole = role;
//                    }
//                }
//                founder = highestRole;
//            } else {
//                founder = rolesMap.get(founderName.toLowerCase());
//                if (founder == null) {
//                    throw new IllegalStateException("Invalid founder role '" + founderName + "'.");
//                }
//            }
//        }
//
//        GroupLevel theLevel = new GroupLevel(id, levelName, rank, rolesMap, plural, commands, tracks, initial, founder);
//        // Children so we can get our allowed children in the future
//        levels.put(theLevel, children);
//        return theLevel;
//    }
}
