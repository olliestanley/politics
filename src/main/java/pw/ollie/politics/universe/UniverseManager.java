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
import gnu.trove.map.hash.TIntObjectHashMap;

import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.group.Group;
import pw.ollie.politics.group.GroupLevel;
import pw.ollie.politics.world.PoliticsWorld;

import org.bson.BSONDecoder;
import org.bson.BSONEncoder;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UniverseManager {
    private final PoliticsPlugin plugin;

    private Map<String, Universe> universes;
    private Map<String, UniverseRules> rules;
    private TIntObjectMap<Group> groups;
    private Map<PoliticsWorld, Map<GroupLevel, Universe>> worldLevels;

    private int nextId = 0xffffffff;

    public UniverseManager(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    public PoliticsPlugin getPlugin() {
        return this.plugin;
    }

    public Universe getUniverse(final String name) {
        return universes.get(name.toLowerCase());
    }

    public UniverseRules getRules(final String rulesName) {
        return rules.get(rulesName);
    }

    public List<UniverseRules> listRules() {
        return new ArrayList<>(rules.values());
    }

    public Universe getUniverse(World world, GroupLevel level) {
        final PoliticsWorld cw = null; // Politics.getWorld(world); TODO - when PoliticsWorld is properly implemented
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

//    public Group getGroupByTag(final String tag) {
//        for (final Group g : groups.valueCollection()) {
//            if (g.getStringProperty(GroupProperty.TAG).equalsIgnoreCase(tag)) {
//                return g;
//            }
//        }
//        return null;
//    }

    public Universe getUniverse(PoliticsWorld world, GroupLevel level) {
        final Map<GroupLevel, Universe> levelUniverses = worldLevels.get(world);
        if (levelUniverses == null) {
            return null;
        }
        return levelUniverses.get(world);
    }

    public List<GroupLevel> getLevelsOfWorld(PoliticsWorld world) {
        final Map<GroupLevel, Universe> levelUniverses = worldLevels.get(world);
        if (levelUniverses == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(levelUniverses.keySet());
    }

    public Universe createUniverse(String name, UniverseRules theRules) {
        final Universe universe = new Universe(name, theRules);
        universes.put(name, universe);
        return universe;
    }

//    public void destroyUniverse(Universe universe) {
//        universes.remove(universe.getName());
//        for (Group group : universe.getGroups()) {
//            universe.destroyGroup(group);
//        }
//    }

    public int nextId() {
        while (getGroupById(nextId) != null) {
            nextId++;
        }
        return nextId;
    }

    public void loadRules() {
        File rulesDir = this.plugin.getFileSystem().getRulesDir();
        this.rules = new HashMap<>();

        for (File file : rulesDir.listFiles()) {
            String fileName = file.getName();
            if (!fileName.endsWith(".yml") || fileName.length() <= 4) {
                continue;
            }

            String name = fileName.substring(0, fileName.length() - 4);
            YamlConfiguration configFile = YamlConfiguration.loadConfiguration(file);
            //UniverseRules thisRules = UniverseRules.load(name, configFile);
            //String ruleName = thisRules.getName();
            //rules.put(ruleName.toLowerCase(), thisRules);
        }
    }

    public void loadUniverses() {
        BSONDecoder decoder = new BasicBSONDecoder();
        universes = new HashMap<>();
        groups = new TIntObjectHashMap<>();
        File universesDir = this.plugin.getFileSystem().getUniversesDir();

        for (File file : universesDir.listFiles()) {
            String fileName = file.getName();
            if (!fileName.endsWith(".ptu") || fileName.length() <= 4) {
                continue;
            }

            byte[] data;
//            try {
//                data = FileUtils.readFileToByteArray(file);
//            } catch (IOException ex) {
//                new InvalidConfigurationException("Could not read universe file `" + fileName + "'!", ex).printStackTrace();
//                continue;
//            }

//            BSONObject object = decoder.readObject(data);
//            Universe universe = Universe.fromBSONObject(object);
//            universes.put(universe.getName(), universe);
//
//            for (final Group group : universe.getGroups()) {
//                if (groups.put(group.getUid(), group) != null) {
//                    PoliticsPlugin.logger().log(Level.WARNING, "Duplicate group id " + group.getUid() + "!");
//                }
//                if (group.getUid() > nextId) {
//                    nextId = group.getUid();
//                }
//            }
        }

        // Populate World levels
        worldLevels = new HashMap<>();
        for (Universe universe : universes.values()) {
            for (GroupLevel level : universe.getRules().getGroupLevels()) {
//                for (PoliticsWorld world : universe.getWorlds()) {
//                    Map<GroupLevel, Universe> levelMap = worldLevels.get(world);
//                    if (levelMap == null) {
//                        levelMap = new HashMap<>();
//                        worldLevels.put(world, levelMap);
//                    }
//                    Universe prev = levelMap.put(level, universe);
//                    if (prev != null) {
//                        new InvalidConfigurationException("Multiple universes are conflicting on the same world! Universe name: "
//                                + universe.getName() + "; Rules name: " + universe.getRules().getName()).printStackTrace();
//                    }
//                }
            }
        }
    }

    public void saveUniverses() {
        BSONEncoder encoder = new BasicBSONEncoder();
        File universesDir = this.plugin.getFileSystem().getUniversesDir();

        for (final Universe universe : universes.values()) {
            if (!universe.canStore()) {
                continue;
            }
            final String fileName = universe.getName() + ".cou";
            final File universeFile = new File(universesDir, fileName);

            final byte[] data = encoder.encode(universe.toBSONObject());
//            try {
//                FileUtils.writeByteArrayToFile(universeFile, data);
//            } catch (final IOException ex) {
//                PoliticsPlugin.logger().log(Level.SEVERE, "Could not save universe file `" + fileName + "' due to error!", ex);
//                continue;
//            }
            // TODO make backups
        }
    }
}
