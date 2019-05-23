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
package pw.ollie.politics.universe;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import pw.ollie.politics.Politics;
import pw.ollie.politics.data.Storable;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.world.PoliticsWorld;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * A Universe is a collection of {@link PoliticsWorld}s which are subject to a particular set of {@link UniverseRules}
 * and share existing {@link GroupLevel}s and {@link Citizen} data.
 */
public final class Universe implements Storable {
    // todo docs
    private final String name;
    private final UniverseRules rules;
    private final List<PoliticsWorld> worlds;
    private final List<Group> groups;
    private final Map<Group, Set<Group>> children;
    private final Map<GroupLevel, List<Group>> levels;

    private LoadingCache<UUID, Set<Group>> citizenGroupCache;

    public Universe(String name, UniverseRules properties, List<PoliticsWorld> worlds) {
        this(name, properties, worlds, new ArrayList<>(), new HashMap<>());
    }

    private Universe(String name, UniverseRules rules, List<PoliticsWorld> worlds, List<Group> groups, Map<Group, Set<Group>> children) {
        this.name = name;
        this.rules = rules;
        this.worlds = worlds;
        this.groups = groups;
        this.children = children;

        buildCitizenCache();

        levels = new HashMap<>();
        for (Group group : groups) {
            getInternalGroups(group.getLevel()).add(group);
        }
    }

    /**
     * Builds the citizen cache for this Universe.
     */
    private void buildCitizenCache() {
        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();

        builder.maximumSize(Politics.getServer().getMaxPlayers());
        builder.expireAfterAccess(5L, TimeUnit.MINUTES);

        citizenGroupCache = builder.build(new CacheLoader<UUID, Set<Group>>() {
            @Override
            public Set<Group> load(UUID id) {
                Set<Group> myGroups = new HashSet<>();
                for (Group group : groups) {
                    if (group.isImmediateMember(id)) {
                        myGroups.add(group);
                    }
                }
                return myGroups;
            }
        });
    }

    /**
     * Gets the name of this Universe.
     * <p>
     * Note: no two Universes may have the same name.
     *
     * @return this Universe's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the configured {@link UniverseRules} of this universe.
     *
     * @return this Universe's configured rule set
     */
    public UniverseRules getRules() {
        return rules;
    }

    /**
     * Gets a {@link List} of all {@link Group}s which exist in this Universe.
     *
     * @return all Groups present in this Universe
     */
    public List<Group> getGroups() {
        return new ArrayList<>(groups);
    }

    /**
     * Gets a {@link List} of all {@link Group}s present in this Universe with the given property set to the given
     * value.
     *
     * @param property the id of the property to check
     * @param value    the requisite value of the property
     * @return all Groups in this Universe with the given value for the given property
     */
    public List<Group> getGroupsByProperty(int property, Object value) {
        List<Group> groups = new ArrayList<>();
        for (Group group : getGroups()) {
            if (group.getProperty(property).equals(value)) {
                groups.add(group);
            }
        }
        return groups;
    }

    /**
     * Gets the first {@link Group} in this Universe found with the given property set to the given value.
     *
     * @param property the id of the property to check
     * @param value    the requisite value of the property
     * @return the first Group in this Universe with the given value for the given property
     */
    public Group getFirstGroupByProperty(int property, Object value) {
        for (Group group : getGroups()) {
            if (group.getProperty(property).equals(value)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Gets the first {@link Group} of the given {@link GroupLevel} in this Universe found with the given property set
     * to the given value.
     *
     * @param level    the GroupLevel to check Groups of
     * @param property the id of the property to check
     * @param value    the requisite value of the property
     * @return the first Group of the given GroupLevel in this Universe with the given value for the given property
     */
    public Group getFirstGroupByProperty(GroupLevel level, int property, Object value) {
        for (Group group : getGroups(level)) {
            if (group.getProperty(property).equals(value)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Attempts to add the given {@link PoliticsWorld} to this Universe, meaning that {@link Group}s present in this
     * Universe will exist in the world and it will be subject to this Universe's {@link UniverseRules}.
     *
     * @param world the world to add to the universe
     * @return whether the world was successfully added
     */
    public boolean addWorld(PoliticsWorld world) {
        List<GroupLevel> levels = rules.getGroupLevels();
        // Check if the rules are already there
        if (world.getLevels().stream().anyMatch(levels::contains)) {
            return false;
        }
        return worlds.add(world);
    }

    public List<PoliticsWorld> getWorlds() {
        return new ArrayList<>(worlds);
    }

    public boolean containsWorld(PoliticsWorld world) {
        return worlds.contains(world);
    }

    public List<Group> getGroups(GroupLevel level) {
        return new ArrayList<>(getInternalGroups(level));
    }

    private List<Group> getInternalGroups(GroupLevel level) {
        return this.levels.computeIfAbsent(level, k -> new ArrayList<>());
    }

    public Set<Group> getChildGroups(Group group) {
        return new HashSet<>(getInternalChildGroups(group));
    }

    private Set<Group> getInternalChildGroups(Group group) {
        if (group == null) {
            return new HashSet<>();
        }
        Set<Group> groupChildren = children.get(group);
        if (groupChildren == null) {
            return new HashSet<>();
        }
        return groupChildren;
    }

    public boolean addChildGroup(Group group, Group child) {
        if (!group.getLevel().canBeChild(child.getLevel())) {
            return false;
        }

        Set<Group> groupChildren = children.computeIfAbsent(group, k -> new HashSet<>());
        groupChildren.add(child);
        return true;
    }

    public boolean removeChildGroup(Group group, Group child) {
        Set<Group> groupChildren = children.get(group);
        if (groupChildren == null) {
            return false;
        }
        return groupChildren.remove(child);
    }

    /**
     * Creates a new group of the given group level.
     * <p>
     * This method does not call an event.
     *
     * @param level the level for the group to create
     * @return a newly created group
     */
    public Group createGroup(GroupLevel level) {
        Group group = new Group(Politics.getUniverseManager().nextId(), level);

        groups.add(group);
        getInternalGroups(level).add(group);
        group.initialize(this);
        Politics.getUniverseManager().addGroup(group);

        return group;
    }

    /**
     * Destroys the given group, but not any of its children.
     * <p>
     * This method does not call an event.
     *
     * @param group the group to destroy
     */
    public void destroyGroup(Group group) {
        destroyGroup(group, false);
    }

    /**
     * Destroys the given group.
     * <p>
     * This method does not call an event.
     *
     * @param group the group to destroy
     * @param deep  whether to destroy the children of the group as well
     */
    public void destroyGroup(Group group, boolean deep) {
        Politics.getWarManager().getInvolvedWars(group).forEach(war -> Politics.getWarManager().finishWar(war, true));

        groups.remove(group);
        getInternalGroups(group.getLevel()).remove(group);
        group.getPlayers().forEach(this::invalidateCitizenGroups);
        if (deep) {
            group.getGroups().forEach(child -> destroyGroup(child, true));
        }

        Politics.getUniverseManager().removeGroup(group.getUid());

        children.remove(group);
        // This can be expensive
        children.values().forEach(set -> set.remove(group));
    }

    public Citizen getCitizen(UUID playerId, String name) {
        return new Citizen(playerId, name, this);
    }

    public Citizen getCitizen(Player player) {
        return getCitizen(player.getUniqueId(), player.getName());
    }

    public Set<Group> getCitizenGroups(UUID player) {
        try {
            Set<Group> groups = citizenGroupCache.get(player);
            if (groups == null) {
                citizenGroupCache.refresh(player);
                groups = citizenGroupCache.get(player);
            }
            return new HashSet<>(groups);
        } catch (ExecutionException e) {
            Politics.getLogger().log(Level.SEVERE, "Could not load a set of citizen groups! This is a PROBLEM!", e);
            return new HashSet<>();
        }
    }

    public Set<Group> getCitizenGroups(OfflinePlayer player) {
        return getCitizenGroups(player.getUniqueId());
    }

    void invalidateCitizenGroups(UUID citizen) {
        citizenGroupCache.invalidate(citizen);
    }

    @Override
    public BasicBSONObject toBSONObject() {
        BasicBSONObject bson = new BasicBSONObject();

        bson.put("name", name);
        bson.put("rules", rules.getName());

        BasicBSONList groupsBson = new BasicBSONList();
        BasicBSONObject childrenBson = new BasicBSONObject();

        for (Group group : groups) {
            if (!group.canStore()) {
                continue;
            }
            // groups
            groupsBson.add(group.toBSONObject());

            // children
            childrenBson.put(Integer.toString(group.getUid()),
                    group.getGroups().stream().map(Group::getUid).collect(Collectors.toCollection(BasicBSONList::new)));
        }

        bson.put("groups", groupsBson);
        bson.put("children", childrenBson);
        bson.put("worlds", worlds.stream().map(PoliticsWorld::getName)
                .collect(Collectors.toCollection(BasicBSONList::new)));

        return bson;
    }

    public static Universe fromBSONObject(BSONObject object) {
        if (!(object instanceof BasicBSONObject)) {
            throw new IllegalStateException("object is not a BasicBsonObject! ERROR ERROR ERROR!");
        }

        BasicBSONObject bobject = (BasicBSONObject) object;

        String aname = bobject.getString("name");
        String rulesName = bobject.getString("rules");
        UniverseRules rules = Politics.getUniverseManager().getRules(rulesName);

        if (rules == null) {
            throw new IllegalStateException("Rules do not exist!");
        }

        List<PoliticsWorld> worlds = new ArrayList<>();
        Object worldsObj = bobject.get("worlds");
        if (!(worldsObj instanceof BasicBSONList)) {
            throw new IllegalStateException("GroupWorlds object is not a list!!! ASDFASDF");
        }

        BasicBSONList worldsBson = (BasicBSONList) worldsObj;
        for (Object worldName : worldsBson) {
            String name = worldName.toString();
            PoliticsWorld world = Politics.getWorldManager().getWorld(name);
            if (world == null) {
                Politics.getLogger().log(Level.WARNING, "GroupWorld `" + name + "' could not be found! (Did you delete it?)");
            } else {
                worlds.add(world);
            }
        }

        Object groupsObj = bobject.get("groups");
        if (!(groupsObj instanceof BasicBSONList)) {
            throw new IllegalStateException("groups isn't a list?! wtfhax?");
        }

        BasicBSONList groupsBson = (BasicBSONList) groupsObj;

        TLongObjectMap<Group> groupMap = new TLongObjectHashMap<>();
        for (Object groupBson : groupsBson) {
            if (!(groupBson instanceof BasicBSONObject)) {
                throw new IllegalStateException("Invalid group!");
            }
            Group c = Group.fromBSONObject(rules, (BasicBSONObject) groupBson);
            groupMap.put(c.getUid(), c);
        }

        Map<Group, Set<Group>> children = new HashMap<>();
        Object childrenObj = bobject.get("children");
        if (!(childrenObj instanceof BasicBSONObject)) {
            throw new IllegalStateException("Missing children report!");
        }

        final BasicBSONObject childrenBson = (BasicBSONObject) childrenObj;
        for (Map.Entry<String, Object> childEntry : childrenBson.entrySet()) {
            String groupId = childEntry.getKey();
            int uid = Integer.parseInt(groupId);
            Group c = groupMap.get(uid);
            if (c == null) {
                throw new IllegalStateException("Unknown group id " + uid);
            }

            Object childsObj = childEntry.getValue();
            if (!(childsObj instanceof BasicBSONList)) {
                throw new IllegalStateException("No bson list found for childsObj");
            }

            Set<Group> childrenn = new HashSet<>();
            BasicBSONList childs = (BasicBSONList) childsObj;

            for (Object childN : childs) {
                long theuid = (Long) childN;
                Group ch = groupMap.get(theuid);
                childrenn.add(ch);
            }

            children.put(c, childrenn);
        }

        List<Group> groupList = new ArrayList<>(groupMap.valueCollection());
        Universe universe = new Universe(aname, rules, worlds, groupList, children);
        groupList.forEach(group -> group.initialize(universe));
        return universe;
    }

    @Override
    public boolean canStore() {
        return true;
    }
}
