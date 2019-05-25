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
import pw.ollie.politics.group.level.GroupLevel;
import pw.ollie.politicstax.command.GroupSettaxCommand;
import pw.ollie.politicstax.tax.TaxationManager;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Core plugin class for PoliticsTax.
 */
public final class PoliticsTaxPlugin extends JavaPlugin {
    private PoliticsTaxConfig config;
    private TaxationManager taxationManager;

    @Override
    public void onEnable() {
        this.config = new PoliticsTaxConfig(this);
        this.config.loadConfig();

        this.taxationManager = new TaxationManager(this);
        this.taxationManager.loadTaxData();

        // todo data save task

        for (GroupLevel groupLevel : Politics.getGroupManager().getGroupLevels()) {
            Politics.getCommandManager().getPoliticsCommand(groupLevel.getId()).registerSubCommand(new GroupSettaxCommand(groupLevel));
        }
    }

    @Override
    public void onDisable() {
        this.taxationManager.saveTaxData(true);
    }

    public PoliticsTaxConfig getTaxConfig() {
        return config;
    }

    public TaxationManager getTaxationManager() {
        return taxationManager;
    }
}
