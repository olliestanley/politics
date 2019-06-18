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
package pw.ollie.politicstax;

import pw.ollie.politics.Politics;
import pw.ollie.politics.PoliticsPlugin;
import pw.ollie.politicstax.command.GroupSettaxCommand;
import pw.ollie.politicstax.tax.TaxationManager;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Core plugin class for PoliticsTax.
 */
public final class PoliticsTaxPlugin extends JavaPlugin {
    private PoliticsTaxConfig config;
    private PoliticsTaxDataSaveTask saveTask;
    private TaxationManager taxationManager;

    @Override
    public void onEnable() {
        this.config = new PoliticsTaxConfig(this);
        this.config.loadConfig();

        this.taxationManager = new TaxationManager(this);
        this.taxationManager.loadTaxData();

        this.saveTask = new PoliticsTaxDataSaveTask(this);
        this.saveTask.runTaskTimer(this, PoliticsPlugin.DATA_SAVE_INTERVAL, PoliticsPlugin.DATA_SAVE_INTERVAL);

        Politics.getGroupManager().streamGroupLevels().forEach(level -> Politics.getCommandManager().getPoliticsCommand(level.getId()).registerSubCommand(new GroupSettaxCommand(level)));
    }

    @Override
    public void onDisable() {
        this.saveTask.cancel();

        this.taxationManager.saveTaxData(true);
    }

    public PoliticsTaxConfig getTaxConfig() {
        return config;
    }

    public TaxationManager getTaxationManager() {
        return taxationManager;
    }
}
