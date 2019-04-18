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
package pw.ollie.politics.world;

import pw.ollie.politics.Politics;
import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politics.world.PoliticsWorld;
import pw.ollie.politics.world.WorldConfig;
import pw.ollie.politics.world.plot.Plot;
import pw.ollie.politics.world.plot.protection.PlotProtectionListener;

import org.apache.commons.io.FileUtils;

import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public final class WorldManager {
    private final PoliticsPlugin plugin;

    private Map<String, WorldConfig> configs;
    private Map<String, PoliticsWorld> worlds;

    public WorldManager(PoliticsPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(new PlotProtectionListener(plugin), plugin);
    }

    public void loadWorldConfigs() {
        configs = new HashMap<>();
        for (File file : plugin.getFileSystem().getWorldConfigDir().listFiles()) {
            String fileName = file.getName();
            if (!fileName.endsWith(".yml") || fileName.length() <= 4) {
                continue;
            }

            String name = fileName.substring(0, fileName.length() - 4);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            WorldConfig wc = WorldConfig.load(name, config);

            configs.put(name, wc);
        }
    }

    public void loadWorlds() {
        BasicBSONDecoder decoder = new BasicBSONDecoder();
        worlds = new HashMap<>();

        for (File file : plugin.getFileSystem().getWorldsDir().listFiles()) {
            String fileName = file.getName();
            if (!fileName.endsWith(".ptw") || fileName.length() <= 4) {
                continue;
            }

            String worldName = fileName.substring(0, fileName.length() - 4);

            byte[] data;
            try {
                data = FileUtils.readFileToByteArray(file);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not read world file `" + fileName + "'!", ex);
                continue;
            }

            WorldConfig config = getWorldConfig(worldName);
            BSONObject object = decoder.readObject(data);
            PoliticsWorld world = new PoliticsWorld(worldName, config, (BasicBSONObject) object);
            worlds.put(world.getName(), world);
        }
    }

    public void saveWorlds() {
        BSONEncoder encoder = new BasicBSONEncoder();
        Politics.getFileSystem().getWorldsDir().mkdirs();

        for (PoliticsWorld world : worlds.values()) {
            if (!world.canStore()) {
                continue;
            }

            String fileName = world.getName() + ".ptw";
            File worldFile = new File(Politics.getFileSystem().getWorldsDir(), fileName);

            byte[] data = encoder.encode(world.toBSONObject());
            try {
                FileUtils.writeByteArrayToFile(worldFile, data);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save universe file `" + fileName + "' due to error!", ex);
                continue;
            }
        }
    }

    public WorldConfig getWorldConfig(String name) {
        WorldConfig conf = configs.get(name);
        if (conf == null) {
            conf = new WorldConfig(name);
            Politics.getFileSystem().getWorldConfigDir().mkdirs();
            File toSave = new File(Politics.getFileSystem().getWorldConfigDir(), name + ".yml");
            YamlConfiguration tc = YamlConfiguration.loadConfiguration(toSave);
            conf.save(tc);
            try {
                tc.save(toSave);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not write a world config file!", e);
            }
            configs.put(name, conf);
        }
        return conf;
    }

    public PoliticsWorld getWorld(final String name) {
        PoliticsWorld world = worlds.get(name);
        if (world == null) {
            world = createWorld(name);
        }
        return world;
    }

    public PoliticsWorld getWorld(final World world) {
        return getWorld(world.getName());
    }

    public Plot getPlotAtChunkPosition(World world, int x, int z) {
        return getWorld(world).getPlotAtChunkPosition(x, z);
    }

    public Plot getPlotAtChunk(Chunk chunk) {
        return getWorld(chunk.getWorld()).getPlotAtChunkPosition(chunk.getX(), chunk.getZ());
    }

    public Plot getPlotAt(Location position) {
        return getPlotAtChunkPosition(position.getWorld(), position.getChunk().getX(), position.getChunk().getZ());
    }

    private PoliticsWorld createWorld(String name) {
        PoliticsWorld world = new PoliticsWorld(name, getWorldConfig(name));
        worlds.put(name, world);
        return world;
    }
}
