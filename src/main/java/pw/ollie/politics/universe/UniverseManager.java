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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;

import pw.ollie.politics.Politics;
import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.data.InvalidConfigurationException;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politics.world.PoliticsWorld;

import org.apache.commons.io.FileUtils;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Manages data and configuration for {@link Universe}s in Politics.
 */
public final class UniverseManager {
    // todo docs
    private final PoliticsPlugin plugin;

    private Map<String, Universe> universes;
    private Map<String, UniverseRules> rules;
    private TIntObjectMap<Group> groups;
    private Map<PoliticsWorld, Map<GroupLevel, Universe>> worldLevels;

    private int nextId = 0;

    public UniverseManager(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    public PoliticsPlugin getPlugin() {
        return this.plugin;
    }

    public Universe getUniverse(String name) {
        return universes.get(name.toLowerCase());
    }

    public UniverseRules getRules(String rulesName) {
        return rules.get(rulesName);
    }

    public List<UniverseRules> listRules() {
        return new ArrayList<>(rules.values());
    }

    public Universe getUniverse(World world, GroupLevel level) {
        PoliticsWorld cw = plugin.getWorldManager().getWorld(world);
        if (cw == null) {
            return null;
        }
        return getUniverse(cw, level);
    }

    public List<GroupLevel> getGroupLevels() {
        List<GroupLevel> ret = new ArrayList<>();
        for (UniverseRules rules : this.rules.values()) {
            ret.addAll(rules.getGroupLevels());
        }
        return ret;
    }

    public Group getGroupById(int id) {
        return groups.get(id);
    }

    public Group getGroupByTag(String tag) {
        for (Group g : groups.valueCollection()) {
            if (g.getTag().equalsIgnoreCase(tag)) {
                return g;
            }
        }
        return null;
    }

    public Set<Universe> getUniverses() {
        return new THashSet<>(universes.values());
    }

    public Set<Universe> getUniverses(PoliticsWorld world) {
        return universes.values().stream()
                .filter(universe -> universe.containsWorld(world))
                .collect(Collectors.toSet());
    }

    public Set<Universe> getUniverses(World world) {
        return getUniverses(plugin.getWorldManager().getWorld(world));
    }

    public Universe getUniverse(PoliticsWorld world, GroupLevel level) {
        Map<GroupLevel, Universe> levelUniverses = worldLevels.get(world);
        if (levelUniverses == null) {
            return null;
        }
        return levelUniverses.get(level);
    }

    public List<GroupLevel> getLevelsOfWorld(PoliticsWorld world) {
        Map<GroupLevel, Universe> levelUniverses = worldLevels.get(world);
        if (levelUniverses == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(levelUniverses.keySet());
    }

    public Universe createUniverse(String name, UniverseRules theRules, List<PoliticsWorld> worlds) {
        Universe universe = new Universe(name, theRules, worlds);
        universes.put(name, universe);
        for (PoliticsWorld world : worlds) {
            worldLevels.putIfAbsent(world, new HashMap<>());
            Map<GroupLevel, Universe> map = worldLevels.get(world);
            for (GroupLevel level : theRules.getGroupLevels()) {
                map.put(level, universe);
            }
        }
        return universe;
    }

    public void destroyUniverse(Universe universe) {
        universes.remove(universe.getName());
        for (Group group : universe.getGroups()) {
            universe.destroyGroup(group);
        }
    }

    public int nextId() {
        while (getGroupById(nextId) != null) {
            nextId++;
        }
        return nextId;
    }

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
                data = FileUtils.readFileToByteArray(file);
            } catch (IOException ex) {
                new InvalidConfigurationException("Could not read universe file `" + fileName + "'!", ex).printStackTrace();
                continue;
            }

            BSONObject object = decoder.readObject(data);
            Universe universe = Universe.fromBSONObject(object);
            universes.put(universe.getName(), universe);

            for (Group group : universe.getGroups()) {
                if (groups.put(group.getUid(), group) != null) {
                    Politics.getLogger().log(Level.WARNING, "Duplicate group id " + group.getUid() + "!");
                }
                if (group.getUid() > nextId) {
                    nextId = group.getUid();
                }
            }
        }

        // Populate World levels
        worldLevels = new THashMap<>();
        for (Universe universe : universes.values()) {
            for (GroupLevel level : universe.getRules().getGroupLevels()) {
                for (PoliticsWorld world : universe.getWorlds()) {
                    Map<GroupLevel, Universe> levelMap = worldLevels.computeIfAbsent(world, k -> new HashMap<>());
                    Universe prev = levelMap.put(level, universe);
                    if (prev != null) {
                        new InvalidConfigurationException("Multiple universes are conflicting on the same world! Universe name: "
                                + universe.getName() + "; Rules name: " + universe.getRules().getName()).printStackTrace();
                    }
                }
            }
        }
    }

    public void saveUniverses() {
        BSONEncoder encoder = new BasicBSONEncoder();
        File universesDir = this.plugin.getFileSystem().getUniversesDir();

        for (Universe universe : universes.values()) {
            if (!universe.canStore()) {
                continue;
            }

            String fileName = universe.getName() + ".ptu";
            File universeFile = new File(universesDir, fileName);

            byte[] data = encoder.encode(universe.toBSONObject());
            try {
                FileUtils.writeByteArrayToFile(universeFile, data);
            } catch (IOException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save universe file `" + fileName + "' due to error!", ex);
                continue;
            }
            // TODO make backups
        }
    }
}
