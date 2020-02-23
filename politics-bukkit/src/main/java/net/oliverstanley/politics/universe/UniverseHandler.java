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
package net.oliverstanley.politics.universe;

import net.oliverstanley.politics.Politics;
import net.oliverstanley.politics.PoliticsPlugin;
import net.oliverstanley.politics.data.InvalidConfigurationException;
import net.oliverstanley.politics.event.PoliticsEventFactory;
import net.oliverstanley.politics.group.Group;
import net.oliverstanley.politics.group.GroupLevel;
import net.oliverstanley.politics.util.config.FileUtil;
import net.oliverstanley.politics.world.PoliticsWorld;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.bson.BSONDecoder;
import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * Manages data and configuration for {@link Universe}s in Politics.
 */
public final class UniverseHandler {
    // todo docs
    private final PoliticsPlugin plugin;

    private Map<String, Universe> universes;
    private Map<String, UniverseRules> rules;
    private TIntObjectMap<Group> groups;
    private Map<PoliticsWorld, Map<GroupLevel, Universe>> worldLevels;

    private int nextId = 0;

    public UniverseHandler(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    public Stream<GroupLevel> groupLevels() {
        return rules.values().stream().flatMap(UniverseRules::groupLevels);
    }

    public Stream<GroupLevel> worldGroupLevels(PoliticsWorld world) {
        return worldLevels.containsKey(world) ? worldLevels.get(world).keySet().stream() : Stream.empty();
    }

    public Stream<Universe> universes() {
        return universes.values().stream();
    }

    public Stream<Universe> worldUniverses(PoliticsWorld world) {
        return universes.values().stream().filter(universe -> universe.containsWorld(world));
    }

    public Stream<Universe> worldUniverses(World world) {
        return worldUniverses(plugin.getWorldHandler().getWorld(world));
    }

    public Stream<Group> groups() {
        return universes().flatMap(Universe::groups);
    }

    public Stream<Group> citizenGroups(Citizen citizen) {
        return citizenGroups(citizen.getUniqueId());
    }

    public Stream<Group> citizenGroups(UUID playerId) {
        return universes().flatMap(u -> u.citizenGroups(playerId));
    }

    public List<UniverseRules> getRules() {
        return new ArrayList<>(rules.values());
    }

    public Optional<Universe> getUniverse(String name) {
        return Optional.ofNullable(universes.get(name.toLowerCase()));
    }

    public Optional<Universe> getUniverse(World world, GroupLevel level) {
        return getUniverse(plugin.getWorldHandler().getWorld(world), level);
    }

    public Optional<Universe> getUniverse(PoliticsWorld world, GroupLevel level) {
        return worldLevels.containsKey(world) ? Optional.ofNullable(worldLevels.get(world).get(level)) : Optional.empty();
    }

    public int getNumUniverses() {
        return universes.size();
    }

    public Universe createUniverse(String name, UniverseRules rules, List<PoliticsWorld> worlds) {
        if (this.rules.putIfAbsent(rules.getName().toLowerCase(), rules) == null) {
            rules.groupLevels().forEach(this.plugin.getCommandManager()::registerGroupCommand);
        }

        Universe universe = new Universe(name, rules, worlds);
        universes.put(name.toLowerCase(), universe);

        worlds.forEach(world -> {
            worldLevels.putIfAbsent(world, new THashMap<>());
            Map<GroupLevel, Universe> map = worldLevels.get(world);
            rules.groupLevels().forEach(level -> map.put(level, universe));
        });

        PoliticsEventFactory.callUniverseCreateEvent(universe);
        return universe;
    }

    public void destroyUniverse(Universe universe) {
        universes.remove(universe.getName());
        universe.groups().forEach(universe::destroyGroup);
        PoliticsEventFactory.callUniverseDestroyEvent(universe);
    }

    // nullable
    public UniverseRules getRules(String rulesName) {
        return rules.get(rulesName);
    }

    public Optional<Group> getGroupById(int id) {
        return Optional.ofNullable(groups.get(id));
    }

    public Optional<Group> getGroupByTag(String tag) {
        return groups.valueCollection().stream().filter(g -> g.getTag().equalsIgnoreCase(tag)).findAny();
    }

    public Optional<GroupLevel> getLevelByName(String name) {
        return groupLevels().filter(level -> level.getName().equalsIgnoreCase(name)).findAny();
    }

    public PoliticsPlugin getPlugin() {
        return this.plugin;
    }

    // loading and storage

    public void loadRules() {
        File rulesDir = this.plugin.getFileSystem().getRulesDir();
        this.rules = new THashMap<>();

        for (File file : Objects.requireNonNull(rulesDir.listFiles())) {
            String fileName = file.getName();
            if (!fileName.endsWith(".yml") || fileName.length() <= 4) {
                continue;
            }

            String name = fileName.substring(0, fileName.length() - 4);
            YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);
            UniverseRules thisRules = UniverseRules.load(name, configFile);
            String ruleName = thisRules.getName();
            rules.put(ruleName.toLowerCase(), thisRules);
        }
    }

    public void saveRules() {
        File rulesDir = plugin.getFileSystem().getRulesDir();

        for (UniverseRules rules : rules.values()) {
            File rulesFile = new File(rulesDir, rules.getName() + ".yml");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(rulesFile);
            rules.save(config);
            File backup = new File(rulesDir, rules.getName() + ".yml.bck");

            try {
                Files.copy(rulesFile.toPath(), backup.toPath());
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not backup universe rules, any changes will not be stored...", e);
                continue;
            }

            try {
                config.save(rulesFile);
                backup.delete();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not save universe rules, restoring backup...", e);
                try {
                    Files.copy(backup.toPath(), rulesFile.toPath());
                } catch (IOException e1) {
                    plugin.getLogger().log(Level.SEVERE, "Could not restore backup, restore manually...", e);
                }
            }
        }
    }

    public void loadUniverses() {
        BSONDecoder decoder = new BasicBSONDecoder();
        universes = new THashMap<>();
        groups = new TIntObjectHashMap<>();
        File universesDir = this.plugin.getFileSystem().getUniversesDir();

        for (File file : Objects.requireNonNull(universesDir.listFiles())) {
            String fileName = file.getName();
            if (!fileName.endsWith(".ptu") || fileName.length() <= 4) {
                continue;
            }

            byte[] data;
            try {
                data = Files.readAllBytes(file.toPath());
            } catch (IOException ex) {
                new InvalidConfigurationException("Could not read universe file `" + fileName + "'!", ex).printStackTrace();
                continue;
            }

            BSONObject object = decoder.readObject(data);
            Universe universe = Universe.fromBSONObject(object);
            universes.put(universe.getName().toLowerCase(), universe);

            universe.groups().forEach(group -> {
                if (groups.putIfAbsent(group.getUid(), group) != null) {
                    Politics.getLogger().log(Level.WARNING, "Duplicate group id " + group.getUid() + "!");
                }
                if (group.getUid() > nextId) {
                    nextId = group.getUid();
                }
            });
        }

        // Populate World levels
        worldLevels = new THashMap<>();

        for (Universe universe : universes.values()) {
            universe.getRules().groupLevels().forEach(level -> universe.worlds().forEach(world -> {
                Map<GroupLevel, Universe> levelMap = worldLevels.computeIfAbsent(world, k -> new THashMap<>());
                Universe prev = levelMap.putIfAbsent(level, universe);
                if (prev != null) {
                    new InvalidConfigurationException("Multiple universes are conflicting on the same world! Universe name: "
                            + universe.getName() + "; Rules name: " + universe.getRules().getName()).printStackTrace();
                }
            }));
        }
    }

    public void saveUniverses() {
        BSONEncoder encoder = new BasicBSONEncoder();
        File universesDir = this.plugin.getFileSystem().getUniversesDir();

        for (Universe universe : universes.values()) {
            if (!universe.shouldStore()) {
                continue;
            }

            String fileName = universe.getName() + ".ptu";
            File universeFile = new File(universesDir, fileName);

            try {
                FileUtil.createBackup(universeFile);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not back up world file for universe '" + universe.getName() + "'. Data will not be saved...", ex);
                continue;
            }

            byte[] data = encoder.encode(universe.toBSONObject());
            try {
                Files.write(universeFile.toPath(), data);
            } catch (IOException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save universe file `" + fileName + "' due to error! Please restore backup...", ex);
            }
        }
    }

    // internal

    void addGroup(Group group) {
        groups.put(group.getUid(), group);
    }

    void removeGroup(int group) {
        groups.remove(group);
    }

    int nextId() {
        while (getGroupById(nextId).isPresent()) {
            nextId++;
        }
        return nextId;
    }
}
