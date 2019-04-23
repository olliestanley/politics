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
package pw.ollie.politics;

import pw.ollie.politics.activity.ActivityManager;
import pw.ollie.politics.command.PoliticsCommandManager;
import pw.ollie.politics.data.PoliticsDataSaveTask;
import pw.ollie.politics.data.PoliticsFileSystem;
import pw.ollie.politics.economy.PoliticsEconomy;
import pw.ollie.politics.economy.TaxationManager;
import pw.ollie.politics.economy.vault.PoliticsEconomyVault;
import pw.ollie.politics.group.GroupManager;
import pw.ollie.politics.group.privilege.PrivilegeManager;
import pw.ollie.politics.group.war.WarManager;
import pw.ollie.politics.universe.UniverseManager;
import pw.ollie.politics.util.visualise.Visualiser;
import pw.ollie.politics.world.WorldManager;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class PoliticsPlugin extends JavaPlugin {
    private static PoliticsPlugin instance;

    private PoliticsFileSystem fileSystem;

    private PoliticsConfig config;

    private PrivilegeManager privilegeManager;
    private WorldManager worldManager;
    private UniverseManager universeManager;
    private GroupManager groupManager;
    private ActivityManager activityManager;
    private WarManager warManager;
    private PoliticsEconomy politicsEconomy;
    private TaxationManager taxationManager;

    private PoliticsCommandManager commandManager;

    private PoliticsDataSaveTask saveTask;

    private Visualiser visualiser;

    @Override
    public void onEnable() {
        instance = this;

        this.fileSystem = new PoliticsFileSystem(this.getDataFolder());

        this.config = new PoliticsConfig(this);
        this.config.loadConfig();

        this.privilegeManager = new PrivilegeManager(this);

        this.worldManager = new WorldManager(this);
        this.worldManager.loadWorldConfigs();
        this.worldManager.loadWorlds();

        this.groupManager = new GroupManager(this);

        this.universeManager = new UniverseManager(this);
        this.universeManager.loadRules();
        this.universeManager.loadUniverses();

        this.activityManager = new ActivityManager(this);

        if (config.areWarsEnabled()) {
            this.warManager = new WarManager(this);
            this.warManager.loadWars();
        }

        PluginManager pluginManager = this.getServer().getPluginManager();

        if (this.config.areEconomicFeaturesEnabled()) {
            if (this.config.getEconomyImplementation().equals("vault") && pluginManager.getPlugin("Vault") != null) {
                this.politicsEconomy = new PoliticsEconomyVault(this);
            } else {
                this.getLogger().log(Level.SEVERE, "Economy features are set to enabled but the specified economy type is invalid. Economic features will not be enabled.");
            }

            if (this.politicsEconomy != null && !this.politicsEconomy.loadEconomy()) {
                this.getLogger().log(Level.SEVERE, "Economy features are set to enabled but the economy type could not load. Economic features will not be enabled.");
                this.politicsEconomy = null;
            }

            if (this.config.isTaxEnabled() && this.politicsEconomy != null) {
                this.taxationManager = new TaxationManager(this);
                this.taxationManager.loadTaxData();
            }
        }

        this.commandManager = new PoliticsCommandManager(this);
        this.commandManager.registerCommands();

        this.saveTask = new PoliticsDataSaveTask(this);
        this.saveTask.runTaskTimer(this, 5 * 60 * 20, 5 * 60 * 20);

        this.visualiser = new Visualiser(this);

        pluginManager.registerEvents(new PoliticsListener(this), this);
    }

    @Override
    public void onDisable() {
        this.saveTask.cancel();

        if (this.config.areWarsEnabled()) {
            this.warManager.saveWars();
        }

        if (this.taxationManager != null) {
            this.taxationManager.saveTaxData(true);
        }

        this.worldManager.saveWorlds();
        this.universeManager.saveRules();
        this.universeManager.saveUniverses();

        instance = null;
    }

    public PoliticsFileSystem getFileSystem() {
        return fileSystem;
    }

    public PoliticsConfig getPoliticsConfig() {
        return config;
    }

    public PrivilegeManager getPrivilegeManager() {
        return privilegeManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public UniverseManager getUniverseManager() {
        return universeManager;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public WarManager getWarManager() {
        return warManager;
    }

    public PoliticsEconomy getEconomy() {
        return politicsEconomy;
    }

    public TaxationManager getTaxationManager() {
        return taxationManager;
    }

    public PoliticsCommandManager getCommandManager() {
        return commandManager;
    }

    public Visualiser getVisualiser() {
        return visualiser;
    }

    public static PoliticsPlugin instance() {
        return instance;
    }
}
