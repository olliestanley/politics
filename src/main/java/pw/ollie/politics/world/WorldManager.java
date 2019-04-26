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
import pw.ollie.politics.util.Position;
import pw.ollie.politics.world.plot.Plot;
import pw.ollie.politics.world.plot.PlotProtectionListener;

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
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Stores, manages, and provides access to configuration and data for worlds in Politics.
 */
public final class WorldManager {
    private final PoliticsPlugin plugin;

    private Map<String, WorldConfig> configs;
    private Map<String, PoliticsWorld> worlds;

    public WorldManager(PoliticsPlugin plugin) {
        this.plugin = plugin;
    }

    private WorldConfig getDefaultConfig(String worldName) {
        return new WorldConfig(worldName, true, true);
    }

    /**
     * Loads world configurations from their configuration files.
     */
    public void loadWorldConfigs() {
        for (World world : plugin.getServer().getWorlds()) {
            File worldFile = new File(plugin.getFileSystem().getWorldConfigDir(), world.getName() + ".yml");
            if (worldFile.exists()) {
                continue;
            }

            YamlConfiguration worldConfig = YamlConfiguration.loadConfiguration(worldFile);
            getDefaultConfig(world.getName()).save(worldConfig);

            try {
                worldConfig.save(worldFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not save default world config for world: " + world.getName(), e);
            }
        }

        configs = new HashMap<>();
        for (File file : Objects.requireNonNull(plugin.getFileSystem().getWorldConfigDir().listFiles())) {
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

    /**
     * Loads world data from their data files.
     */
    public void loadWorlds() {
        BasicBSONDecoder decoder = new BasicBSONDecoder();
        worlds = new HashMap<>();

        for (File file : Objects.requireNonNull(plugin.getFileSystem().getWorldsDir().listFiles())) {
            String fileName = file.getName();
            if (!fileName.endsWith(".ptw") || fileName.length() <= 4) {
                continue;
            }

            String worldName = fileName.substring(0, fileName.length() - 4);

            byte[] data;
            try {
                data = Files.readAllBytes(file.toPath());;
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not read world file `" + fileName + "'!", ex);
                continue;
            }

            WorldConfig config = getWorldConfig(worldName);
            BSONObject object = decoder.readObject(data);
            PoliticsWorld world = new PoliticsWorld(worldName, config, (BasicBSONObject) object);
            worlds.put(world.getName(), world);
        }

        plugin.getServer().getPluginManager().registerEvents(new PlotProtectionListener(plugin), plugin);
    }

    /**
     * Saves world data to data files.
     */
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
                Files.write(worldFile.toPath(), data);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save universe file `" + fileName + "' due to error!", ex);
            }
        }
    }

    /**
     * Gets the {@link WorldConfig} associated with the world with the given name.
     * <p>
     * If there is no current WorldConfig associated with the given name, a default config is created.
     *
     * @param name the name of the world to get config for
     * @return the WorldConfig for the world with the given name
     */
    public WorldConfig getWorldConfig(String name) {
        WorldConfig conf = configs.get(name);
        if (conf == null) {
            conf = getDefaultConfig(name);
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

    /**
     * Gets the {@link PoliticsWorld} for the world with the given name.
     * <p>
     * If there is no current PoliticsWorld associated with the given name, a blank one is created.
     *
     * @param name the name of the world to get data for
     * @return the PoliticsWorld for the world with the given name
     */
    public PoliticsWorld getWorld(String name) {
        PoliticsWorld world = worlds.get(name);
        if (world == null) {
            world = createWorld(name);
        }
        return world;
    }

    /**
     * Gets the {@link PoliticsWorld} for the given Bukkit {@link World}.
     * <p>
     * If there is no current PoliticsWorld associated with the given world, a blank one is created.
     *
     * @param world the world to get data for
     * @return the PoliticsWorld for the given world
     */
    public PoliticsWorld getWorld(World world) {
        return getWorld(world.getName());
    }

    /**
     * Gets the {@link Plot} object for the given chunk coordinates.
     *
     * @param world the world to search in
     * @param x     the chunk x coordinate
     * @param z     the chunk z coordinate
     * @return the Plot at the given chunk position in the given World
     */
    public Plot getPlotAtChunkPosition(World world, int x, int z) {
        return getWorld(world).getPlotAtChunkPosition(x, z);
    }

    /**
     * Gets the {@link Plot} object for the given {@link Chunk}.
     *
     * @param chunk the Chunk to get the Plot at
     * @return the Plot for the given Chunk
     */
    public Plot getPlotAtChunk(Chunk chunk) {
        return getWorld(chunk.getWorld()).getPlotAtChunkPosition(chunk.getX(), chunk.getZ());
    }

    /**
     * Gets the {@link Plot} object for the chunk at the given {@link Location}.
     *
     * @param position the Location to get the Plot at
     * @return the Plot at the given Location
     */
    public Plot getPlotAt(Location position) {
        return getPlotAtChunkPosition(position.getWorld(), position.getChunk().getX(), position.getChunk().getZ());
    }

    /**
     * Gets the {@link Plot} object for the chunk at the given {@link Position}.
     *
     * @param position the Position to get the Plot at
     * @return the Plot at the given Position
     */
    public Plot getPlotAt(Position position) {
        return getPlotAt(position.toLocation());
    }

    private PoliticsWorld createWorld(String name) {
        PoliticsWorld world = new PoliticsWorld(name, getWorldConfig(name));
        worlds.put(name, world);
        return world;
    }
}
