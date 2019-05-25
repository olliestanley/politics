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
package pw.ollie.politicswar;

import pw.ollie.politicswar.war.WarManager;

import org.bukkit.plugin.java.JavaPlugin;

public class PoliticsWarPlugin extends JavaPlugin {
    private WarManager warManager;

    @Override
    public void onEnable() {
        this.warManager = new WarManager(this);
        this.warManager.loadWars();
    }

    @Override
    public void onDisable() {
        this.warManager.saveWars();
    }

    /**
     * Gets the {@link WarManager} associated with this plugin instance.
     *
     * @return the plugin WarManager instance
     */
    public WarManager getWarManager() {
        return warManager;
    }
}
