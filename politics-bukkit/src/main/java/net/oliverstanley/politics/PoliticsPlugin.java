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
package net.oliverstanley.politics;

import net.oliverstanley.politics.command.PoliticsCommandRegistrar;
import net.oliverstanley.politics.data.PoliticsDataSaveTask;
import net.oliverstanley.politics.data.PoliticsFileSystem;
import net.oliverstanley.politics.listener.NotificationListener;
import net.oliverstanley.politics.listener.CombatListener;
import net.oliverstanley.politics.listener.PlayerListener;
import net.oliverstanley.politics.privilege.PrivilegeHandler;
import net.oliverstanley.politics.universe.UniverseHandler;
import net.oliverstanley.politics.world.WorldHandler;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

/**
 * Core plugin class for Politics.
 * <p>
 * Contains access methods to each branch of the plugin.
 */
public final class PoliticsPlugin extends JavaPlugin {
    public static final long DATA_SAVE_INTERVAL = 5 * 60 * 20;

    private static PoliticsPlugin instance;

    private PoliticsFileSystem fileSystem;

    private PrivilegeHandler privilegeHandler;
    private WorldHandler worldHandler;
    private UniverseHandler universeHandler;

    private PoliticsCommandRegistrar commandManager;
    private PoliticsDataSaveTask saveTask;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        instance = this;

        PluginManager pluginManager = this.getServer().getPluginManager();

        this.fileSystem = new PoliticsFileSystem(this.getDataFolder());

        this.privilegeHandler = new PrivilegeHandler(this);

        this.worldHandler = new WorldHandler(this);
        this.worldHandler.loadWorldConfigs();
        this.worldHandler.loadWorlds();

        pluginManager.registerEvents(new NotificationListener(this), this);
        pluginManager.registerEvents(new CombatListener(this), this);

        this.universeHandler = new UniverseHandler(this);
        this.universeHandler.loadRules();
        this.universeHandler.loadUniverses();

        this.commandManager = new PoliticsCommandRegistrar(this);
        this.commandManager.registerCommands();

        this.saveTask = new PoliticsDataSaveTask(this);
        this.saveTask.runTaskTimer(this, DATA_SAVE_INTERVAL, DATA_SAVE_INTERVAL);

        pluginManager.registerEvents(new PlayerListener(this), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
        this.saveTask.cancel();

        this.worldHandler.saveWorlds();
        this.universeHandler.saveRules();
        this.universeHandler.saveUniverses();

        instance = null;
    }

    /**
     * Gets the {@link PoliticsFileSystem} of the plugin. This provides access to the directories Politics stores data
     * and configuration in.
     *
     * @return the PoliticsFileSystem object associated with this plugin instance
     */
    public PoliticsFileSystem getFileSystem() {
        return fileSystem;
    }

    /**
     * Gets the {@link PrivilegeHandler} associated with this plugin instance.
     *
     * @return the plugin PrivilegeHandler instance
     */
    public PrivilegeHandler getPrivilegeHandler() {
        return privilegeHandler;
    }

    /**
     * Gets the {@link WorldHandler} associated with this plugin instance.
     *
     * @return the plugin WorldHandler instance
     */
    public WorldHandler getWorldHandler() {
        return worldHandler;
    }

    /**
     * Gets the {@link UniverseHandler} associated with this plugin instance.
     *
     * @return the plugin UniverseHandler instance
     */
    public UniverseHandler getUniverseHandler() {
        return universeHandler;
    }

    public PoliticsCommandRegistrar getCommandManager() {
        return commandManager;
    }

    /**
     * Gets the {@link PoliticsPlugin} instance.
     *
     * @return the running plugin instance
     */
    public static PoliticsPlugin instance() {
        return instance;
    }

    // constructors for MockBukkit testing

    public PoliticsPlugin() {
    }

    protected PoliticsPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }
}
